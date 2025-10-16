

package com.feedlink.feedlink.viewmodel


import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feedlink.feedlink.model.SignUpRequest
import com.feedlink.feedlink.model.SignUpResponse
import com.feedlink.feedlink.repository.AuthRepository
import kotlinx.coroutines.launch


class SignupViewModel(
    private val repo: AuthRepository
) : ViewModel() {


    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)
    var signupSuccess = mutableStateOf<SignUpResponse?>(null)


    fun signup(request: SignUpRequest) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            signupSuccess.value = null


            val result = repo.signup(request)


            if (result.isSuccess) {
                signupSuccess.value = result.getOrNull()
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
            errorMessage.contains("email", ignoreCase = true) -> "Email already in use or invalid."
            errorMessage.contains("<!DOCTYPE html>", ignoreCase = true) -> "Something went wrong, try again later."
            else -> errorMessage ?: "An unknown error occurred."
        }
    }
}