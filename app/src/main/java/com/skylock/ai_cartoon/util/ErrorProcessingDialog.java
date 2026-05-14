package com.skylock.ai_cartoon.util;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.skylock.ai_cartoon.R;
import com.skylock.ai_cartoon.callback.ProcessingListener;
import com.skylock.ai_cartoon.databinding.DialogErrorProcessingBinding;

/* loaded from: classes.dex */
public class ErrorProcessingDialog extends DialogFragment {
    private DialogErrorProcessingBinding binding;
    private ProcessingListener processingListener;

    public static ErrorProcessingDialog display(FragmentManager fragmentManager, ProcessingListener processingListener) {
        ErrorProcessingDialog errorProcessingDialog = new ErrorProcessingDialog();
        errorProcessingDialog.processingListener = processingListener;
        errorProcessingDialog.show(fragmentManager, ErrorProcessingDialog.class.getName());
        return errorProcessingDialog;
    }

    @SuppressLint("ResourceType")
    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setStyle(0, 0x7f150037);
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(-2, -2);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            try {
                getDialog().getWindow().setFlags(8, 8);
                getDialog().getWindow().getDecorView().setSystemUiVisibility(5380);
                getDialog().getWindow().clearFlags(1024);
            } catch (Exception unused) {
            }
        }
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        DialogErrorProcessingBinding inflate = DialogErrorProcessingBinding.inflate(layoutInflater, viewGroup, false);
        this.binding = inflate;
        inflate.tvOk.setOnClickListener(new View.OnClickListener() { // from class: mobi.zeezoo.photoenhancer.feature.view.dialog.ErrorProcessingDialog$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ErrorProcessingDialog.this.lambda$onCreateView$0(view);
            }
        });
        this.binding.tvTryAgain.setOnClickListener(new View.OnClickListener() { // from class: mobi.zeezoo.photoenhancer.feature.view.dialog.ErrorProcessingDialog$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ErrorProcessingDialog.this.lambda$onCreateView$1(view);
            }
        });
        return this.binding.getRoot();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateView$0(View view) {
        ProcessingListener processingListener = this.processingListener;
        if (processingListener != null) {
            processingListener.onCancel();
        }
        dismiss();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateView$1(View view) {
        ProcessingListener processingListener = this.processingListener;
        if (processingListener != null) {
            processingListener.onRetry();
        }
        dismiss();
    }

    @Override // androidx.fragment.app.DialogFragment
    public void dismiss() {
        try {
            FragmentActivity activity = getActivity();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            super.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
