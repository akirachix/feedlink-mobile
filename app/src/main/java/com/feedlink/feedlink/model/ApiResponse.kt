package com.feedlink.feedlink.model

import com.google.gson.annotations.SerializedName

data class SignUpResponse(
    val token: String,
    val email: String,
    val role: String? = null,
    @SerializedName("user_id")

    val userId: String
)

data class SignInResponse(
    val token: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    val email: String
)
data class VerificationResponse(
    val success: Boolean,
    val detail: String,
    val message: String?
)