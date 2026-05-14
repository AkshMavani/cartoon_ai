package com.skylock.ai_cartoon.base;


import android.app.Application;
import android.content.Context;

import com.skylock.ai_cartoon.util.SharePrefUtils;
import com.skylock.ai_cartoon.model.DemoLibraryModel;
import com.skylock.ai_cartoon.util.Constants;
import com.skylock.ai_cartoon.util.EmptyUtils;
import com.skylock.ai_cartoon.util.ImageUtils;
import com.skylock.ai_cartoon.util.SharePreferenceRepositoryImpl;

import java.util.List;

public class MyApp extends Application {

    private static MyApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Initialize global things here
        SharePrefUtils.INSTANCE.init(this);
        initializeDemoLibrary(this);

    }
    public void initializeDemoLibrary(Context context) {
        // 1. Get current data from repository
        List<DemoLibraryModel> listDemoLibraryModel = SharePreferenceRepositoryImpl.getSharedPreferences().getListDemoLibraryModel();


        // 3. Refresh library if empty or update is required
        if (EmptyUtils.isEmpty(listDemoLibraryModel)) {
            listDemoLibraryModel = ImageUtils.getDemoLibrary(context);
            System.out.println("DEMO_LIBRARY Refreshed: " + listDemoLibraryModel.size());

            // Save back to preferences
            SharePreferenceRepositoryImpl.getSharedPreferences().saveListDemoLibraryModel(listDemoLibraryModel);
        }

        // 4. Update global constant
        Constants.DEMO_LIBRARY = listDemoLibraryModel;
    }
    public static MyApp getInstance() {
        return instance;
    }
}