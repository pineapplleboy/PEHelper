package com.example.pehelper.presentation.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pehelper.data.model.StudentProfileModel
import com.example.pehelper.data.network.PEAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

sealed class StudentProfileState {
    object Loading : StudentProfileState()
    data class Success(val profile: StudentProfileModel) : StudentProfileState()
    data class Error(val message: String) : StudentProfileState()
}

class StudentProfileViewModel : ViewModel(), KoinComponent {
    private val api: PEAPI by inject()
    
    private val _profileState = MutableStateFlow<StudentProfileState>(StudentProfileState.Loading)
    val profileState: StateFlow<StudentProfileState> = _profileState.asStateFlow()
    
    fun getStudentProfile() {
        viewModelScope.launch {
            _profileState.value = StudentProfileState.Loading
            try {
                val response = api.getStudentProfile()
                if (response.isSuccessful) {
                    response.body()?.let { profile ->
                        _profileState.value = StudentProfileState.Success(profile)
                    } ?: run {
                        _profileState.value = StudentProfileState.Error("Пустой ответ от сервера")
                    }
                } else {
                    _profileState.value = StudentProfileState.Error("Ошибка: ${response.code()}")
                }
            } catch (e: Exception) {
                _profileState.value = StudentProfileState.Error("Ошибка сети: ${e.message}")
            }
        }
    }
} 