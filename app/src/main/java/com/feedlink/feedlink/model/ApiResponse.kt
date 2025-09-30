package com.feedlink.feedlink.model

data class SignUpResponse(
    val token: String,
    val email: String,
    val role: String
)
data class SignInResponse(
    val token: String,
    val email: String,
    val role: String
)
data class VerificationResponse(
    val success: Boolean,
    val detail: String,
    val message: String?
)
