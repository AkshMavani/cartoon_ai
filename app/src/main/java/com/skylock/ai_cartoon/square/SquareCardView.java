package com.skylock.ai_cartoon.square;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.cardview.widget.CardView;

/* loaded from: classes6.dex */
public class SquareCardView extends CardView {
    public SquareCardView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public SquareCardView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public SquareCardView(Context context) {
        super(context);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.cardview.widget.CardView, android.widget.FrameLayout, android.view.View
    public void onMeasure(int i, int i2) {
        setMeasuredDimension(getDefaultSize(0, i), getDefaultSize(0, i2));
        int makeMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824);
        super.onMeasure(makeMeasureSpec, makeMeasureSpec);
    }
}
