package com.skylock.ai_cartoon.activity;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.skylock.ai_cartoon.R;
import com.skylock.ai_cartoon.base.BaseActivity;
import com.skylock.ai_cartoon.databinding.ActivityCropImageBinding;
import com.skylock.ai_cartoon.model.Gender;
import com.skylock.ai_cartoon.util.BottomSheetGender;
import com.skylock.ai_cartoon.util.Constants;
import com.skylock.ai_cartoon.util.EmptyUtils;
import com.skylock.ai_cartoon.util.Feature;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;

public class CropImageActivity extends BaseActivity {

    private static final String TAG = "CropImageActivity";

    // View Binding
    private ActivityCropImageBinding binding;

    // State
    private Dialog dialogChooseGender;
    private Gender gender = Gender.OTHER;
    private int imageWidth;
    private int imageHeight;

    // Lazy-equivalent: read from intent only when needed

    // ------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCropImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        showGenderBottomSheet();


        // Ad banner height
        binding.bottomCrop.tvLoadingAd.getLayoutParams().height =
                Constants.dpToPixel(Constants.IS_TABLET ? 90 : 60);
        binding.bottomCrop.tvLoadingAd.requestLayout();

        // Read intent extras
        final String imageUri = getIntent().getStringExtra("image_uri");
        final String feature  = getIntent().getStringExtra("feature");
        final int positionImg = getIntent().getIntExtra("position_img", 0);

        Log.d(TAG, "onCreate positionImg: " + positionImg);
        Log.d(TAG, "onCreate imageBefore: " + imageUri);

        // Load image into crop view
        if (EmptyUtils.isNotEmpty(imageUri)) {
            String filePath = imageUri.replace("file:///", "");
            binding.imageCropView.setImageFilePath(filePath);
        }

        // Default aspect ratio 3:4
        binding.imageCropView.setAspectRatio(3, 4);
        updateStatusViewSelect(binding.bottomCrop.ratio34btn);

        // Image dimensions from intent (used later when launching processing activity)
        imageWidth  = getIntent().getIntExtra("image_width", 0);
        imageHeight = getIntent().getIntExtra("image_height", 0);

        // Aspect-ratio buttons
        binding.bottomCrop.ratio11btn.setOnClickListener(v -> crop(v, 1, 1));
        binding.bottomCrop.ratio34btn.setOnClickListener(v -> crop(v, 3, 4));
        binding.bottomCrop.ratio43btn.setOnClickListener(v -> crop(v, 4, 3));
        binding.bottomCrop.ratio169btn.setOnClickListener(v -> crop(v, 16, 9));
        binding.bottomCrop.ratio916btn.setOnClickListener(v -> crop(v, 9, 16));
        binding.bottomCrop.ratio45btn.setOnClickListener(v -> crop(v, 4, 5));
        binding.bottomCrop.ratio54btn.setOnClickListener(v -> crop(v, 5, 4));


        // AI Hugging flow vs. normal generate flow
        if (Feature.AI_HUGGING.getValue().equals(feature)) {
            setupAiHuggingMode(positionImg);
        } else {
            binding.bottomCrop.btnGenerate.setOnClickListener(v -> onGenerateClick(feature));
        }

        // Back button
        binding.imgBack.setOnClickListener(v -> finish());

