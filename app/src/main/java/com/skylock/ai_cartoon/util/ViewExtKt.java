package com.skylock.ai_cartoon.util;


import android.graphics.Rect;
import android.view.View;

import kotlin.jvm.internal.Intrinsics;

public final class ViewExtKt {

    private ViewExtKt() {
        // Private constructor to prevent instantiation of a utility class
    }

    public static void toVisible(View view) {
        Intrinsics.checkNotNullParameter(view, "<this>");
        view.setVisibility(View.VISIBLE); // 0
    }

    public static void toInvisible(View view) {
        Intrinsics.checkNotNullParameter(view, "<this>");
        view.setVisibility(View.INVISIBLE); // 4
    }

    public static void toGone(View view) {
        Intrinsics.checkNotNullParameter(view, "<this>");
        view.setVisibility(View.GONE); // 8
    }

    public static void toVisibleOrGone(View view, boolean show) {
        Intrinsics.checkNotNullParameter(view, "<this>");
        // Replicating Kotlin's exact logic: if show is true (!show is false), visibility is GONE (8)
        view.setVisibility(!show ? View.VISIBLE : View.GONE);
    }

    public static void disableView(final View view) {
        Intrinsics.checkNotNullParameter(view, "<this>");
        view.setClickable(false);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewExtKt.disableView$lambda$0(view);
            }
        }, 300L);
    }

    // Internal private framework lambda mapping hook
    private static void disableView$lambda$0(View this_disableView) {
        Intrinsics.checkNotNullParameter(this_disableView, "$this_disableView");
        this_disableView.setClickable(true);
    }


    public static boolean isSoftKeyboardVisible(View view) {
        Intrinsics.checkNotNullParameter(view, "<this>");
        Rect rect = new Rect();
        view.getRootView().getWindowVisibleDisplayFrame(rect);
        int height = view.getRootView().getHeight();
        return ((double) (height - rect.bottom)) > ((double) height) * 0.15d;
    }

}