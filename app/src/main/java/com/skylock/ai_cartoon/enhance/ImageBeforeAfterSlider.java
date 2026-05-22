package com.skylock.ai_cartoon.enhance;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.skylock.ai_cartoon.R;
import com.skylock.ai_cartoon.util.Constants;

import java.util.HashMap;
import java.util.logging.Logger;

public class ImageBeforeAfterSlider extends CardView {
    public static int ImageBeforeAfterSlider_scaleSlideType = 0x00000001;
    public static int ImageBeforeAfterSlider_image_before_disable_zoom = 0x00000000;
    public static int[] ImageBeforeAfterSlider = {R.attr.image_before_disable_zoom, R.attr.scaleSlideType};
    private final Logger logger;
    private final HashMap<String, Bitmap> map;
    private final int progress;
    private final Boolean swipeSlider;
    private Integer autoSlideDuration;
    private ImageView btnBeautifier;
    private TextView btnChangePhoto;
    private Context context;
    private FrameLayout flBefore;
    private FrameLayout flSlider;
    private FrameLayout flSliderBottom;
    private FrameLayout flSliderTop;
    private int height;
    private ZoomImageView imgAfter;
    private ZoomImageView imgBefore;
    private ImageView imgBg;
    private ImageView imgFlipback;
    private boolean initView;
    private boolean isDisableZoom;
    private boolean isFlipBackActive;
    private boolean isInitMatrix;
    private boolean isUpdateAffter;
    private boolean isUpdateBefore;
    private LinearLayout llTitle;
    private boolean lockVibrate;
    private OnClickListener onClickListener;
    private RelativeLayout rlAfter;
    private RelativeLayout rlBefore;
    private CardView rootView;
    private ImageView.ScaleType scaleType;
    private SeekBarTouch seekSlider;
    private TextView tvAfter;
    private TextView tvBefore;
    private TextView tvDesc;
    private TextView tvTitle;
    private ViewListener viewListener;
    private int width;

    public ImageBeforeAfterSlider(Context context) {
        super(context);
        this.lockVibrate = false;
        this.progress = 400;
        this.width = 0;
        this.height = 0;
        this.logger = Logger.getLogger(ImageBeforeAfterSlider.class.getName());
        this.swipeSlider = false;
        this.autoSlideDuration = 0;
        this.initView = false;
        this.isDisableZoom = true;
        this.isUpdateBefore = false;
        this.isUpdateAffter = false;
        this.map = new HashMap<>();
        init(context, null);
    }

    public ImageBeforeAfterSlider(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.lockVibrate = false;
        this.progress = 400;
        this.width = 0;
        this.height = 0;
        this.logger = Logger.getLogger(ImageBeforeAfterSlider.class.getName());
        this.swipeSlider = false;
        this.autoSlideDuration = 0;
        this.initView = false;
        this.isDisableZoom = true;
        this.isUpdateBefore = false;
        this.isUpdateAffter = false;
        this.map = new HashMap<>();
        init(context, attributeSet);
    }

    public ImageBeforeAfterSlider(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.lockVibrate = false;
        this.progress = 400;
        this.width = 0;
        this.height = 0;
        this.logger = Logger.getLogger(ImageBeforeAfterSlider.class.getName());
        this.swipeSlider = false;
        this.autoSlideDuration = 0;
        this.initView = false;
        this.isDisableZoom = true;
        this.isUpdateBefore = false;
        this.isUpdateAffter = false;
        this.map = new HashMap<>();
        init(context, attributeSet);
    }

