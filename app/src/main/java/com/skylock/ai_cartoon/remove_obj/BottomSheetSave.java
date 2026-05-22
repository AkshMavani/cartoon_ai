package com.skylock.ai_cartoon.remove_obj;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.skylock.ai_cartoon.R;
import com.skylock.ai_cartoon.databinding.BottomDialogSaveBinding;

import java.util.Objects;


/* loaded from: classes6.dex */
public class BottomSheetSave extends BottomSheetDialog {
    private BottomDialogSaveBinding binding;
    private ToolActionListener toolActionListener;

    public BottomSheetSave(Context context, final ToolActionListener toolActionListener) {
        super(context, R.style.SheetDialog);
        BottomDialogSaveBinding inflate = BottomDialogSaveBinding.inflate(getLayoutInflater());
        this.binding = inflate;
        setContentView(inflate.getRoot());
        getBehavior().setState(3);
        Objects.requireNonNull(getWindow()).getDecorView().setSystemUiVisibility(5380);
        this.binding.itemAds.setOnClickListener(new View.OnClickListener() { // from class: mobi.zeezoo.photoenhancer.feature.view.bottom_sheet.BottomSheetSave$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                BottomSheetSave.this.lambda$new$0(toolActionListener, view);
            }
        });
        this.binding.itemPro.setOnClickListener(new View.OnClickListener() { // from class: mobi.zeezoo.photoenhancer.feature.view.bottom_sheet.BottomSheetSave$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                BottomSheetSave.this.lambda$new$1(toolActionListener, view);
            }
        });
    }

    protected BottomSheetSave(Context context, boolean z, OnCancelListener onCancelListener) {
        super(context, z, onCancelListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(ToolActionListener toolActionListener, View view) {
        dismiss();
        if (toolActionListener != null) {
            toolActionListener.watchAds();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(ToolActionListener toolActionListener, View view) {
        dismiss();
        if (toolActionListener != null) {
            toolActionListener.onPro();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override
    // com.google.android.material.bottomsheet.BottomSheetDialog, androidx.appcompat.app.AppCompatDialog, androidx.activity.ComponentDialog, android.app.Dialog
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override // android.app.Dialog
    public void show() {
        super.show();
    }

    @Override // com.google.android.material.bottomsheet.BottomSheetDialog, android.app.Dialog
    public void setCanceledOnTouchOutside(boolean z) {
        super.setCanceledOnTouchOutside(z);
    }

    @Override
    // androidx.appcompat.app.AppCompatDialog, android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        super.dismiss();
    }

    /* loaded from: classes6.dex */
    public interface ToolActionListener {
        void onPro();

        void watchAds();
    }
}
