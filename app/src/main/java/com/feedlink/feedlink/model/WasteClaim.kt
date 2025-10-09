package com.feedlink.feedlink.model

import com.google.gson.annotations.SerializedName

data class WasteClaim(
    @SerializedName("waste_id")
    val wasteId: Int? = null,

    val user: Int? = null,
    @SerializedName("listing_id")
    val listingId: Int? = null,

    @SerializedName("claim_time")
    val claimTime: String? = null,

    @SerializedName("claim_status")
    val claimStatus: String? = null,

    val pin: String? = null,

    @SerializedName("created_at")
    val createdAt: String? = null,

    @SerializedName("updated_at")
    val updatedAt: String? = null
)