package com.example.pehelper.data.model

import com.google.gson.annotations.SerializedName

data class StudentEventsResponse(
    @SerializedName("events")
    val events: List<SportsEventModel>
)
