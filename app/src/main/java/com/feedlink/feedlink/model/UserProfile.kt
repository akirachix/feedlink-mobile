package com.feedlink.feedlink.model
import com.google.gson.annotations.SerializedName
data class UserProfile(
    @SerializedName("id") val id: String,
    @SerializedName("first_name") var firstName: String?,
    @SerializedName("last_name") var lastName: String?,
    @SerializedName("email") var email: String?,
    @SerializedName("role") val role: String?,
    @SerializedName("profile_picture") var profilePicture: String?,
    @SerializedName("address") var address: String?,
    @SerializedName("till_number") val tillNumber: String?,
    @SerializedName("latitude") var latitude: Double?,
    @SerializedName("longitude") var longitude: Double?
)