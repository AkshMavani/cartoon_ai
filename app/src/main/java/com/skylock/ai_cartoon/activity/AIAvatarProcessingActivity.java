package com.skylock.ai_cartoon.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.skylock.ai_cartoon.R;
import com.skylock.ai_cartoon.base.BaseActivity;
import com.skylock.ai_cartoon.callback.ProcessingFinishListener;
import com.skylock.ai_cartoon.callback.ProcessingListener;
import com.skylock.ai_cartoon.databinding.ActivityAiavatarProcessingBinding;
import com.skylock.ai_cartoon.databinding.ActivityProcessingBinding;
import com.skylock.ai_cartoon.model.CartoonAI;
import com.skylock.ai_cartoon.model.ImageModel;
import com.skylock.ai_cartoon.model.ImageResponse;
import com.skylock.ai_cartoon.util.Constants;
import com.skylock.ai_cartoon.util.ErrorProcessingDialog;
import com.skylock.ai_cartoon.util.SharePreferenceRepositoryImpl;
import com.skylock.ai_cartoon.util.ThreadUtils;
import com.skylock.ai_cartoon.viewmodel.CartoonViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;


public final class AIAvatarProcessingActivity extends BaseActivity {

    private static final String TAG = "AIAvatarProcessingActivity";
    private static final long PROGRESS_INTERVAL_MS = 800L;
    private static final long RESUME_DELAY_MS = 1000L;
    private static final int PROGRESS_MAX = 100;
    private static final int PROGRESS_STEP = 1;
    private static final int PROGRESS_FAST_THRESHOLD = 70;
    private static final long DISMISS_FAST_INTERVAL = 50L;
    private static final long DISMISS_SLOW_INTERVAL = 150L;
    private static final int DISMISS_STEP = 5;

    private ActivityProcessingBinding binding;
    private CartoonViewModel viewModel;
     private Disposable disposable;
    private String feature;
    private String imageUri = "";
    private String style = "";
    private String gender = "other";

    private int progress;
    private boolean isProcessing;
    private boolean loadedResultImage;
    private boolean startedActivityResult;

    private final Logger logger = Logger.getLogger(AIAvatarProcessingActivity.class.getName());
    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private final Handler showAdHandler = new Handler(Looper.getMainLooper());
    private Runnable timerRunnable;
    private PowerManager.WakeLock wakeLock;



    @Override
    public void onBackPressed() {
        // blocked intentionally
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProcessingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(CartoonViewModel.class);

        // Read intent extras
        imageUri = getIntent().getStringExtra("image_before") != null
                ? getIntent().getStringExtra("image_before") : "";
        style = "christmas";
        gender = getIntent().getStringExtra("gender") != null
                ? getIntent().getStringExtra("gender") : "other";
        feature = getIntent().getStringExtra("feature");
        Log.e(TAG, "buildProcessingIntent: "+imageUri);
        // Acquire wake lock
        Glide.with(this).load(imageUri).into(binding.imgPreview);
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        if (powerManager != null) {
            wakeLock = powerManager.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK,
                    TAG + ":WakeLock"
            );
            wakeLock.acquire(10 * 60 * 1000L); // 10 minutes max
        }

        // Hide premium banner
        binding.cvGoPremium.setVisibility(View.GONE);

        // Close button

        // Observe error codes
       /* viewModel.getErrorCode().observe(this, errorCode -> {
            logger.info("errorCode: " + errorCode);
        //    if (errorCode == null || errorCode == 0) return;
            if (errorCode == 98) {
                Constants.showToast(this, getString(R.string.server_busy_try_again_10s));
            } else {
                Constants.showToast(this, getString(R.string.process_image_failed));
            }
            onFail();
        });*/

