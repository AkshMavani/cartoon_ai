package com.skylock.ai_cartoon.util;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public final class SaveFileUtils {

    private SaveFileUtils() {}

    public static final File FolderPathShow =
            new File(Environment.getExternalStorageDirectory() + "/Pictures");

    public static void createFolder() {
        if (!FolderPathShow.exists()) {
            FolderPathShow.mkdirs();
        }
    }

    public static void shareImage(Context context, String imagePath) {
        if (context == null || imagePath == null) return;
        try {
            String insertedUri = MediaStore.Images.Media.insertImage(
                    context.getContentResolver(), imagePath, "", null
            );
            if (insertedUri == null) return;

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, "Libs");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(insertedUri));
            intent.setType("image/*");
            context.startActivity(Intent.createChooser(intent, "Libs"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File saveBitmapFileEditor(
            Context context,
            Bitmap bitmap,
            String fileName,
            String extension
    ) throws IOException {
        if (context == null || bitmap == null || fileName == null || extension == null) {
            throw new IOException("Invalid parameters for saveBitmapFileEditor");
        }

        File outputDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (outputDir == null) {
            outputDir = context.getCacheDir();
        }
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        File outputFile = new File(outputDir, fileName + "." + extension);
        Bitmap.CompressFormat format = extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg")
                ? Bitmap.CompressFormat.JPEG
                : Bitmap.CompressFormat.PNG;

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            bitmap.compress(format, 100, fos);
            fos.flush();
        }

        return outputFile;
    }

    public static File bitmapConvertToFile(Context context, Bitmap bitmap, String extension) {
        if (context == null || bitmap == null || extension == null) return null;

        clearApplicationData(context);

        File outputFile = new File(
                context.getCacheDir(),
                "temp" + System.currentTimeMillis() + "." + extension
        );

        Bitmap.CompressFormat format = extension.equals("jpg")
                ? Bitmap.CompressFormat.JPEG
                : Bitmap.CompressFormat.PNG;

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            bitmap.compress(format, 100, fos);
            fos.flush();
            return outputFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void clearApplicationData(Context context) {
        if (context == null) return;
        File cacheDir = context.getCacheDir();
        String[] files = cacheDir.list();
        if (files == null) return;

        for (String name : files) {
            File file = new File(cacheDir, name);
            if (file.getAbsolutePath().contains("lib_ai_face")) {
                deleteDir(file);
            }
        }
    }

    public static boolean deleteDir(File file) {
        if (file == null) return false;

        if (file.isDirectory()) {
            String[] children = file.list();
            if (children != null) {
                for (String child : children) {
                    if (!deleteDir(new File(file, child))) {
                        return false;
                    }
                }
            }
        }
        return file.delete();
    }

    private static boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }
}