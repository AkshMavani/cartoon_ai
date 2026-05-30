package com.skylock.ai_cartoon.enhance;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.skylock.ai_cartoon.R;
import com.skylock.ai_cartoon.activity.AIAvatarProcessingActivity;
import com.skylock.ai_cartoon.activity.ActivityProcess;
import com.skylock.ai_cartoon.model.SizeImage;
import com.skylock.ai_cartoon.remove_obj.RemoveObjActivity;
import com.skylock.ai_cartoon.util.Feature;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class ActivityEnhanceResult extends AppCompatActivity
        implements ImageBeforeAfterSlider.ViewListener {

    // ─── Constants ────────────────────────────────────────────────────────────
    private static final long TOOLTIP_DELAY_MS = 800L;
    private static final long CLICK_DEBOUNCE_MS = 600L;
    private final List<ResultItem> resultItemList = new ArrayList<>();
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    // ─── Cache file registry ──────────────────────────────────────────────────
    // Every local cache file created during this session is registered here.
    // All entries are deleted in onDestroy so the device cache never accumulates.
    // Only btnSave explicitly writes to the gallery; all other paths stay here.
    private final List<File> sessionCacheFiles = new ArrayList<>();

    // True when the user tapped btnVersion3 (triple upscale). In this mode
    // AIAvatarProcessingActivity is told to cache the final result locally
    // rather than returning a raw HTTPS URL, so nothing leaks to the gallery.
    private boolean isCacheOnlySession = false;
    // ─── Views ────────────────────────────────────────────────────────────────
    private ImageBeforeAfterSlider imageBeforeAfterSlider;
    private ImageView btnSave;
    private AppCompatImageView btnGenerate;
    private TextView btnContinue;
    private View tooltipView;
    private RecyclerView rvResult;
    // ─── State ────────────────────────────────────────────────────────────────
    private String beforeImageUrl = "";
    private String afterImageUrl = "";

    private String featureSelected = "enhance";
    private int displayWidth = 0;
    private int displayHeight = 0;
    private ResultItem faceSelected = null;
    private ResultItemAdapter resultItemAdapter;
    private long lastClickTime = 0L;

    // ─── Lifecycle ────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enhance_result);

        readIntentExtras();
        initViews();
        setupRecyclerView();
        setupClickListeners();
        loadImageAndSetupSlider();
        showTooltipDelayed();
        selectedItemSet();
    }

    // ─── Intent ───────────────────────────────────────────────────────────────

    private void readIntentExtras() {
        beforeImageUrl = getIntent().getStringExtra("before_path") != null
                ? getIntent().getStringExtra("before_path") : "";
        afterImageUrl = getIntent().getStringExtra("after_url") != null
                ? getIntent().getStringExtra("after_url") : "";
        featureSelected = getIntent().getStringExtra("feature") != null ? getIntent().getStringExtra("feature") : "enhance";
    }

    private void selectedItemSet() {
        TextView tvCallToAction = findViewById(R.id.tvCallToAction);
        if (featureSelected.equals(Feature.ENHANCE.getValue())) {
            tvCallToAction.setText(getString(R.string.preview_call_to_action));
        } else {

        }
    }
    // ─── Views ────────────────────────────────────────────────────────────────

    private void initViews() {
        imageBeforeAfterSlider = findViewById(R.id.ibasPreview);
        btnSave = findViewById(R.id.btnSave);
        btnGenerate = findViewById(R.id.btnGenerate);
        View showCase = findViewById(R.id.showCase);
        View moreTool = findViewById(R.id.moreTool);
        btnContinue = showCase.findViewById(R.id.btn_continue);
        tooltipView = showCase.findViewById(R.id.showCase);
        rvResult = findViewById(R.id.rvResult);
        AppCompatImageView imgTick = findViewById(R.id.itemResultSaveChange);
        AppCompatImageView imgClose = findViewById(R.id.itemResultClose);
        LinearLayout llRecycleView = findViewById(R.id.llRecycelview);
        RelativeLayout layoutTickClose = findViewById(R.id.layoutTickClose);
        FrameLayout imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
        imgTick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageBeforeAfterSlider.setFlipBackVisibility(true);
                imageBeforeAfterSlider.setSeekSlider(false);

                llRecycleView.setVisibility(GONE);
                moreTool.setVisibility(VISIBLE);
                layoutTickClose.setVisibility(GONE);

            }
        });
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (tooltipView != null) tooltipView.setVisibility(GONE);
        if (btnContinue != null) btnContinue.setVisibility(GONE);
        if (imageBeforeAfterSlider != null) {
            imageBeforeAfterSlider.setOnClickViewListener(this);
        }

        moreTool.findViewById(R.id.cvEnhance).setOnClickListener(v -> {
            launchAIProcessing(Feature.ENHANCE.getValue());
        });

        moreTool.findViewById(R.id.cvCartoon).setOnClickListener(v -> {
            launchAIProcessing(Feature.RETOUCH.getValue());
        });

        moreTool.findViewById(R.id.cvRemoveObj).setOnClickListener(v -> {
            String imageToUse = (faceSelected != null && faceSelected.getUrlAfter() != null)
                    ? faceSelected.getUrlAfter()
                    : afterImageUrl;
            Intent intent = new Intent(this, RemoveObjActivity.class);
            intent.putExtra("image_uri", imageToUse);
            startActivity(intent);
            finish();
        });

        moreTool.findViewById(R.id.cvDescratch).setOnClickListener(v -> {
            launchAIProcessing(Feature.DESCRATCH.getValue());
        });

        moreTool.findViewById(R.id.cvColorize).setOnClickListener(v -> {
            launchAIProcessing(Feature.COLORIZE.getValue());
        });

        moreTool.findViewById(R.id.cvDehaze).setOnClickListener(v -> {
            launchAIProcessing(Feature.DESCRATCH.getValue());
        });

        moreTool.findViewById(R.id.cvBrighten).setOnClickListener(v -> {
            launchAIProcessing(Feature.BRIGHTEN.getValue());
        });

    }

    private void launchAIProcessing(String feature) {
        String imageToUse = (faceSelected != null && faceSelected.getUrlAfter() != null)
                ? faceSelected.getUrlAfter()
                : afterImageUrl;

        Intent intent = new Intent(this, AIAvatarProcessingActivity.class);
        intent.putExtra("image_before", imageToUse);
        intent.putExtra("feature", feature);
        startActivity(intent);
        finish();
    }
    // ─── RecyclerView ─────────────────────────────────────────────────────────

    private void setupRecyclerView() {
        resultItemAdapter = new ResultItemAdapter(this, resultItemList);

        if (rvResult != null) {
            rvResult.setLayoutManager(
                    new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            );
            rvResult.setAdapter(resultItemAdapter);
        }

        // Add initial result from intent
        if (!afterImageUrl.isEmpty()) {
            addResultItem(beforeImageUrl, afterImageUrl, null);
        }

        // Item click → switch slider preview
        if (rvResult != null) {
            rvResult.addOnItemTouchListener(new RecyclerItemClickListener(this,
                    (view, position) -> handleItemSelection(position)));
        }
    }

    // ─── Click listeners ──────────────────────────────────────────────────────

    private void setupClickListeners() {
        TextView btnVersion3 = findViewById(R.id.btnVersion3);
        if (btnVersion3 != null) {
            btnVersion3.setOnClickListener(v -> {
                if (!isSafeClick()) return;
                hideTooltip();
                startTripleUpscale();
            });
        }
        if (btnSave != null) {
            btnSave.setOnClickListener(v -> {
                if (!isSafeClick()) return;
                saveImageToGallery();
            });
        }

        if (btnGenerate != null) {
            btnGenerate.setOnClickListener(v -> {
                if (!isSafeClick()) return;
                hideTooltip();

                // Always enhance from the currently selected item's "after" URL,
                // so each generate pass builds on whichever result the user picked.
                String urlToEnhance = faceSelected != null
                        ? faceSelected.getUrlAfter()
                        : afterImageUrl;

                if (urlToEnhance == null || urlToEnhance.isEmpty()) {
                    Toast.makeText(this, "No image available to enhance", Toast.LENGTH_SHORT).show();
                    return;
                }

                // ── Disable button while processing so user can't double-tap ──
                btnGenerate.setEnabled(false);
                btnGenerate.setAlpha(0.5f);

                // Build the intent for the processing screen.
                // FLAG_ACTIVITY_SINGLE_TOP ensures that when AIAvatarProcessingActivity
                // finishes and calls startActivity(resultIntent back to us), Android
                // routes it through onNewIntent() rather than creating a second copy
                // of this activity — so all accumulated results are preserved.
                Intent intent = new Intent(this, com.skylock.ai_cartoon.activity.AIAvatarProcessingActivity.class);
                intent.putExtra("image_before", urlToEnhance);
                intent.putExtra("feature", featureSelected);
                intent.putExtra("total_upscale_passes", 0);
                intent.putExtra("upscale_remaining", 0);
                // Pass the original root before image so the result screen can
                // always show the real original on the left side of the slider.
                intent.putExtra("original_before_uri", beforeImageUrl);
                // Tell AIAvatarProcessingActivity to return the result back HERE
                // via FLAG_ACTIVITY_SINGLE_TOP + onNewIntent instead of finishing us.
                intent.putExtra("return_to_enhance_result", true);

                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_nothing);
                // ── Do NOT call finish() ──────────────────────────────────────
                // Keeping this activity alive is what allows onNewIntent() to
                // receive each successive result and append it to the RecyclerView.
            });
        }

        if (btnContinue != null) {
            btnContinue.setOnClickListener(v -> {
                //  if (!isSafeClick()) return;
                if (tooltipView != null) tooltipView.setVisibility(GONE);
                //gotoResultTool();
            });
        }

        if (tooltipView != null) {
            // Tap anywhere on tooltip to dismiss
            tooltipView.setOnClickListener(v -> hideTooltip());
        }
    }

    // ─── Tooltip ──────────────────────────────────────────────────────────────

    private void showTooltipDelayed() {
        uiHandler.postDelayed(() -> {
            if (!isFinishing() && !isDestroyed() && tooltipView != null) {
                tooltipView.setVisibility(VISIBLE);
                tooltipView.setAlpha(0f);
                tooltipView.animate().alpha(1f).setDuration(300).start();
            }
        }, TOOLTIP_DELAY_MS);
    }

    private void hideTooltip() {
        if (tooltipView != null) {
            tooltipView.animate().alpha(0f).setDuration(200).withEndAction(() ->
                    tooltipView.setVisibility(GONE)).start();
        }
    }

    // ─── Generate new result ──────────────────────────────────────────────────

    /**
     * Launches AIAvatarProcessingActivity to run another enhancement pass.
     * Uses the currently selected result's "after" URL as the new "before" input
     * — so each pass upscales the previous result.
     */

    private void startTripleUpscale() {
        // Use the currently selected item's after URL as the input so the user
        // can chain triple-upscale on top of a previous generate result.
        String urlToUpscale = faceSelected != null
                ? faceSelected.getUrlAfter()
                : afterImageUrl;

        if (urlToUpscale == null || urlToUpscale.isEmpty()) {
            Toast.makeText(this, "No image to upscale", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mark this activity as cache-only: all results stay in cache, never
        // written to gallery automatically. The user must tap btnSave explicitly.
        isCacheOnlySession = true;

        // Disable button while processing
        TextView btnVersion3 = findViewById(R.id.btnVersion3);
        if (btnVersion3 != null) {
            btnVersion3.setEnabled(false);
            btnVersion3.setAlpha(0.5f);
        }
        if (btnGenerate != null) {
            btnGenerate.setEnabled(false);
            btnGenerate.setAlpha(0.5f);
        }

        Intent intent = new Intent(this, AIAvatarProcessingActivity.class);
        intent.putExtra("image_before", urlToUpscale);
        intent.putExtra("feature", featureSelected);
        intent.putExtra("total_upscale_passes", 3);
        intent.putExtra("upscale_remaining", 3);
        intent.putExtra("original_before_uri", beforeImageUrl);
        // Signal: return the result as a local cache file path, not an HTTPS URL,
        // and route it back here via onNewIntent (singleTop).
        intent.putExtra("return_to_enhance_result", true);
        intent.putExtra("cache_only_result", true);

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_nothing);
        // ── Do NOT finish() ─────────────────────────────────────────────────
        // The activity must stay alive so onNewIntent receives the result
        // and appends it to the RecyclerView without losing existing items.
    }
    // ─── Item selection ───────────────────────────────────────────────────────

    private void handleItemSelection(int position) {
        if (position < 0 || position >= resultItemList.size()) return;

        // Deselect all
        for (ResultItem item : resultItemList) {
            item.setSelected(false);
        }

        // Select tapped item
        ResultItem selected = resultItemList.get(position);
        selected.setSelected(true);
        faceSelected = selected;

        resultItemAdapter.notifyDataSetChanged();

        // Update slider to show selected before/after
        if (imageBeforeAfterSlider != null) {
            imageBeforeAfterSlider.setImages(
                    selected.getUrlBefore(),
                    selected.getUrlAfter()
            );
        }

        // Show continue button when user selects a result
        if (btnContinue != null) {
            btnContinue.setVisibility(VISIBLE);
        }
    }

    // ─── Add new result to adapter ────────────────────────────────────────────

    /**
     * Called when a new enhancement result arrives (from intent or new API response).
     * Adds a new ResultItem to the list, selects it, and updates the slider.
     */
    private void addResultItem(String urlBefore, String urlAfter, SizeImage size) {
        // Deselect all existing
        for (ResultItem item : resultItemList) {
            item.setSelected(false);
        }

        String label = "Result " + (resultItemList.size() + 1);
        ResultItem newItem = new ResultItem(label, urlBefore, urlAfter, size, true);
        resultItemList.add(newItem);
        faceSelected = newItem;

        resultItemAdapter.notifyDataSetChanged();

        // Scroll to last item
        if (rvResult != null) {
            rvResult.post(() ->
                    rvResult.smoothScrollToPosition(resultItemList.size() - 1));
        }

        // Show continue button
        if (btnContinue != null) {
            btnContinue.setVisibility(VISIBLE);
        }

        // Update slider
        setupSliderWithItem(newItem);
    }

    // ─── Slider setup ─────────────────────────────────────────────────────────

    private void loadImageAndSetupSlider() {
        if (beforeImageUrl.isEmpty()) {
            setupSliderDirect();
            return;
        }

        Glide.with(this)
                .asBitmap()
                .load(beforeImageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource,
                                                Transition<? super Bitmap> transition) {
                        int imageWidth = resource.getWidth();
                        int imageHeight = resource.getHeight();
                        if (imageBeforeAfterSlider != null) {
                            imageBeforeAfterSlider.post(() ->
                                    computeAndApplyDimensions(imageWidth, imageHeight));
                        }
                    }

                    @Override
                    public void onLoadCleared(Drawable placeholder) {
                    }
                });
    }

    private void setupSliderDirect() {
        if (imageBeforeAfterSlider != null) {
            setupSlider(
                    getResources().getDisplayMetrics().widthPixels,
                    getResources().getDisplayMetrics().heightPixels
            );
        }
    }

    private void computeAndApplyDimensions(int imageWidth, int imageHeight) {
        if (imageBeforeAfterSlider == null) return;

        int containerW = imageBeforeAfterSlider.getMeasuredWidth();
        int containerH = imageBeforeAfterSlider.getMeasuredHeight();

        if (containerW == 0) containerW = getResources().getDisplayMetrics().widthPixels;
        if (containerH == 0) containerH = getResources().getDisplayMetrics().heightPixels;

        int viewW, viewH;
        if (imageWidth >= imageHeight) {
            viewW = containerW;
            viewH = (containerW * imageHeight) / Math.max(imageWidth, 1);
        } else {
            viewH = containerH;
            viewW = (containerH * imageWidth) / Math.max(imageHeight, 1);
            if (viewW > containerW) {
                viewW = containerW;
                viewH = (containerW * imageHeight) / Math.max(imageWidth, 1);
            }
        }

        displayWidth = viewW;
        displayHeight = viewH;

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(viewW, viewH);
        lp.gravity = android.view.Gravity.CENTER;
        imageBeforeAfterSlider.setLayoutParams(lp);

        setupSlider(viewW, viewH);
    }

    private void setupSlider(int width, int height) {
        if (imageBeforeAfterSlider == null) return;

        imageBeforeAfterSlider.setEnableChangePhoto(false);
        imageBeforeAfterSlider.setEnableFaceIcon(false, false);
        //     imageBeforeAfterSlider.setAutoSlideDuration(2000, true);
        imageBeforeAfterSlider.setLockVibrate(false);
        imageBeforeAfterSlider.setSeekSlider(true);
        imageBeforeAfterSlider.setFlipBackVisibility(false);
        imageBeforeAfterSlider.setSliderThumb(R.drawable.cycle_arrow);

        imageBeforeAfterSlider.setImagesResult(
                beforeImageUrl,
                afterImageUrl,
                null,
                null,
                featureSelected,
                width,
                height
        );
    }

    private void setupSliderWithItem(ResultItem item) {
        if (imageBeforeAfterSlider == null || item == null) return;
        imageBeforeAfterSlider.setImages(item.getUrlBefore(), item.getUrlAfter());
    }

    // ─── onNewIntent — receives result when coming back from processing ────────
    // This is triggered by AIAvatarProcessingActivity when it finishes and
    // "return_to_enhance_result" is true. Because ActivityEnhanceResult is
    // declared singleTop in AndroidManifest.xml, Android reuses the existing
    // instance and calls this method instead of re-running onCreate — so the
    // full resultItemList is still intact and the new item is simply appended.

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        String newAfterUrl = intent.getStringExtra("after_url");
        String newBeforeUrl = intent.getStringExtra("before_path");

        if (newAfterUrl != null && !newAfterUrl.isEmpty()) {
            afterImageUrl = newAfterUrl;
            String beforeForThisItem = (newBeforeUrl != null && !newBeforeUrl.isEmpty())
                    ? newBeforeUrl
                    : beforeImageUrl;

            // Register the result file in the session cache registry if it is a
            // local path (cache_only_result mode). This ensures it is cleaned up
            // in onDestroy and never accidentally left on the device.
            if (!newAfterUrl.startsWith("http")) {
                File resultFile = new File(newAfterUrl);
                if (resultFile.exists() && !sessionCacheFiles.contains(resultFile)) {
                    sessionCacheFiles.add(resultFile);
                }
            }

            addResultItem(beforeForThisItem, newAfterUrl, null);
        }

        // Re-enable all action buttons now that processing is complete
        if (btnGenerate != null) {
            btnGenerate.setEnabled(true);
            btnGenerate.setAlpha(1.0f);
        }
        TextView btnVersion3 = findViewById(R.id.btnVersion3);
        if (btnVersion3 != null) {
            btnVersion3.setEnabled(true);
            btnVersion3.setAlpha(1.0f);
        }
    }

    // ─── Continue / ResultTool ────────────────────────────────────────────────

    private void gotoResultTool() {
        if (faceSelected == null) return;

        Intent intent = new Intent(this, ActivityProcess.class);
        intent.putExtra("image_after", faceSelected.getUrlAfter());
        intent.putExtra("image_before", faceSelected.getUrlBefore());
        intent.putExtra("feature", featureSelected);

        if (faceSelected.getSize() != null) {
            Integer w = faceSelected.getSize().getWidth();
            Integer h = faceSelected.getSize().getHeight();
            intent.putExtra("image_width", w != null ? w : 0);
            intent.putExtra("image_height", h != null ? h : 0);
        }

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_nothing);
        finish();
    }

    // ─── Save ─────────────────────────────────────────────────────────────────

    private void saveImageToGallery() {
        String urlToSave = faceSelected != null ? faceSelected.getUrlAfter() : afterImageUrl;

        if (urlToSave == null || urlToSave.isEmpty()) {
            Toast.makeText(this, "No image to save", Toast.LENGTH_SHORT).show();
            return;
        }

        // If local file path
        if (!urlToSave.startsWith("http")) {
            File file = new File(urlToSave.replace("file://", ""));
            if (file.exists()) {
                Glide.with(this).asBitmap().load(file).into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource,
                                                Transition<? super Bitmap> transition) {
                        saveBitmapToGallery(resource);
                    }

                    @Override
                    public void onLoadCleared(Drawable placeholder) {
                    }
                });
                return;
            }
        }

        // URL — download via Glide then save
        Glide.with(this).asBitmap().load(urlToSave).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource,
                                        Transition<? super Bitmap> transition) {
                saveBitmapToGallery(resource);
            }

            @Override
            public void onLoadFailed(Drawable errorDrawable) {
                Toast.makeText(ActivityEnhanceResult.this,
                        "Failed to load image", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoadCleared(Drawable placeholder) {
            }
        });
    }

    private void saveBitmapToGallery(Bitmap bitmap) {
        if (bitmap == null) {
            Toast.makeText(this, "No image available to save", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String fileName = "Enhanced_" + System.currentTimeMillis() + ".jpg";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                values.put(MediaStore.Images.Media.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES + "/Enhanced");
                values.put(MediaStore.Images.Media.IS_PENDING, 1);

                Uri uri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                if (uri != null) {
                    try (OutputStream out = getContentResolver().openOutputStream(uri)) {
                        if (out != null) bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out);
                    }
                    values.clear();
                    values.put(MediaStore.Images.Media.IS_PENDING, 0);
                    getContentResolver().update(uri, values, null, null);
                    Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_SHORT).show();
                }
            } else {
                String path = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES).getAbsolutePath();
                File dir = new File(path + "/Enhanced");
                if (!dir.exists()) dir.mkdirs();
                File file = new File(dir, fileName);
                try (OutputStream out = new java.io.FileOutputStream(file)) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out);
                }
                // Notify gallery
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.fromFile(file)));
                Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Save failed: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    // ─── Click debounce ───────────────────────────────────────────────────────

    private boolean isSafeClick() {
        long now = SystemClock.elapsedRealtime();
        if (now - lastClickTime < CLICK_DEBOUNCE_MS) return false;
        lastClickTime = now;
        return true;
    }

    // ─── Lifecycle cleanup ────────────────────────────────────────────────────

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uiHandler.removeCallbacksAndMessages(null);
        // ── Clean up every cache file created during this session ─────────────
        // These are intermediate/final results that the user did NOT explicitly
        // save to the gallery. Deleting them here keeps the device cache lean.
        for (File f : sessionCacheFiles) {
            if (f != null && f.exists()) {
                boolean deleted = f.delete();
                Log.d("ActivityEnhanceResult",
                        "Cache cleanup: " + f.getName() + " deleted=" + deleted);
            }
        }
        sessionCacheFiles.clear();
    }

    // ─── ImageBeforeAfterSlider.ViewListener ──────────────────────────────────

    @Override
    public void onClickBeautifier() {
    }

    @Override
    public void onClickChangePhoto() {
    }

    // ─── RecyclerView item click helper ──────────────────────────────────────

    private static class RecyclerItemClickListener
            extends RecyclerView.SimpleOnItemTouchListener {

        private final OnItemClickListener listener;
        private final android.view.GestureDetector gestureDetector;

        RecyclerItemClickListener(android.content.Context context,
                                  OnItemClickListener listener) {
            this.listener = listener;
            gestureDetector = new android.view.GestureDetector(context,
                    new android.view.GestureDetector.SimpleOnGestureListener() {
                        @Override
                        public boolean onSingleTapUp(android.view.MotionEvent e) {
                            return true;
                        }
                    });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv,
                                             android.view.MotionEvent e) {
            View childView = rv.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && gestureDetector.onTouchEvent(e)) {
                listener.onItemClicked(childView, rv.getChildAdapterPosition(childView));
                return true;
            }
            return false;
        }

        interface OnItemClickListener {
            void onItemClicked(View view, int position);
        }
    }
}