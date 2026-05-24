package com.skylock.ai_cartoon.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import com.bumptech.glide.Glide;
import com.skylock.ai_cartoon.R;
import com.skylock.ai_cartoon.base.BaseActivity;
import com.skylock.ai_cartoon.callback.ProcessingListener;
import com.skylock.ai_cartoon.databinding.ActivityAiavatarProcessingBinding;
import com.skylock.ai_cartoon.databinding.ActivityProcessingBinding;
import com.skylock.ai_cartoon.enhance.ActivityEnhanceResult;
import com.skylock.ai_cartoon.enhance.EnhancerViewModel;
import com.skylock.ai_cartoon.enhance.EnhancerViewModelFactory;
import com.skylock.ai_cartoon.enhance.ProcessTypeConfig;
import com.skylock.ai_cartoon.model.ImageModel;
import com.skylock.ai_cartoon.model.ImageResponse;
import com.skylock.ai_cartoon.util.Constants;
import com.skylock.ai_cartoon.util.ErrorProcessingDialog;
import com.skylock.ai_cartoon.util.ThreadUtils;
import com.skylock.ai_cartoon.viewmodel.CartoonViewModel;
import com.skylock.ai_cartoon.viewmodel.CartoonViewModel.ErrorEvent;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

public final class AIAvatarProcessingActivity extends BaseActivity {

    private static final String TAG = "AIAvatarProcessingActivity";

    // ── Progress animation constants ─────────────────────────────────────────
    private static final long PROGRESS_INTERVAL_MS = 800L;
    private static final long RESUME_DELAY_MS = 1000L;
    private static final int PROGRESS_MAX = 100;
    private static final int PROGRESS_STEP = 1;
    private static final int PROGRESS_FAST_THRESHOLD = 70;
    private static final long DISMISS_FAST_INTERVAL = 50L;
    private static final long DISMISS_SLOW_INTERVAL = 150L;
    private static final int DISMISS_STEP = 5;

    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private final Handler showAdHandler = new Handler(Looper.getMainLooper());

    private ActivityProcessingBinding binding;
    private CartoonViewModel cartoonViewModel;
    private EnhancerViewModel enhancerViewModel;
    private String currentFeature = "";

    // ── Progress ─────────────────────────────────────────────────────────────
    private Disposable dismissDisposable;
    private Runnable timerRunnable;
    private int progress;       // always the GLOBAL 0-100 value shown on screen

    // ── State flags ──────────────────────────────────────────────────────────
    private boolean isProcessing;
    private boolean loadedResultImage;
    private boolean startedActivityResult;
    private boolean errorDialogShown;

    // ── Intent data ──────────────────────────────────────────────────────────
    private String imageUri = "";
    private String style = "";
    private String gender = "other";
    private String feature = "";

    // ── Triple-upscale state ─────────────────────────────────────────────────
    //
    // totalUpscalePasses  – how many passes were requested (0 = normal, 3 = triple).
    // upscaleRemaining    – passes still left including the current one.
    //                       pass 1 → remaining 3, pass 2 → 2, pass 3 → 1.
    // originalBeforeUri   – the very first "before" image, preserved across all
    //                       passes so the result screen always shows the real original.
    //
    // Progress segmentation (triple only):
    //   pass 1  animates  0 → 32   (ceiling kept at passProgressCeiling = 33)
    //   pass 2  animates 33 → 65   (ceiling 66)
    //   pass 3  animates 66 → 100  (ceiling 100)
    //
    // For normal (non-triple) flow all three fields stay at their default values
    // and nothing changes.
    // ─────────────────────────────────────────────────────────────────────────
    private int totalUpscalePasses = 0;
    private int upscaleRemaining = 0;
    private String originalBeforeUri = "";

    /**
     * Incremented each time we start a new pass so ViewModelProvider gets a
     * unique key and actually creates a fresh EnhancerViewModel instance.
     */
    private int enhancerPassKey = 0;

    /**
     * The global progress value this pass must NOT exceed while animating.
     * Computed once in onCreate from totalUpscalePasses / upscaleRemaining.
     */
    private int passProgressCeiling = PROGRESS_MAX; // default: full 100