    public void init(Context context, AttributeSet attributeSet) {
        this.scaleType = ImageView.ScaleType.MATRIX;
        this.isFlipBackActive = true;
        @SuppressLint("ResourceType") TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, ImageBeforeAfterSlider);
        try {
            int i = obtainStyledAttributes.getInt(ImageBeforeAfterSlider_scaleSlideType, 0);
            if (obtainStyledAttributes.hasValue(ImageBeforeAfterSlider_image_before_disable_zoom)) {
                this.isDisableZoom = obtainStyledAttributes.getBoolean(ImageBeforeAfterSlider_image_before_disable_zoom, true);
            }
            if (i == 0) {
                this.scaleType = ImageView.ScaleType.MATRIX;
            } else if (i == 3) {
                this.scaleType = ImageView.ScaleType.CENTER;
            } else if (i == 6) {
                this.scaleType = ImageView.ScaleType.CENTER_CROP;
            }
            obtainStyledAttributes.recycle();
            this.context = context;
            View.inflate(context, R.layout.layout_image_before_after_silder, this);
            this.rootView = findViewById(R.id.root);
            this.seekSlider = findViewById(R.id.seekSlider);
            this.rlAfter = findViewById(R.id.imgAfter);
            this.rlBefore = findViewById(R.id.imgBefore);
            initImageZoom();
            this.imgBg = findViewById(R.id.imgBg);
            this.flSlider = findViewById(R.id.flSlider);
            this.flSliderTop = findViewById(R.id.flSliderTop);
            this.flSliderBottom = findViewById(R.id.flSliderBottom);
            this.flBefore = findViewById(R.id.flBefore);
            this.tvTitle = findViewById(R.id.tvTitle);
            this.tvDesc = findViewById(R.id.tvDesc);
            this.llTitle = findViewById(R.id.llTitle);
            this.imgFlipback = findViewById(R.id.imgFlipback);
            this.tvBefore = findViewById(R.id.tvBefore);
            this.tvAfter = findViewById(R.id.tvAfter);
            this.btnChangePhoto = findViewById(R.id.btnChangePhoto);
            this.btnBeautifier = findViewById(R.id.btnBeautifier);
            setCardBackgroundColor(16777215);
            this.btnChangePhoto.setOnClickListener(new OnClickListener() { // from class: mobi.zeezoo.photoenhancer.feature.view.ImageBeforeAfterSlider$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    ImageBeforeAfterSlider.this.lambda$init$0(view);
                }
            });
            this.btnBeautifier.setOnClickListener(new OnClickListener() { // from class: mobi.zeezoo.photoenhancer.feature.view.ImageBeforeAfterSlider$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    ImageBeforeAfterSlider.this.lambda$init$1(view);
                }
            });
            this.imgAfter.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: mobi.zeezoo.photoenhancer.feature.view.ImageBeforeAfterSlider.1
                @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
                public void onGlobalLayout() {
                    if (ImageBeforeAfterSlider.this.initView) {
                        return;
                    }
                    ImageBeforeAfterSlider.this.initView = true;
                    ImageBeforeAfterSlider.this.logger.info("width Image Compare: " + ImageBeforeAfterSlider.this.imgAfter.getMeasuredWidth());
                    ImageBeforeAfterSlider imageBeforeAfterSlider = ImageBeforeAfterSlider.this;
                    imageBeforeAfterSlider.width = imageBeforeAfterSlider.imgAfter.getMeasuredWidth();
                    ImageBeforeAfterSlider imageBeforeAfterSlider2 = ImageBeforeAfterSlider.this;
                    imageBeforeAfterSlider2.height = imageBeforeAfterSlider2.imgAfter.getMeasuredHeight();
                    ImageBeforeAfterSlider imageBeforeAfterSlider3 = ImageBeforeAfterSlider.this;
                    imageBeforeAfterSlider3.reSizeView(Integer.valueOf(imageBeforeAfterSlider3.width), Integer.valueOf(ImageBeforeAfterSlider.this.height));
                    ImageBeforeAfterSlider.this.activeAnimation();
                }
            });
            this.seekSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: mobi.zeezoo.photoenhancer.feature.view.ImageBeforeAfterSlider.2
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar) {
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onProgressChanged(SeekBar seekBar, int i2, boolean z) {
                    if (ImageBeforeAfterSlider.this.seekSlider.getVisibility() != View.VISIBLE) {
                        return;
                    }
                    ViewGroup.LayoutParams layoutParams = ImageBeforeAfterSlider.this.flBefore.getLayoutParams();
                    layoutParams.width = i2;
                    layoutParams.height = ImageBeforeAfterSlider.this.height;
                    ImageBeforeAfterSlider.this.flBefore.setLayoutParams(layoutParams);
                }
            });
            this.imgFlipback.setOnTouchListener(new OnTouchListener() { // from class: mobi.zeezoo.photoenhancer.feature.view.ImageBeforeAfterSlider.3
                @Override // android.view.View.OnTouchListener
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    ImageBeforeAfterSlider.this.logger.info("MotionEvent:  " + motionEvent.getAction());
                    if (motionEvent.getAction() == 1) {
                        if (ImageBeforeAfterSlider.this.isFlipBackActive) {
                            ImageBeforeAfterSlider.this.flBefore.setVisibility(View.VISIBLE);
                            ImageBeforeAfterSlider.this.seekSlider.setVisibility(View.VISIBLE);
                            ImageBeforeAfterSlider.this.flSlider.setVisibility(View.VISIBLE);
                            ImageBeforeAfterSlider.this.tvAfter.setVisibility(View.VISIBLE);
                        } else {
                            ImageBeforeAfterSlider.this.flBefore.setVisibility(View.GONE);
                            ImageBeforeAfterSlider.this.rlAfter.setVisibility(View.VISIBLE);
                        }
                    } else if (motionEvent.getAction() == 0) {
                        if (ImageBeforeAfterSlider.this.isFlipBackActive) {
                            ImageBeforeAfterSlider.this.flBefore.setVisibility(View.GONE);
                            ImageBeforeAfterSlider.this.seekSlider.setVisibility(View.GONE);
                            ImageBeforeAfterSlider.this.flSlider.setVisibility(View.GONE);
                            ImageBeforeAfterSlider.this.tvAfter.setVisibility(View.GONE);
                        } else {
                            ImageBeforeAfterSlider.this.flBefore.setVisibility(View.VISIBLE);
                            ImageBeforeAfterSlider.this.rlAfter.setVisibility(View.GONE);
                        }
                    }
                    return true;
                }
            });
        } catch (Throwable th) {
            obtainStyledAttributes.recycle();
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$init$0(View view) {
        ViewListener viewListener = this.viewListener;
        if (viewListener != null) {
            viewListener.onClickChangePhoto();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$init$1(View view) {
        ViewListener viewListener = this.viewListener;
        if (viewListener != null) {
            viewListener.onClickBeautifier();
        }
    }

    public void setFlipBackVisibility(boolean z) {
        this.imgFlipback.setVisibility(z ? View.VISIBLE : View.GONE);
    }

    private void initImageZoom() {
        RelativeLayout.LayoutParams layoutParams;
        ViewGroup viewGroup;
        ViewGroup viewGroup2;
        this.isUpdateBefore = false;
        this.isUpdateAffter = false;
        ZoomImageView zoomImageView = this.imgAfter;
        if (zoomImageView != null && (viewGroup2 = (ViewGroup) zoomImageView.getParent()) != null) {
            viewGroup2.removeView(this.imgAfter);
        }
        ZoomImageView zoomImageView2 = this.imgBefore;
        if (zoomImageView2 != null && (viewGroup = (ViewGroup) zoomImageView2.getParent()) != null) {
            viewGroup.removeView(this.imgBefore);
        }
        this.rlBefore.removeAllViews();
        this.rlAfter.removeAllViews();
        this.imgBefore = null;
        this.imgAfter = null;
        if (this.width != 0 && this.height != 0 && this.scaleType == ImageView.ScaleType.MATRIX) {
            System.out.println("initImageZoom" + this.width + this.height);
            layoutParams = new RelativeLayout.LayoutParams(this.width, this.height);
        } else {
            layoutParams = new RelativeLayout.LayoutParams(-1, -1);
        }
        ZoomImageView zoomImageView3 = new ZoomImageView(getContext());
        this.imgBefore = zoomImageView3;
        zoomImageView3.setLayoutParams(layoutParams);
        this.imgBefore.disableZoom(this.isDisableZoom);
        this.imgBefore.setScaleType(this.scaleType);
        this.imgBefore.setOnUpdateMatrixImage(new ZoomImageView.OnUpdateMatrixImage() { // from class: mobi.zeezoo.photoenhancer.feature.view.ImageBeforeAfterSlider.4
            @Override // mobi.zeezoo.photoenhancer.feature.view.ZoomImageView.OnUpdateMatrixImage
            public void updateMatrix(Matrix matrix, boolean z) {
                if (ImageBeforeAfterSlider.this.scaleType != ImageView.ScaleType.MATRIX) {
                    return;
                }
                if (ImageBeforeAfterSlider.this.isUpdateAffter && ImageBeforeAfterSlider.this.isUpdateBefore) {
                    if (matrix != null && z) {
                        ImageBeforeAfterSlider.this.imgAfter.setMatrixUpdate(matrix, ImageBeforeAfterSlider.this.imgBefore.getZoomMatrix(), ImageBeforeAfterSlider.this.imgBefore.getMatrixValues(), ImageBeforeAfterSlider.this.imgBefore.getOldScale());
                    }
                    ImageBeforeAfterSlider.this.seekSlider.isDisableTouch = z;
                }
                if (ImageBeforeAfterSlider.this.isDisableZoom) {
                    ImageBeforeAfterSlider.this.seekSlider.isDisableTouch = false;
                }
            }

            @Override // mobi.zeezoo.photoenhancer.feature.view.ZoomImageView.OnUpdateMatrixImage
            public void setZoom(float f, float f2, float f3) {
                if (ImageBeforeAfterSlider.this.scaleType != ImageView.ScaleType.MATRIX) {
                    return;
                }
                ImageBeforeAfterSlider.this.imgAfter.setZoomImageNew(f, f2, f3);
            }
        });
        this.rlBefore.addView(this.imgBefore);
        ZoomImageView zoomImageView4 = new ZoomImageView(getContext());
        this.imgAfter = zoomImageView4;
        zoomImageView4.setLayoutParams(layoutParams);
        this.imgAfter.setScaleType(this.scaleType);
        this.imgAfter.disableZoom(this.isDisableZoom);
        this.imgAfter.setOnUpdateMatrixImage(new ZoomImageView.OnUpdateMatrixImage() { // from class: mobi.zeezoo.photoenhancer.feature.view.ImageBeforeAfterSlider.5
            @Override // mobi.zeezoo.photoenhancer.feature.view.ZoomImageView.OnUpdateMatrixImage
            public void updateMatrix(Matrix matrix, boolean z) {
                if (ImageBeforeAfterSlider.this.scaleType != ImageView.ScaleType.MATRIX) {
                    return;
                }
                if (ImageBeforeAfterSlider.this.isUpdateAffter && ImageBeforeAfterSlider.this.isUpdateBefore) {
                    if (matrix != null && z) {
                        ImageBeforeAfterSlider.this.imgBefore.setMatrixUpdate(matrix, ImageBeforeAfterSlider.this.imgAfter.getZoomMatrix(), ImageBeforeAfterSlider.this.imgAfter.getMatrixValues(), ImageBeforeAfterSlider.this.imgAfter.getOldScale());
                    }
                    ImageBeforeAfterSlider.this.seekSlider.isDisableTouch = z;
                }
                if (ImageBeforeAfterSlider.this.isDisableZoom) {
                    ImageBeforeAfterSlider.this.seekSlider.isDisableTouch = false;
                }
            }

            @Override // mobi.zeezoo.photoenhancer.feature.view.ZoomImageView.OnUpdateMatrixImage
            public void setZoom(float f, float f2, float f3) {
                if (ImageBeforeAfterSlider.this.scaleType != ImageView.ScaleType.MATRIX) {
                    return;
                }
                ImageBeforeAfterSlider.this.imgBefore.setZoomImageNew(f, f2, f3);
            }
        });
        this.rlAfter.addView(this.imgAfter);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void activeAnimation() {
        if (this.autoSlideDuration.intValue() > 0) {
            ResizeWidthAnimation resizeWidthAnimation = new ResizeWidthAnimation(this.flBefore, this.width, 0);
            final ResizeWidthAnimation resizeWidthAnimation2 = new ResizeWidthAnimation(this.flBefore, 0, this.width / 2);
            Animation loadAnimation = AnimationUtils.loadAnimation(this.context, android.R.anim.fade_in);
            final Animation loadAnimation2 = AnimationUtils.loadAnimation(this.context, android.R.anim.fade_out);
            final Animation loadAnimation3 = AnimationUtils.loadAnimation(this.context, android.R.anim.fade_in);
            resizeWidthAnimation.setDuration(this.autoSlideDuration.intValue());
            resizeWidthAnimation2.setDuration(this.autoSlideDuration.intValue() / 2);
            this.flBefore.startAnimation(resizeWidthAnimation);
            this.rlBefore.startAnimation(loadAnimation);
            this.rlAfter.startAnimation(loadAnimation);
            resizeWidthAnimation.setAnimationListener(new Animation.AnimationListener() { // from class: mobi.zeezoo.photoenhancer.feature.view.ImageBeforeAfterSlider.6
                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationStart(Animation animation) {
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationEnd(Animation animation) {
                    ImageBeforeAfterSlider.this.flBefore.startAnimation(resizeWidthAnimation2);
                    ImageBeforeAfterSlider.this.rlAfter.startAnimation(loadAnimation2);
                    ImageBeforeAfterSlider.this.rlBefore.startAnimation(loadAnimation2);
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationRepeat(Animation animation) {
                    ImageBeforeAfterSlider.this.logger.info("onAnimationRepeat");
                }
            });
            resizeWidthAnimation2.setAnimationListener(new Animation.AnimationListener() { // from class: mobi.zeezoo.photoenhancer.feature.view.ImageBeforeAfterSlider.7
                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationRepeat(Animation animation) {
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationStart(Animation animation) {
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationEnd(Animation animation) {
                    ImageBeforeAfterSlider.this.seekSlider.startAnimation(loadAnimation3);
                    ImageBeforeAfterSlider.this.seekSlider.setVisibility(View.VISIBLE);
                    if (ImageBeforeAfterSlider.this.lockVibrate) {
                        return;
                    }
                    Constants.vibrate(ImageBeforeAfterSlider.this.context, 200L);
                }
            });
        }
    }

    public void reSizeView(Integer num, Integer num2) {
        this.logger.info("reSizeView width: " + num);
        this.logger.info("reSizeView height: " + num2);
        if (num == null) {
            num = Integer.valueOf(this.width);
        }
        if (num2 == null) {
            num2 = Integer.valueOf(this.height);
        }
        this.seekSlider.setMax(num.intValue());
        this.seekSlider.setProgress(num.intValue() / 2);
        ViewGroup.LayoutParams layoutParams = this.rlAfter.getLayoutParams();
        layoutParams.width = num.intValue();
        layoutParams.height = num2.intValue();
        this.rlAfter.setLayoutParams(layoutParams);
        ViewGroup.LayoutParams layoutParams2 = this.flBefore.getLayoutParams();
        layoutParams2.width = this.isFlipBackActive ? num.intValue() / 2 : num.intValue();
        layoutParams2.height = num2.intValue();
        this.flBefore.setLayoutParams(layoutParams2);
        ViewGroup.LayoutParams layoutParams3 = this.rlBefore.getLayoutParams();
        layoutParams3.width = num.intValue();
        layoutParams3.height = num2.intValue();
        this.rlBefore.setLayoutParams(layoutParams3);
        ((MarginLayoutParams) this.seekSlider.getLayoutParams()).setMargins(0, (int) (num2.intValue() / 1.8d), 0, 0);
        this.seekSlider.requestLayout();
        requestLayout();
    }

    public SeekBarTouch getSeekSlider() {
        return this.seekSlider;
    }

    public void setSeekSlider(boolean z) {
        this.seekSlider.setVisibility(z ? View.VISIBLE : View.GONE);
        this.flSlider.setVisibility(z ? View.VISIBLE : View.GONE);
        this.flBefore.setVisibility(z ? View.VISIBLE : View.GONE);
        this.tvAfter.setVisibility(z ? View.VISIBLE : View.GONE);
        this.tvBefore.setVisibility(z ? View.VISIBLE : View.GONE);
        this.isFlipBackActive = z;
    }

    @Override // android.view.View
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        this.logger.info("onClickListener..........");
        super.setOnClickListener(onClickListener);
    }

    @Override // androidx.cardview.widget.CardView
    public void setRadius(float f) {
        setCardElevation(f);
        super.setRadius(f);
    }

    public void setImages(int i, int i2) {
        this.logger.info("setImages......");
        this.imgAfter.setImageResource(i2);
        this.imgBefore.setImageResource(i);
    }

    public void setImages(String str, String str2) {
        Glide.with(this.context).load(str2).into(this.imgAfter);
        Glide.with(this.context).load(str).into(this.imgBefore);
    }

    public void setImages(Bitmap bitmap, Bitmap bitmap2) {
        this.logger.info("setBitmap");
        this.imgAfter.setImageBitmap(bitmap2);
        this.imgBefore.setImageBitmap(bitmap);
    }

    public void setImagesResult(final String str, final String str2, final Loading loading, final Processing processing, String str3, final Integer num, final Integer num2) {
        if (num != null) {
            this.width = num.intValue();
        }
        if (num2 != null) {
            this.height = num2.intValue();
        }
        initImageZoom();
        this.llTitle.setVisibility(View.GONE);
        if (this.isFlipBackActive) {
            this.tvBefore.setVisibility(View.VISIBLE);
            this.tvAfter.setVisibility(View.VISIBLE);
        }
        if (str3 != null && str3.equals("removebg")) {
            this.imgBg.setVisibility(View.VISIBLE);
        }
        if (str2 != null) {
            if (loading != null) {
                loading.show();
            }
            try {
                if (!this.map.containsKey(str2)) {
                    Glide.with(this.context).asBitmap().load(str2).into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // do nothing
                        }

                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            Integer num3 = num;
                            if (num3 != null && num2 != null) {
                                Bitmap crop = BitmapCropUtils.crop(resource, num3.intValue(), num2.intValue(), 0.0f, 0.0f);
                                ImageBeforeAfterSlider.this.reSizeView(num, num2);
                                ImageBeforeAfterSlider.this.imgAfter.setImageBitmap(crop);
                                ImageBeforeAfterSlider.this.imgAfter.performClick();
                                ImageBeforeAfterSlider.this.isUpdateAffter = true;
                                ImageBeforeAfterSlider.this.map.put(str2, crop);
                            }
                            Loading loading2 = loading;
                            if (loading2 != null) {
                                loading2.dismiss();
                            }
                            Processing processing2 = processing;
                            if (processing2 != null) {
                                processing2.onDismissDialog(null);
                            }
                        }
                    });
                } else {
                    reSizeView(num, num2);
                    this.imgAfter.setImageBitmap(this.map.get(str2));
                    this.isUpdateAffter = true;
                    if (loading != null) {
                        loading.dismiss();
                    }
                    if (processing != null) {
                        processing.onDismissDialog();
                    }
                }
                if (!this.map.containsKey(str)) {
                    Glide.with(this.context).asBitmap().load(str).into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // do nothing
                        }

                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            Integer num3 = num;
                            if (num3 == null || num2 == null) {
                                return;
                            }
                            Bitmap crop = BitmapCropUtils.crop(resource, num3.intValue(), num2.intValue(), 0.0f, 0.0f);
                            ImageBeforeAfterSlider.this.reSizeView(num, num2);
                            ImageBeforeAfterSlider.this.imgBefore.setImageBitmap(crop);
                            ImageBeforeAfterSlider.this.imgBefore.setVisibility(View.VISIBLE);
                            ImageBeforeAfterSlider.this.imgBefore.performClick();
                            ImageBeforeAfterSlider.this.isUpdateBefore = true;
                            ImageBeforeAfterSlider.this.map.put(str, crop);
                        }
                    });
                } else {
                    reSizeView(num, num2);
                    this.imgBefore.setImageBitmap(this.map.get(str));
                    this.isUpdateBefore = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { // from class: mobi.zeezoo.photoenhancer.feature.view.ImageBeforeAfterSlider.10
            @Override // java.lang.Runnable
            public void run() {
                ImageBeforeAfterSlider.this.isInitMatrix = true;
            }
        }, 1000L);
    }

    public void setEnableChangePhoto(boolean z) {
        this.btnChangePhoto.setVisibility(z ? View.VISIBLE : View.GONE);
    }

    public void setEnableFaceIcon(boolean z, boolean z2) {
        this.btnBeautifier.setVisibility(z ? View.VISIBLE : View.GONE);
        // Use system drawables as fallback for missing resources
        try {
            this.btnBeautifier.setImageDrawable(this.context.getDrawable(z2 ? android.R.drawable.ic_menu_camera : android.R.drawable.ic_menu_edit));
        } catch (Exception e) {
            // Fallback if drawables are not available
            this.btnBeautifier.setImageDrawable(null);
        }
    }

    public void setTitle(String str) {
        if (str == null) {
            this.tvTitle.setText("");
        } else {
            this.tvTitle.setText(str);
        }
    }

    public void setSliderThumb(int i) {
        this.seekSlider.setThumb(this.context.getDrawable(i));
    }

    public void setSize(String str) {
        if (str.equals("large") || str.equals("xlarge")) {
            if (str.equals("xlarge")) {
                this.seekSlider.setThumb(this.context.getDrawable(R.drawable.cycle_arrow));
            }
            this.tvTitle.setTextSize(str.equals("large") ? 24.0f : 32.0f);
            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) this.flSlider.getLayoutParams();
            marginLayoutParams.width = Constants.dpToPixel(2.0f);
            marginLayoutParams.setMargins(Constants.dpToPixel(-1.0f), 0, 0, 0);
            this.flSlider.setLayoutParams(marginLayoutParams);
            MarginLayoutParams marginLayoutParams2 = (MarginLayoutParams) this.flSliderTop.getLayoutParams();
            marginLayoutParams2.setMargins(0, 0, 0, 0);
            this.flSliderTop.setLayoutParams(marginLayoutParams2);
            MarginLayoutParams marginLayoutParams3 = (MarginLayoutParams) this.flSliderBottom.getLayoutParams();
            marginLayoutParams3.setMargins(0, 0, 0, 0);
            this.flSliderBottom.setLayoutParams(marginLayoutParams3);
            this.tvTitle.setGravity(3);
            int applyDimension = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, str.equals("large") ? 12.0f : 20.0f, this.context.getResources().getDisplayMetrics());
            this.tvTitle.setPadding(applyDimension, applyDimension, applyDimension, applyDimension / 2);
            if (str.equals("xlarge")) {
                try {
                    this.llTitle.setBackgroundColor(this.context.getColor(android.R.color.darker_gray));
                } catch (Exception e) {
                    // Fallback if drawable is not available
                    this.llTitle.setBackgroundColor(this.context.getColor(android.R.color.transparent));
                }
                return;
            }
            return;
        }
        this.seekSlider.setThumb(this.context.getDrawable(R.drawable.cycle_arrow));
    }

    public void setAutoSlideDuration(Integer num, Boolean bool) {
        this.autoSlideDuration = num;
        if (num.intValue() > 0) {
            this.seekSlider.setVisibility(View.GONE);
            this.flSlider.setBackgroundColor(this.context.getColor(R.color.white));
            if (bool.booleanValue()) {
                activeAnimation();
            }
        }
    }

    public void setDesc(String str) {
        if (str != null) {
            this.tvDesc.setVisibility(View.VISIBLE);
            this.tvDesc.setText(str);
        }
    }

    public boolean isLockVibrate() {
        return this.lockVibrate;
    }

    public void setLockVibrate(boolean z) {
        this.lockVibrate = z;
    }

    public void setOnClickViewListener(ViewListener viewListener) {
        this.viewListener = viewListener;
    }

    /**
     * Get the after image bitmap
     *
     * @return Bitmap of the after image, or null if not available
     */
    public Bitmap getAfterImageBitmap() {
        if (imgAfter != null && imgAfter.getDrawable() != null) {
            try {
                // Enable drawing cache
                imgAfter.setDrawingCacheEnabled(true);
                imgAfter.buildDrawingCache();

                // Get the bitmap
                Bitmap bitmap = imgAfter.getDrawingCache();

                // Create a copy since drawing cache can be recycled
                Bitmap copy = bitmap != null ? bitmap.copy(bitmap.getConfig(), false) : null;

                // Disable drawing cache
                imgAfter.setDrawingCacheEnabled(false);

                return copy;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    /**
     * Get the after image bitmap from the cached map.
     * This method returns any non-recycled Bitmap in the cache;
     * for a more specific lookup, use getCachedAfterImageBitmap(String afterImageUrl).
     *
     * @return Bitmap from the cached map, or null if not available
     */
    public Bitmap getCachedAfterImageBitmap() {
        if (map != null && !map.isEmpty()) {
            for (Bitmap bitmap : map.values()) {
                if (bitmap != null && !bitmap.isRecycled()) {
                    return bitmap;
                }
            }
        }
        return null;
    }

    /**
     * Get the after image bitmap from the cached map using the specific URL key.
     *
     * @param afterImageUrl The URL of the after image
     * @return Bitmap from the cached map, or null if not available
     */
    public Bitmap getCachedAfterImageBitmap(String afterImageUrl) {
        if (map != null && !map.isEmpty() && afterImageUrl != null) {
            Bitmap bitmap = map.get(afterImageUrl);
            if (bitmap != null && !bitmap.isRecycled()) {
                return bitmap;
            }
        }
        return getCachedAfterImageBitmap(); // fallback to general method
    }

    /**
     * Get the after image ZoomImageView for direct access
     *
     * @return ZoomImageView containing the after image
     */
    public ZoomImageView getAfterImageView() {
        return imgAfter;
    }

    /**
     * Get the before image ZoomImageView for direct access
     *
     * @return ZoomImageView containing the before image
     */
    public ZoomImageView getBeforeImageView() {
        return imgBefore;
    }

    /* loaded from: classes5.dex */
    public interface ViewListener {
        void onClickBeautifier();

        void onClickChangePhoto();
    }
}