package com.example.pehelper.presentation.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pehelper.data.model.LoginUserModel
import com.example.pehelper.data.model.RefreshTokenModel
import com.example.pehelper.data.model.AuthTokensModel
import com.example.pehelper.data.network.PEAPI
import com.google.gson.Gson
import com.example.pehelper.data.model.ErrorResponse
import com.example.pehelper.data.repository.TokenStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Response
import android.util.Base64
import org.json.JSONObject

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data class Success(val response: Response<AuthTokensModel>) : AuthState()
    data class Error(val error: String) : AuthState()
}

class AuthViewModel : ViewModel(), KoinComponent {
    private val api: PEAPI by inject()
    private val tokenStorage: TokenStorage by inject()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = api.login(LoginUserModel(email, password))
                if (response.isSuccessful) {
                    tokenStorage.accessToken = response.body()?.accessToken
                    tokenStorage.refreshToken = response.body()?.refreshToken
                    _authState.value = AuthState.Success(response)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        val err = Gson().fromJson(errorBody, ErrorResponse::class.java)
                        when {
                            !err.title.isNullOrBlank() && !err.detail.isNullOrBlank() -> err.title + "\n" + err.detail
                            !err.detail.isNullOrBlank() -> err.detail
                            !err.title.isNullOrBlank() && !err.errors.isNullOrEmpty() -> {
                                val firstField = err.errors.entries.firstOrNull()
                                val firstMsg = firstField?.value?.firstOrNull()
                                if (firstMsg != null) err.title + "\n" + firstMsg else err.title
                            }
                            !err.title.isNullOrBlank() -> err.title
                            !err.message.isNullOrBlank() -> err.message
                            else -> errorBody
                        }
                    } catch (e: Exception) {
                        if (!errorBody.isNullOrBlank()) errorBody else null
                    }
                    _authState.value = AuthState.Error(errorMessage ?: "Unknown error")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun refresh(refreshToken: String?) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = api.refresh(RefreshTokenModel(refreshToken))
                if (response.isSuccessful) {
                    tokenStorage.accessToken = response.body()?.accessToken
                    tokenStorage.refreshToken = response.body()?.refreshToken
                    _authState.value = AuthState.Success(response)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        val err = Gson().fromJson(errorBody, ErrorResponse::class.java)
                        when {
                            !err.title.isNullOrBlank() && !err.detail.isNullOrBlank() -> err.title + "\n" + err.detail
                            !err.detail.isNullOrBlank() -> err.detail
                            !err.title.isNullOrBlank() && !err.errors.isNullOrEmpty() -> {
                                val firstField = err.errors.entries.firstOrNull()
                                val firstMsg = firstField?.value?.firstOrNull()
                                if (firstMsg != null) err.title + "\n" + firstMsg else err.title
                            }
                            !err.title.isNullOrBlank() -> err.title
                            !err.message.isNullOrBlank() -> err.message
                            else -> errorBody
                        }
                    } catch (e: Exception) {
                        if (!errorBody.isNullOrBlank()) errorBody else null
                    }
                    _authState.value = AuthState.Error(errorMessage ?: "Unknown error")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun logout() {
        tokenStorage.clearTokens()
    }

    fun checkAndRefreshTokens() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val accessToken = tokenStorage.accessToken
            val refreshToken = tokenStorage.refreshToken

            if (accessToken != null && isAccessTokenValid(accessToken)) {
                _authState.value = AuthState.Success(Response.success(AuthTokensModel(accessToken, refreshToken)))
                return@launch
            }

            if (refreshToken == null) {
                _authState.value = AuthState.Error("No tokens found")
                return@launch
            }

            try {
                val response = api.refresh(RefreshTokenModel(refreshToken))
                if (response.isSuccessful) {
                    tokenStorage.accessToken = response.body()?.accessToken
                    tokenStorage.refreshToken = response.body()?.refreshToken
                    _authState.value = AuthState.Success(response)
                } else {
                    tokenStorage.clearTokens()
                    _authState.value = AuthState.Error("Token refresh failed")
                }
            } catch (e: Exception) {
                tokenStorage.clearTokens()
                _authState.value = AuthState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    private fun isAccessTokenValid(token: String): Boolean {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return false
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING))
            val json = JSONObject(payload)
            val exp = json.getLong("exp")
            val now = System.currentTimeMillis() / 1000
            exp > now
        } catch (e: Exception) {
            false
        }
    }
} 