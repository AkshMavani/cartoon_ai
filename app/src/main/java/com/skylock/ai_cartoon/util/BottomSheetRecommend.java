package com.skylock.ai_cartoon.util;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.skylock.ai_cartoon.R;
import com.skylock.ai_cartoon.databinding.BottomDialogRecommendBinding;

import java.util.Objects;

public class BottomSheetRecommend extends BottomSheetDialog {
    private BottomDialogRecommendBinding binding;
    private String feature;

    public BottomSheetRecommend(Context context, String str) {
        super(context, R.style.SheetDialog);
        BottomDialogRecommendBinding inflate = BottomDialogRecommendBinding.inflate(getLayoutInflater());
        this.binding = inflate;
        setContentView(inflate.getRoot());
        getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        ((Window) Objects.requireNonNull(getWindow())).getDecorView().setSystemUiVisibility(5380);
        this.feature = str;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.material.bottomsheet.BottomSheetDialog, androidx.appcompat.app.AppCompatDialog, androidx.activity.ComponentDialog, android.app.Dialog
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        applyGradientToTextView(this.binding.header2);
        applyGradientToTextView(this.binding.header3);
        this.binding.btnClose.setOnClickListener(new View.OnClickListener() { // from class: mobi.zeezoo.photoenhancer.feature.view.bottom_sheet.BottomSheetRecommend$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                BottomSheetRecommend.this.lambda$onCreate$0(view);
            }
        });
        this.binding.close.setOnClickListener(new View.OnClickListener() { // from class: mobi.zeezoo.photoenhancer.feature.view.bottom_sheet.BottomSheetRecommend$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                BottomSheetRecommend.this.lambda$onCreate$1(view);
            }
        });
        this.binding.header.setText(ToolEnhanceUtils.getFeature(getContext(), this.feature) + " " + getContext().getString(R.string.label_tips));
        if (this.feature.equals("enhance")) {
            this.binding.imgNotRecommend1.setImageResource(R.drawable.bad_enhance_01);
            this.binding.imgNotRecommend2.setImageResource(R.drawable.bad_enhance_02);
            this.binding.imgNotRecommend3.setImageResource(R.drawable.bad_enhance_03);
            this.binding.imgNotRecommend4.setImageResource(R.drawable.bad_enhance_04);
            this.binding.imgRecommend1.setImageResource(R.drawable.good_enhance_01);
            this.binding.imgRecommend2.setImageResource(R.drawable.good_enhance_02);
            this.binding.imgRecommend3.setImageResource(R.drawable.good_enhance_03);
            this.binding.recommendDesc.setText(getContext().getString(R.string.label_recommend_enhance_desc));
            this.binding.notRecommendDesc.setText(getContext().getString(R.string.label_not_recommend_enhance_desc));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$0(View view) {
        dismiss();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$1(View view) {
        dismiss();
    }

    protected BottomSheetRecommend(Context context, boolean z, DialogInterface.OnCancelListener onCancelListener) {
        super(context, z, onCancelListener);
    }

    @Override // android.app.Dialog
    public void show() {
        super.show();
    }

    @Override // com.google.android.material.bottomsheet.BottomSheetDialog, android.app.Dialog
    public void setCanceledOnTouchOutside(boolean z) {
        super.setCanceledOnTouchOutside(z);
    }

    @Override // androidx.appcompat.app.AppCompatDialog, android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        super.dismiss();
    }

    public void applyGradientToTextView(TextView textView) {
        textView.getPaint().setShader(new LinearGradient(0.0f, 0.0f, 0.0f, textView.getHeight(), getContext().getColor(R.color.gradient_primary), getContext().getColor(R.color.gradient_secondary), Shader.TileMode.CLAMP));
        textView.invalidate();
    }
}
