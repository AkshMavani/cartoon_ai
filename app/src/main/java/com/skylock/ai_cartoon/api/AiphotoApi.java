package com.skylock.ai_cartoon.api;

 import com.skylock.ai_cartoon.model.AiphotoResponse;
 import com.skylock.ai_cartoon.model.GetImageRequest;
 import com.skylock.ai_cartoon.model.HealthResponse;
 import com.skylock.ai_cartoon.model.ImageResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

/* loaded from: classes7.dex */
public interface AiphotoApi {
    @POST("process")
    @Multipart
    Call<AiphotoResponse> cartoon(@Part MultipartBody.Part part, @Part("url") RequestBody requestBody, @Part("style") RequestBody requestBody2, @Part("prompt") RequestBody requestBody3, @Part("strength") RequestBody requestBody4, @Part("platform") RequestBody requestBody5);


    @POST("video2video")
    @Multipart
    Call<AiphotoResponse> genVideoGenerate(@Header("Authorization") String str, @Part("push_token") RequestBody requestBody, @Part MultipartBody.Part part, @Part("style") RequestBody requestBody2, @Part("prompt") RequestBody requestBody3, @Part("model") RequestBody requestBody4, @Part("app_name") RequestBody requestBody5);

    @POST("generate")
    @Multipart
    Call<AiphotoResponse> generate(@Header("Authorization") String str, @Part MultipartBody.Part part, @Part("url") RequestBody requestBody, @Part MultipartBody.Part part2, @Part("style") RequestBody requestBody2, @Part("prompt") RequestBody requestBody3, @Part("n_prompt") RequestBody requestBody4, @Part("strength") RequestBody requestBody5, @Part("gender") RequestBody requestBody6);

    @GET
    Call<ResponseBody> getCountry(@Url String str);

    @GET("health")
    Call<HealthResponse> checkHealth(@Header("Authorization") String str);
    @POST("get_images")
    Call<AiphotoResponse> getPhoto(@Body GetImageRequest getImageRequest);

    @GET("get_video")
    Call<AiphotoResponse> getVideo(@Header("Authorization") String str, @Query("video_name") String str2);

    @POST("process")
    @Multipart
    Call<AiphotoResponse> processPhoto(@Header("Authorization") String str, @Part MultipartBody.Part part, @Part("url") RequestBody requestBody, @Part MultipartBody.Part part2, @Part("masks") RequestBody requestBody2, @Part("sign") RequestBody requestBody3, @Part("type") RequestBody requestBody4, @Part("sub_type") RequestBody requestBody5, @Part("version") Integer num, @Part("f_version") Integer num2, @Part("platform") RequestBody requestBody6);

    @POST("process")
    @Multipart
    Call<AiphotoResponse> processPhoto(@Part MultipartBody.Part part, @Part MultipartBody.Part part2, @Part("url") RequestBody requestBody, @Part MultipartBody.Part part3, @Part("sign") RequestBody requestBody2, @Part("type") RequestBody requestBody3, @Part("sub_type") RequestBody requestBody4, @Part("version") Integer num, @Part("f_version") Integer num2);

    @POST("process")
    @Multipart
    Call<AiphotoResponse> processPhoto(@Part MultipartBody.Part part, @Part("url") RequestBody requestBody, @Part MultipartBody.Part part2, @Part("sign") RequestBody requestBody2, @Part("type") RequestBody requestBody3, @Part("sub_type") RequestBody requestBody4, @Part("version") Integer num, @Part("f_version") Integer num2, @Part("platform") RequestBody requestBody5);

    @POST("process")
    @Multipart
    Call<AiphotoResponse> processPhoto(@Part MultipartBody.Part part, @Part("type") RequestBody requestBody, @Part("platform") RequestBody requestBody2);

    @POST("get_image_version")
    @Multipart
    Call<AiphotoResponse> processVersionPhoto(@Part("images") List<ImageResponse> list, @Part("type") RequestBody requestBody, @Part("version") Integer num);
}
