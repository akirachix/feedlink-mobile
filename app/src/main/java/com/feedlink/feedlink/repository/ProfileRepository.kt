package com.feedlink.feedlink.repository

import com.feedlink.feedlink.api.ApiInterface
import com.feedlink.feedlink.model.UserProfile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File

class ProfileRepository(private val api: ApiInterface) {
    suspend fun fetchUserProfileById(userId: Int): Response<UserProfile> {
        return api.getUserProfileById(userId)
    }
    suspend fun updateProfileWithImage(
        userId: Int,
        firstName: String?,
        lastName: String?,
        email: String?,
        address: String?,
        role: String?,
        tillNumber: String?,
        imageFile: File?
    ): Response<UserProfile> {
        val firstNamePart = firstName?.let { MultipartBody.Part.createFormData("first_name", it) }
        val lastNamePart = lastName?.let { MultipartBody.Part.createFormData("last_name", it) }
        val emailPart = email?.let { MultipartBody.Part.createFormData("email", it) }
        val addressPart = address?.let { MultipartBody.Part.createFormData("address", it) }
        val rolePart = role?.let { MultipartBody.Part.createFormData("role", it) }
        val tillNumberPart = tillNumber?.let { MultipartBody.Part.createFormData("till_number", it) }
        val imagePart = imageFile?.let {
            val reqFile = it.asRequestBody("image/*".toMediaType())
            MultipartBody.Part.createFormData("profile_picture", it.name, reqFile)
        }
        return api.updateUserProfileWithImage(
            userId = userId,
            firstName = firstNamePart,
            lastName = lastNamePart,
            email = emailPart,
            address = addressPart,
            role = rolePart,
            tillNumber = tillNumberPart,
            profilePicture = imagePart
        )
    }
}