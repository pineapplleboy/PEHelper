package com.example.pehelper.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class StudentPairsResponse(
	@SerializedName("pairs")
	val pairs: List<StudentPairModel>
)

data class StudentPairModel(
	@SerializedName("id")
	val id: String,
	@SerializedName("pairNumber")
	val pairNumber: Int,
	@SerializedName("teacher")
	val teacher: TeacherInfo,
	@SerializedName("subject")
	val subject: Subject,
	@SerializedName("date")
	val date: String,
	@SerializedName("isAttended")
	val isAttended: Boolean = false,
	@SerializedName("status")
	val status: String? = null
)

data class LessonModel(
	val id: String,
	val title: String,
	val time: String,
	val teacherName: String,
	val isVisited: Boolean = false,
	val status: String? = null
)

@RequiresApi(Build.VERSION_CODES.O)
fun StudentPairModel.toLessonModel(): LessonModel {
	val formattedTime = formatTime(date)
	return LessonModel(
		id = this.id,
        title = subject.name ?: "Предмет не указан",
        time = formattedTime,
        teacherName = teacher.fullName,
        isVisited = isAttended,
        status = this.status
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