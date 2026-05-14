package com.skylock.ai_cartoon.util;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.AppCompatImageView;


import com.bumptech.glide.Glide;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public final class ExtensionKt {

    private ExtensionKt() {}

    public static void handleBackPressed(ComponentActivity activity, Runnable action) {
        if (activity == null || action == null) return;
        activity.getOnBackPressedDispatcher().addCallback(activity, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                action.run();
            }
        });
    }

    public static void loadImage(AppCompatImageView imageView, String url) {
        if (imageView == null) return;
        Glide.with(imageView.getContext())
                .load(url)
                .placeholder(com.skylock.ai_cartoon.R.drawable.place_holder)
                .skipMemoryCache(true)
                .into(imageView);
    }

    public static void loadImage(AppCompatImageView imageView, int resId) {
        if (imageView == null) return;
        Glide.with(imageView.getContext())
                .load(resId)
                .placeholder(com.skylock.ai_cartoon.R.drawable.place_holder)
                .skipMemoryCache(true)
                .into(imageView);
    }

    public static void loadImage(ImageView imageView, String url) {
        if (imageView == null) return;
        Glide.with(imageView.getContext())
                .load(url)
                .placeholder(com.skylock.ai_cartoon.R.drawable.place_holder)
                .skipMemoryCache(true)
                .into(imageView);
    }

    public static void loadImage(ImageView imageView, int resId) {
        if (imageView == null) return;
        Glide.with(imageView.getContext())
                .load(resId)
                .placeholder(com.skylock.ai_cartoon.R.drawable.place_holder)
                .skipMemoryCache(true)
                .into(imageView);
    }

    public static Bitmap convertViewToBitmap(View view) {
        if (view == null) return null;
        float targetWidth = 1920f;
        int targetHeight = (int) (targetWidth / ((float) view.getWidth() / view.getHeight()));
        Bitmap bitmap = Bitmap.createBitmap((int) targetWidth, targetHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        float scale = Math.min(targetWidth / view.getWidth(), (float) targetHeight / view.getHeight());
        canvas.scale(scale, scale);
        view.draw(canvas);
        return bitmap;
    }

    public static String saveBitmapToStorage(Bitmap bitmap, Context context, String fileName) {
        if (bitmap == null || context == null || fileName == null) return null;
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fullName = fileName + timestamp + ".png";

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fullName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

            if (Build.VERSION.SDK_INT >= 29) {
                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
                values.put(MediaStore.Images.Media.IS_PENDING, 1);
            }

            ContentResolver resolver = context.getContentResolver();
            Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri == null) return null;

            try (OutputStream outputStream = resolver.openOutputStream(uri)) {
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                }
            }

            if (Build.VERSION.SDK_INT >= 29) {
                values.clear();
                values.put(MediaStore.Images.Media.IS_PENDING, 0);
                resolver.update(uri, values, null, null);
            }

            return uri.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String saveViewAsImage(View view, Context context) {
        if (view == null || context == null) return null;
        Bitmap bitmap = convertViewToBitmap(view);
        if (bitmap == null) return null;
        return saveBitmapToStorage(bitmap, context, "AiphotoEnhancer_");
    }
}
