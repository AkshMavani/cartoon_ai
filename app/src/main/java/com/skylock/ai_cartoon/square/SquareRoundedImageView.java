package com.skylock.ai_cartoon.square;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.appcompat.widget.AppCompatImageView;

/* loaded from: classes9.dex */
public class SquareRoundedImageView extends AppCompatImageView {
    public SquareRoundedImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public SquareRoundedImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public SquareRoundedImageView(Context context) {
        super(context);
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(getDefaultSize(0, i), getDefaultSize(0, i2));
        int makeMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824);
        super.onMeasure(makeMeasureSpec, makeMeasureSpec);
    }
}
