package com.feedlink.feedlink.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feedlink.feedlink.repository.AuthRepository
import kotlinx.coroutines.launch

class ForgotPasswordViewModel : ViewModel(){
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)
    private val repo = AuthRepository()

    fun sendOtp(email: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            val result = repo.sendOtp(email)
            if (result.isSuccess) {
                onSuccess()
            } else {
                errorMessage.value = parseErrorMessage(result.exceptionOrNull()?.message)
            }
            isLoading.value = false
        }
    }

    fun verifyOtp(email: String, otp: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            val result = repo.verifyOtp(email, otp)
            if (result.isSuccess) {
                onSuccess()
            } else {
                errorMessage.value = parseErrorMessage(result.exceptionOrNull()?.message)
            }
            isLoading.value = false
        }
    }

    fun resetPassword(email: String, newPassword: String, confirmPassword: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            val result = repo.resetPassword(email, newPassword, confirmPassword)
            if (result.isSuccess) {
                onSuccess()
            } else {
                errorMessage.value = parseErrorMessage(result.exceptionOrNull()?.message)
            }
            isLoading.value = false
        }
    }
    private fun parseErrorMessage(error: String?): String {
        if (error.isNullOrEmpty()) return "Something went wrong. Please try again."
        return when {
            error.contains("User with this email does not exist", ignoreCase = true) ||
                    (error.contains("detail", ignoreCase = true) && error.contains("does not exist", ignoreCase = true)) ->
                "No account found with this email."
            error.contains("email", ignoreCase = true) ->
                "Invalid or missing email address."
            error.contains("timeout", ignoreCase = true) ||
                    error.contains("network", ignoreCase = true) ||
                    error.contains("failed", ignoreCase = true) ||
                    error.contains("error", ignoreCase = true) ->
                "Network error. Please check your connection."
            else -> "Something went wrong. Please try again."
        }
    }

}