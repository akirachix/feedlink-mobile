package com.feedlink.feedlink.api

import com.feedlink.feedlink.model.ForgotPasswordRequest
import com.feedlink.feedlink.model.ResetPasswordRequest
import com.feedlink.feedlink.model.SignInRequest
import com.feedlink.feedlink.model.SignInResponse
import com.feedlink.feedlink.model.SignUpRequest
import com.feedlink.feedlink.model.SignUpResponse
import com.feedlink.feedlink.model.VerificationResponse
import com.feedlink.feedlink.model.VerificationRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface ApiInterface {

    @POST("signup/")
    suspend fun signup(@Body request: SignUpRequest): Response<SignUpResponse>

    @POST("login/")
    suspend fun login(@Body request: SignInRequest): Response<SignInResponse>

    @POST("forgotpassword/")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<VerificationResponse>

    @POST("reset/")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<VerificationResponse>

    @POST("verification/")
    suspend fun verification(@Body request: VerificationRequest): Response<VerificationResponse>
}