package com.feedlink.feedlink.model

data class SignUpResponse(
    val token: String,
    val email: String,
    val role: String? = null
)
data class SignInResponse(
    val token: String,
    val email: String,
    val role: String? = null
)
data class VerificationResponse(
    val success: Boolean,
    val detail: String,
    val message: String?
)
