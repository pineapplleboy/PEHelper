package com.example.pehelper.presentation.screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pehelper.data.model.CuratorGroupResponse
import com.example.pehelper.data.model.CreateStudentActivityRequest
import com.example.pehelper.data.network.PEAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.io.FileOutputStream

sealed class CuratorGroupsState {
    object Idle : CuratorGroupsState()
    object Loading : CuratorGroupsState()
    data class Success(val data: CuratorGroupResponse) : CuratorGroupsState()
    data class Error(val error: String) : CuratorGroupsState()
}

sealed class CreateActivityState {
    object Idle : CreateActivityState()
    object Loading : CreateActivityState()
    object Success : CreateActivityState()
    data class Error(val error: String) : CreateActivityState()
}

class CuratorGroupsViewModel : ViewModel(), KoinComponent {
    private val api: PEAPI by inject()
    private val _state = MutableStateFlow<CuratorGroupsState>(CuratorGroupsState.Idle)
    val state: StateFlow<CuratorGroupsState> = _state.asStateFlow()
    
    private val _createActivityState = MutableStateFlow<CreateActivityState>(CreateActivityState.Idle)
    val createActivityState: StateFlow<CreateActivityState> = _createActivityState.asStateFlow()
    
    private val avatarCache = mutableMapOf<String, Uri?>()

    fun loadGroup(groupNumber: String) {
        viewModelScope.launch {
            _state.value = CuratorGroupsState.Loading
            try {
                val response = api.getCuratorGroup(groupNumber)
                _state.value = CuratorGroupsState.Success(response)
            } catch (e: Exception) {
                _state.value = CuratorGroupsState.Error(e.localizedMessage ?: "Ошибка загрузки")
            }
        }
    }
    
    fun createStudentActivity(studentId: String, comment: String, classesAmount: Int) {
        viewModelScope.launch {
            _createActivityState.value = CreateActivityState.Loading
            try {
                val request = CreateStudentActivityRequest(comment, classesAmount)
                val response = api.createStudentActivity(studentId, request)
                
                if (response.isSuccessful) {
                    _createActivityState.value = CreateActivityState.Success
                } else {
                    _createActivityState.value = CreateActivityState.Error("Ошибка создания активности: ${response.code()}")
                }
            } catch (e: Exception) {
                _createActivityState.value = CreateActivityState.Error(e.localizedMessage ?: "Ошибка сети")
            }
        }
    }
    
    fun resetCreateActivityState() {
        _createActivityState.value = CreateActivityState.Idle
    }
    
    suspend fun loadAvatar(context: Context, userId: String): Uri? {
        if (avatarCache.containsKey(userId)) return avatarCache[userId]
        
        return try {
            val response = api.getAvatar(userId)
            
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val bytes = responseBody.bytes()
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    
                    if (bitmap != null) {
                        val tempFile = File.createTempFile("avatar_$userId", ".jpg", context.cacheDir)
                        val outputStream = FileOutputStream(tempFile)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        outputStream.close()
                        
                        val uri = Uri.fromFile(tempFile)
                        avatarCache[userId] = uri
                        uri
                    } else {
                        avatarCache[userId] = null
                        null
                    }
                } else {
                    avatarCache[userId] = null
                    null
                }
            } else {
                avatarCache[userId] = null
                null
            }
        } catch (e: Exception) {
            avatarCache[userId] = null
            null
        }
    }

    fun resetState() {
        _state.value = CuratorGroupsState.Idle
    }
} 