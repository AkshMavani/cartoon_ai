package com.skylock.ai_cartoon.textview



import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class TextViewApp : AppCompatTextView {

    companion object {
        private const val ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android"
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        applyCustomFont(context, attributeSet)
    }

    constructor(context: Context, attributeSet: AttributeSet?, i: Int) : super(context, attributeSet, i) {
        applyCustomFont(context, attributeSet)
    }

    private fun applyCustomFont(context: Context, attributeSet: AttributeSet?) {
        attributeSet?.let {
            // Retrieve the textStyle attribute (normal, bold, italic) from the XML
            val textStyle = it.getAttributeIntValue(ANDROID_SCHEMA, "textStyle", 0)

            // Set the custom typeface using your existing FontCache utility
            typeface = FontCache.selectTypeface(context, textStyle)
        }

        // Disable default font padding as per the original Java code
        includeFontPadding = false
    }
}