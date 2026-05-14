package com.skylock.ai_cartoon.util;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.skylock.ai_cartoon.model.DemoLibraryModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharePreferenceRepositoryImpl {

    private static final String KEY_DEMO_LIBRARY = "key_demo_library";
    private static final String KEY_VERSION_MODEL = "key_version_model";

    private static SharePreferenceRepositoryImpl instance;
    private final Gson gson;

    private SharePreferenceRepositoryImpl() {
        this.gson = new Gson();
    }

    public static SharePreferenceRepositoryImpl getSharedPreferences() {
        if (instance == null) {
            instance = new SharePreferenceRepositoryImpl();
        }
        return instance;
    }

    // --- Demo Library Management ---

    public List<DemoLibraryModel> getListDemoLibraryModel() {
        String json = SharePrefUtils.getString(KEY_DEMO_LIBRARY);
        if (json.isEmpty()) return new ArrayList<>();

        Type type = new TypeToken<List<DemoLibraryModel>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public void saveListDemoLibraryModel(List<DemoLibraryModel> list) {
        String json = gson.toJson(list);
        SharePrefUtils.saveKey(KEY_DEMO_LIBRARY, json);
    }


}