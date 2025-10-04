package com.feedlink.feedlink.auth
import android.content.Context
import android.util.Log

object TokenManager {
    private var currentAuthToken: String? = "bf1e24434e1528647ab7a59bfdaaad2f21f25022"
    private var currentUserId: String? = "108"
    fun initialize(context: Context) {
        Log.d("TokenManager", "Initialized. Token: $currentAuthToken, UserID: $currentUserId")
    }
    fun getAuthToken(): String? = currentAuthToken?.takeIf { it.isNotBlank() }
    fun getUserId(): String? = currentUserId?.takeIf { it.isNotBlank() }
    fun saveAuthData(token: String, userId: String) {
        currentAuthToken = token
        currentUserId = userId
    }
    fun clearAuthData() {
        currentAuthToken = null
        currentUserId = null
    }
}