package com.skylock.ai_cartoon.util;

import android.content.Context;
import android.content.SharedPreferences;

import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

public final class SharePrefUtils {
    private static final String PREF_NAME = "AppName";
    private static SharedPreferences sharePref;
    public static final SharePrefUtils INSTANCE = new SharePrefUtils();
    public static final int $stable = 8;

    private SharePrefUtils() {
    }

    public final void init(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        if (sharePref != null) {
            return;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, 0);
        Intrinsics.checkNotNullExpressionValue(sharedPreferences, "getSharedPreferences(...)");
        sharePref = sharedPreferences;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static  <T> void saveKey(String key, T value) {
        Intrinsics.checkNotNullParameter(key, "key");
        SharedPreferences sharedPreferences = null;
        if (value instanceof String) {
            SharedPreferences sharedPreferences2 = sharePref;
            if (sharedPreferences2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("sharePref");
            } else {
                sharedPreferences = sharedPreferences2;
            }
            sharedPreferences.edit().putString(key, (String) value).apply();
            return;
        }
        if (value instanceof Integer) {
            SharedPreferences sharedPreferences3 = sharePref;
            if (sharedPreferences3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("sharePref");
            } else {
                sharedPreferences = sharedPreferences3;
            }
            sharedPreferences.edit().putInt(key, ((Number) value).intValue()).apply();
            return;
        }
        if (value instanceof Boolean) {
            SharedPreferences sharedPreferences4 = sharePref;
            if (sharedPreferences4 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("sharePref");
            } else {
                sharedPreferences = sharedPreferences4;
            }
            sharedPreferences.edit().putBoolean(key, ((Boolean) value).booleanValue()).apply();
            return;
        }
        if (value instanceof Long) {
            SharedPreferences sharedPreferences5 = sharePref;
            if (sharedPreferences5 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("sharePref");
            } else {
                sharedPreferences = sharedPreferences5;
            }
            sharedPreferences.edit().putLong(key, ((Number) value).longValue()).apply();
            return;
        }
        if (value instanceof Float) {
            SharedPreferences sharedPreferences6 = sharePref;
            if (sharedPreferences6 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("sharePref");
            } else {
                sharedPreferences = sharedPreferences6;
            }
            sharedPreferences.edit().putFloat(key, ((Number) value).floatValue()).apply();
        }
    }

    public static String getString(String key) {
        String obj;
        Intrinsics.checkNotNullParameter(key, "key");
        SharedPreferences sharedPreferences = sharePref;
        if (sharedPreferences == null) {
            Intrinsics.throwUninitializedPropertyAccessException("sharePref");
            sharedPreferences = null;
        }
        String string = sharedPreferences.getString(key, "");
        return (string == null || (obj = StringsKt.trim((CharSequence) string).toString()) == null) ? "" : obj;
    }

    public static /* synthetic */ int getInt$default(SharePrefUtils sharePrefUtils, String str, int i, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            i = 0;
        }
        return sharePrefUtils.getInt(str, i);
    }

    public final int getInt(String key, int defaultValue) {
        Intrinsics.checkNotNullParameter(key, "key");
        SharedPreferences sharedPreferences = sharePref;
        if (sharedPreferences == null) {
            Intrinsics.throwUninitializedPropertyAccessException("sharePref");
            sharedPreferences = null;
        }
        return sharedPreferences.getInt(key, defaultValue);
    }

    public static /* synthetic */ boolean getBoolean$default(SharePrefUtils sharePrefUtils, String str, boolean z, int i, Object obj) {
        if ((i & 2) != 0) {
            z = false;
        }
        return sharePrefUtils.getBoolean(str, z);
    }

    public final boolean getBoolean(String key, boolean defaultValue) {
        Intrinsics.checkNotNullParameter(key, "key");
        SharedPreferences sharedPreferences = sharePref;
        if (sharedPreferences == null) {
            Intrinsics.throwUninitializedPropertyAccessException("sharePref");
            sharedPreferences = null;
        }
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public static /* synthetic */ long getLong$default(SharePrefUtils sharePrefUtils, String str, long j, int i, Object obj) {
        if ((i & 2) != 0) {
            j = 0;
        }
        return sharePrefUtils.getLong(str, j);
    }

    public final long getLong(String key, long defaultValue) {
        Intrinsics.checkNotNullParameter(key, "key");
        SharedPreferences sharedPreferences = sharePref;
        if (sharedPreferences == null) {
            Intrinsics.throwUninitializedPropertyAccessException("sharePref");
            sharedPreferences = null;
        }
        return sharedPreferences.getLong(key, defaultValue);
    }

    public static /* synthetic */ float getFloat$default(SharePrefUtils sharePrefUtils, String str, float f, int i, Object obj) {
        if ((i & 2) != 0) {
            f = 0.0f;
        }
        return sharePrefUtils.getFloat(str, f);
    }

    public final float getFloat(String key, float defaultValue) {
        Intrinsics.checkNotNullParameter(key, "key");
        SharedPreferences sharedPreferences = sharePref;
        if (sharedPreferences == null) {
            Intrinsics.throwUninitializedPropertyAccessException("sharePref");
            sharedPreferences = null;
        }
        return sharedPreferences.getFloat(key, defaultValue);
    }
    public static void setGenderString(String key, String value){
        SharedPreferences sharedPreferences=sharePref;
        sharedPreferences.edit().putString(key, (String) value).apply();
    }
    public static String getGenderString(String key) {
        // 1. Check if sharePref is initialized to avoid NullPointerException
        if (sharePref == null) {
            return "";
        }

        // 2. Retrieve the string. The second parameter "" is the default value.
        String value = sharePref.getString(key, "");

        // 3. Return the value, safely handling potential nulls
        return (value != null) ? value : "";
    }
}
