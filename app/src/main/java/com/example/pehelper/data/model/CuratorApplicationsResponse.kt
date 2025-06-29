package com.example.pehelper.data.model

import com.google.gson.annotations.SerializedName

data class CuratorApplicationsResponse(
    @SerializedName("attendances")
    val applications: List<AttendanceApplication>
)

data class AttendanceApplication(
    @SerializedName("id")
    val id: String,
    @SerializedName("event")
    val event: SportsEventModel,
    @SerializedName("profile")
    val profile: StudentProfileModel,
    @SerializedName("status")
    val status: String,
    @SerializedName("createdAt")
    val createdAt: String
) 