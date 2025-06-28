package com.example.pehelper.presentation.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pehelper.data.model.Subject
import com.example.pehelper.data.model.TeacherPairModel
import com.example.pehelper.data.network.PEAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

sealed class TeacherPairsState {
	data object Loading : TeacherPairsState()
	data class Success(val pairs: List<TeacherPairModel>) : TeacherPairsState()
	data class Error(val error: String) : TeacherPairsState()
}

sealed class TeacherSubjectsState {
	object Loading : TeacherSubjectsState()
	data class Success(val subjects: List<Subject>) : TeacherSubjectsState()
	data class Error(val error: String) : TeacherSubjectsState()
}

sealed class CreatePairState {
	object Idle : CreatePairState()
	object Loading : CreatePairState()
	object Success : CreatePairState()
	data class Error(val error: String) : CreatePairState()
}

class TeacherPairsViewModel : ViewModel(), KoinComponent {
	private val api: PEAPI by inject()

	private val _pairsState = MutableStateFlow<TeacherPairsState>(TeacherPairsState.Loading)
	val pairsState: StateFlow<TeacherPairsState> = _pairsState.asStateFlow()

	private val _subjectsState = MutableStateFlow<TeacherSubjectsState>(TeacherSubjectsState.Loading)
	val subjectsState: StateFlow<TeacherSubjectsState> = _subjectsState.asStateFlow()

	private val _createPairState = MutableStateFlow<CreatePairState>(CreatePairState.Idle)
	val createPairState: StateFlow<CreatePairState> = _createPairState.asStateFlow()

	fun getPairs() {
		viewModelScope.launch {
			_pairsState.value = TeacherPairsState.Loading
			try {
				val response = api.getTeacherPairs()
				_pairsState.value = TeacherPairsState.Success(response.pairs)
			} catch (e: Exception) {
				_pairsState.value = TeacherPairsState.Error(e.localizedMessage ?: "Ошибка загрузки")
			}
		}
	}

	fun getTeacherSubjects() {
		viewModelScope.launch {
			_subjectsState.value = TeacherSubjectsState.Loading
			try {
				val response = api.getTeacherSubjects()
				_subjectsState.value = TeacherSubjectsState.Success(response.subjects)
			} catch (e: Exception) {
				_subjectsState.value = TeacherSubjectsState.Error(e.localizedMessage ?: "Ошибка загрузки предметов")
			}
		}
	}

	fun createPair(subjectId: String) {
		viewModelScope.launch {
			_createPairState.value = CreatePairState.Loading
			try {
				api.createTeacherPair(subjectId)
				_createPairState.value = CreatePairState.Success
				getPairs()
			} catch (e: Exception) {
				_createPairState.value = CreatePairState.Error(e.localizedMessage ?: "Ошибка создания пары")
			}
		}
	}
}