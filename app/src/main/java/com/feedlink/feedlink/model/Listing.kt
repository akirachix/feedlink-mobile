package com.feedlink.feedlink.model

import com.google.gson.annotations.SerializedName

data class Listing(
<<<<<<< HEAD
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
=======
    @SerializedName("product_type") val productType: String,
    val category: String,
    val description: String,
    val quantity: String,
    @SerializedName("listing_id") val listingId: Int,
    @SerializedName("original_price") val originalPrice: Float,
    @SerializedName("discounted_price") val discountedPrice: Float,
    @SerializedName("expiry_date") val expiryDate: String,
    val image: String?,
    @SerializedName("image_url") val imageUrl: String?,
    val status: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("upload_method") val uploadMethod: String,
    @SerializedName("pickup_window_duration") val pickupWindowDuration: String,
    val unit: String,
    val producer: Int,

>>>>>>> 93200a6b2ef82ae4c36044f694a6b170c8be4d57
)