        // Observe image response
        // Observe image response in onCreate
        viewModel.getImageResponse().observe(this, imageResponse -> {
            if (imageResponse != null) {
                Log.e("response_iss", "onCreate: call " + imageResponse.getUrl());

                // 1. Create the intent for your result activity
                Intent intent = new Intent(this,ActivityProcess.class);

                // 2. Pass the core image data
                intent.putExtra("cartoonUrl", imageResponse.getUrl().toString());
                intent.putExtra("image_before", this.imageUri);
                intent.putExtra("isfromCartton", true);

                // 3. Pass dimensions from the response
                if (imageResponse.getSize() != null) {
                    Integer width = imageResponse.getSize().getWidth();
                    Integer height = imageResponse.getSize().getHeight();

                    // Check for null to avoid NullPointerException before calling intValue()
                    intent.putExtra("image_width", width != null ? width.intValue() : 0);
                    intent.putExtra("image_height", height != null ? height.intValue() : 0);
                }

                // 4. Pass style, gender, and feature from activity fields
                intent.putExtra("style", this.style); // Or getIntent().getStringExtra("style")
                intent.putExtra("gender", this.gender);
                intent.putExtra("feature", this.feature);

                // 5. Cleanup and Start Activity
                clearMultiPhotos(); // Helper to reset the selection state
                this.startedActivityResult = true;

                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_nothing);
                finish();

                loadedResultImage = true;
            }
        });
    }

    private void clearMultiPhotos() {
        Iterator<ImageModel> it = Constants.MULTI_PHOTOS_NEW.iterator();
        while (it.hasNext()) {
            it.next().setSelectNumber(0);
        }
        Constants.MULTI_PHOTOS_NEW.clear();
    }
    public void onDismissLoading(final ProcessingFinishListener finishListener) {
      //  if (disposable != null) return;

        if (timerHandler != null && timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }

        int currentProgress = this.progress;
        long interval = currentProgress < PROGRESS_FAST_THRESHOLD
                ? DISMISS_FAST_INTERVAL : DISMISS_SLOW_INTERVAL;

        if (currentProgress != 0 && currentProgress < PROGRESS_MAX) {
            disposable = Observable.interval(interval, interval, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(tick -> {
                        if (progress < PROGRESS_MAX) {
                            progress = Math.min(progress + DISMISS_STEP, PROGRESS_MAX);
                            updateProgressUI();
                        } else {
                            cancel();
                            if (finishListener != null) finishListener.onFinish();
                        }
                    }, throwable -> {
                        Log.e(TAG, "Interval error", throwable);
                        if (finishListener != null) finishListener.onFinish();
                    });
        } else if (finishListener != null) {
            finishListener.onFinish();
        }
    }

    private void updateProgressUI() {
        ThreadUtils.runOnMainThread(() -> {
            binding.progressBar.setProgress(progress);
            binding.tvLoading.setText(getString(R.string.label_ai_generating) + " " + progress + "%");
            binding.lottie.setProgress(progress);
        });
    }

    private void autoProcess() {
        isProcessing = true;

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                progress = progress + PROGRESS_STEP;
                if (progress <= 98) {
                    binding.progressBar.setProgress(progress);
                    binding.tvLoading.setText(
                            getString(R.string.label_ai_generating) + " " + progress + "%"
                    );
                    binding.lottie.setProgress(progress);
                }
                timerHandler.postDelayed(this, PROGRESS_INTERVAL_MS);
            }
        };

        timerHandler.postDelayed(timerRunnable, 0L);

   //     if (viewModel != null && cartoonAISelected != null) {
            viewModel.onCartoon(imageUri, null, 1.0, gender,getIntent().getStringExtra("style"));
     //   }
    }

    private void startResultActivity(ImageResponse imageResponse) {
        Log.e("getResonseiss", "startResultActivity: "+imageResponse );
    //    if (startedActivityResult) return;

        Iterator<ImageModel> it = Constants.MULTI_PHOTOS_NEW.iterator();
        while (it.hasNext()) {
            it.next().setSelectNumber(0);
        }
        Constants.MULTI_PHOTOS_NEW.clear();

        startedActivityResult = true;

      /*  Intent intent = new Intent(this, AIAvatarResultActivity.class);
        intent.putExtra("image_after", imageResponse.getUrl());
        intent.putExtra("image_before", imageUri);

        if (imageResponse.getSize() != null) {
            if (imageResponse.getSize().getWidth() != null) {
                intent.putExtra("image_width", imageResponse.getSize().getWidth().intValue());
            }
            if (imageResponse.getSize().getHeight() != null) {
                intent.putExtra("image_height", imageResponse.getSize().getHeight().intValue());
            }
        }

        if (cartoonAISelected != null) {
            intent.putExtra("style", cartoonAISelected.getName());
        }
        intent.putExtra("gender", gender);
        intent.putExtra("feature", getIntent().getStringExtra("feature"));

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_nothing);
        finish();*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.cvGoPremium.setVisibility(View.GONE);

        if (!isProcessing) {
            new Handler(Looper.getMainLooper()).postDelayed(this::autoProcess, RESUME_DELAY_MS);
        }
    }

    public void onFail() {
        isProcessing = false;
        ErrorProcessingDialog.display(getSupportFragmentManager(), new ProcessingListener() {
            @Override
            public void onRetry() {
                progress = 0;
                autoProcess();
            }

            @Override
            public void onCancel() {
                finish();
            }
        });
    }

    private void cancel() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        disposable = null;
    }

    @Override
    public void finish() {
        if (viewModel != null) {
            viewModel.setActivityFinished();
        }
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timerRunnable != null) {
            showAdHandler.removeCallbacks(timerRunnable);
            timerHandler.removeCallbacks(timerRunnable);
        }
        timerHandler.removeCallbacksAndMessages(null);
        showAdHandler.removeCallbacksAndMessages(null);
        cancel();

        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    @NonNull
    @Override
    public ViewBinding inflateBinding(@NotNull LayoutInflater inflater) {
        return ActivityAiavatarProcessingBinding.inflate(getLayoutInflater());
    }
}