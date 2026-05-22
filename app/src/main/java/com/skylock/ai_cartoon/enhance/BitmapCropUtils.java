package com.skylock.ai_cartoon.enhance;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Utility class for bitmap cropping and manipulation
 */
public class BitmapCropUtils {

    /**
     * Crop bitmap to specified dimensions
     */
    public static Bitmap crop(Bitmap source, int targetWidth, int targetHeight, float offsetX, float offsetY) {
        if (source == null) {
            return null;
        }

        try {
            int sourceWidth = source.getWidth();
            int sourceHeight = source.getHeight();

            if (targetWidth <= 0 || targetHeight <= 0) {
                return source;
            }

            // Calculate scale to fit the target size while maintaining aspect ratio
            float scaleX = (float) targetWidth / sourceWidth;
            float scaleY = (float) targetHeight / sourceHeight;
            float scale = Math.min(scaleX, scaleY);

            // Calculate the scaled dimensions
            int scaledWidth = Math.round(sourceWidth * scale);
            int scaledHeight = Math.round(sourceHeight * scale);

            // Create transformation matrix
            Matrix matrix = new Matrix();
            matrix.setScale(scale, scale);

            // Create the scaled bitmap
            Bitmap scaledBitmap = Bitmap.createBitmap(source, 0, 0, sourceWidth, sourceHeight, matrix, true);

            // If the scaled bitmap is exactly the target size, return it
            if (scaledWidth == targetWidth && scaledHeight == targetHeight) {
                return scaledBitmap;
            }

            // Otherwise, crop to exact target size
            int cropX = Math.max(0, (scaledWidth - targetWidth) / 2);
            int cropY = Math.max(0, (scaledHeight - targetHeight) / 2);

            // Apply offset if provided
            cropX += (int) offsetX;
            cropY += (int) offsetY;

            // Ensure crop coordinates are within bounds
            cropX = Math.max(0, Math.min(cropX, scaledWidth - targetWidth));
            cropY = Math.max(0, Math.min(cropY, scaledHeight - targetHeight));

            Bitmap croppedBitmap = Bitmap.createBitmap(scaledBitmap, cropX, cropY, targetWidth, targetHeight);

            // Clean up intermediate bitmap if it's different from source
            if (scaledBitmap != source && scaledBitmap != croppedBitmap) {
                scaledBitmap.recycle();
            }

            return croppedBitmap;

        } catch (Exception e) {
            e.printStackTrace();
            return source;
        }
    }

    /**
     * Scale bitmap to fit within target dimensions while maintaining aspect ratio
     */
    public static Bitmap scaleToFit(Bitmap source, int maxWidth, int maxHeight) {
        if (source == null) {
            return null;
        }

        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        if (sourceWidth <= maxWidth && sourceHeight <= maxHeight) {
            return source;
        }

        float scale = Math.min((float) maxWidth / sourceWidth, (float) maxHeight / sourceHeight);

        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);

        return Bitmap.createBitmap(source, 0, 0, sourceWidth, sourceHeight, matrix, true);
    }

    /**
     * Create a center cropped bitmap
     */
    public static Bitmap centerCrop(Bitmap source, int targetWidth, int targetHeight) {
        return crop(source, targetWidth, targetHeight, 0, 0);
    }
}