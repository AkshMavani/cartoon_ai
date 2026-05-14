package com.skylock.ai_cartoon.util;



import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;


/**
 * Ported from Kotlin Feature Enum.
 * Handles app features, their usage limits, and counting.
 */
public enum Feature {
    ENHANCE("enhance"),
    COLORIZE("colorize"),
    DESCRATCH("descratch"),
    BRIGHTEN("brighten"),
    REMOVEOBJ("removeobj"),
    RETOUCH("retouch"),
    AI_FACE_ANIMATION("ai_face_animation"),
    RESTORE_OLD_PHOTO("superrestore"),
    AI_HUGGING(Constants.AI_HUGGING),
    HAIR_STYLE(Constants.HAIR_STYLE),
    AI_FILTER("cartoon"),
    DEHAZE("dehaze"),
    BLUR_BG("blurbg"),
    HEADSHOT("aiavatar");

    private final String value;

    Feature(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Static utility methods (formerly Kotlin Companion Object)
     */
    public static class Companion {

        @Nullable
        public static Feature fromString(@NonNull String value) {
            Objects.requireNonNull(value);
            return Arrays.stream(Feature.values())
                    .filter(f -> f.name().equalsIgnoreCase(value))
                    .findFirst()
                    .orElse(null);
        }

        private static boolean isFeatureLimit(String featureName) {
            Feature feature = fromString(featureName);
            if (feature == null) return false;

            // Equivalent to the WhenMappings/Switch in Kotlin
            switch (feature) {
                case ENHANCE:
                case DESCRATCH:
                case REMOVEOBJ:
                case BRIGHTEN:
                case RETOUCH:
                case DEHAZE:
                case COLORIZE:
                case HAIR_STYLE:
                    return true;
                default:
                    return false;
            }
        }

        public static boolean isLimitFeature(@NonNull String feature) {
           /* Objects.requireNonNull(feature);

            // If user is premium, there are no limits


            String dateKey = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String key = "count_" + feature + "_" + dateKey;

            long usageCount = SharePrefUtils.INSTANCE.getLong(key, 0L);

            Log.d("FEATURE", "isLimitFeature: " + feature + " Count: " + usageCount + " Limit: " + limit);
            return usageCount >= limit;*/
            return false;
        }

        public static void addCountFeature(@NonNull String feature) {
            Objects.requireNonNull(feature);

                @SuppressLint({"NewApi", "LocalSuppress"}) String dateKey = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String key = "count_" + feature + "_" + dateKey;

            long currentCount = SharePrefUtils.INSTANCE.getLong(key, 0L);
            SharePrefUtils.INSTANCE.saveKey(key, currentCount + 1);
        }

        public static boolean isLimitAiHeadshot() {
        /*    if (SharePreferenceRepositoryImpl.getSharedPreferences().getPremium() != null) {
                return false;
            }

            String dateKey = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String key = "count_" + Feature.HEADSHOT.getValue() + "_" + dateKey;

            long usageCount = SharePrefUtils.INSTANCE.getLong(key, 0L);
            long limit = RemoteConfig.INSTANCE.getCartoonLimitUse();

            Log.d("FEATURE", "isLimitAiHeadshot: " + usageCount + " Limit: " + limit);
            return usageCount >= limit;*/
            return  false;
        }




    }
}