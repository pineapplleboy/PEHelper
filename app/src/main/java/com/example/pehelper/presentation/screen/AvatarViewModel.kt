package com.example.pehelper.presentation.screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pehelper.data.network.PEAPI
import com.example.pehelper.utils.ImageUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.ByteArrayInputStream

sealed class AvatarState {
    data object Idle : AvatarState()
    data object Loading : AvatarState()
    data object Success : AvatarState()
    data class Error(val error: String) : AvatarState()
}

sealed class AvatarLoadState {
    data object Idle : AvatarLoadState()
    data object Loading : AvatarLoadState()
    data class Success(val avatarUri: Uri?) : AvatarLoadState()
    data class Error(val error: String) : AvatarLoadState()
}

class AvatarViewModel : ViewModel(), KoinComponent {
    private val api: PEAPI by inject()
    
    private val _avatarState = MutableStateFlow<AvatarState>(AvatarState.Idle)
    val avatarState: StateFlow<AvatarState> = _avatarState.asStateFlow()
    
    private val _avatarLoadState = MutableStateFlow<AvatarLoadState>(AvatarLoadState.Idle)
    val avatarLoadState: StateFlow<AvatarLoadState> = _avatarLoadState.asStateFlow()
    
    fun loadAvatar(userId: String) {
        viewModelScope.launch {
            _avatarLoadState.value = AvatarLoadState.Loading
            
            try {
                val response = api.getAvatar(userId)
                
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val bytes = responseBody.bytes()
                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        
                        if (bitmap != null) {
                            val tempFile = java.io.File.createTempFile("avatar_", ".jpg")
                            val outputStream = java.io.FileOutputStream(tempFile)
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                            outputStream.close()
                            
                            val avatarUri = Uri.fromFile(tempFile)
                            _avatarLoadState.value = AvatarLoadState.Success(avatarUri)
                        } else {
                            _avatarLoadState.value = AvatarLoadState.Success(null)
                        }
                    } else {
                        _avatarLoadState.value = AvatarLoadState.Success(null)
                    }
                } else {
                    _avatarLoadState.value = AvatarLoadState.Error("Ошибка загрузки: ${response.code()}")
                }
            } catch (e: Exception) {
                _avatarLoadState.value = AvatarLoadState.Error(e.localizedMessage ?: "Ошибка сети")
            }
        }
    }
    
    fun uploadAvatar(context: Context, userId: String, imageUri: Uri, avatarId: String? = null) {
        viewModelScope.launch {
            _avatarState.value = AvatarState.Loading
            
            try {
                val multipartPart = ImageUtils.createAvatarMultipartPart(context, imageUri)
                if (multipartPart != null) {
                    val response = api.uploadAvatar(userId, multipartPart)
                    
                    if (response.isSuccessful) {
                        _avatarState.value = AvatarState.Success
                        avatarId?.let { loadAvatar(it) }
                    } else {
                        _avatarState.value = AvatarState.Error("Ошибка загрузки: ${response.code()}")
                    }
                } else {
                    _avatarState.value = AvatarState.Error("Ошибка обработки изображения")
                }
            } catch (e: Exception) {
                _avatarState.value = AvatarState.Error(e.localizedMessage ?: "Ошибка сети")
            }
        }
    }
    
    fun resetAvatarState() {
        _avatarState.value = AvatarState.Idle
    }
    
    fun resetAvatarLoadState() {
        _avatarLoadState.value = AvatarLoadState.Idle
    }
} 