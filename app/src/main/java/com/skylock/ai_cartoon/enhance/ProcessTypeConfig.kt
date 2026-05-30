package com.skylock.ai_cartoon.enhance


import com.skylock.ai_cartoon.util.Feature

object ProcessTypeConfig {
    data class ApiConfig(val baseUrl: String, val apiType: String)

    private val configMap = mapOf(
        Feature.ENHANCE.value to ApiConfig(
            "https://api.enhance.zeezoo.mobi:8081/photo/",
            "enhance"
        )/*<---------------------Done*/,
        Feature.BRIGHTEN.value to ApiConfig(
            "http://api.colorize.zeezoo.mobi:8080/photo/",
            "brighten"
        )/*<---------------------Done*/,
        Feature.DEHAZE.value to ApiConfig(
            "http://api.colorize.zeezoo.mobi:8080/photo/",
            "dehaze"
        )/*<---------------------Done*/,
        Feature.COLORIZE.value to ApiConfig(
            "http://api.colorize.zeezoo.mobi:8080/photo/",
            "colorize"
        )/*<---------------------Done*/,
        Feature.DESCRATCH.value to ApiConfig(
            "http://api.enhance.zeezoo.mobi:8080/photo/",
            "descratch"
        )/*<---------------------Done*/,
        Feature.RETOUCH.value to ApiConfig(
            "https://api.enhance.zeezoo.mobi:8081/photo/",
            "retouch"
        )/*<---------------------Done*/,
        Feature.RESTORE_OLD_PHOTO.value to ApiConfig(
            "https://api.enhance.zeezoo.mobi:8081/photo/",
            "superrestore"
        )/*<---------------------Done*/,
        Feature.BLUR_BG.value to ApiConfig(
            "https://api.enhance.zeezoo.mobi:8081/photo/",
            "blurbg"
        )/*<---------------------Done*/

    )

    fun getConfig(feature: String): ApiConfig? = configMap[feature]
}