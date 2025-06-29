package com.example.pehelper.data.network

object ApiConstants {
    const val BASE_URL = "https://b084-77-110-108-130.ngrok-free.app"
    
    // Auth endpoints
    const val LOGIN = "/api/login"
    const val REFRESH = "/api/refresh"
    const val LOGOUT = "/api/logout"
    
    // Profile endpoints
    const val STUDENT_PROFILE = "/api/profile/student"
    const val TEACHER_PROFILE = "/api/profile/teacher"
    const val CURATOR_PROFILE = "/api/profile/curator"
    const val ALL_ATTENDANCES = "/api/profile/all-attendances"
    
    // Session
    const val SESSION = "/api/session"
    
    // Sports events
    const val SPORTS_EVENTS = "/api/sports/events"
    const val CURATOR_EVENTS = "/api/curator/events"
    const val SPORTS_EVENT_BY_ID = "/api/sports/events/{id}"
    const val UPDATE_ATTENDANCE_STATUS = "/api/sports/events/{id}/application"
    
    // Teacher endpoints
    const val TEACHER_PAIRS = "/api/teacher/pairs"
    const val TEACHER_SUBJECTS = "/api/teacher/subjects"
    const val PENDING_ATTENDANCES = "/api/teacher/attendances/pending/{pairId}"
    const val SOLVED_ATTENDANCES = "/api/teacher/attendances/solved/{pairId}"
    const val ACCEPT_ATTENDANCE = "/api/teacher/pairs/{pairId}"
    const val DECLINE_ATTENDANCE = "/api/teacher/pairs/{pairId}"
    
    // Student endpoints
    const val STUDENT_PAIRS = "/api/student/pairs"
    const val STUDENT_EVENTS = "/api/student/events"
    const val STUDENT_APPLICATION = "/api/student/application/{id}"
    const val STUDENT_ATTENDANCE = "/api/student/attendance/{pairId}"
    const val ATTENDANCE_STATUS = "/api/student/attendance/{pairId}"
    
    // Avatar
    const val AVATAR = "/api/avatar"
    
    // Curator endpoints
    const val CURATOR_APPLICATIONS = "/api/curator/event/applications"
    const val APPROVE_APPLICATION = "/api/curator/event/check/{eventId}"
    const val REJECT_APPLICATION = "/api/curator/event/check/{eventId}"
    const val CURATOR_STUDENT_PROFILE = "/api/curator/profile/{studentId}"
    const val CURATOR_GROUP = "/api/curator/group"
    
    // Helper functions
    fun getAvatarUrl(avatarId: String): String {
        return "$BASE_URL$AVATAR?id=$avatarId"
    }
    
    fun getSportsEventUrl(eventId: String): String {
        return SPORTS_EVENT_BY_ID.replace("{id}", eventId)
    }
    
    fun getStudentApplicationUrl(eventId: String): String {
        return STUDENT_APPLICATION.replace("{id}", eventId)
    }
    
    fun getPendingAttendancesUrl(pairId: String): String {
        return PENDING_ATTENDANCES.replace("{pairId}", pairId)
    }
    
    fun getSolvedAttendancesUrl(pairId: String): String {
        return SOLVED_ATTENDANCES.replace("{pairId}", pairId)
    }
    
    fun getAcceptAttendanceUrl(pairId: String): String {
        return ACCEPT_ATTENDANCE.replace("{pairId}", pairId)
    }
    
    fun getDeclineAttendanceUrl(pairId: String): String {
        return DECLINE_ATTENDANCE.replace("{pairId}", pairId)
    }
    
    fun getStudentAttendanceUrl(pairId: String): String {
        return STUDENT_ATTENDANCE.replace("{pairId}", pairId)
    }
    
    fun getAttendanceStatusUrl(pairId: String): String {
        return ATTENDANCE_STATUS.replace("{pairId}", pairId)
    }
    
    fun getApproveApplicationUrl(eventId: String): String {
        return APPROVE_APPLICATION.replace("{eventId}", eventId)
    }
    
    fun getRejectApplicationUrl(eventId: String): String {
        return REJECT_APPLICATION.replace("{eventId}", eventId)
    }
    
    fun getCuratorStudentProfileUrl(studentId: String): String {
        return CURATOR_STUDENT_PROFILE.replace("{studentId}", studentId)
    }
} 