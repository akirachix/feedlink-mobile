package com.feedlink.feedlink.api

import com.feedlink.feedlink.model.UserProfile
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

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
}