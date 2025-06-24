package com.example.pehelper.presentation.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pehelper.data.model.TeacherProfileModel
import com.example.pehelper.data.network.PEAPI
import com.example.pehelper.data.repository.TokenStorage
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


sealed class ProfileState {
    data object Idle : ProfileState()
    data object Loading : ProfileState()
    data class Success(val profile: TeacherProfileModel) : ProfileState()
    data class Error(val error: String) : ProfileState()
}


class ProfileViewModel : ViewModel(), KoinComponent {
    private val api: PEAPI by inject()
    private val tokenStorage: TokenStorage by inject()

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    fun getTeacherProfile() {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            try {
                val response = api.getTeacherProfile()
                if (response.isSuccessful && response.body() != null) {
                    _profileState.value = ProfileState.Success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string()
                    _profileState.value = ProfileState.Error(parseError(errorBody) ?: "Unknown error")
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    private fun parseError(errorBody: String?): String? {
        return try {
            val err = Gson().fromJson(errorBody, com.example.pehelper.data.model.ErrorResponse::class.java)
            when {
                !err.title.isNullOrBlank() && !err.detail.isNullOrBlank() -> err.title + "\n" + err.detail
                !err.detail.isNullOrBlank() -> err.detail
                !err.title.isNullOrBlank() && !err.errors.isNullOrEmpty() -> {
                    val firstMsg = err.errors.entries.firstOrNull()?.value?.firstOrNull()
                    if (firstMsg != null) err.title + "\n" + firstMsg else err.title
                }
                !err.title.isNullOrBlank() -> err.title
                !err.message.isNullOrBlank() -> err.message
                else -> errorBody
            }
        } catch (e: Exception) {
            if (!errorBody.isNullOrBlank()) errorBody else null
        }
    }
} 