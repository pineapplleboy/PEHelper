package com.example.pehelper.data.repository

import android.content.Context
import android.content.SharedPreferences

class TokenStorage(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var accessToken: String?
        get() = prefs.getString(ACCESS_TOKEN_KEY, null)
        set(value) = prefs.edit().putString(ACCESS_TOKEN_KEY, value).apply()

    var refreshToken: String?
        get() = prefs.getString(REFRESH_TOKEN_KEY, null)
        set(value) = prefs.edit().putString(REFRESH_TOKEN_KEY, value).apply()

    var role: String?
        get() = prefs.getString(ROLE_KEY, null)
        set(value) = prefs.edit().putString(ROLE_KEY, value).apply()

    fun clearTokens() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "auth_prefs"
        private const val ACCESS_TOKEN_KEY = "access_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
        private const val ROLE_KEY = "role"
    }
} 