package com.feedlink.feedlink.api
import com.feedlink.feedlink.model.Listing
import com.feedlink.feedlink.model.StatusRequest
import com.feedlink.feedlink.model.WasteClaim
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path
import com.feedlink.feedlink.model.*
import retrofit2.Response
import retrofit2.http.*

import com.feedlink.feedlink.model.UserProfile
import com.feedlink.feedlink.network.OrderStatusResponse
import com.feedlink.feedlink.network.PaymentStatusResponse
import com.feedlink.feedlink.network.StkPushRequest
import com.feedlink.feedlink.network.StkPushSuccessResponse
import okhttp3.MultipartBody

interface ApiInterface {

    @GET("users/{userId}/")
    suspend fun getUserProfileById(@Path("userId") userId: Int): Response<UserProfile>

    @Multipart
    @PUT("users/{userId}/")
    suspend fun updateUserProfileWithImage(
        @Path("userId") userId: Int,
        @Part firstName: MultipartBody.Part?,
        @Part lastName: MultipartBody.Part?,
        @Part email: MultipartBody.Part?,
        @Part address: MultipartBody.Part?,
        @Part role: MultipartBody.Part?,
        @Part tillNumber: MultipartBody.Part?,
        @Part profilePicture: MultipartBody.Part?
    ): Response<UserProfile>

    @GET("listings/")
    suspend fun fetchListings(
        @Query("latitude") latitude: Double? = null,
        @Query("longitude") longitude: Double? = null
    ): Response<List<Listing>>

    @GET("listings/{id}/")
    suspend fun fetchProductDetail(
        @Path("id") listingId: Int
    ): Response<Listing>
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

    @POST("signup/")
    suspend fun signup(@Body request: SignUpRequest): Response<SignUpResponse>

    @POST("login/")
    suspend fun login(@Body request: SignInRequest): Response<SignInResponse>

    @POST("forgot-password/")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<VerificationResponse>

    @POST("reset/")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<VerificationResponse>

    @POST("verification/")
    suspend fun verification(@Body request: VerificationRequest): Response<VerificationResponse>


    @GET("listings/")
    suspend fun getAvailableListings(): List<Listing>





    @GET("listings/{id}")
    suspend fun getListingById(@Path("id") listingId: Int): Listing

    @POST("ussdpush")
    suspend fun initiateStkPush(
        @Body stkPushRequest: StkPushRequest
    ): Response<StkPushSuccessResponse>

    @GET("orders/{order_id}/")
    suspend fun getOrderStatus(
        @Path("order_id") orderId: Int
    ): Response<OrderStatusResponse>

    @GET("payments/{payment_id}/")
    suspend fun getPaymentStatus(
        @Path("payment_id") paymentId: String
    ): Response<PaymentStatusResponse>
}