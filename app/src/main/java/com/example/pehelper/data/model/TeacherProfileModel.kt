package com.example.pehelper.data.model

import com.google.gson.annotations.SerializedName

data class Subject(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?
)

data class TeacherSubjectsResponse(
    @SerializedName("subjects")
    val subjects: List<Subject>
)

data class TeacherProfileModel(
    @SerializedName("id")
    val id: String?,
    @SerializedName("fullName")
    val fullName: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("role")
    val role: String?,
    @SerializedName("avatarId")
    val avatarId: String?,
    @SerializedName("subjects")
    val subjects: List<Subject>? = null
) 