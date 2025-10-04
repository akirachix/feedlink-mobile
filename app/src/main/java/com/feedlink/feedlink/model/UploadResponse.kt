package com.feedlink.feedlink.model
import com.google.gson.annotations.SerializedName
data class UploadResponse(
    @SerializedName("url") val url: String
)