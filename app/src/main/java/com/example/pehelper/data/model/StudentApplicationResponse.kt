package com.example.pehelper.data.model

import com.google.gson.annotations.SerializedName

data class StudentApplicationResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("isApplied")
    val isApplied: Boolean
) 