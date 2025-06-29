package com.example.pehelper.presentation.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pehelper.data.model.AllAttendancesResponse
import com.example.pehelper.data.network.PEAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

sealed class AllAttendancesState {
    data object Idle : AllAttendancesState()
    data object Loading : AllAttendancesState()
    data class Success(val data: AllAttendancesResponse) : AllAttendancesState()
    data class Error(val error: String) : AllAttendancesState()
}

class AllAttendancesViewModel : ViewModel(), KoinComponent {
    private val api: PEAPI by inject()

    private val _attendancesState = MutableStateFlow<AllAttendancesState>(AllAttendancesState.Idle)
    val attendancesState: StateFlow<AllAttendancesState> = _attendancesState.asStateFlow()

    fun getAllAttendances() {
        viewModelScope.launch {
            _attendancesState.value = AllAttendancesState.Loading
            try {
                val response = api.getAllAttendances()
                if (response.isSuccessful && response.body() != null) {
                    _attendancesState.value = AllAttendancesState.Success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string()
                    _attendancesState.value = AllAttendancesState.Error(
                        parseError(errorBody) ?: "Неизвестная ошибка"
                    )
                }
            } catch (e: Exception) {
                _attendancesState.value = AllAttendancesState.Error(
                    e.localizedMessage ?: "Ошибка загрузки"
                )
            }
        }
    }

    private fun parseError(errorBody: String?): String? {
        return try {
            errorBody
        } catch (e: Exception) {
            null
        }
    }
} 