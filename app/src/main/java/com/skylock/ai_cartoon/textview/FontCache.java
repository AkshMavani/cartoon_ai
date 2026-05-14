package com.skylock.ai_cartoon.textview;

import android.content.Context;
import android.graphics.Typeface;
import java.util.HashMap;

/* loaded from: classes8.dex */
public class FontCache {
    private static HashMap<String, Typeface> sFontCache = new HashMap<>();

    private static Typeface getTypeface(String str, Context context) {
        Typeface typeface = sFontCache.get(str);
        if (typeface == null) {
            try {
                typeface = Typeface.createFromAsset(context.getAssets(), str);
                sFontCache.put(str, typeface);
            } catch (Exception unused) {
                return null;
            }
        }
        return typeface;
    }

    public static Typeface selectTypeface(Context context, int i) {
        if (i == 1) {
            return getTypeface("fonts/roboto_bold.ttf", context);
        }
        if (i == 2) {
            return getTypeface("fonts/Urbanist-Italic.ttf", context);
        }
        if (i == 3) {
            return getTypeface("fonts/Urbanist-BoldItalic.ttf", context);
        }
        return getTypeface("fonts/roboto.ttf", context);
    }

    public static Typeface selectBottomTextViewTypeface(Context context) {
        return getTypeface("fonts/roboto_medium.ttf", context);
    }
}
