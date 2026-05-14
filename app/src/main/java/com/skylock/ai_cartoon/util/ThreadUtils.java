package com.skylock.ai_cartoon.util;


import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.schedulers.Schedulers;

/* loaded from: classes6.dex */
public class ThreadUtils {
    public static void runOnMainThread(Action action) {
        Completable.fromAction(action).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread()).subscribe();
    }

    public static void ioThread(Action action) {
        Completable.fromAction(action).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();
    }
}
