package com.skylock.ai_cartoon.remove_obj

import com.google.gson.annotations.SerializedName
import com.skylock.ai_cartoon.model.Representation
import java.io.Serializable

class MaskRemoveResponse(
    @SerializedName("masks")
    var masks: List<MaskRemove>? = null,

    @SerializedName("code")
    var code: Int = 0
) : Representation(), Serializable