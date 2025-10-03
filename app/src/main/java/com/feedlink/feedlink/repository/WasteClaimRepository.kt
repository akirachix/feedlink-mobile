package com.feedlink.feedlink.repository

import com.feedlink.feedlink.api.ApiInterface
import com.feedlink.feedlink.model.StatusRequest
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

    suspend fun updateClaimStatus(claimId: Int, status: String): Result<WasteClaim> {
        return try {
            val statusRequest = StatusRequest(status)
            val updatedClaim = api.updateClaimStatus(claimId, statusRequest)
            Result.success(updatedClaim)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}