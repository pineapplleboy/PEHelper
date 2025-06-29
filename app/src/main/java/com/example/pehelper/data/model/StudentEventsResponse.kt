package com.example.pehelper.data.model

import com.google.gson.annotations.SerializedName

data class StudentEventsResponse(
    @SerializedName("events")
    val events: List<StudentEventModel>
)

data class StudentEventModel(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("classesAmount")
    val classesAmount: Int,
    @SerializedName("description")
    val description: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("faculty")
    val faculty: Faculty,
    @SerializedName("isApplied")
    val isApplied: Boolean = false,
    @SerializedName("status")
    val status: String? = null
) 