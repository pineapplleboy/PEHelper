package com.example.pehelper.presentation.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pehelper.data.network.PEAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Response

sealed class ProfileState {
    data object Idle : ProfileState()
    data object Loading : ProfileState()
    data class Success(val response: Response<Unit>) : ProfileState()
    data class Error(val error: String) : ProfileState()
}

class ProfileViewModel : ViewModel(), KoinComponent {
    private val api: PEAPI by inject()

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    fun getStudentProfile() {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            try {
                val response = api.getStudentProfile()
                if (response.isSuccessful) {
                    _profileState.value = ProfileState.Success(response)
                } else {
                    _profileState.value = ProfileState.Error(response.message())
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun getTeacherProfile() {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            try {
                val response = api.getTeacherProfile()
                if (response.isSuccessful) {
                    _profileState.value = ProfileState.Success(response)
                } else {
                    _profileState.value = ProfileState.Error(response.message())
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun getCuratorProfile() {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            try {
                val response = api.getCuratorProfile()
                if (response.isSuccessful) {
                    _profileState.value = ProfileState.Success(response)
                } else {
                    _profileState.value = ProfileState.Error(response.message())
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }
} 