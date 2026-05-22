package com.skylock.ai_cartoon.enhance;

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
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
    }

    // ─── Intent ───────────────────────────────────────────────────────────────

    private void readIntentExtras() {
        beforeImageUrl = getIntent().getStringExtra("before_path") != null
                ? getIntent().getStringExtra("before_path") : "";
        afterImageUrl = getIntent().getStringExtra("after_url") != null
                ? getIntent().getStringExtra("after_url") : "";
        featureSelected = getIntent().getStringExtra("feature") != null
                ? getIntent().getStringExtra("feature") : "enhance";
    }

    // ─── Views ────────────────────────────────────────────────────────────────

    private void initViews() {
        imageBeforeAfterSlider = findViewById(R.id.ibasPreview);
        btnSave = findViewById(R.id.btnSave);
        btnGenerate = findViewById(R.id.btnGenerate);
        View showCase = findViewById(R.id.showCase);
        btnContinue = showCase.findViewById(R.id.btn_continue);
        tooltipView = showCase.findViewById(R.id.showCase);
        rvResult = findViewById(R.id.rvResult);

        if (tooltipView != null) tooltipView.setVisibility(View.GONE);
        if (btnContinue != null) btnContinue.setVisibility(View.GONE);
        if (imageBeforeAfterSlider != null) {
            imageBeforeAfterSlider.setOnClickViewListener(this);
        }
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
        btnVersion3.setOnClickListener(v -> startTripleUpscale());
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
                String urlToEnhance = faceSelected != null ? faceSelected.getUrlAfter() : afterImageUrl;

                if (urlToEnhance == null || urlToEnhance.isEmpty()) {
                    Toast.makeText(this, "No image available to enhance", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Intent setup matching the expectations of AIAvatarProcessingActivity
                Intent intent = new Intent(this, com.skylock.ai_cartoon.activity.AIAvatarProcessingActivity.class);
                intent.putExtra("image_before", urlToEnhance);
                intent.putExtra("feature", featureSelected); // e.g., "enhance"

                // Explicitly set upscale passes to 0 for a standard single-pass operation
                intent.putExtra("total_upscale_passes", 0);
                intent.putExtra("upscale_remaining", 0);
                intent.putExtra("original_before_uri", beforeImageUrl); // Retain your absolute root image path

                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_nothing);

                // Finishes the current result screen so navigating back takes the user
                // out of the generation flow completely rather than accumulating screens.
                finish();
            });
        }

        if (btnContinue != null) {
            btnContinue.setOnClickListener(v -> {
                //  if (!isSafeClick()) return;
                if (tooltipView != null) tooltipView.setVisibility(View.GONE);
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
                tooltipView.setVisibility(View.VISIBLE);
                tooltipView.setAlpha(0f);
                tooltipView.animate().alpha(1f).setDuration(300).start();
            }
        }, TOOLTIP_DELAY_MS);
    }

    private void hideTooltip() {
        if (tooltipView != null) {
            tooltipView.animate().alpha(0f).setDuration(200).withEndAction(() ->
                    tooltipView.setVisibility(View.GONE)).start();
        }
    }

    // ─── Generate new result ──────────────────────────────────────────────────

    /**
     * Launches AIAvatarProcessingActivity to run another enhancement pass.
     * Uses the currently selected result's "after" URL as the new "before" input
     * — so each pass upscales the previous result.
     */

    private void startTripleUpscale() {
        if (beforeImageUrl == null || beforeImageUrl.isEmpty()) {
            Toast.makeText(this, "No image to upscale", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, AIAvatarProcessingActivity.class);
        intent.putExtra("image_before", beforeImageUrl);
        intent.putExtra("feature", "enhance");      // ← change to your feature key if different
        intent.putExtra("total_upscale_passes", 3);
        intent.putExtra("upscale_remaining", 3);
        intent.putExtra("original_before_uri", beforeImageUrl);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_nothing);
        finish();
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
            btnContinue.setVisibility(View.VISIBLE);
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
            btnContinue.setVisibility(View.VISIBLE);
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
        imageBeforeAfterSlider.setAutoSlideDuration(2000, true);
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        String newAfterUrl = intent.getStringExtra("after_url");
        String newBeforeUrl = intent.getStringExtra("before_path");

        if (newAfterUrl != null && !newAfterUrl.isEmpty()) {
            afterImageUrl = newAfterUrl;
            if (newBeforeUrl != null) beforeImageUrl = newBeforeUrl;
            addResultItem(beforeImageUrl, afterImageUrl, null);
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