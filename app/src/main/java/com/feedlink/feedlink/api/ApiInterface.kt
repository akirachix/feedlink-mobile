package com.feedlink.feedlink.api

import com.feedlink.feedlink.model.Listing
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
                @Body statusRequest: String
        ): WasteClaim
}