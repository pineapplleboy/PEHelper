package com.example.pehelper.presentation.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pehelper.data.model.AttendanceApplication
import com.example.pehelper.data.network.PEAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

sealed class ApplicationsListState {
    data object Loading : ApplicationsListState()
    data class Success(val applications: List<AttendanceApplication>) : ApplicationsListState()
    data class Error(val error: String) : ApplicationsListState()
}

sealed class ApplicationActionState {
    data object Idle : ApplicationActionState()
    data object Loading : ApplicationActionState()
    data object Success : ApplicationActionState()
    data class Error(val error: String) : ApplicationActionState()
}

class CuratorApplicationsViewModel : ViewModel(), KoinComponent {
    private val api: PEAPI by inject()
    
    private val _applicationsState = MutableStateFlow<ApplicationsListState>(ApplicationsListState.Loading)
    val applicationsState: StateFlow<ApplicationsListState> = _applicationsState.asStateFlow()

    private val _actionState = MutableStateFlow<ApplicationActionState>(ApplicationActionState.Idle)
    val actionState: StateFlow<ApplicationActionState> = _actionState.asStateFlow()

    fun getApplications() {
        viewModelScope.launch {
            _applicationsState.value = ApplicationsListState.Loading
            try {
                val response = api.getCuratorApplications()
                _applicationsState.value = ApplicationsListState.Success(response.applications)
            } catch (e: Exception) {
                _applicationsState.value = ApplicationsListState.Error(e.localizedMessage ?: "Ошибка загрузки")
            }
        }
    }

    fun approveApplication(eventId: String, studentId: String) {
        viewModelScope.launch {
            _actionState.value = ApplicationActionState.Loading
            try {
                val response = api.approveApplication(eventId, studentId)
                if (response.isSuccessful) {
                    _actionState.value = ApplicationActionState.Success
                    getApplications()
                } else {
                    _actionState.value = ApplicationActionState.Error("Ошибка сервера: ${response.code()}")
                }
            } catch (e: Exception) {
                _actionState.value = ApplicationActionState.Error(e.localizedMessage ?: "Ошибка подтверждения")
            }
        }
    }

    fun rejectApplication(eventId: String, studentId: String) {
        viewModelScope.launch {
            _actionState.value = ApplicationActionState.Loading
            try {
                val response = api.rejectApplication(eventId, studentId)
                if (response.isSuccessful) {
                    _actionState.value = ApplicationActionState.Success
                    getApplications()
                } else {
                    _actionState.value = ApplicationActionState.Error("Ошибка сервера: ${response.code()}")
                }
            } catch (e: Exception) {
                _actionState.value = ApplicationActionState.Error(e.localizedMessage ?: "Ошибка отклонения")
            }
        }
    }

    fun resetActionState() {
        _actionState.value = ApplicationActionState.Idle
    }
} 