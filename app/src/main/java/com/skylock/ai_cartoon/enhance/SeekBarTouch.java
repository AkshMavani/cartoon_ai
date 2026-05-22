package com.skylock.ai_cartoon.enhance;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/* loaded from: classes6.dex */
public class SeekBarTouch extends androidx.appcompat.widget.AppCompatSeekBar {
    public boolean isDisableTouch;

    public SeekBarTouch(Context context) {
        super(context);
        this.isDisableTouch = false;
    }

    public SeekBarTouch(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.isDisableTouch = false;
    }

    public SeekBarTouch(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.isDisableTouch = false;
    }

    @Override // android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (this.isDisableTouch) {
            return false;
        }
        int i = getThumb().getBounds().left;
        int i2 = getThumb().getBounds().right;
        int width = getThumb().getBounds().width() * 2;
        if (motionEvent.getX() < i - width || motionEvent.getX() > i2 + width) {
            return false;
        }
        return super.dispatchTouchEvent(motionEvent);
    }
}
