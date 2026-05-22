package com.skylock.ai_cartoon.remove_obj;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.skylock.ai_cartoon.R;
import com.skylock.ai_cartoon.databinding.BottomDialogDiscardResultBinding;

import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: DialogDiscardResult.kt */

public class DialogDiscardResult extends Dialog {
    public static final int $stable = 8;
    private final Context context;
    private final boolean isFinish;
    private BottomDialogDiscardResultBinding binding;

    public /* synthetic */ DialogDiscardResult(Context context, boolean z, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(context, (i & 2) == 0 && z);
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public DialogDiscardResult(Context context, boolean z) {
        super(context, R.style.SlideDialog);
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
        this.isFinish = z;
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BottomDialogDiscardResultBinding inflate = BottomDialogDiscardResultBinding.inflate(getLayoutInflater());
        Intrinsics.checkNotNullExpressionValue(inflate, "inflate(...)");
        this.binding = inflate;
        BottomDialogDiscardResultBinding bottomDialogDiscardResultBinding = null;
        if (inflate == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            inflate = null;
        }
        setContentView(inflate.getRoot());
        Window window = getWindow();
        View decorView = window != null ? window.getDecorView() : null;
        if (decorView != null) {
            decorView.setSystemUiVisibility(5380);
        }
        BottomDialogDiscardResultBinding bottomDialogDiscardResultBinding2 = this.binding;
        if (bottomDialogDiscardResultBinding2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            bottomDialogDiscardResultBinding2 = null;
        }
        TextView cancel = bottomDialogDiscardResultBinding2.cancel;
        Intrinsics.checkNotNullExpressionValue(cancel, "cancel");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        /* View.setOnSafeClick(cancel, new Function1<View, Unit>() { // from class: mobi.zeezoo.photoenhancer.feature.view.dialog.DialogDiscardResult$onCreate$1
         *//* JADX INFO: Access modifiers changed from: package-private *//*
            {
                super(1);
            }

            @Override // kotlin.jvm.functions.Function1
            public *//* bridge *//* *//* synthetic *//* Unit invoke(View view) {
                invoke2(view);
                return Unit.INSTANCE;
            }

            *//* renamed from: invoke, reason: avoid collision after fix types in other method *//*
            public final void invoke2(View it2) {
                Intrinsics.checkNotNullParameter(it2, "it");
                DialogDiscardResult.this.dismiss();
            }
        });*/
        BottomDialogDiscardResultBinding bottomDialogDiscardResultBinding3 = this.binding;
        if (bottomDialogDiscardResultBinding3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
        } else {
            bottomDialogDiscardResultBinding = bottomDialogDiscardResultBinding3;
        }
        TextView discard = bottomDialogDiscardResultBinding.discard;
        Intrinsics.checkNotNullExpressionValue(discard, "discard");
        discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        /*  ViewExtKt.setOnSafeClick(discard, new Function1<View, Unit>() { // from class: mobi.zeezoo.photoenhancer.feature.view.dialog.DialogDiscardResult$onCreate$2
         *//* JADX INFO: Access modifiers changed from: package-private *//*
            {
                super(1);
            }

            @Override // kotlin.jvm.functions.Function1
            public *//* bridge *//* *//* synthetic *//* Unit invoke(View view) {
                invoke2(view);
                return Unit.INSTANCE;
            }

            *//* renamed from: invoke, reason: avoid collision after fix types in other method *//*
            public final void invoke2(View it2) {
                boolean z;
                Context context;
                Intrinsics.checkNotNullParameter(it2, "it");
                DialogDiscardResult.this.dismiss();
                z = DialogDiscardResult.this.isFinish;
                if (z) {
                    RxBus.getDefault().post(new CloseScreenEvent(true));
                }
                context = DialogDiscardResult.this.context;
                Activity activity = context instanceof Activity ? (Activity) context : null;
                if (activity != null) {
                    activity.finish();
                }
            }
        });*/
    }
}
