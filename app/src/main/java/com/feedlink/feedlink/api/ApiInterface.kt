package com.feedlink.feedlink.api

import com.feedlink.feedlink.model.Listing
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

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


}