        // Gender button visibility
       /* if (Constants.HAIR_STYLE.equals(feature) || Constants.AI_HUGGING.equals(feature)) {
            binding.btnGender.setVisibility(View.GONE);
        } else {
            initChooseGenderView();
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();

            binding.bottomCrop.viewPremium.setVisibility(View.VISIBLE);
            binding.bottomCrop.tvWatchAd.setText(getString(R.string.watch_ads));

    }

    @Override
    protected void onDestroy() {
        binding.loading.setVisibility(View.GONE);
        super.onDestroy();
    }

    // ------------------------------------------------------------------
    // Generate (normal feature flow)
    // ------------------------------------------------------------------

    private void onGenerateClick(String feature) {
        // Premium gate



        binding.loading.setVisibility(View.VISIBLE);
        new Thread(() -> cropAndLaunchProcessing(feature)).start();
    }

    private void cropAndLaunchProcessing(String feature) {
        if (binding.imageCropView.isChangingScale()) return;

        Bitmap croppedBitmap = binding.imageCropView.getCroppedImage();
        if (croppedBitmap == null) return;

        File croppedFile = bitmapConvertToFile(croppedBitmap);
        if (!croppedFile.exists()) return;

        runOnUiThread(() -> {
            Intent intent = buildProcessingIntent(croppedFile, getIntent().getStringExtra("style"), feature);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_nothing);
            finish();
            new Handler().postDelayed(this::finish, 500);
        });
    }

    // ------------------------------------------------------------------
    // AI Hugging flow
    // ------------------------------------------------------------------

    private void setupAiHuggingMode(int positionImg) {
        binding.bottomCrop.btnGenerate.setVisibility(View.GONE);
        binding.bottomCrop.btnDone.setVisibility(View.VISIBLE);
        binding.bottomCrop.btnDone.setOnClickListener(v -> onAiHuggingDoneClick(positionImg));
    }

    private void onAiHuggingDoneClick(int positionImg) {
        binding.loading.setVisibility(View.VISIBLE);
        new Thread(() -> cropAndSaveForHugging(positionImg)).start();
    }

    private void cropAndSaveForHugging(int positionImg) {
        if (binding.imageCropView.isChangingScale()) return;

        Bitmap croppedBitmap = binding.imageCropView.getCroppedImage();
        if (croppedBitmap == null) return;

        File tempFile = bitmapConvertToFile(croppedBitmap);
        if (!tempFile.exists()) return;

        String targetName = (positionImg == 1) ? "ai_hugging_1.jpg" : "ai_hugging_2.jpg";
        File targetFile = new File(tempFile.getParent(), targetName);

        if (targetFile.exists()) {
            targetFile.delete();
        }

        if (tempFile.renameTo(targetFile)) {
            runOnUiThread(() -> {
              /*  if (positionImg == 1) {
                    AIHuggingData.INSTANCE.setUrlImage1(targetFile.getAbsolutePath());
                } else {
                    AIHuggingData.INSTANCE.setUrlImage2(targetFile.getAbsolutePath());
                }
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_nothing);
                new Handler().postDelayed(() -> {
                    RxBus.getDefault().postSticky(new CloseScreenEvent(false, 1, null));
                    finish();
                }, 500);*/
            });
        } else {
            runOnUiThread(() -> {
                binding.loading.setVisibility(View.GONE);
                Toast.makeText(this, "Failed to save the file.", Toast.LENGTH_SHORT).show();
            });
        }
    }

    // ------------------------------------------------------------------
    // Crop helpers
    // ------------------------------------------------------------------

    private void crop(View clickedView, int widthRatio, int heightRatio) {
        // Block while loading
        if (binding.loading.getVisibility() != View.GONE) return;

        updateStatusViewSelect(clickedView);

        if (isPossibleCrop(widthRatio, heightRatio)) {
            binding.imageCropView.setAspectRatio(widthRatio, heightRatio);
        } else {
            Toast.makeText(this, "Can not crop", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isPossibleCrop(int widthRatio, int heightRatio) {
        Bitmap viewBitmap = binding.imageCropView.getViewBitmap();
        if (viewBitmap == null) return false;
        return viewBitmap.getWidth() >= widthRatio || viewBitmap.getHeight() >= heightRatio;
    }

    private void updateStatusViewSelect(View selectedView) {
        setRatioBtnBackground(binding.bottomCrop.ratio11btn,  selectedView);
        setRatioBtnBackground(binding.bottomCrop.ratio34btn,  selectedView);
        setRatioBtnBackground(binding.bottomCrop.ratio43btn,  selectedView);
        setRatioBtnBackground(binding.bottomCrop.ratio169btn, selectedView);
        setRatioBtnBackground(binding.bottomCrop.ratio916btn, selectedView);
        setRatioBtnBackground(binding.bottomCrop.ratio45btn,  selectedView);
        setRatioBtnBackground(binding.bottomCrop.ratio54btn,  selectedView);
    }

    private void setRatioBtnBackground(View btn, View selectedView) {
        btn.setBackgroundResource(
                btn == selectedView
                        ? R.drawable.bg_border_crop_selected
                        : R.drawable.bg_border_crop
        );
    }

    // ------------------------------------------------------------------
    // Gender chooser
    // ------------------------------------------------------------------

  /*  private void initChooseGenderView() {
        dialogChooseGender = new BottomSheetDialog(this, R.style.SheetDialog);
        BottomSheetChooseGenderBinding sheetBinding = BottomSheetChooseGenderBinding.inflate(getLayoutInflater());

        dialogChooseGender.setContentView(sheetBinding.getRoot());

        Window window = dialogChooseGender.getWindow();
        if (window != null) {
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        sheetBinding.btnMale.setOnClickListener(v -> onChooseGender(Gender.MALE));
        sheetBinding.btnFemale.setOnClickListener(v -> onChooseGender(Gender.FEMALE));
        sheetBinding.btnOther.setOnClickListener(v -> onChooseGender(Gender.OTHER));

        dialogChooseGender.setCancelable(false);
        dialogChooseGender.setCanceledOnTouchOutside(false);
        dialogChooseGender.setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                dialog.dismiss();
                return true;
            }
            return false;
        });

        // Restore previously saved gender
        Gender savedGender = SharePreferenceRepositoryImpl.getSharedPreferences().getGender();
        if (savedGender != null) {
            onChooseGender(savedGender);
        }

        // Open dialog on gender button click
        binding.btnGender.setOnClickListener(v -> dialogChooseGender.show());

        // Premium banner in bottom crop bar
        binding.bottomCrop.viewPremium.setOnClickListener(v -> goPremium("crop"));
    }*/

   /* private void onChooseGender(Gender chosenGender) {
        this.gender = chosenGender;
        SharePreferenceRepositoryImpl.getSharedPreferences().setGender(chosenGender);

        switch (chosenGender) {
            case MALE:
                binding.imgGender.setImageDrawable(getDrawable(R.drawable.ic_male));
                binding.btnGender.setBackgroundResource(R.drawable.bg_gender_male);
                binding.tvGender.setText(getString(R.string.male));
                break;
            case FEMALE:
                binding.imgGender.setImageDrawable(getDrawable(R.drawable.ic_female));
                binding.btnGender.setBackgroundResource(R.drawable.bg_gender_female);
                binding.tvGender.setText(getString(R.string.female));
                break;
            default: // OTHER
                binding.imgGender.setImageDrawable(getDrawable(R.drawable.ic_gender_other));
                binding.btnGender.setBackgroundResource(R.drawable.bg_gender_other);
                binding.tvGender.setText(getString(R.string.other));
                break;
        }

        if (dialogChooseGender != null && dialogChooseGender.isShowing()) {
            dialogChooseGender.dismiss();
        }
    }*/

    // ------------------------------------------------------------------
    // Intent builder for AIAvatarProcessingActivity
    // ------------------------------------------------------------------

    private Intent buildProcessingIntent(File file, String style, String feature) {
        Intent intent = new Intent(this, AIAvatarProcessingActivity.class);
        Log.e(TAG, "buildProcessingIntent: "+file.getAbsolutePath() );
        intent.putExtra("image_before", file.getAbsolutePath());
        intent.putExtra("image_width",   imageWidth);
        intent.putExtra("image_height",  imageHeight);
        intent.putExtra("style",         style);
        intent.putExtra("gender",        gender);
        intent.putExtra("feature", feature);
        return intent;
    }

    // ------------------------------------------------------------------
    // Bitmap → File
    // ------------------------------------------------------------------

    /**
     * Saves the given bitmap to a temporary JPEG file in the cache directory.
     * Also updates {@link #imageWidth} and {@link #imageHeight} from the bitmap dimensions.
     *
     * @return the saved {@link File}, or an empty-path File on failure.
     */
    private File bitmapConvertToFile(Bitmap bitmap) {
        clearLibCacheFiles();

        File outputFile = new File(
                getCacheDir(),
                "lib_" + System.currentTimeMillis() + ".jpg"
        );

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();

            imageHeight = bitmap.getHeight();
            imageWidth  = bitmap.getWidth();

            // Notify media scanner
            MediaScannerConnection.scanFile(
                    this,
                    new String[]{outputFile.getAbsolutePath()},
                    null,
                    (path, uri) -> { /* no-op */ }
            );

        } catch (Exception e) {
            e.printStackTrace();
            outputFile = new File(""); // signal failure with empty path
        } finally {
            if (fos != null) {
                try { fos.close(); } catch (Exception e) { e.printStackTrace(); }
            }
        }

        return outputFile;
    }

    /**
     * Deletes any previously cached lib_ files to free space before saving a new one.
     */
    public void clearLibCacheFiles() {
        File cacheDir = getCacheDir();
        String[] files = cacheDir.list();
        if (files == null) return;

        for (String name : files) {
            if (name.contains("lib_")) {
                deleteDir(new File(cacheDir, name));
            }
        }
    }
    public static boolean deleteDir(File file) {
        if (file != null && file.isDirectory()) {
            for (String str : file.list()) {
                if (!deleteDir(new File(file, str))) {
                    return false;
                }
            }
        }
        return file.delete();
    }
    @NonNull
    @Override
    public ViewBinding inflateBinding(@NotNull LayoutInflater inflater) {
        return ActivityCropImageBinding.inflate(getLayoutInflater());
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    public void showGenderBottomSheet() {
        BottomSheetGender bottomSheet = new BottomSheetGender();

        // Set the listener to capture the selection
        bottomSheet.setOnGenderClickListener(selectedGender -> {
            this.gender = selectedGender;
        });

        bottomSheet.show(getSupportFragmentManager(), "BottomSheetGender");
    }


}