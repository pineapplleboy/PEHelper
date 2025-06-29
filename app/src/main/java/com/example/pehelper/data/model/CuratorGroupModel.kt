package com.example.pehelper.data.model

data class CuratorGroupResponse(
    val faculty: Faculty?,
    val group: String?,
    val students: List<CuratorGroupStudent>?
)

data class CuratorGroupStudent(
    val id: String?,
    val name: String?,
    val course: Int?,
    val group: String?,
    val role: String?,
    val avatarId: String?
) 