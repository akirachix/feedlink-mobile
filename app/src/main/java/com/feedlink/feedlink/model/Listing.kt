package com.feedlink.feedlink.model

import com.google.gson.annotations.SerializedName

data class Listing(
    @SerializedName("listing_id")
    val listingId: Int? = null,

    @SerializedName("product_type")
    val productType: String? = null,

    @SerializedName("category")
    val category: String? = null,

    val description: String? = null,
    val quantity: String? = null,
    @SerializedName("original_price")
    val originalPrice: String? = null,
    @SerializedName("expiry_date")
    val expiryDate: String? = null,
    @SerializedName("discounted_price")
    val discountedPrice: String? = null,

    @SerializedName("image")
    val imageUrl: String? = null,

    @SerializedName("image_url")
    val placeholderImageUrl: String? = null,

    val status: String? = null,

    @SerializedName("created_at")
    val createdAt: String? = null,

    @SerializedName("updated_at")
    val updatedAt: String? = null,

    @SerializedName("upload_method")
    val uploadMethod: String? = null,

    @SerializedName("pickup_window_duration")
    val pickupWindowDuration: String? = null,

    val unit: String? = null,

    val producer: Int? = null
)