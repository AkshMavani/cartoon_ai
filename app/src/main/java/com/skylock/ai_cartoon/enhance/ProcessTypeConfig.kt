package com.skylock.ai_cartoon.enhance


import com.skylock.ai_cartoon.util.Feature

object ProcessTypeConfig {
    data class ApiConfig(val baseUrl: String, val apiType: String)

    private val configMap = mapOf(
        Feature.ENHANCE.value to ApiConfig(
            "https://api.enhance.zeezoo.mobi:8081/photo/",
            "enhance"
        ),
        Feature.BRIGHTEN.value to ApiConfig(
            "http://api.colorize.zeezoo.mobi:8080/photo/",
            "brighten"
        ),
        Feature.DEHAZE.value to ApiConfig(
            "http://api.colorize.zeezoo.mobi:8080/photo/",
            "dehaze"
        ),
        Feature.COLORIZE.value to ApiConfig(
            "http://api.colorize.zeezoo.mobi:8080/photo/",
            "colorize"
        ),
        Feature.DESCRATCH.value to ApiConfig(
            "http://api.enhance.zeezoo.mobi:8080/photo/",
            "descratch"
        ),
        Feature.RETOUCH.value to ApiConfig(
            "https://api.enhance.zeezoo.mobi:8081/photo/",
            "retouch"
        ), Feature.RESTORE_OLD_PHOTO.value to ApiConfig(
            "https://api.enhance.zeezoo.mobi:8081/photo/",
            "superrestore"
        )
        // Add other features (REMOVE_BG, SUPER_RESTORE, etc.) if needed
    )

    fun getConfig(feature: String): ApiConfig? = configMap[feature]
}