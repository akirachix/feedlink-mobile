
package com.feedlink.feedlink.model

import com.google.gson.annotations.SerializedName

data class Listing(
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

    )
