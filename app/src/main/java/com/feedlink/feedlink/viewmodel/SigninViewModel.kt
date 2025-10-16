package com.feedlink.feedlink.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feedlink.feedlink.model.SignInRequest
import com.feedlink.feedlink.model.SignInResponse
import com.feedlink.feedlink.repository.AuthRepository
import kotlinx.coroutines.launch

class SigninViewModel(
    private val repo: AuthRepository
) : ViewModel() {

    var isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)
    var signInResponse = mutableStateOf<SignInResponse?>(null)
    var userRole = mutableStateOf<String?>(null)

    fun signin(email: String, password: String) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            signInResponse.value = null
            userRole.value = null

            val result = repo.signin(SignInRequest(email, password))

            if (result.isSuccess) {
                val response = result.getOrNull()!!
                signInResponse.value = response

                val userId = response.userId.toIntOrNull()
                if (userId != null) {
                    val roleResult = repo.fetchUserRole(userId)
                    if (roleResult.isSuccess) {
                        userRole.value = roleResult.getOrNull()
                    } else {
                        errorMessage.value = "Login succeeded, but failed to load your role."
                    }
                } else {
                    errorMessage.value = "Invalid user ID received."
                }
            } else {
                errorMessage.value = result.exceptionOrNull()?.message ?: "Sign in failed"
            }
            isLoading.value = false
        }
    }

    private fun String.toIntOrNull(): Int? {
        return try {
            this.toInt()
        } catch (e: NumberFormatException) {
            null
        }
    }
}