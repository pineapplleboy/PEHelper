package com.example.pehelper.data.model

import com.google.gson.annotations.SerializedName

data class CreateStudentActivityRequest(
    @SerializedName("comment")
    val comment: String,
    @SerializedName("classesAmount")
    val classesAmount: Int
) 