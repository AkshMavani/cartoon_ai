package com.skylock.ai_cartoon.remove_obj


import com.google.gson.annotations.SerializedName
import com.skylock.ai_cartoon.model.Representation
import java.io.Serializable

class MaskRemove : Representation(), Serializable {

    @SerializedName("boxes")
    var boxes: List<Float>? = null

    var id: Int = 0
    var isDisable: Boolean = false

    @SerializedName("mask_url")
    var maskUrl: String? = null

    @SerializedName("obj_url")
    var objUrl: String? = null

    var selected: Boolean = false
}
