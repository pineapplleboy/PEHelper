package com.example.pehelper.presentation.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pehelper.data.model.StudentPairModel
import com.example.pehelper.data.model.StudentEventModel
import com.example.pehelper.data.network.PEAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

sealed class StudentPairsState {
	data object Loading : StudentPairsState()
	data class Success(val pairs: List<StudentPairModel>) : StudentPairsState()
	data class Error(val error: String) : StudentPairsState()
}

sealed class StudentEventsState {
	data object Loading : StudentEventsState()
	data class Success(val events: List<StudentEventModel>) : StudentEventsState()
	data class Error(val error: String) : StudentEventsState()
}

sealed class AttendanceState {
	data object Idle : AttendanceState()
	data object Loading : AttendanceState()
	data object Success : AttendanceState()
	data class Error(val error: String) : AttendanceState()
}

sealed class ApplicationState {
	data object Idle : ApplicationState()
	data object Loading : ApplicationState()
	data object Success : ApplicationState()
	data class Error(val error: String) : ApplicationState()
}

class StudentPairsViewModel : ViewModel(), KoinComponent {
	private val api: PEAPI by inject()

	private val _pairsState = MutableStateFlow<StudentPairsState>(StudentPairsState.Loading)
	val pairsState: StateFlow<StudentPairsState> = _pairsState.asStateFlow()

	private val _eventsState = MutableStateFlow<StudentEventsState>(StudentEventsState.Loading)
	val eventsState: StateFlow<StudentEventsState> = _eventsState.asStateFlow()

	private val _attendanceState = MutableStateFlow<AttendanceState>(AttendanceState.Idle)
	val attendanceState: StateFlow<AttendanceState> = _attendanceState.asStateFlow()

	private val _applicationState = MutableStateFlow<ApplicationState>(ApplicationState.Idle)
	val applicationState: StateFlow<ApplicationState> = _applicationState.asStateFlow()

	fun getPairs() {
		viewModelScope.launch {
			_pairsState.value = StudentPairsState.Loading
			try {
				val response = api.getStudentPairs()
				
				// Получаем статус посещаемости для каждой пары
				val pairsWithStatus = response.pairs.map { pair ->
					try {
						val attendanceResponse = api.getAttendanceStatus(pair.id)
						pair.copy(
							isAttended = attendanceResponse.isAttended,
							status = attendanceResponse.status
						)
					} catch (e: Exception) {
						// Если не удалось получить статус, оставляем исходные данные
						pair
					}
				}
				_pairsState.value = StudentPairsState.Success(pairsWithStatus)
			} catch (e: Exception) {
				_pairsState.value = StudentPairsState.Error(e.localizedMessage ?: "Ошибка загрузки")
			}
		}
	}

	fun getEvents() {
		viewModelScope.launch {
			_eventsState.value = StudentEventsState.Loading
			try {
				val response = api.getStudentEvents()
				
				// Получаем статус заявки для каждого мероприятия
				val eventsWithStatus = response.events.map { event ->
					try {
						val applicationResponse = api.getStudentApplication(event.id)
						event.copy(
							isApplied = applicationResponse.isApplied,
							status = applicationResponse.status
						)
					} catch (e: Exception) {
						// Если не удалось получить статус, оставляем исходные данные
						event
					}
				}
				_eventsState.value = StudentEventsState.Success(eventsWithStatus)
			} catch (e: Exception) {
				_eventsState.value = StudentEventsState.Error(e.localizedMessage ?: "Ошибка загрузки")
			}
		}
	}

	fun markAttendance(pairId: String) {
		viewModelScope.launch {
			_attendanceState.value = AttendanceState.Loading
			try {
				val response = api.markAttendance(pairId)
				if (response.isSuccessful) {
					_attendanceState.value = AttendanceState.Success
					getPairs() // Обновляем список после изменения
				} else {
					_attendanceState.value = AttendanceState.Error("Ошибка сервера: ${response.code()}")
				}
			} catch (e: Exception) {
				_attendanceState.value = AttendanceState.Error(e.localizedMessage ?: "Ошибка отметки")
			}
		}
	}

	fun cancelAttendance(pairId: String) {
		viewModelScope.launch {
			_attendanceState.value = AttendanceState.Loading
			try {
				val response = api.cancelAttendance(pairId)
				if (response.isSuccessful) {
					_attendanceState.value = AttendanceState.Success
					getPairs() // Обновляем список после изменения
				} else {
					_attendanceState.value = AttendanceState.Error("Ошибка сервера: ${response.code()}")
				}
			} catch (e: Exception) {
				_attendanceState.value = AttendanceState.Error(e.localizedMessage ?: "Ошибка отмены")
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
					getEvents() // Обновляем список после изменения
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
					getEvents() // Обновляем список после изменения
				} else {
					_applicationState.value = ApplicationState.Error("Ошибка сервера: ${response.code()}")
				}
			} catch (e: Exception) {
				_applicationState.value = ApplicationState.Error(e.localizedMessage ?: "Ошибка отмены заявки")
			}
		}
	}

	fun resetAttendanceState() {
		_attendanceState.value = AttendanceState.Idle
	}

	fun resetApplicationState() {
		_applicationState.value = ApplicationState.Idle
	}
} 