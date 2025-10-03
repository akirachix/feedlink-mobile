package com.feedlink.feedlink.model

import com.google.gson.annotations.SerializedName

data class ListingItem(
    @SerializedName("listing_id")
    val listingId: Int,

    @SerializedName("image")
    val image: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("original_price")
    val originalPrice: Float,

    @SerializedName("discounted_price")
    val discountedPrice: Float,

    @SerializedName("quantity")
    val quantity: String?,


)