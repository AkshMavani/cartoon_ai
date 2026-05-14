package com.skylock.ai_cartoon.util

import android.content.Context
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearSmoothScroller


class CustomSmoothScroller(context: Context, private val millisecondsPerInch: Float) :
    LinearSmoothScroller(context) {
    // androidx.recyclerview.widget.LinearSmoothScroller
    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
        return this.millisecondsPerInch / displayMetrics.densityDpi
    }
}
