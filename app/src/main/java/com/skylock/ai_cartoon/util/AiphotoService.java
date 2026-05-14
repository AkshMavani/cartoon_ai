package com.skylock.ai_cartoon.util;

import android.util.Log;

import com.skylock.ai_cartoon.api.AiphotoApi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AiphotoService {
    public static String AI_FACE_URL = "https://api.aicartoon.zeezoo.mobi:8081/photo/";
    public static String AI_VIDEO_URL = "https://api.aivideo.zeezoo.mobi:8081/video/";
    static final String BASE_URL = "https://api.enhance.zeezoo.mobi:8081/photo/";
    public static String CARTOON_NEW_URL = "not";
    public static String CARTOON_URL = "https://api.aicartoon.zeezoo.mobi:8081/cartoon/";
    //https://api.aicartoon.zeezoo.mobi:8081/cartoon/
    static final String COLORIZE_URL = "http://api.colorize.zeezoo.mobi:8080/photo/";
    public static String ENHANCE_URL = "https://api.enhance.zeezoo.mobi:8081/photo/";
    static final String REMOVEBG_URL = "http://api.removebg.zeezoo.mobi:8080/photo/";
    public static String REMOVEOBJ_URL = "https://api.removeobj.zeezoo.mobi:8081/photo/";
    public static String SUPER_RESTORE_URL = "https://api.enhance.zeezoo.mobi:8081/photo/";

    public static List<String> VIP_URLS = new ArrayList();

    public static AiphotoApi getService(String str) {
        String str2;
        if (str.equals("enhance") || str.equals("descratch")) {
            str2 = ENHANCE_URL;
        } else if (str.equals("colorize") || str.equals("brighten") || str.equals("dehaze")) {
            str2 = COLORIZE_URL;
        } else if (str.equals("removebg") || str.equals("blurbg")) {
            str2 = REMOVEBG_URL;
        } else if (str.equals("cartoon") || str.equals(Constants.HAIR_STYLE)) {
            str2 = CARTOON_URL;
        } else if (str.equals("removeobj")) {
            str2 = REMOVEOBJ_URL;
        } else if (str.equals("ai_video")) {
            str2 = AI_VIDEO_URL;
        } else if (str.equals("ai_face_animation")) {
            str2 = AI_FACE_URL;
        } else if (!str.equals(Feature.RESTORE_OLD_PHOTO.getValue())) {
            str2 = BASE_URL;
        } else {
            str2 = SUPER_RESTORE_URL;
        }
         return (AiphotoApi) new Retrofit.Builder().baseUrl(str2).addConverterFactory(GsonConverterFactory.create()).client(getRequestHeaderSSL()).build().create(AiphotoApi.class);
    }


    private static OkHttpClient getRequestHeader() {
        return ClientBuilder.INSTANCE.getUnsafeOkHttpClient().addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).readTimeout(60L, TimeUnit.SECONDS).connectTimeout(60L, TimeUnit.SECONDS).writeTimeout(20L, TimeUnit.SECONDS).build();
    }

    private static OkHttpClient getRequestHeaderSSL() {
        return ClientBuilder.INSTANCE.getUnsafeOkHttpClient().addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).readTimeout(60L, TimeUnit.SECONDS).connectTimeout(60L, TimeUnit.SECONDS).writeTimeout(20L, TimeUnit.SECONDS).build();
    }

    public static AiphotoApi getServiceUrl(String str) {
        return (AiphotoApi) new Retrofit.Builder().baseUrl(str).addConverterFactory(GsonConverterFactory.create()).client(getRequestHeader()).build().create(AiphotoApi.class);
    }
}

