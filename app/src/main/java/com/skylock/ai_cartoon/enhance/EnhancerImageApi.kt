package com.skylock.ai_cartoon.enhance

// FILE: enhancer/EnhancerImageApi.kt

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface EnhancerImageApi {
    @Multipart
    @POST("process")
    suspend fun processPhoto(
        @Part file: MultipartBody.Part,
        @Part("url") url: RequestBody,
        @Part("sign") sign: RequestBody,
        @Part("type") type: RequestBody,
        @Part("sub_type") subType: RequestBody,
        @Part("version") version: RequestBody,
        @Part("f_version") fVersion: RequestBody,
        @Part("platform") platform: RequestBody
    ): EnhancerAiResponse

    @POST("get_images")
    suspend fun getImages(
        @Body request: EnhancerImageRequest
    ): EnhancerAiResponse
}