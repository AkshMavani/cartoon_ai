package com.skylock.ai_cartoon.util;

import android.content.Context;
import androidx.annotation.NonNull;

import com.skylock.ai_cartoon.R;
import com.skylock.ai_cartoon.model.ToolEnhance;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Utility class for handling Photo Enhancer tools and features.
 */
public final class ToolEnhanceUtils {

    // Private constructor to prevent instantiation (Utility Pattern)
    private ToolEnhanceUtils() {}

    /**
     * Returns a list of available enhancement tools.
     */
    @NonNull
    public static ArrayList<ToolEnhance> getListToolEnhance(@NonNull Context context) {
        Objects.requireNonNull(context, "context cannot be null");

        ArrayList<ToolEnhance> list = new ArrayList<>();

        // Enhance Photo
        list.add(new ToolEnhance(
                context.getString(R.string.label_enhance_photo),
                R.drawable.ic_enhance_new,
                Feature.ENHANCE.getValue()
        ));

        // Restore Old Photo
        list.add(new ToolEnhance(
                context.getString(R.string.restore_old_photo_feature),
                R.drawable.ic_restore_old_photo,
                Feature.RESTORE_OLD_PHOTO.getValue()
        ));

        // Remove Object
        list.add(new ToolEnhance(
                context.getString(R.string.label_remove_object),
                R.drawable.ic_remove_object,
                Feature.REMOVEOBJ.getValue()
        ));

        // Beauty Plus / Retouch
        list.add(new ToolEnhance(
                context.getString(R.string.label_beauty_plus),
                R.drawable.ic_beauty,
                Feature.RETOUCH.getValue()
        ));

        // Colorize
        list.add(new ToolEnhance(
                context.getString(R.string.label_colorize),
                R.drawable.ic_colorize_photo,
                Feature.COLORIZE.getValue()
        ));

        // Descratch
        list.add(new ToolEnhance(
                context.getString(R.string.label_descratch),
                R.drawable.ic_remove_sratchs,
                Feature.DESCRATCH.getValue()
        ));

        return list;
    }

    /**
     * Returns the display name for a given feature key.
     */
    @NonNull
    public static String getFeature(@NonNull Context context, @NonNull String featureKey) {
        Objects.requireNonNull(context, "context cannot be null");
        Objects.requireNonNull(featureKey, "featureKey cannot be null");

        switch (featureKey) {
            case "ai_face_animation":
                return "Ai Face Animation";

            case "enhance":
                return context.getString(R.string.enhance);

            case "colorize":
                return context.getString(R.string.colorize);

            case "descratch":
                return context.getString(R.string.descratch);

            case "dehaze":
                return context.getString(R.string.dehaze);

            case "brighten":
                return context.getString(R.string.brighten);

            case "removeobj":
                return context.getString(R.string.removeobj);

            case "retouch":
                return context.getString(R.string.label_beauty_plus);

            case "aiavatar":
                return context.getString(R.string.ai_headshot);

            case "beautifier":
                return context.getString(R.string.face_beautifier);

            default:
                // Handle complex checks that aren't simple constants
                if (featureKey.equals("cartoon") || featureKey.equals(Constants.AI_HUGGING)) {
                    return context.getString(R.string.ai_filter);
                }

                if (featureKey.equals(Constants.HAIR_STYLE)) {
                    return context.getString(R.string.label_ai_hair_color);
                }

                if (featureKey.equals(Feature.RESTORE_OLD_PHOTO.getValue())) {
                    return context.getString(R.string.restore_old_photo);
                }

                // Default fallback
                return context.getString(R.string.enhance);
        }
    }
}