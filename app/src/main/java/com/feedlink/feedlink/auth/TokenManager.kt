package com.feedlink.feedlink.auth

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    private const val PREF_NAME = "FEEDLINK_PREFS"
    private const val KEY_ACCESS_TOKEN = "ACCESS_TOKEN"
    private const val KEY_EMAIL = "EMAIL"
    private const val KEY_USER_ID = "USER_ID"
    private const val KEY_USER_ROLE = "USER_ROLE"

    private var prefs: SharedPreferences? = null

    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun getAuthToken(): String? =
        prefs?.getString(KEY_ACCESS_TOKEN, null)?.takeIf { it.isNotBlank() }

    fun getEmail(): String? =
        prefs?.getString(KEY_EMAIL, null)?.takeIf { it.isNotBlank() }

    fun getUserId(): String? =
        prefs?.getString(KEY_USER_ID, null)?.takeIf { it.isNotBlank() }

    fun getUserRole(): String =
        prefs?.getString(KEY_USER_ROLE, "buyer") ?: "buyer"

    fun saveAuthData(token: String, email: String, userId: String, role: String = "buyer") {
        prefs?.edit()?.apply {
            putString(KEY_ACCESS_TOKEN, token)
            putString(KEY_EMAIL, email)
            putString(KEY_USER_ID, userId)
            putString(KEY_USER_ROLE, role)
            apply()
        }
    }

    fun clearAuthData() {
        prefs?.edit()?.apply {
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_EMAIL)
            remove(KEY_USER_ID)
            remove(KEY_USER_ROLE)
            apply()
        }
    }

    fun isLoggedIn(): Boolean = !getAuthToken().isNullOrBlank()
}
