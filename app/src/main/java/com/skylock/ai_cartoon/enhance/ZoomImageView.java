package com.skylock.ai_cartoon.enhance;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.OverScroller;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.view.ViewCompat;

/**
 * Perfect ZoomImageView implementation with smooth zooming, panning,
 * and gesture support. Optimized for performance and user experience.
 */
public class ZoomImageView extends AppCompatImageView {

    // Constants
    public static final float MIN_SCALE = 1.0f;
    public static final float MID_SCALE = 1.75f;
    public static final float MAX_SCALE = 3.0f;
    private static final long ZOOM_DURATION = 300L;
    private static final long PAN_DURATION = 300L;

    // Matrix objects for transformations
    private final Matrix baseMatrix = new Matrix();
    private final Matrix zoomMatrix = new Matrix();
    private final Matrix drawMatrix = new Matrix();
    private final Matrix savedMatrix = new Matrix();
    private final DecelerateInterpolator interpolator = new DecelerateInterpolator();
    private final RectF displayRect = new RectF();
    // Fling handling
    private final FlingRunnable flingRunnable = new FlingRunnable();
    private final Paint debugPaint = new Paint();
    // Gesture detectors
    private ScaleGestureDetector scaleDetector;
    private GestureDetector gestureDetector;
    // Animation and scrolling
    private ValueAnimator zoomAnimator;
    private ValueAnimator panAnimator;
    private OverScroller scroller;
    // View dimensions and bounds
    private int viewWidth;
    private int viewHeight;
    private float[] matrixValues = new float[9];
    // Touch handling
    private float touchSlop;
    private boolean isZooming = false;
    private boolean isDragging = false;
    private boolean handlingDismiss = false;
    // Callbacks
    private OnClickListener onClickListener;
    private OnLongClickListener onLongClickListener;
    private OnMatrixChangedListener onMatrixChangedListener;
    private OnDismissListener onDismissListener;
    private OnProgressListener onProgressListener;
    private OnUpdateMatrixImage onUpdateMatrixImage;
    // Configuration
    private boolean swipeToDismissEnabled = false;
    private boolean disallowPagingWhenZoomed = true;
    private boolean zoomEnabled = true;
    private boolean zoomDisabled = false;
    // Debug
    private boolean debugMode = false;
    // Backward compatibility fields
    private float oldScale = 1.0f;

    public ZoomImageView(Context context) {
        super(context);
        init();
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        super.setScaleType(ScaleType.MATRIX);

        // Initialize touch slop
        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        // Initialize gesture detectors
        scaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        gestureDetector = new GestureDetector(getContext(), new GestureListener());

        // Initialize scroller
        scroller = new OverScroller(getContext(), interpolator);

        // Initialize debug paint
        debugPaint.setColor(0xFFFF0000);
        debugPaint.setTextSize(30f);
        debugPaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w - getPaddingLeft() - getPaddingRight();
        viewHeight = h - getPaddingTop() - getPaddingBottom();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        if (drawable != null) {
            resetZoom();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed && getDrawable() != null) {
            resetZoom();
        }
    }

    /**
     * Reset zoom to fit the image in the view
     */
    public void resetZoom() {
        Drawable drawable = getDrawable();
        if (drawable == null || viewWidth <= 0 || viewHeight <= 0) {
            return;
        }

        int drawableWidth = drawable.getIntrinsicWidth();
        int drawableHeight = drawable.getIntrinsicHeight();

        if (drawableWidth <= 0 || drawableHeight <= 0) {
            return;
        }

        RectF drawableRect = new RectF(0, 0, drawableWidth, drawableHeight);
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);

        baseMatrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER);
        zoomMatrix.reset();

        updateMatrix();
    }

    /**
     * Set zoom level to specific scale
     */
    public void setZoom(float scale) {
        setZoom(scale, viewWidth / 2f, viewHeight / 2f);
    }

    /**
     * Set zoom level to specific scale with focus point
     */
    public void setZoom(float scale, float focusX, float focusY) {
        scale = Math.max(MIN_SCALE, Math.min(MAX_SCALE, scale));

        cancelAnimations();

        float currentScale = getCurrentScale();
        animateZoom(currentScale, scale, focusX, focusY);
    }

    /**
     * Get current zoom scale
     */
    public float getCurrentScale() {
        zoomMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }

    /**
     * Check if image is zoomed
     */
    public boolean isZoomed() {
        return getCurrentScale() > MIN_SCALE + 0.01f;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!zoomEnabled || zoomDisabled) {
            return super.onTouchEvent(event);
        }

        boolean handled = false;

        if (scaleDetector != null) {
            handled = scaleDetector.onTouchEvent(event);
        }

        if (gestureDetector != null && !isZooming) {
            handled = gestureDetector.onTouchEvent(event) || handled;
        }

        if (event.getAction() == MotionEvent.ACTION_UP ||
                event.getAction() == MotionEvent.ACTION_CANCEL) {
            isZooming = false;
            isDragging = false;
        }

        return handled || super.onTouchEvent(event);
    }

    private void animateZoom(float startScale, float endScale, float focusX, float focusY) {
        if (zoomAnimator != null) {
            zoomAnimator.cancel();
        }

        zoomAnimator = ValueAnimator.ofFloat(startScale, endScale);
        zoomAnimator.setDuration(ZOOM_DURATION);
        zoomAnimator.setInterpolator(interpolator);

        zoomAnimator.addUpdateListener(animation -> {
            float animatedScale = (float) animation.getAnimatedValue();
            float scaleFactor = animatedScale / getCurrentScale();
            zoomMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
            checkAndDisplayMatrix();
        });

        zoomAnimator.start();
    }

    private void animatePan(float fromX, float fromY, float toX, float toY) {
        if (panAnimator != null) {
            panAnimator.cancel();
        }

        panAnimator = ValueAnimator.ofFloat(0f, 1f);
        panAnimator.setDuration(PAN_DURATION);
        panAnimator.setInterpolator(interpolator);

        panAnimator.addUpdateListener(animation -> {
            float progress = (float) animation.getAnimatedValue();
            float currentX = fromX + (toX - fromX) * progress;
            float currentY = fromY + (toY - fromY) * progress;

            zoomMatrix.setTranslate(currentX, currentY);
            updateMatrix();
        });

        panAnimator.start();
    }

    private void cancelAnimations() {
        if (zoomAnimator != null) {
            zoomAnimator.cancel();
        }
        if (panAnimator != null) {
            panAnimator.cancel();
        }
        if (scroller != null) {
            scroller.forceFinished(true);
        }
        removeCallbacks(flingRunnable);
    }

    private void checkAndDisplayMatrix() {
        constrainMatrix();
        updateMatrix();
    }

    private void constrainMatrix() {
        RectF rect = getMatrixRectF();
        if (rect == null) return;

        float deltaX = 0;
        float deltaY = 0;

        if (rect.width() <= viewWidth) {
            deltaX = (viewWidth - rect.width()) / 2 - rect.left;
        } else if (rect.left > 0) {
            deltaX = -rect.left;
        } else if (rect.right < viewWidth) {
            deltaX = viewWidth - rect.right;
        }

        if (rect.height() <= viewHeight) {
            if (!handlingDismiss) {
                deltaY = (viewHeight - rect.height()) / 2 - rect.top;
            }
        } else if (rect.top > 0) {
            deltaY = -rect.top;
        } else if (rect.bottom < viewHeight) {
            deltaY = viewHeight - rect.bottom;
        }

        zoomMatrix.postTranslate(deltaX, deltaY);
    }

    private RectF getMatrixRectF() {
        Drawable drawable = getDrawable();
        if (drawable == null) return null;

        displayRect.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        getDrawMatrix().mapRect(displayRect);
        return displayRect;
    }

    private Matrix getDrawMatrix() {
        drawMatrix.set(baseMatrix);
        drawMatrix.postConcat(zoomMatrix);
        return drawMatrix;
    }

    private void updateMatrix() {
        setImageMatrix(getDrawMatrix());

        if (onMatrixChangedListener != null) {
            onMatrixChangedListener.onMatrixChanged(getDrawMatrix(), true);
        }

        if (onUpdateMatrixImage != null) {
            onUpdateMatrixImage.updateMatrix(getDrawMatrix(), isDragging || isZooming);
        }

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (debugMode) {
            drawDebugInfo(canvas);
        }
    }

    private void drawDebugInfo(Canvas canvas) {
        String scaleInfo = String.format("Scale: %.2f", getCurrentScale());
        String matrixInfo = String.format("Matrix: [%.1f, %.1f]",
                getCurrentTransX(), getCurrentTransY());

        canvas.drawText(scaleInfo, 20, 50, debugPaint);
        canvas.drawText(matrixInfo, 20, 90, debugPaint);
    }

    private float getCurrentTransX() {
        zoomMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MTRANS_X];
    }

    private float getCurrentTransY() {
        zoomMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MTRANS_Y];
    }

    private void handleSwipeToDismiss(float deltaY) {
        handlingDismiss = true;

        float progress = Math.abs(deltaY) / (viewHeight / 3f);
        progress = Math.min(1f, progress);

        if (onProgressListener != null) {
            onProgressListener.onProgress(progress);
        }

        if (progress >= 1f && onDismissListener != null) {
            onDismissListener.onDismiss();
        }
    }

    // Backward compatibility methods
    public void disableZoom(boolean disable) {
        this.zoomDisabled = disable;
        this.zoomEnabled = !disable;
    }

    public void setOnUpdateMatrixImage(OnUpdateMatrixImage listener) {
        this.onUpdateMatrixImage = listener;
    }

    public Matrix getZoomMatrix() {
        return new Matrix(zoomMatrix);
    }

    public float[] getMatrixValues() {
        zoomMatrix.getValues(matrixValues);
        return matrixValues.clone();
    }

    public float getOldScale() {
        return oldScale;
    }

    public void setMatrixUpdate(Matrix matrix, Matrix zoomMatrix, float[] matrixValues, float currentScale) {
        if (matrix != null) {
            this.zoomMatrix.set(zoomMatrix);
            this.oldScale = currentScale;
            this.matrixValues = matrixValues.clone();
            setImageMatrix(matrix);
            invalidate();
        }
    }

    public void setZoomImageNew(float scale, float x, float y) {
        zoomMatrix.postScale(scale, scale, x, y);
        checkAndDisplayMatrix();
    }

    public float getCurrentZoom() {
        return getCurrentScale();
    }

    public void setCurrentZoom(float zoom) {
        setZoom(zoom);
    }

    // Public setters
    @Override
    public void setOnClickListener(OnClickListener l) {
        this.onClickListener = l;
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        this.onLongClickListener = l;
    }

    public void setOnMatrixChangedListener(OnMatrixChangedListener listener) {
        this.onMatrixChangedListener = listener;
    }

    public void setOnDismissListener(OnDismissListener listener) {
        this.onDismissListener = listener;
    }

    public void setOnProgressListener(OnProgressListener listener) {
        this.onProgressListener = listener;
    }

    // Getters
    public boolean isSwipeToDismissEnabled() {
        return swipeToDismissEnabled;
    }

    public void setSwipeToDismissEnabled(boolean enabled) {
        this.swipeToDismissEnabled = enabled;
    }

    public boolean isDisallowPagingWhenZoomed() {
        return disallowPagingWhenZoomed;
    }

    public void setDisallowPagingWhenZoomed(boolean disallow) {
        this.disallowPagingWhenZoomed = disallow;
    }

    public boolean isZoomEnabled() {
        return zoomEnabled;
    }

    public void setZoomEnabled(boolean enabled) {
        this.zoomEnabled = enabled;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean enabled) {
        this.debugMode = enabled;
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelAnimations();
    }

    // Interfaces
    public interface OnMatrixChangedListener {
        void onMatrixChanged(Matrix matrix, boolean isUserAction);
    }

    public interface OnDismissListener {
        void onDismiss();
    }

    public interface OnProgressListener {
        void onProgress(float progress);
    }

    /**
     * Interface for backward compatibility with ImageBeforeAfterSlider
     */
    public interface OnUpdateMatrixImage {
        void updateMatrix(Matrix drawMatrix, boolean isTouch);

        void setZoom(float scale, float x, float y);
    }

    // Gesture Listeners
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            isZooming = true;
            oldScale = getCurrentScale();
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();

            if (Float.isNaN(scaleFactor) || Float.isInfinite(scaleFactor)) {
                return false;
            }

            float currentScale = getCurrentScale();
            float newScale = currentScale * scaleFactor;

            // Prevent over-scaling
            if (newScale > MAX_SCALE && scaleFactor > 1f) {
                return false;
            }
            if (newScale < MIN_SCALE && scaleFactor < 1f) {
                return false;
            }

            zoomMatrix.postScale(scaleFactor, scaleFactor,
                    detector.getFocusX(), detector.getFocusY());
            checkAndDisplayMatrix();

            // Notify the callback
            if (onUpdateMatrixImage != null) {
                onUpdateMatrixImage.setZoom(scaleFactor, detector.getFocusX(), detector.getFocusY());
            }

            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            isZooming = false;
            oldScale = getCurrentScale();

            float currentScale = getCurrentScale();
            if (currentScale < MIN_SCALE) {
                setZoom(MIN_SCALE, detector.getFocusX(), detector.getFocusY());
            }
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            cancelAnimations();
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (onClickListener != null) {
                onClickListener.onClick(ZoomImageView.this);
                return true;
            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (onLongClickListener != null) {
                onLongClickListener.onLongClick(ZoomImageView.this);
            }
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            float currentScale = getCurrentScale();
            float targetScale = currentScale > MIN_SCALE ? MIN_SCALE : MID_SCALE;
            setZoom(targetScale, e.getX(), e.getY());
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (isZooming) {
                return false;
            }

            RectF rect = getMatrixRectF();
            if (rect == null) return false;

            boolean canPanHorizontally = rect.width() > viewWidth;
            boolean canPanVertically = rect.height() > viewHeight || swipeToDismissEnabled;

            if (canPanHorizontally || canPanVertically) {
                isDragging = true;

                float dx = canPanHorizontally ? -distanceX : 0;
                float dy = canPanVertically ? -distanceY : 0;

                zoomMatrix.postTranslate(dx, dy);
                checkAndDisplayMatrix();

                // Handle swipe to dismiss
                if (swipeToDismissEnabled && !isZoomed() && Math.abs(dy) > Math.abs(dx)) {
                    handleSwipeToDismiss(dy);
                }

                return true;
            }

            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (!isZoomed() || isDragging) {
                return false;
            }

            RectF rect = getMatrixRectF();
            if (rect == null) return false;

            int maxX = Math.max(0, (int) (rect.width() - viewWidth));
            int maxY = Math.max(0, (int) (rect.height() - viewHeight));

            flingRunnable.fling(
                    (int) -rect.left, (int) -rect.top,
                    (int) -velocityX, (int) -velocityY,
                    0, maxX, 0, maxY
            );

            post(flingRunnable);
            return true;
        }
    }

    // Fling handling
    private class FlingRunnable implements Runnable {
        private int currentX, currentY;

        public void fling(int startX, int startY, int velocityX, int velocityY,
                          int minX, int maxX, int minY, int maxY) {
            scroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);
            currentX = startX;
            currentY = startY;
        }

        @Override
        public void run() {
            if (scroller.isFinished()) {
                return;
            }

            if (scroller.computeScrollOffset()) {
                int newX = scroller.getCurrX();
                int newY = scroller.getCurrY();

                zoomMatrix.postTranslate(currentX - newX, currentY - newY);
                updateMatrix();

                currentX = newX;
                currentY = newY;

                ViewCompat.postOnAnimation(ZoomImageView.this, this);
            }
        }
    }
}