package com.example.pehelper.data.model

import com.google.gson.annotations.SerializedName

data class AllAttendancesResponse(
    @SerializedName("student")
    val student: StudentProfileModel,
    @SerializedName("pairs")
    val pairs: List<PairAttendance>,
    @SerializedName("otherActivities")
    val otherActivities: List<OtherActivity> = emptyList(),
    @SerializedName("events")
    val events: List<EventAttendance>
)

data class PairAttendance(
    @SerializedName("classesAmount")
    val classesAmount: Int,
    @SerializedName("pair")
    val pair: TeacherPairModel,
    @SerializedName("status")
    val status: String
)

data class OtherActivity(
    @SerializedName("id")
    val id: String,
    @SerializedName("comment")
    val comment: String,
    @SerializedName("classesAmount")
    val classesAmount: Int,
    @SerializedName("date")
    val date: String
)

data class EventAttendance(
    @SerializedName("event")
    val event: EventInfo,
    @SerializedName("status")
    val status: String
)

data class EventInfo(
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
    val faculty: Faculty
) 