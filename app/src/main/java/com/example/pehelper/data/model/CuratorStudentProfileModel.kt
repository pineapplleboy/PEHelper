package com.example.pehelper.data.model

data class CuratorStudentProfileResponse(
    val student: CuratorStudentProfile,
    val pairs: List<CuratorStudentPair>,
    val otherActivities: List<CuratorStudentOtherActivity>,
    val events: List<CuratorStudentEvent>
)

data class CuratorStudentProfile(
    val id: String,
    val email: String?,
    val fullName: String?,
    val role: String?,
    val course: Int?,
    val group: String?,
    val faculty: Faculty?,
    val classesAmount: Int?,
    val avatarId: String?
)

data class CuratorStudentPair(
    val classesAmount: Int?,
    val pair: CuratorStudentPairInfo,
    val status: String?
)

data class CuratorStudentPairInfo(
    val id: String,
    val pairNumber: Int?,
    val teacher: CuratorStudentTeacher?,
    val subject: CuratorStudentSubject?,
    val date: String?
)

data class CuratorStudentTeacher(
    val id: String,
    val email: String?,
    val fullName: String?,
    val role: String?,
    val subjects: List<CuratorStudentSubject>?,
    val avatarId: String?
)

data class CuratorStudentSubject(
    val id: String,
    val name: String?
)

data class CuratorStudentOtherActivity(
    val id: String,
    val comment: String?,
    val classesAmount: Int?,
    val date: String?
)

data class CuratorStudentEvent(
    val event: CuratorStudentEventInfo,
    val status: String?
)

data class CuratorStudentEventInfo(
    val id: String,
    val name: String?,
    val classesAmount: Int?,
    val description: String?,
    val date: String?,
    val faculty: Faculty?
) 