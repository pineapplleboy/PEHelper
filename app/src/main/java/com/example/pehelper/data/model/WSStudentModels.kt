package com.example.pehelper.data.model

import com.google.gson.annotations.SerializedName

data class WSStudent(
	@SerializedName("Id") val id: String?,
	@SerializedName("Name") val name: String?,
	@SerializedName("Course") val course: Int?,
	@SerializedName("Group") val group: String?,
	@SerializedName("Role") val role: Int?,
	@SerializedName("AvatarId") val avatarId: String?
)

data class WSPair(
	@SerializedName("Id") val id: String?
)

data class WSAttendanceData(
	@SerializedName("Pair") val pair: WSPair,
	@SerializedName("Student") val student: WSStudent
)

data class WSMessage(
	@SerializedName("Message") val message: String,
	@SerializedName("Data") val data: WSAttendanceData
)