    /**
     * The global progress value this pass STARTS from.
     * Set as the initial value of {@link #progress} in onCreate.
     */
    private int passProgressStart = 0; // default: 0

    // ── Wake lock ────────────────────────────────────────────────────────────
    private PowerManager.WakeLock wakeLock;

    // ─────────────────────────────────────────────────────────────────────────
    // Lifecycle
    // ─────────────────────────────────────────────────────────────────────────

    private static void copyFile(File src, File dst) throws Exception {
        try (InputStream in = new FileInputStream(src);
             OutputStream out = new FileOutputStream(dst)) {
            byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProcessingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        readIntentExtras();
        acquireWakeLock();

        currentFeature = feature;

        // ── Compute per-pass progress window ─────────────────────────────────
        if (totalUpscalePasses > 0 && upscaleRemaining > 0) {
            // e.g. total=3, remaining=3 → pass index 0 → window [0, 33]
            //      total=3, remaining=2 → pass index 1 → window [33, 66]
            //      total=3, remaining=1 → pass index 2 → window [66, 100]
            int passIndex = totalUpscalePasses - upscaleRemaining;          // 0-based
            int segmentSize = PROGRESS_MAX / totalUpscalePasses;              // 33 for 3 passes
            passProgressStart = passIndex * segmentSize;
            passProgressCeiling = (upscaleRemaining == 1)
                    ? PROGRESS_MAX                                                 // last pass goes to 100
                    : passProgressStart + segmentSize;
            progress = passProgressStart;
            Log.d(TAG, "Pass " + (passIndex + 1) + "/" + totalUpscalePasses
                    + "  progress window [" + passProgressStart + ", " + passProgressCeiling + "]");
        }
        // ─────────────────────────────────────────────────────────────────────

        // Check if this feature is supported by EnhancerViewModel (AI tools)
        if (ProcessTypeConfig.INSTANCE.getConfig(currentFeature) != null) {
            EnhancerViewModelFactory factory = new EnhancerViewModelFactory(currentFeature);
            enhancerViewModel = new ViewModelProvider(this, factory).get(EnhancerViewModel.class);
            observeEnhancerViewModel();
        } else {
            cartoonViewModel = new ViewModelProvider(this).get(CartoonViewModel.class);
            observeCartoonViewModel();
        }

        // Seed the progress bar to the start of this pass so it never jumps back
        binding.progressBar.setProgress(progress);
        binding.tvLoading.setText(getString(R.string.label_ai_generating) + " " + progress + "%");

        Glide.with(this).load(imageUri).into(binding.imgPreview);
        binding.cvGoPremium.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.cvGoPremium.setVisibility(View.GONE);
        if (!isProcessing) {
            // Use resolveUriThenProcess instead of autoProcess so that remote
            // HTTPS URLs (passed from ActivityEnhanceResult) are downloaded to
            // a local temp file before the enhancer API is called.
            new Handler(Looper.getMainLooper()).postDelayed(this::resolveUriThenProcess, RESUME_DELAY_MS);
        }
    }

    @Override
    public void finish() {
        if (cartoonViewModel != null) cartoonViewModel.setActivityFinished();
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopProgressAnimation();
        timerHandler.removeCallbacksAndMessages(null);
        showAdHandler.removeCallbacksAndMessages(null);
        cancelDismissDisposable();
        releaseWakeLock();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Cartoon ViewModel observation (unchanged)
    // ─────────────────────────────────────────────────────────────────────────

    @NonNull
    @Override
    public ViewBinding inflateBinding(@NotNull LayoutInflater inflater) {
        return ActivityAiavatarProcessingBinding.inflate(getLayoutInflater());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Enhancer ViewModel observation
    // ─────────────────────────────────────────────────────────────────────────

    private void observeCartoonViewModel() {
        cartoonViewModel.getImageResponse().observe(this, imageResponse -> {
            if (imageResponse == null) return;
            errorDialogShown = false;
            Log.d(TAG, "imageResponse received: " + imageResponse.getUrl());
            onCartoonSuccess(imageResponse);
        });

        cartoonViewModel.getErrorEvent().observe(this, errorEvent -> {
            if (errorEvent == null) return;
            Log.e(TAG, "errorEvent: " + errorEvent.name());
            handleErrorEvent(errorEvent);
        });
    }

    private void observeEnhancerViewModel() {
        enhancerViewModel.getResultUrl().observe(this, url -> {
            if (url != null && !url.isEmpty()) {
                errorDialogShown = false;
                onEnhanceSuccess(url);
            }
        });

        enhancerViewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                handleErrorEvent(ErrorEvent.SERVER_ERROR);
            }
        });

        enhancerViewModel.getProgress().observe(this, vmPercent -> {
            // vmPercent is the ViewModel's own 0-100 value.
            // Map it into [passProgressStart, passProgressCeiling] so the
            // global bar never resets or overshoots its segment.
            if (vmPercent == null) return;
            int mapped = mapVmProgressToGlobal(vmPercent);
            if (mapped > progress && progress < passProgressCeiling) {
                progress = mapped;
                updateProgressUI();
            }
        });

        enhancerViewModel.getEnhancementStage().observe(this, stage -> {
            if (stage == EnhancerViewModel.EnhancementStage.ERROR) {
                handleErrorEvent(ErrorEvent.SERVER_ERROR);
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Error handling
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Maps the EnhancerViewModel's internal 0-100 progress into the
     * global [passProgressStart … passProgressCeiling] window for this pass.
     * <p>
     * For a normal (non-triple) call passProgressStart=0 and
     * passProgressCeiling=100, so the mapping is identity.
     */
    private int mapVmProgressToGlobal(int vmPercent) {
        int windowSize = passProgressCeiling - passProgressStart;
        return passProgressStart + (vmPercent * windowSize / 100);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Success handlers
    // ─────────────────────────────────────────────────────────────────────────

    private void handleErrorEvent(ErrorEvent event) {
        if (errorDialogShown) return;
        isProcessing = false;
        errorDialogShown = true;

        ErrorProcessingDialog.display(
                getSupportFragmentManager(),
                event,
                new ProcessingListener() {
                    @Override
                    public void onRetry() {
                        errorDialogShown = false;
                        // On retry reset only this pass's start value,
                        // not the whole bar
                        progress = passProgressStart;
                        updateProgressUI();
                        if (enhancerViewModel != null) {
                            autoProcess();
                        } else {
                            cartoonViewModel.retryFromScratch();
                            autoProcess();
                        }
                    }

                    @Override
                    public void onCancel() {
                        finish();
                    }
                });
    }

    private void onCartoonSuccess(ImageResponse imageResponse) {
        if (startedActivityResult) return;
        stopProgressAnimation();
        onDismissLoading(() -> {
            startedActivityResult = true;
            loadedResultImage = true;
            navigateToCartoonResult(imageResponse);
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // onEnhanceSuccess
    //
    // Normal flow (totalUpscalePasses == 0):
    //   → go straight to ActivityEnhanceResult
    //
    // Triple-upscale flow (totalUpscalePasses == 3):
    //   upscaleRemaining > 1 → fill this pass's segment to its ceiling,
    //                          download the result, start next pass in SAME activity
    //   upscaleRemaining == 1 → final pass; onDismissLoading fills 66→100,
    //                           then navigate to ActivityEnhanceResult
    // ─────────────────────────────────────────────────────────────────────────

    private void navigateToCartoonResult(ImageResponse imageResponse) {
        clearMultiPhotos();
        Intent intent = new Intent(this, AIAvatarResultActivity.class);
        intent.putExtra("cartoonUrl", imageResponse.getUrl() != null ? imageResponse.getUrl() : "");
        intent.putExtra("image_before", imageUri);
        intent.putExtra("isfromCartton", true);
        intent.putExtra("style", style);
        intent.putExtra("gender", gender);
        intent.putExtra("feature", feature);
        if (imageResponse.getSize() != null) {
            Integer w = imageResponse.getSize().getWidth();
            Integer h = imageResponse.getSize().getHeight();
            intent.putExtra("image_width", w != null ? w : 0);
            intent.putExtra("image_height", h != null ? h : 0);
        }
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_nothing);
        finish();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // smoothFillToCeiling
    //
    // Quickly animates progress from its current value up to `target`,
    // then calls `onDone` on the main thread.
    // Used between passes so the user sees the bar fill each segment
    // before the next API call begins.
    // ─────────────────────────────────────────────────────────────────────────

    private void onEnhanceSuccess(String resultUrl) {
        if (startedActivityResult) return;
        stopProgressAnimation();

        Log.d(TAG, "onEnhanceSuccess  upscaleRemaining=" + upscaleRemaining
                + "  url=" + resultUrl);

        if (upscaleRemaining > 1) {
            // ── Not the last pass yet ────────────────────────────────────────
            // Animate the bar smoothly to the ceiling of this pass's segment,
            // then download and kick off the next pass — all in ONE activity,
            // no screen transition.
            startedActivityResult = true; // block duplicate calls
            smoothFillToCeiling(passProgressCeiling, () ->
                    downloadAndStartNextPass(resultUrl, upscaleRemaining - 1));
        } else {
            // ── Last pass (or normal single-pass) ────────────────────────────
            // onDismissLoading will animate to 100, then open result screen.
            onDismissLoading(() -> {
                startedActivityResult = true;
                clearMultiPhotos();

                String beforeToShow = (!originalBeforeUri.isEmpty())
                        ? originalBeforeUri
                        : imageUri;

                boolean returnToExisting = getIntent().getBooleanExtra("return_to_enhance_result", false);

                Intent intent = new Intent(AIAvatarProcessingActivity.this,
                        ActivityEnhanceResult.class);
                intent.putExtra("before_path", beforeToShow);
                intent.putExtra("after_url", resultUrl);

                if (returnToExisting) {
                    // ActivityEnhanceResult is singleTop — this routes the result
                    // into its onNewIntent() so the existing instance (with all
                    // previously accumulated results) receives the new item and
                    // appends it to the RecyclerView without any data loss.
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                }

                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_nothing);
                finish();
            });
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // downloadAndStartNextPass
    //
    // Downloads the URL from the completed pass to a temp file, then
    // re-configures THIS activity for the next pass and calls autoProcess()
    // again — exactly like calling onCreate a second time but without
    // destroying and recreating the Activity.
    // ─────────────────────────────────────────────────────────────────────────

    private void smoothFillToCeiling(int target, Runnable onDone) {
        cancelDismissDisposable();
        // Use a fast 40ms tick to fill the remaining segment quickly but visibly
        dismissDisposable = Observable
                .interval(40L, 40L, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tick -> {
                    if (progress < target) {
                        progress = Math.min(progress + 2, target);
                        updateProgressUI();
                    } else {
                        cancelDismissDisposable();
                        if (onDone != null) onDone.run();
                    }
                }, throwable -> {
                    Log.e(TAG, "smoothFillToCeiling error", throwable);
                    if (onDone != null) onDone.run();
                });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // configureAndRunNextPass
    //
    // Updates all per-pass state fields for the upcoming pass and creates
    // a fresh EnhancerViewModel for it, then calls autoProcess().
    // ─────────────────────────────────────────────────────────────────────────

    private void downloadAndStartNextPass(final String resultUrl, final int nextRemaining) {
        Log.d(TAG, "downloadAndStartNextPass  nextRemaining=" + nextRemaining + "  url=" + resultUrl);

        new Thread(() -> {
            try {
                // Download via Glide (synchronous — already off main thread)
                File cachedFile = Glide.with(AIAvatarProcessingActivity.this)
                        .asFile()
                        .load(resultUrl)
                        .submit()
                        .get();

                File tempFile = new File(getCacheDir(),
                        "upscale_pass_" + System.currentTimeMillis() + ".jpg");
                copyFile(cachedFile, tempFile);

                Log.d(TAG, "Downloaded to: " + tempFile.getAbsolutePath());

                runOnUiThread(() -> configureAndRunNextPass(tempFile.getAbsolutePath(), nextRemaining));

            } catch (Exception e) {
                Log.e(TAG, "downloadAndStartNextPass failed", e);
                runOnUiThread(() -> handleErrorEvent(ErrorEvent.SERVER_ERROR));
            }
        }).start();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // autoProcess  (unchanged logic, respects passProgressCeiling in timer)
    // ─────────────────────────────────────────────────────────────────────────

    private void configureAndRunNextPass(String nextImagePath, int nextRemaining) {
        // Update input image for the next API call
        imageUri = nextImagePath;

        // Recalculate progress window
        upscaleRemaining = nextRemaining;
        int passIndex = totalUpscalePasses - upscaleRemaining;
        int segmentSize = PROGRESS_MAX / totalUpscalePasses;
        passProgressStart = passIndex * segmentSize;
        passProgressCeiling = (upscaleRemaining == 1)
                ? PROGRESS_MAX
                : passProgressStart + segmentSize;

        Log.d(TAG, "configureAndRunNextPass  passIndex=" + passIndex
                + "  window=[" + passProgressStart + ", " + passProgressCeiling + "]"
                + "  imageUri=" + imageUri);

        // Reset flags for the new pass
        isProcessing = false;
        startedActivityResult = false;
        errorDialogShown = false;

        // Keep the local progress field in sync with where smoothFillToCeiling
        // left the bar (it filled exactly to the previous ceiling == this pass's
        // start). Refresh the label so it never shows stale text during the
        // download gap before autoProcess() restarts the timer.
        progress = passProgressStart;
        updateProgressUI();

        // ── Create a truly fresh ViewModel for the next pass ─────────────────
        // ViewModelProvider caches by key; bump enhancerPassKey so we get a
        // brand-new instance (and a clean _progress LiveData starting at 0)
        // instead of the stale one that still holds values near 100.
        // We also remove the previous LiveData observers first; because
        // observeEnhancerViewModel() uses observe(this, …) the observers are
        // lifecycle-bound and would otherwise accumulate across passes.
        if (enhancerViewModel != null) {
            enhancerViewModel.getResultUrl().removeObservers(this);
            enhancerViewModel.getErrorMessage().removeObservers(this);
            enhancerViewModel.getProgress().removeObservers(this);
            enhancerViewModel.getEnhancementStage().removeObservers(this);
        }
        enhancerPassKey++;
        EnhancerViewModelFactory factory = new EnhancerViewModelFactory(currentFeature);
        enhancerViewModel = new ViewModelProvider(this, factory)
                .get("enhancer_pass_" + enhancerPassKey, EnhancerViewModel.class);
        observeEnhancerViewModel();

        // Kick off the API call immediately (no RESUME_DELAY needed)
        autoProcess();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // URI → File helper (unchanged)
    // ─────────────────────────────────────────────────────────────────────────

    private void autoProcess() {
        isProcessing = true;

        // Capture ceiling for this pass so the lambda uses the right value
        final int ceiling = passProgressCeiling;

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                // Stop 2 below the ceiling so the API result can push it the rest of the way
                if (progress < ceiling - 2) {
                    progress += PROGRESS_STEP;
                    binding.progressBar.setProgress(progress);
                    binding.tvLoading.setText(
                            getString(R.string.label_ai_generating) + " " + progress + "%");
                    binding.lottie.setProgress(progress);
                }
                timerHandler.postDelayed(this, PROGRESS_INTERVAL_MS);
            }
        };
        timerHandler.postDelayed(timerRunnable, 0L);

        if (enhancerViewModel != null) {
            File imageFile = getImageFileFromUri(imageUri);
            if (imageFile == null || !imageFile.exists()) {
                Log.e(TAG, "Image file not found: " + imageUri);
                handleErrorEvent(ErrorEvent.SERVER_ERROR);
                return;
            }
            enhancerViewModel.processPhoto(imageFile, "");
        } else if (cartoonViewModel != null) {
            cartoonViewModel.onCartoon(imageUri, null, 1.0, gender,
                    getIntent().getStringExtra("style"));
        } else {
            Log.e(TAG, "No ViewModel initialized");
            handleErrorEvent(ErrorEvent.SERVER_ERROR);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // onDismissLoading  (unchanged — animates from current progress → 100)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Resolves any URI/URL string to a local {@link File} the enhancer API can consume.
     * <p>
     * Handles four cases in priority order:
     * <ol>
     *   <li>Absolute local path that already exists on disk (temp files from
     *       {@link #downloadAndStartNextPass}).</li>
     *   <li>{@code file://} scheme → strip prefix and return the file.</li>
     *   <li>{@code content://} scheme → convert via {@link Constants#convertMediaUriToPath}.</li>
     *   <li>{@code http://} / {@code https://} scheme → download synchronously via Glide
     *       into a uniquely-named temp file in {@link #getCacheDir()}.
     *       <b>This method must only be called off the main thread when the URL is remote.</b>
     *       {@link #autoProcess()} calls it on the main thread for local paths (fast), and
     *       the new {@link #resolveUriThenProcess()} helper moves the work to a background
     *       thread when the URI is remote.</li>
     * </ol>
     */
    private File getImageFileFromUri(String uriString) {
        if (uriString == null || uriString.isEmpty()) return null;
        try {
            // ── 1. Direct absolute path (temp files, no scheme) ──────────────
            File direct = new File(uriString);
            if (direct.exists()) return direct;

            Uri uri = Uri.parse(uriString);
            String scheme = uri.getScheme();

            // ── 2. file:// ────────────────────────────────────────────────────
            if ("file".equalsIgnoreCase(scheme)) {
                String path = uri.getPath();
                if (path != null) {
                    File file = new File(path);
                    if (file.exists()) return file;
                }
            }

            // ── 3. content:// ─────────────────────────────────────────────────
            if ("content".equalsIgnoreCase(scheme)) {
                String realPath = Constants.convertMediaUriToPath(this, uri);
                if (realPath != null) {
                    File file = new File(realPath);
                    if (file.exists()) return file;
                }
            }

            // ── 4. http:// / https:// → download via Glide (blocking) ─────────
            // NOTE: This branch is only reached from a background thread
            // (see resolveUriThenProcess). Never call on the main thread.
            if ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)) {
                Log.d(TAG, "Remote URL detected — downloading to cache: " + uriString);
                File cachedFile = Glide.with(getApplicationContext())
                        .asFile()
                        .load(uriString)
                        .submit()
                        .get(); // blocking — must be off main thread

                File tempFile = new File(getCacheDir(),
                        "enhance_input_" + System.currentTimeMillis() + ".jpg");
                copyFile(cachedFile, tempFile);
                Log.d(TAG, "Downloaded remote image to: " + tempFile.getAbsolutePath());
                return tempFile;
            }

            // ── Fallback: try convertMediaUriToPath on any remaining schemes ──
            String converted = Constants.convertMediaUriToPath(this, uri);
            if (converted != null) {
                File fallback = new File(converted);
                if (fallback.exists()) return fallback;
            }

        } catch (Exception e) {
            Log.e(TAG, "Error converting URI to file: " + uriString, e);
        }
        return null;
    }

    /**
     * Detects whether {@link #imageUri} is a remote URL and, if so, resolves it
     * on a background thread before handing off to {@link #autoProcess()}.
     * For local paths autoProcess() is called directly on the current thread.
     * <p>
     * Call this instead of {@code autoProcess()} whenever the URI may be remote
     * (i.e. came from {@link com.skylock.ai_cartoon.enhance.ActivityEnhanceResult}).
     */
    private void resolveUriThenProcess() {
        Uri uri = Uri.parse(imageUri);
        String scheme = uri.getScheme();
        boolean isRemote = "http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme);

        if (!isRemote) {
            // Already local — no download needed, run directly
            autoProcess();
            return;
        }

        // Show progress so the user doesn't see a frozen screen while downloading
        isProcessing = true;
        binding.progressBar.setProgress(progress);
        binding.tvLoading.setText(getString(R.string.label_ai_generating) + " " + progress + "%");

        new Thread(() -> {
            try {
                File cachedFile = Glide.with(getApplicationContext())
                        .asFile()
                        .load(imageUri)
                        .submit()
                        .get();

                File tempFile = new File(getCacheDir(),
                        "enhance_input_" + System.currentTimeMillis() + ".jpg");
                copyFile(cachedFile, tempFile);

                Log.d(TAG, "resolveUriThenProcess: downloaded to " + tempFile.getAbsolutePath());

                runOnUiThread(() -> {
                    imageUri = tempFile.getAbsolutePath(); // replace URL with local path
                    autoProcess();                         // now safe — local file exists
                });

            } catch (Exception e) {
                Log.e(TAG, "resolveUriThenProcess failed", e);
                runOnUiThread(() -> handleErrorEvent(ErrorEvent.SERVER_ERROR));
            }
        }).start();
    }

    private void onDismissLoading(final Runnable finishCallback) {
        stopProgressAnimation();
        int currentProgress = this.progress;
        long interval = currentProgress < PROGRESS_FAST_THRESHOLD
                ? DISMISS_FAST_INTERVAL
                : DISMISS_SLOW_INTERVAL;

        if (currentProgress < PROGRESS_MAX) {
            cancelDismissDisposable();
            dismissDisposable = Observable
                    .interval(interval, interval, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(tick -> {
                        if (progress < PROGRESS_MAX) {
                            progress = Math.min(progress + DISMISS_STEP, PROGRESS_MAX);
                            updateProgressUI();
                        } else {
                            cancelDismissDisposable();
                            if (finishCallback != null) finishCallback.run();
                        }
                    }, throwable -> {
                        Log.e(TAG, "dismiss animation error", throwable);
                        if (finishCallback != null) finishCallback.run();
                    });
        } else {
            if (finishCallback != null) finishCallback.run();
        }
    }

    private void updateProgressUI() {
        ThreadUtils.runOnMainThread(() -> {
            binding.progressBar.setProgress(progress);
            binding.tvLoading.setText(
                    getString(R.string.label_ai_generating) + " " + progress + "%");
            binding.lottie.setProgress(progress);
        });
    }

    private void stopProgressAnimation() {
        if (timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
            timerRunnable = null;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Intent extras
    // ─────────────────────────────────────────────────────────────────────────

    private void cancelDismissDisposable() {
        if (dismissDisposable != null && !dismissDisposable.isDisposed()) {
            dismissDisposable.dispose();
        }
        dismissDisposable = null;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Utilities
    // ─────────────────────────────────────────────────────────────────────────

    private void readIntentExtras() {
        Intent i = getIntent();
        imageUri = i.getStringExtra("image_before") != null ? i.getStringExtra("image_before") : "";
        feature = i.getStringExtra("feature") != null ? i.getStringExtra("feature") : "";
        style = i.getStringExtra("style") != null ? i.getStringExtra("style") : "christmas";
        gender = i.getStringExtra("gender") != null ? i.getStringExtra("gender") : "other";

        // Triple-upscale extras (0 / "" when not a chained call)
        totalUpscalePasses = i.getIntExtra("total_upscale_passes", 0);
        upscaleRemaining = i.getIntExtra("upscale_remaining", 0);
        originalBeforeUri = i.getStringExtra("original_before_uri") != null
                ? i.getStringExtra("original_before_uri") : "";

        Log.d(TAG, "feature=" + feature
                + "  imageUri=" + imageUri
                + "  totalPasses=" + totalUpscalePasses
                + "  upscaleRemaining=" + upscaleRemaining
                + "  originalBefore=" + originalBeforeUri);
    }

    private void acquireWakeLock() {
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if (pm != null) {
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG + ":WakeLock");
            wakeLock.acquire(10 * 60 * 1000L);
        }
    }

    private void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    private void clearMultiPhotos() {
        Iterator<ImageModel> it = Constants.MULTI_PHOTOS_NEW.iterator();
        while (it.hasNext()) {
            it.next().setSelectNumber(0);
        }
        Constants.MULTI_PHOTOS_NEW.clear();
    }
}