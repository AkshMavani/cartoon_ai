package com.skylock.ai_cartoon.api;

import android.util.Log;

import com.skylock.ai_cartoon.util.AiphotoService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RunpodService {

    private static final String TAG = "RunpodService";
    private static final String DEFAULT_URL = "https://api.enhance.zeezoo.mobi:8081/photo/";

    private RunpodService() {}

    public static RunpodApi getService(String type) {
        String baseUrl;

        if ("enhance".equals(type)) {
            baseUrl = AiphotoService.ENHANCE_URL;
        } else if ("cartoon".equals(type)) {
            baseUrl = AiphotoService.CARTOON_URL;
        } else {
            baseUrl = DEFAULT_URL;
        }

        Log.i(TAG, "API URL: " + baseUrl);

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(getRequestHeader())
                .build()
                .create(RunpodApi.class);
    }

    private static OkHttpClient getRequestHeader() {
        return new OkHttpClient.Builder()
                .readTimeout(60L, TimeUnit.SECONDS)
                .connectTimeout(60L, TimeUnit.SECONDS)
                .writeTimeout(20L, TimeUnit.SECONDS)
                .build();
    }
}
