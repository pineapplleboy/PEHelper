package com.example.pehelper.presentation.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pehelper.data.model.CreateSportsEventRequest
import com.example.pehelper.data.model.SportsEventModel
import com.example.pehelper.data.network.PEAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

sealed class CreateEventState {
    data object Idle : CreateEventState()
    data object Loading : CreateEventState()
    data object Success : CreateEventState()
    data class Error(val error: String) : CreateEventState()
}

sealed class EventsListState {
    data object Loading : EventsListState()
    data class Success(val events: List<SportsEventModel>) : EventsListState()
    data class Error(val error: String) : EventsListState()
}

sealed class EventDetailState {
    data object Loading : EventDetailState()
    data class Success(val event: SportsEventModel) : EventDetailState()
    data class Error(val error: String) : EventDetailState()
    data object Deleted : EventDetailState()
}

class SportsEventsViewModel : ViewModel(), KoinComponent {
    private val api: PEAPI by inject()
    private val _createState = MutableStateFlow<CreateEventState>(CreateEventState.Idle)
    val createState: StateFlow<CreateEventState> = _createState.asStateFlow()

    private val _eventsState = MutableStateFlow<EventsListState>(EventsListState.Loading)
    val eventsState: StateFlow<EventsListState> = _eventsState.asStateFlow()

    private val _eventDetailState = MutableStateFlow<EventDetailState>(EventDetailState.Loading)
    val eventDetailState: StateFlow<EventDetailState> = _eventDetailState.asStateFlow()

    fun createEvent(request: CreateSportsEventRequest) {
        viewModelScope.launch {
            _createState.value = CreateEventState.Loading
            try {
                api.createSportsEvent(request)
                _createState.value = CreateEventState.Success
                getEvents()
            } catch (e: Exception) {
                _createState.value = CreateEventState.Error(e.localizedMessage ?: "Ошибка создания")
            }
        }
    }

    fun getEvents() {
        viewModelScope.launch {
            _eventsState.value = EventsListState.Loading
            try {
                val response = api.getSportsEvents()
                _eventsState.value = EventsListState.Success(response.events)
            } catch (e: Exception) {
                _eventsState.value = EventsListState.Error(e.localizedMessage ?: "Ошибка загрузки")
            }
        }
    }

    fun getEventById(id: String) {
        viewModelScope.launch {
            _eventDetailState.value = EventDetailState.Loading
            try {
                val event = api.getSportsEventById(id)
                _eventDetailState.value = EventDetailState.Success(event)
            } catch (e: Exception) {
                _eventDetailState.value = EventDetailState.Error(e.localizedMessage ?: "Ошибка загрузки")
            }
        }
    }

    fun deleteEvent(id: String) {
        viewModelScope.launch {
            _eventDetailState.value = EventDetailState.Loading
            try {
                api.deleteSportsEvent(id)
                _eventDetailState.value = EventDetailState.Deleted
                getEvents()
            } catch (e: Exception) {
                _eventDetailState.value = EventDetailState.Error(e.localizedMessage ?: "Ошибка удаления")
            }
        }
    }

    fun updateAttendanceStatus(eventId: String, userId: String, status: String) {
        viewModelScope.launch {
            try {
                api.updateAttendanceStatus(eventId, userId, status)
                getEventById(eventId)
            } catch (_: Exception) {

            }
        }
    }
} 