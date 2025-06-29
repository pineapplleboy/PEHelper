package com.example.pehelper.presentation.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pehelper.data.model.StudentEventModel
import com.example.pehelper.data.network.PEAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

sealed class StudentEventDetailState {
	data object Loading : StudentEventDetailState()
	data class Success(val event: StudentEventModel) : StudentEventDetailState()
	data class Error(val error: String) : StudentEventDetailState()
}

class StudentEventDetailViewModel : ViewModel(), KoinComponent {
	private val api: PEAPI by inject()

	private val _eventState = MutableStateFlow<StudentEventDetailState>(StudentEventDetailState.Loading)
	val eventState: StateFlow<StudentEventDetailState> = _eventState.asStateFlow()

	private val _applicationState = MutableStateFlow<ApplicationState>(ApplicationState.Idle)
	val applicationState: StateFlow<ApplicationState> = _applicationState.asStateFlow()

	fun getEventDetail(eventId: String) {
		viewModelScope.launch {
			_eventState.value = StudentEventDetailState.Loading
			try {
				val response = api.getStudentEvents()
				val event = response.events.find { it.id == eventId }
				
				if (event != null) {
					try {
						val applicationResponse = api.getStudentApplication(eventId)
						val eventWithStatus = event.copy(
							isApplied = applicationResponse.isApplied,
							status = applicationResponse.status
						)
						_eventState.value = StudentEventDetailState.Success(eventWithStatus)
					} catch (e: Exception) {
						_eventState.value = StudentEventDetailState.Success(event)
					}
				} else {
					_eventState.value = StudentEventDetailState.Error("Мероприятие не найдено")
				}
			} catch (e: Exception) {
				_eventState.value = StudentEventDetailState.Error(e.localizedMessage ?: "Ошибка загрузки")
			}
		}
	}

	fun createApplication(eventId: String) {
		viewModelScope.launch {
			_applicationState.value = ApplicationState.Loading
			try {
				val response = api.createStudentApplication(eventId)
				if (response.isSuccessful) {
					_applicationState.value = ApplicationState.Success
					getEventDetail(eventId)
				} else {
					_applicationState.value = ApplicationState.Error("Ошибка сервера: ${response.code()}")
				}
			} catch (e: Exception) {
				_applicationState.value = ApplicationState.Error(e.localizedMessage ?: "Ошибка подачи заявки")
			}
		}
	}

	fun deleteApplication(eventId: String) {
		viewModelScope.launch {
			_applicationState.value = ApplicationState.Loading
			try {
				val response = api.deleteStudentApplication(eventId)
				if (response.isSuccessful) {
					_applicationState.value = ApplicationState.Success
					getEventDetail(eventId)
				} else {
					_applicationState.value = ApplicationState.Error("Ошибка сервера: ${response.code()}")
				}
			} catch (e: Exception) {
				_applicationState.value = ApplicationState.Error(e.localizedMessage ?: "Ошибка отмены заявки")
			}
		}
	}

	fun resetApplicationState() {
		_applicationState.value = ApplicationState.Idle
	}
} 