package com.example.pehelper.presentation.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pehelper.data.model.SportsEventModel
import com.example.pehelper.data.network.PEAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CuratorEventsViewModel : ViewModel(), KoinComponent {
    private val api: PEAPI by inject()
    
    private val _eventsState = MutableStateFlow<EventsListState>(EventsListState.Loading)
    val eventsState: StateFlow<EventsListState> = _eventsState.asStateFlow()

    fun getEvents() {
        viewModelScope.launch {
            _eventsState.value = EventsListState.Loading
            try {
                val response = api.getCuratorEvents()
                _eventsState.value = EventsListState.Success(response.events)
            } catch (e: Exception) {
                _eventsState.value = EventsListState.Error(e.localizedMessage ?: "Ошибка загрузки")
            }
        }
    }
} 