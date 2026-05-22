package com.skylock.ai_cartoon.enhance

// FILE: enhancer/RetrofitClient.kt


import com.skylock.ai_cartoon.util.ClientBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    fun createApi(baseUrl: String): EnhancerImageApi {
        val client = ClientBuilder.INSTANCE.unsafeOkHttpClient.build()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EnhancerImageApi::class.java)
    }
}