package com.feedlink.feedlink.repository

import com.feedlink.feedlink.api.ApiInterface
import com.feedlink.feedlink.model.Listing

class ListingRepository(private val api: ApiInterface) {
    suspend fun getAvailableListings(): Result<List<Listing>> {
        return try {
            val listings = api.getAvailableListings()
            Result.success(listings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}