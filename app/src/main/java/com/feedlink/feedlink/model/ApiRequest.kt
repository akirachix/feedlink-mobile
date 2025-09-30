package com.feedlink.feedlink.model

import com.google.gson.annotations.SerializedName


data class SignUpRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("first_name")
    val firstName: String,

    @SerializedName("last_name")
    val lastName: String,

    @SerializedName("role")
    val role: String
)

data class SignInRequest(
    val email: String,
    val password: String
)

data class ForgotPasswordRequest(
    val email: String
)

data class ResetPasswordRequest(
    val email: String,
    val password: String,
    val confirm_password: String
)


data class VerificationRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("otp")
    val otp: String
)

