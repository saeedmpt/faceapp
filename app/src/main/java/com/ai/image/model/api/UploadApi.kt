package com.ai.chatapp.model.api

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class UploadApi {
    @SerializedName(value = "pic0", alternate = ["pic1", "pic2", "pic3", "pic4"])
    @Expose
    val path: String? = null

    @SerializedName("is_verified")
    @Expose
    val is_verified: String? = null
}