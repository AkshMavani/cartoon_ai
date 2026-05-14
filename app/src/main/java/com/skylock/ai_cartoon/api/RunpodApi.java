package com.skylock.ai_cartoon.api;

import com.skylock.ai_cartoon.model.RunpodRequest;
import com.skylock.ai_cartoon.model.RunpodResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RunpodApi {
    @POST("run")
    Call<RunpodResponse> run(@Header("Authorization") String str, @Body RunpodRequest runpodRequest);

    @GET("status/{id}")
    Call<RunpodResponse> status(@Header("Authorization") String str, @Path("id") String str2);
}
