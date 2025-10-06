
package com.feedlink.feedlink.repository

import com.feedlink.feedlink.api.ApiInterface
import com.feedlink.feedlink.model.Listing
import retrofit2.Response

class ListingsRepository(private val api: ApiInterface) {
    suspend fun fetchListings(latitude: Double?, longitude: Double?): Response<List<Listing>> =
        api.fetchListings(latitude, longitude)

    suspend fun fetchProductDetail(listingId: Int): Response<Listing> =
        api.fetchProductDetail(listingId)
}