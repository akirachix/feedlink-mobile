package com.feedlink.feedlink.repository

import com.feedlink.feedlink.api.ApiInterface
import com.feedlink.feedlink.model.Listing

class ListingRepository(private val api: ApiInterface) {
    suspend fun getAvailableListings(latitude: Double?, longitude: Double?): Result<List<Listing>> {
        return try {
            val response = api.fetchListings()
            if (response.isSuccessful) {
                response.body()?.let { listings ->
                    Result.success(listings)
                } ?: Result.failure(Exception("Response body is null"))
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}