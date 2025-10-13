package com.feedlink.feedlink.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feedlink.feedlink.model.UserProfile
import com.feedlink.feedlink.repository.ProfileRepository
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class ProfileViewModel(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _userProfile = MutableLiveData<UserProfile?>()
    val userProfile: LiveData<UserProfile?> = _userProfile

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isUpdating = MutableLiveData<Boolean>()
    val isUpdating: LiveData<Boolean> = _isUpdating

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _profileUpdateSuccess = MutableLiveData<Boolean>()
    val profileUpdateSuccess: LiveData<Boolean> = _profileUpdateSuccess

    private var selectedImageFile: File? = null

    fun fetchUserProfile(userId: Int, forceRefresh: Boolean = false) {
        val currentProfileId = _userProfile.value?.id?.toIntOrNull()
        if (!forceRefresh && currentProfileId == userId) return

        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val response = repository.fetchUserProfileById(userId)
                if (response.isSuccessful) {
                    _userProfile.value = response.body()
                } else {
                    _error.value = "Load failed: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Network error"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onImageSelected(context: Context, uri: Uri?) {
        selectedImageFile = null
        uri?.let {
            selectedImageFile = if (it.scheme == "content") {
                uriToFile(context, it)
            } else if (it.scheme == "file") {
                File(it.path ?: return)
            } else {
                null
            }
        }
    }

    fun saveProfileWithImage(
        userId: Int,
        firstName: String,
        lastName: String,
        email: String
    ) {
        val current = _userProfile.value ?: return
        _isUpdating.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val response = repository.updateProfileWithImage(
                    userId = userId,
                    firstName = firstName.ifBlank { null },
                    lastName = lastName.ifBlank { null },
                    email = email.ifBlank { null },
                    address = current.address,
                    role = current.role,
                    tillNumber = current.tillNumber,
                    imageFile = selectedImageFile
                )

                if (response.isSuccessful) {
                    val updatedProfile = response.body()
                    if (updatedProfile != null) {
                        val finalProfile = if (selectedImageFile != null) {
                            updatedProfile.copy(
                                profilePicture = updatedProfile.profilePicture?.let { url ->
                                    url.trim().takeIf { it.isNotBlank() }?.let { trimmedUrl ->
                                        if (trimmedUrl.startsWith("http")) {
                                            "$trimmedUrl?cb=${System.currentTimeMillis()}"
                                        } else {
                                            trimmedUrl
                                        }
                                    }
                                }
                            )
                        } else {
                            updatedProfile
                        }
                        _userProfile.value = finalProfile
                        _profileUpdateSuccess.value = true
                    } else {
                        _error.value = "Update succeeded but no profile returned"
                    }
                } else {
                    val err = response.errorBody()?.string() ?: "Unknown"
                    Log.e("VM", "Update failed: ${response.code()}, $err")
                    _error.value = "Save failed (${response.code()})"
                }
            } catch (e: Exception) {
                Log.e("VM", "Exception", e)
                _error.value = "Upload error"
            } finally {
                _isUpdating.value = false
                selectedImageFile = null
            }
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
                FileOutputStream(file).use { output -> input.copyTo(output) }
                file
            }
        } catch (e: Exception) {
            Log.e("VM", "URI to file failed", e)
            null
        }
    }

    fun clearError() { _error.value = null }
    fun resetUpdateSuccessFlag() { _profileUpdateSuccess.value = false }
}