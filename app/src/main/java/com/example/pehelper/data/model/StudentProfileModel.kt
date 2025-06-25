package com.example.pehelper.data.model

import com.google.gson.annotations.SerializedName

data class Faculty(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?
)

data class StudentProfileModel(
    @SerializedName("id") val id: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("fullName") val fullName: String?,
    @SerializedName("role") val role: String?,
    @SerializedName("course") val course: Int?,
    @SerializedName("group") val group: String?,
    @SerializedName("faculty") val faculty: Faculty?,
    @SerializedName("classesAmount") val classesAmount: Int?,
    @SerializedName("appointmentDate") val appointmentDate: String? = null
) 