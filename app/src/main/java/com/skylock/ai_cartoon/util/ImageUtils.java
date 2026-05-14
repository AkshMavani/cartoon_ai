package com.skylock.ai_cartoon.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.skylock.ai_cartoon.R;
import com.skylock.ai_cartoon.model.DemoLibraryModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageUtils {

    public static List<DemoLibraryModel> getDemoLibrary(Context context) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(getImageDemoFromDrawable(context, "enhance", R.drawable.demo_enhance_before_1, R.drawable.demo_enhance_after_1, R.drawable.demo_enhance_preview_1));
        arrayList.add(getImageDemoFromDrawable(context, "enhance", R.drawable.demo_enhance_before_2, R.drawable.demo_enhance_after_2, R.drawable.demo_enhance_preview_2));
        arrayList.add(getImageDemoFromDrawable(context, "descratch", R.drawable.demo_descratch_before_1, R.drawable.demo_descratch_after_1, R.drawable.demo_descratch_preview_1));
        arrayList.add(getImageDemoFromDrawable(context, "descratch", R.drawable.demo_descratch_before_2, R.drawable.demo_descratch_after_2, R.drawable.demo_descratch_preview_2));
        arrayList.add(getImageDemoFromDrawable(context, "colorize", R.drawable.demo_colorize_before_1, R.drawable.demo_colorize_after_1, R.drawable.demo_colorize_preview_1));
        arrayList.add(getImageDemoFromDrawable(context, "colorize", R.drawable.demo_colorize_before_2, R.drawable.demo_colorize_after_2, R.drawable.demo_colorize_preview_2));
        arrayList.add(getImageDemoFromDrawable(context, "retouch", R.drawable.demo_retouch_before_1, R.drawable.demo_retouch_after_1, R.drawable.demo_retouch_preview_1));
        arrayList.add(getImageDemoFromDrawable(context, "retouch", R.drawable.demo_retouch_before_2, R.drawable.demo_retouch_after_2, R.drawable.demo_retouch_preview_2));
        return arrayList;
    }
    public static DemoLibraryModel getImageDemoFromDrawable(Context context, String str, int i, int i2, int i3) {
        File bitmapConvertToFile = SaveFileUtils.bitmapConvertToFile(context, BitmapFactory.decodeResource(context.getResources(), i), "jpg");
        Bitmap decodeResource = BitmapFactory.decodeResource(context.getResources(), i2);
        File bitmapConvertToFile2 = SaveFileUtils.bitmapConvertToFile(context, decodeResource, "jpg");
        File bitmapConvertToFile3 = SaveFileUtils.bitmapConvertToFile(context, BitmapFactory.decodeResource(context.getResources(), i3), "jpg");
        Log.i("FILE", "FIle uri: " + bitmapConvertToFile3.getAbsolutePath());
        DemoLibraryModel demoLibraryModel = new DemoLibraryModel(str, "file://" + bitmapConvertToFile.getAbsolutePath(), "file://" + bitmapConvertToFile2.getAbsolutePath(), "file://" + bitmapConvertToFile3.getAbsolutePath(), Integer.valueOf(decodeResource.getWidth()), Integer.valueOf(decodeResource.getHeight()));
        System.out.println("demoLibraryModel" + demoLibraryModel.getAfter());
        return demoLibraryModel;
    }

}
