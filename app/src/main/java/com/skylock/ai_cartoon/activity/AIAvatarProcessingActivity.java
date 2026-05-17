package com.skylock.ai_cartoon.activity;

import android.content.Intent;
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
import com.skylock.ai_cartoon.model.ImageModel;
import com.skylock.ai_cartoon.model.ImageResponse;
import com.skylock.ai_cartoon.util.Constants;
import com.skylock.ai_cartoon.util.ErrorProcessingDialog;
import com.skylock.ai_cartoon.util.ThreadUtils;
import com.skylock.ai_cartoon.viewmodel.CartoonViewModel;
import com.skylock.ai_cartoon.viewmodel.CartoonViewModel.ErrorEvent;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * AIAvatarProcessingActivity — Production-ready
 * <p>
 * Handles all ErrorEvent cases from CartoonViewModel:
 * NO_INTERNET        → dialog: "No Internet Connection" — Retry restarts from scratch
 * SERVER_TIMEOUT     → dialog: "Request Timed Out"     — Retry restarts from scratch
 * SERVER_ERROR       → dialog: "Server Error"          — Retry restarts from scratch
 * MAX_POLL_EXCEEDED  → dialog: "Still Working…"        — Retry restarts from scratch
 * NO_RESPONSE        → dialog: "Server Error"          — Retry restarts from scratch
 * <p>
 * On retry: progress resets to 0, progress animation restarts, ViewModel re-submits request.
 */
public final class AIAvatarProcessingActivity extends BaseActivity {

    private static final String TAG = "AIAvatarProcessingActivity";

    // Progress animation constants
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
    private CartoonViewModel viewModel;
    // Progress animation
    private Disposable dismissDisposable;
    private Runnable timerRunnable;
    private int progress;

    // State flags
    private boolean isProcessing;
    private boolean loadedResultImage;
    private boolean startedActivityResult;
    private boolean errorDialogShown; // guard against duplicate dialogs

    // Intent data
    private String imageUri = "";
    private String style = "";
    private String gender = "other";
    private String feature = "";

    // Wake lock
    private PowerManager.WakeLock wakeLock;

    // ─────────────────────────────────────────────────────────────────────────
    // Lifecycle
    // ─────────────────────────────────────────────────────────────────────────


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProcessingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(CartoonViewModel.class);
        readIntentExtras();
        acquireWakeLock();

        Glide.with(this).load(imageUri).into(binding.imgPreview);
        binding.cvGoPremium.setVisibility(View.GONE);

        observeViewModel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.cvGoPremium.setVisibility(View.GONE);

        if (!isProcessing) {
            // Small delay so the UI is fully drawn before starting
            new Handler(Looper.getMainLooper())
                    .postDelayed(this::autoProcess, RESUME_DELAY_MS);
        }
    }

    @Override
    public void finish() {
        if (viewModel != null) viewModel.setActivityFinished();
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

    @NonNull
    @Override
    public ViewBinding inflateBinding(@NotNull LayoutInflater inflater) {
        return ActivityAiavatarProcessingBinding.inflate(getLayoutInflater());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ViewModel observation
    // ─────────────────────────────────────────────────────────────────────────

    private void observeViewModel() {
        // ── Success ──────────────────────────────────────────────────────────
        viewModel.getImageResponse().observe(this, imageResponse -> {
            if (imageResponse == null) return;
            errorDialogShown = false; // reset guard for this session
            Log.d(TAG, "imageResponse received: " + imageResponse.getUrl());
            onImageReady(imageResponse);
        });

        // ── Error events ─────────────────────────────────────────────────────
        viewModel.getErrorEvent().observe(this, errorEvent -> {
            if (errorEvent == null) return;
            Log.e(TAG, "errorEvent: " + errorEvent.name());
            handleErrorEvent(errorEvent);
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Error handling — the main production logic
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Maps every ErrorEvent to the right dialog and wires up Retry / Cancel.
     * All cases ultimately call retryFromScratch() on the ViewModel on retry.
     */
    private void handleErrorEvent(ErrorEvent event) {
        // Guard: don't show duplicate dialogs
        if (errorDialogShown) return;

        // Stop the fake progress animation; it will restart on retry
        stopProgressAnimation();
        isProcessing = false;
        errorDialogShown = true;

        ErrorProcessingDialog.display(
                getSupportFragmentManager(),
                event,
                new ProcessingListener() {
                    @Override
                    public void onRetry() {
                        errorDialogShown = false;
                        progress = 0;
                        updateProgressUI();
                        // Restart the ViewModel request from scratch
                        viewModel.retryFromScratch();
                        // Restart the fake progress animation
                        autoProcess();
                    }

                    @Override
                    public void onCancel() {
                        finish();
                    }
                });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Success flow
    // ─────────────────────────────────────────────────────────────────────────

    private void onImageReady(ImageResponse imageResponse) {
        if (startedActivityResult) return; // prevent double-navigation
        stopProgressAnimation();

        // Animate progress bar to 100% before navigating
        onDismissLoading(() -> {
            startedActivityResult = true;
            loadedResultImage = true;
            navigateToResult(imageResponse);
        });
    }

    private void navigateToResult(ImageResponse imageResponse) {
        clearMultiPhotos();

        Intent intent = new Intent(this, AIAvatarResultActivity.class);
        intent.putExtra("cartoonUrl", imageResponse.getUrl() != null
                ? imageResponse.getUrl() : "");
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
    // Progress animation
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Starts (or restarts) the fake 0→98% progress animation and
     * kicks off the actual API call in the ViewModel.
     */
    private void autoProcess() {
        isProcessing = true;

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (progress < 98) {
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

        // Tell the ViewModel to generate
        viewModel.onCartoon(imageUri, null, 1.0, gender,
                getIntent().getStringExtra("style"));
    }

    /**
     * Animate the progress bar from its current position to 100%
     * then invoke finishListener.onFinish().
     */
    private void onDismissLoading(final Runnable finishCallback) {
        stopProgressAnimation();

        int currentProgress = this.progress;
        long interval = currentProgress < PROGRESS_FAST_THRESHOLD
                ? DISMISS_FAST_INTERVAL : DISMISS_SLOW_INTERVAL;

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

    private void cancelDismissDisposable() {
        if (dismissDisposable != null && !dismissDisposable.isDisposed()) {
            dismissDisposable.dispose();
        }
        dismissDisposable = null;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Utility
    // ─────────────────────────────────────────────────────────────────────────

    private void readIntentExtras() {
        Intent i = getIntent();
        imageUri = i.getStringExtra("image_before") != null
                ? i.getStringExtra("image_before") : "";
        style = "christmas"; // fixed for this flow
        gender = i.getStringExtra("gender") != null
                ? i.getStringExtra("gender") : "other";
        feature = i.getStringExtra("feature") != null
                ? i.getStringExtra("feature") : "";
        Log.d(TAG, "imageUri=" + imageUri + " gender=" + gender + " feature=" + feature);
    }

    private void acquireWakeLock() {
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if (pm != null) {
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG + ":WakeLock");
            wakeLock.acquire(10 * 60 * 1000L); // max 10 min
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