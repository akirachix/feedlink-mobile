package com.feedlink.feedlink.repository


import android.util.Log
import com.feedlink.feedlink.api.ApiInterface
import com.feedlink.feedlink.model.Listing
import com.feedlink.feedlink.model.WasteClaim


class WasteClaimRepository(private val api: ApiInterface) {
    suspend fun getWasteClaims(): Result<List<WasteClaim>> {
        return try {
            val claims = api.getWasteClaims()
            Result.success(claims)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun addWasteClaim(claim: WasteClaim): Result<WasteClaim> {
        return try {
            val newClaim = api.createWasteClaim(claim)
            Result.success(newClaim)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun getWasteClaimById(claimId: Int): Result<WasteClaim> {
        return try {
            val claim = api.getWasteClaimById(claimId)
            Result.success(claim)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun updateClaimStatus(wasteId: Int, status: String): Result<Unit> {
        return try {
            Log.d("API_DEBUG", "Repository: Updating status for wasteId: $wasteId. URL will be: waste-claims/$wasteId/")
            val statusMap = mapOf("claim_status" to status)
            val response = api.updateWasteClaimStatus(wasteId, statusMap)
            if (response.isSuccessful) {
                Log.d("API_DEBUG", "Repository: PATCH update successful.")
                Result.success(Unit)
            } else {
                Log.e("API_DEBUG", "Repository: PATCH update failed. Code: ${response.code()}, Message: ${response.message()}")
                Result.failure(Exception("Update failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("API_DEBUG", "Repository: Exception during PATCH update: ${e.message}")
            Result.failure(e)
        }
    }


    suspend fun getListingById(listingId: Int): Result<Listing> {
        return try {
            val listing = api.getListingById(listingId)
            Result.success(listing)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

