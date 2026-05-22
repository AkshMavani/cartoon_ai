package com.skylock.ai_cartoon.enhance;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Animation for resizing view width
 */
public class ResizeWidthAnimation extends Animation {
    private final View view;
    private final int startWidth;
    private final int targetWidth;
    private final int deltaWidth;

    public ResizeWidthAnimation(View view, int startWidth, int targetWidth) {
        this.view = view;
        this.startWidth = startWidth;
        this.targetWidth = targetWidth;
        this.deltaWidth = targetWidth - startWidth;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = (int) (startWidth + deltaWidth * interpolatedTime);
        view.setLayoutParams(params);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}