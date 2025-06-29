package com.example.pehelper.data.model

import com.google.gson.annotations.SerializedName

data class AllAttendancesResponse(
    @SerializedName("student")
    val student: StudentProfileModel,
    @SerializedName("pairs")
    val pairs: List<PairAttendance>,
    @SerializedName("otherActivities")
    val otherActivities: List<Any> = emptyList(),
    @SerializedName("events")
    val events: List<EventAttendance>
)

data class PairAttendance(
    @SerializedName("classesAmount")
    val classesAmount: Int,
    @SerializedName("pair")
    val pair: PairInfo,
    @SerializedName("status")
    val status: String
)

data class PairInfo(
    @SerializedName("id")
    val id: String,
    @SerializedName("pairNumber")
    val pairNumber: Int,
    @SerializedName("teacher")
    val teacher: TeacherInfo,
    @SerializedName("subject")
    val subject: Subject,
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