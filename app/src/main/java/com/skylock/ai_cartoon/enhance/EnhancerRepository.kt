package com.skylock.ai_cartoon.enhance


import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

// FILE: enhance/EnhancerRepository.kt


class EnhancerRepository(private val baseUrl: String) {

    private val api = RetrofitClient.createApi(baseUrl)

    suspend fun processPhoto(file: File, sign: String, type: String): EnhancerAiResponse {
        val filePart = file.toMultipartPart("file")
        return api.processPhoto(
            file = filePart,
            url = "".toTextBody(),
            sign = sign.toTextBody(),
            type = type.toTextBody(),
            subType = "default".toTextBody(),
            version = "1".toTextBody(),
            fVersion = "1".toTextBody(),
            platform = "android".toTextBody()
        )
    }

    suspend fun getImages(id: String, name: String, width: Int, height: Int): EnhancerAiResponse {
        val request = EnhancerImageRequest(
            images = listOf(
                EnhancerPhotoResponse(
                    id = id,
                    name = name,
                    url = "",
                    size = EnhancerSizeImage(width, height)
                )
            )
        )
        return api.getImages(request)
    }

    private fun File.toMultipartPart(fieldName: String): MultipartBody.Part {
        val requestFile = asRequestBody("image/*".toMediaType())
        return MultipartBody.Part.createFormData(fieldName, name, requestFile)
    }

    private fun String.toTextBody(): RequestBody = toRequestBody("text/plain".toMediaType())
}