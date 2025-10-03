package com.feedlink.feedlink.api

<<<<<<< HEAD
import com.feedlink.feedlink.model.Listing
import com.feedlink.feedlink.model.StatusRequest
import com.feedlink.feedlink.model.WasteClaim
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiInterface {
        @GET("listings/")
        suspend fun getAvailableListings(): List<Listing>

        @GET("wasteclaims/")
        suspend fun getWasteClaims(): List<WasteClaim>

        @GET("wasteclaims/{id}")
        suspend fun getWasteClaimById(@Path("id") claimId: Int): WasteClaim

        @POST("wasteclaims/")
        suspend fun createWasteClaim(@Body claim: WasteClaim): WasteClaim

        @PUT("waste-claims/{id}/status")
        suspend fun updateClaimStatus(
                @Path("id") claimId: Int,
                @Body statusRequest: StatusRequest
        ): WasteClaim
=======
import com.feedlink.feedlink.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    @GET("listings/")
    suspend fun fetchListings(
        @Query("latitude") latitude: Double?,
        @Query("longitude") longitude: Double?
    ): Response<List<Listing>>

    @GET("listings/{id}/")
    suspend fun fetchProductDetail(
        @Path("id") listingId: Int
    ): Response<Listing>

    @POST("signup/")
    suspend fun signup(@Body request: SignUpRequest): Response<SignUpResponse>

    @POST("login/")
    suspend fun login(@Body request: SignInRequest): Response<SignInResponse>

    @POST("forgot password/")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<VerificationResponse>

    @POST("reset/")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<VerificationResponse>

    @POST("verification/")
    suspend fun verification(@Body request: VerificationRequest): Response<VerificationResponse>
>>>>>>> 93200a6b2ef82ae4c36044f694a6b170c8be4d57
}