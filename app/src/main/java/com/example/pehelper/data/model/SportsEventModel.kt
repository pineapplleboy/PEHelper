package com.example.pehelper.data.model

import com.google.gson.annotations.SerializedName

data class SportsEventsResponse(
    val events: List<SportsEventModel>
)

data class SportsEventModel(
    val id: String,
    val name: String,
    val classesAmount: Int,
    val description: String,
    val date: String,
    val faculty: Faculty?,
    val attendances: List<Attendance>? = null
)

data class AttendanceProfile(
    @SerializedName("id") val id: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("fullName") val fullName: String?,
    @SerializedName("role") val role: String?,
    @SerializedName("course") val course: Int?,
    @SerializedName("group") val group: String?,
    @SerializedName("faculty") val faculty: Faculty?,
    @SerializedName("classesAmount") val classesAmount: Int?
)

data class Attendance(
    @SerializedName("profile") val profile: AttendanceProfile,
    @SerializedName("status") val status: String
)