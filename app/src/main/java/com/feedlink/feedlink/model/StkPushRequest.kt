package com.feedlink.feedlink.network

import com.google.gson.annotations.SerializedName

data class StkPushRequest(
    @SerializedName("phone_number")
    val phoneNumber: String,

    @SerializedName("amount")
    val amount: Int,

    @SerializedName("account_reference")
    val accountReference: String,

    @SerializedName("transaction_desc")
    val transactionDesc: String
)
