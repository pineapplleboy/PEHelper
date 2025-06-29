package com.example.pehelper.presentation.viewmodel

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pehelper.data.model.Attendance
import com.example.pehelper.data.model.AttendanceModel
import com.example.pehelper.data.model.AttendanceResponse
import com.example.pehelper.data.model.WSStudent
import com.example.pehelper.data.model.WSMessage
import com.example.pehelper.data.network.PEAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import com.google.gson.Gson
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.io.FileOutputStream

class LessonStudentsViewModel(private val pairId: String) : ViewModel(), KoinComponent {
	private val api: PEAPI by inject()

	private val _pending = MutableStateFlow<List<AttendanceModel>>(emptyList())
	val pending: StateFlow<List<AttendanceModel>> = _pending.asStateFlow()

	private val _solved = MutableStateFlow<List<AttendanceModel>>(emptyList())
	val solved: StateFlow<List<AttendanceModel>> = _solved.asStateFlow()

	private val gson = Gson()
	private var webSocket: WebSocket? = null

	init {
		loadPending()
		loadSolved()
		connectWebSocket()
	}

	private val avatarCache = mutableMapOf<String, Uri?>()

	suspend fun loadAvatar(context: Context, userId: String): Uri? {
		if (avatarCache.containsKey(userId)) return avatarCache[userId]
		return try {
			val response = api.getAvatar(userId)
			if (response.isSuccessful) {
				val bytes = response.body()?.bytes()
				if (bytes != null) {
					val tempFile = withContext(Dispatchers.IO) {
						File.createTempFile("avatar_${'$'}userId", ".jpg", context.cacheDir)
					}
					FileOutputStream(tempFile).use { it.write(bytes) }
					val uri = Uri.fromFile(tempFile)
					avatarCache[userId] = uri
					uri
				} else null
			} else null
		} catch (e: Exception) {
			null
		}
	}

	fun loadPending() {
		viewModelScope.launch(Dispatchers.IO) {
			try {
				val response = api.getPendingAttendances(pairId)
				_pending.value = response.attendances
			} catch (e: Exception) {
				Log.e("LessonStudentsVM", "Error loading pending", e)
			}
		}
	}

	fun loadSolved() {
		viewModelScope.launch(Dispatchers.IO) {
			try {
				val response = api.getSolvedAttendances(pairId)
				_solved.value = response.attendances
			} catch (e: Exception) {
				Log.e("LessonStudentsVM", "Error loading solved", e)
			}
		}
	}

	private fun connectWebSocket() {
		viewModelScope.launch(Dispatchers.IO) {
			val client = OkHttpClient()
			val request = Request.Builder().url("ws://10.0.2.2:8181").build()
			webSocket = client.newWebSocket(request, object : WebSocketListener() {
				override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
					Log.d("WS", "WebSocket opened: ${'$'}response")
				}
				override fun onMessage(webSocket: WebSocket, text: String) {
					Log.d("WS", "Received message: ${'$'}text")
					try {
						val message = gson.fromJson(text, WSMessage::class.java)
						Log.d("WS", "Parsed message: ${'$'}message")
						if (message.message == "New pair attendance" && message.data.pair.id == pairId) {
							val student = message.data.student
							Log.d("WS", "Adding student: ${'$'}student")
							viewModelScope.launch(Dispatchers.Main) {
								val exists = _pending.value.any { it.student?.id == student.id }
								if (!exists) {
									val newAttendance = AttendanceModel(
										classesAmount = null,
										status = "Pending",
										student = com.example.pehelper.data.model.AttendanceStudent(
											id = student.id,
											name = student.name,
											course = student.course,
											group = student.group,
											role = student.role?.toString(),
											avatarId = student.avatarId
										)
									)
									_pending.value = _pending.value + newAttendance
								}
							}
						}
						if (message.message == "Pair attendance deleted" && message.data.pair.id == pairId) {
							val studentId = message.data.student.id
							viewModelScope.launch(Dispatchers.Main) {
								_pending.value = _pending.value.filter { it.student?.id != studentId }
								_solved.value = _solved.value.filter { it.student?.id != studentId }
							}
						}
					} catch (e: Exception) {
						Log.e("WS", "Error parsing message: ${'$'}text", e)
					}
				}
				override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
					Log.d("WS", "Received bytes: ${'$'}bytes")
				}
				override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
					Log.e("WS", "WebSocket failure", t)
				}
				override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
					Log.d("WS", "WebSocket closing: ${'$'}code, ${'$'}reason")
				}
				override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
					Log.d("WS", "WebSocket closed: ${'$'}code, ${'$'}reason")
				}
			})
		}
	}

	fun acceptAttendance(userId: String, classesAmount: Int = 1) {
		viewModelScope.launch(Dispatchers.IO) {
			try {
				api.acceptAttendance(pairId, userId, classesAmount)
				loadPending()
				loadSolved()
			} catch (e: Exception) {
				Log.e("LessonStudentsVM", "Error accepting attendance", e)
			}
		}
	}

	fun declineAttendance(userId: String) {
		viewModelScope.launch(Dispatchers.IO) {
			try {
				api.declineAttendance(pairId, userId)
				loadPending()
				loadSolved()
			} catch (e: Exception) {
				Log.e("LessonStudentsVM", "Error declining attendance", e)
			}
		}
	}

	override fun onCleared() {
		super.onCleared()
		webSocket?.close(1000, null)
	}
}