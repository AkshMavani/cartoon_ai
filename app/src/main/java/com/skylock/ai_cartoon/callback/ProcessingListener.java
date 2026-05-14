package com.skylock.ai_cartoon.callback;

/* loaded from: classes6.dex */
public interface ProcessingListener {
    default void onCancel() {
    }

    void onRetry();
}
