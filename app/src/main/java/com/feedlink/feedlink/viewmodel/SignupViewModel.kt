package com.feedlink.feedlink.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feedlink.feedlink.model.SignUpRequest
import com.feedlink.feedlink.model.SignUpResponse
import com.feedlink.feedlink.repository.AuthRepository
import kotlinx.coroutines.launch

class SignupViewModel(
    private val repo: AuthRepository // 👈 Injected via constructor
) : ViewModel() {

    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)
    var SignupSuccess = mutableStateOf<SignUpResponse?>(null)

    // ❌ Removed: private val repo = AuthRepository()

    fun signup(request: SignUpRequest) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            SignupSuccess.value = null

            val result = repo.signup(request)

            if (result.isSuccess) {
                // ✅ Safe: getOrNull() already returns SignUpResponse? — no cast needed
                SignupSuccess.value = result.getOrNull()
            } else {
                val exception = result.exceptionOrNull()
                errorMessage.value = parseErrorMessage(exception?.message)
            }

            isLoading.value = false
        }
    }

    private fun parseErrorMessage(errorMessage: String?): String {
        return when {
            errorMessage.isNullOrEmpty() -> "Signup failed. Please try again."
            errorMessage.contains("practice_number", ignoreCase = true) -> "Invalid practice number"
            errorMessage.contains("email", ignoreCase = true) -> "Email already in use or invalid."
            errorMessage.contains("<!DOCTYPE html>", ignoreCase = true) -> "Something went wrong, try again later."
            else -> errorMessage
        }
    }
}