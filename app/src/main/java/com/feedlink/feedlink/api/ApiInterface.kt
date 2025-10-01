package com.feedlink.feedlink.api

import com.feedlink.feedlink.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    // === Listings Endpoints ===
    @GET("listings/")
    suspend fun fetchListings(
        @Query("latitude") latitude: Double?,
        @Query("longitude") longitude: Double?
    ): Response<List<Listing>>

    @GET("listings/{id}/")
    suspend fun fetchProductDetail(
        @Path("id") listingId: Int
    ): Response<Listing>

    // === Auth Endpoints ===
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