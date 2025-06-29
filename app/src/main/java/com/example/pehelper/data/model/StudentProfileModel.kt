package com.example.pehelper.data.model

import com.google.gson.annotations.SerializedName

data class Faculty(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?
)

data class StudentProfileModel(
    @SerializedName("id")
    val id: String?,
    @SerializedName("email")
    val email: String,
    @SerializedName("fullName")
    val fullName: String,
    @SerializedName("role")
    val role: String,
    @SerializedName("course")
    val course: Int,
    @SerializedName("group")
    val group: String,
    @SerializedName("faculty")
    val faculty: Faculty,
    @SerializedName("classesAmount")
    val classesAmount: Int,
    @SerializedName("avatarId")
    val avatarId: String?,
    @SerializedName("pairs")
    val pairs: List<StudentPairModel>,
    @SerializedName("otherActivities")
    val otherActivities: List<OtherActivityModel>,
    @SerializedName("events")
    val events: List<StudentEventModel>
)

data class OtherActivityModel(
    @SerializedName("id")
    val id: String?,
    @SerializedName("comment")
    val comment: String,
    @SerializedName("classesAmount")
    val classesAmount: Int,
    @SerializedName("date")
    val date: String
)

data class StudentEventModel(
    @SerializedName("event")
    val event: SportsEventModel,
    @SerializedName("status")
    val status: String
)