package com.example.pehelper.presentation.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pehelper.data.model.LoginUserModel
import com.example.pehelper.data.model.RefreshTokenModel
import com.example.pehelper.data.network.PEAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Response

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data class Success(val response: Response<Unit>) : AuthState()
    data class Error(val error: String) : AuthState()
}

class AuthViewModel : ViewModel(), KoinComponent {
    private val api: PEAPI by inject()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = api.login(LoginUserModel(email, password))
                if (response.isSuccessful) {
                    _authState.value = AuthState.Success(response)
                } else {
                    _authState.value = AuthState.Error(response.message())
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
                    _authState.value = AuthState.Success(response)
                } else {
                    _authState.value = AuthState.Error(response.message())
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = api.logout()
                if (response.isSuccessful) {
                    _authState.value = AuthState.Success(response)
                } else {
                    _authState.value = AuthState.Error(response.message())
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }
} 