package com.skylock.ai_cartoon.enhance

// FILE: enhancer/EnhancerModels.kt

data class EnhancerImageRequest(
    val images: List<EnhancerPhotoResponse>
)

data class EnhancerPhotoResponse(
    val id: String,
    val name: String,
    val url: String?,
    val size: EnhancerSizeImage
)

data class EnhancerSizeImage(
    val width: Int,
    val height: Int
)

data class EnhancerAiResponse(
    val code: Int,
    val msg: String,
    val images: List<EnhancerPhotoResponse>?,
    val faces: List<Any>?
)