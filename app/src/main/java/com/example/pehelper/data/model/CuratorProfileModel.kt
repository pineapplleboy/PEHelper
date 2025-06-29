package com.example.pehelper.data.model

import com.google.gson.annotations.SerializedName

data class CuratorProfileModel(
    @SerializedName("id") val id: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("fullName") val fullName: String?,
    @SerializedName("role") val role: String?,
    @SerializedName("avatarId") val avatarId: String?,
    @SerializedName("subjects") val subjects: List<Subject>?,
    @SerializedName("faculties") val faculties: List<Faculty>?
)