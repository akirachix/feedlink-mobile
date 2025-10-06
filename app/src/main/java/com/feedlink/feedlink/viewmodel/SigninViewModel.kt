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


    fun signin(email: String, password: String) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            signInResponse.value = null


            val result = repo.signin(SignInRequest(email, password))


            if (result.isSuccess) {
                signInResponse.value = result.getOrNull()
            } else {
                errorMessage.value = result.exceptionOrNull()?.message ?: "Sign in failed"
            }
            isLoading.value = false
        }
    }
}

