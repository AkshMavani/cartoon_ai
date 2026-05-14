package com.skylock.ai_cartoon.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
@Parcelize
data class CartoonStyle(
    val name: String,
    val iconUrl: String,
    val styleKey: String,
    val isPremium: Boolean = false
) : Parcelable
