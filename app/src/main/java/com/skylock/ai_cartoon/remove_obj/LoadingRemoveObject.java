package com.skylock.ai_cartoon.remove_obj;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skylock.ai_cartoon.R;
import com.skylock.ai_cartoon.callback.ProcessingFinishListener;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;

/* loaded from: classes6.dex */
public class LoadingRemoveObject extends Dialog {
    private final Context context;
    private final Handler timerHandler;
    Logger logger;
    private Disposable disposable;
    private LinearLayout itemContent;
    private int progress;
    private TextView textContent;
    private TextView textLoading;
    private Runnable timerRunnable;

    public LoadingRemoveObject(Context context) {
        super(context);
        this.logger = Logger.getLogger(LoadingRemoveObject.class.getName());
        this.timerHandler = new Handler();
        this.context = context;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$onDismissLoading$2(ProcessingFinishListener processingFinishListener, Throwable th) {
        Log.e("Interval", "Error", th);
        if (processingFinishListener != null) {
            processingFinishListener.onFinish();
        }
    }

    public void updateLoadingRemoveAuto(String str) {
        this.textContent.setText(str);
        this.textLoading.setVisibility(View.GONE);
        this.itemContent.setVisibility(View.VISIBLE);
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.dialog_loading_remove_object);
        setCanceledOnTouchOutside(false);
        this.textContent = findViewById(R.id.contentLoading);
        this.textLoading = findViewById(R.id.tvLoading);
        this.itemContent = findViewById(R.id.itemContent);
        if (getWindow() != null) {
            WindowManager.LayoutParams attributes = getWindow().getAttributes();
            attributes.width = -1;
            attributes.dimAmount = 0.5f;
            getWindow().addFlags(2);
            getWindow().setAttributes(attributes);
        }
        this.itemContent.setVisibility(View.GONE);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
    }

    @Override // android.app.Dialog
    public void show() {
        this.logger.info("Loading.....");
        try {
            getWindow().setFlags(8, 8);
            Activity activity = (Activity) this.context;
            if (activity == null || activity.isFinishing()) {
                return;
            }
            super.show();
            getWindow().getDecorView().setSystemUiVisibility(5380);
            getWindow().clearFlags(8);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        try {
            Activity activity = (Activity) this.context;
            if (activity == null || activity.isFinishing()) {
                return;
            }
            super.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDismissLoading(final ProcessingFinishListener processingFinishListener) {
        Runnable runnable;
        System.out.println("imageResultUrl11111");
        Handler handler = this.timerHandler;
        if (handler != null && (runnable = this.timerRunnable) != null) {
            handler.removeCallbacks(runnable);
        }
        int i = this.progress;
        long j = i < 70 ? 50L : 150L;
        if (i != 0 && i < 100) {
            this.disposable = Observable.interval(j, j, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() { // from class: mobi.zeezoo.photoenhancer.feature.view.LoadingRemoveObject$$ExternalSyntheticLambda2
                @Override // io.reactivex.rxjava3.functions.Consumer
                public void accept(Object obj) {
                    LoadingRemoveObject.this.lambda$onDismissLoading$1(processingFinishListener, (Long) obj);
                }
            }, new Consumer() { // from class: mobi.zeezoo.photoenhancer.feature.view.LoadingRemoveObject$$ExternalSyntheticLambda3
                @Override // io.reactivex.rxjava3.functions.Consumer
                public void accept(Object obj) {
                    LoadingRemoveObject.lambda$onDismissLoading$2(processingFinishListener, (Throwable) obj);
                }
            });
        } else if (processingFinishListener != null) {
            processingFinishListener.onFinish();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onDismissLoading$1(ProcessingFinishListener processingFinishListener, Long l) {
        int i = this.progress;
        if (i < 100) {
            int i2 = i + 5;
            this.progress = i2;
            if (i2 > 100) {
                this.progress = 100;
            }
            ThreadUtils.runOnMainThread(new Action() { // from class: mobi.zeezoo.photoenhancer.feature.view.LoadingRemoveObject$$ExternalSyntheticLambda1
                @Override // io.reactivex.functions.Action
                public void run() {
                    LoadingRemoveObject.this.lambda$onDismissLoading$0();
                }
            });
            return;
        }
        cancelDisposable();
        dismiss();
        if (processingFinishListener != null) {
            processingFinishListener.onFinish();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onDismissLoading$0() {
        this.textLoading.setText(this.context.getString(R.string.label_title_remove_obj) + " " + this.progress + "%");
    }

    private void cancelDisposable() {
        Disposable disposable = this.disposable;
        if (disposable != null && !disposable.isDisposed()) {
            this.disposable.dispose();
        }
        this.progress = 0;
        com.skylock.ai_cartoon.remove_obj.ThreadUtils.runOnMainThread(new Action() { // from class: mobi.zeezoo.photoenhancer.feature.view.LoadingRemoveObject$$ExternalSyntheticLambda0
            @Override // io.reactivex.functions.Action
            public void run() {
                LoadingRemoveObject.this.lambda$cancelDisposable$3();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$cancelDisposable$3() {
        this.itemContent.setVisibility(View.GONE);
    }

    public void showProcessing() {
        this.timerRunnable = new Runnable() { // from class: mobi.zeezoo.photoenhancer.feature.view.LoadingRemoveObject.1
            @Override // java.lang.Runnable
            public void run() {
                LoadingRemoveObject.this.progress++;
                if (LoadingRemoveObject.this.progress <= 98) {
                    LoadingRemoveObject.this.textLoading.setText(LoadingRemoveObject.this.context.getString(R.string.label_title_remove_obj) + " " + LoadingRemoveObject.this.progress + "%");
                }
                LoadingRemoveObject.this.timerHandler.postDelayed(this, 300L);
            }
        };
        this.textContent.setText(getContext().getString(R.string.label_content_remove_obj_server));
        this.timerHandler.postDelayed(this.timerRunnable, 0L);
        this.itemContent.setVisibility(View.VISIBLE);
        this.textLoading.setVisibility(View.VISIBLE);
        show();
    }
}
