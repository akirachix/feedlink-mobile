package com.feedlink.feedlink.model

import com.google.gson.annotations.SerializedName
data class StatusRequest(
    @SerializedName("claim_status")
    val claimStatus: String
)

data class UpdateOrderStatusRequest(
    @SerializedName("order_status")
    val orderStatus: String
)

