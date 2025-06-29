package com.example.pehelper.data.model

import com.google.gson.annotations.SerializedName

data class AttendanceStatusResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("isAttended")
    val isAttended: Boolean
) 