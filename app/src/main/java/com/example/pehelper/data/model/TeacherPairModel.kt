package com.example.pehelper.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class TeacherPairsResponse(
	@SerializedName("pairs")
	val pairs: List<TeacherPairModel>
)

data class TeacherPairModel(
	@SerializedName("id")
	val id: String,
	@SerializedName("pairNumber")
	val pairNumber: Int,
	@SerializedName("teacher")
	val teacher: TeacherInfo,
	@SerializedName("subject")
	val subject: Subject,
	@SerializedName("date")
	val date: String
)

data class TeacherInfo(
	@SerializedName("id")
	val id: String,
	@SerializedName("email")
	val email: String,
	@SerializedName("fullName")
	val fullName: String,
	@SerializedName("role")
	val role: String,
	@SerializedName("subjects")
	val subjects: List<Subject>
)

@RequiresApi(Build.VERSION_CODES.O)
fun TeacherPairModel.toLessonModel(): LessonModel {
	val formattedTime = formatTime(date)
	return LessonModel(
		title = subject.name ?: "Предмет не указан",
		time = formattedTime,
		teacherName = teacher.fullName ?: "Преподаватель не указан",
		isVisited = false
	)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatTime(dateString: String): String {
	return try {
		val dateTime = LocalDateTime.parse(dateString.replace("Z", ""))
		val formatter = DateTimeFormatter.ofPattern("HH:mm")
		dateTime.format(formatter)
	} catch (e: Exception) {
		"Время не указано"
	}
}