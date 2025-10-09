package com.feedlink.feedlink.network

import com.google.gson.annotations.SerializedName

data class StkPushSuccessResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("response")
    val darajaResponse: DarajaResponse,

    @SerializedName("payment_id")
    val paymentId: String
)

data class DarajaResponse(
    @SerializedName("CheckoutRequestID")
    val checkoutRequestID: String
)

data class OrderStatusResponse(
    @SerializedName("order_id")
    val orderId: Int,

    @SerializedName("payment_status")
    val paymentStatus: String,

    @SerializedName("pickup_pin")
    val pickupPin: String?,

    @SerializedName("total_amount")
    val totalAmount: String
)

data class PaymentStatusResponse(
    @SerializedName("transaction_id")
    val transactionId: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("amount")
    val amount: String,

    @SerializedName("mpesa_receipt_number")
    val mpesaReceiptNumber: String?
)
data class Order(
    @SerializedName("order_id")
    val orderId: Int,

    @SerializedName("order_date")
    val orderDate: String?,

    @SerializedName("order_status")
    val orderStatus: String?,

    @SerializedName("pin")
    val pin: String?
)