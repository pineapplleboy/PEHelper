package com.example.pehelper.data.model

import com.google.gson.annotations.SerializedName

data class Subject(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?
)

data class TeacherProfileModel(
    @SerializedName("fullName")
    val fullName: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("subjects")
    val subjects: List<Subject>? = null
) 