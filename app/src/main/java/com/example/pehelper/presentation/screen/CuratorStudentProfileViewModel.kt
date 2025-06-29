package com.example.pehelper.presentation.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pehelper.data.model.CuratorStudentProfileResponse
import com.example.pehelper.data.network.PEAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

sealed class CuratorStudentProfileState {
    object Loading : CuratorStudentProfileState()
    data class Success(val data: CuratorStudentProfileResponse) : CuratorStudentProfileState()
    data class Error(val error: String) : CuratorStudentProfileState()
}

class CuratorStudentProfileViewModel : ViewModel(), KoinComponent {
    private val api: PEAPI by inject()
    private val _state = MutableStateFlow<CuratorStudentProfileState>(CuratorStudentProfileState.Loading)
    val state: StateFlow<CuratorStudentProfileState> = _state.asStateFlow()

    fun loadProfile(studentId: String) {
        viewModelScope.launch {
            _state.value = CuratorStudentProfileState.Loading
            try {
                val response = api.getCuratorStudentProfile(studentId)
                _state.value = CuratorStudentProfileState.Success(response)
            } catch (e: Exception) {
                _state.value = CuratorStudentProfileState.Error(e.localizedMessage ?: "Ошибка загрузки")
            }
        }
    }
} 