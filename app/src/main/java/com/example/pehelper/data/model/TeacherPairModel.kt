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
		id = this.id,
		title = subject.name ?: "Предмет не указан",
		time = formattedTime,
		teacherName = teacher.fullName,
		isVisited = false,
		status = null
	)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatTime(dateString: String): String {
	return try {
		val utcDateTime = LocalDateTime.parse(dateString.replace("Z", ""))
		val utcZonedDateTime = utcDateTime.atZone(java.time.ZoneOffset.UTC)
		val localZonedDateTime = utcZonedDateTime.withZoneSameInstant(java.time.ZoneId.systemDefault())
		val formatter = DateTimeFormatter.ofPattern("HH:mm")
		localZonedDateTime.format(formatter)
	} catch (e: Exception) {
		"Время не указано"
	}
}