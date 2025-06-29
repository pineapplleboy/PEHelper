package com.example.pehelper.data.model

import com.google.gson.annotations.SerializedName

data class AttendanceStudent(
	@SerializedName("id") val id: String?,
	@SerializedName("name") val name: String?,
	@SerializedName("course") val course: Int?,
	@SerializedName("group") val group: String?,
	@SerializedName("role") val role: String?,
	@SerializedName("avatarId") val avatarId: String?
)

data class AttendanceModel(
	@SerializedName("classesAmount") val classesAmount: Int?,
	@SerializedName("status") val status: String?,
	@SerializedName("student") val student: AttendanceStudent?
)

data class AttendanceResponse(
	@SerializedName("attendances") val attendances: List<AttendanceModel>
)