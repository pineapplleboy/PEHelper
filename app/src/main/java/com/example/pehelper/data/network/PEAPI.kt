package com.example.pehelper.data.network

import com.example.pehelper.data.model.AuthTokensModel
import com.example.pehelper.data.model.CuratorProfileModel
import com.example.pehelper.data.model.LoginUserModel
import com.example.pehelper.data.model.RefreshTokenModel
import com.example.pehelper.data.model.StudentProfileModel
import com.example.pehelper.data.model.TeacherProfileModel
import com.example.pehelper.data.model.CreateSportsEventRequest
import com.example.pehelper.data.model.SportsEventModel
import com.example.pehelper.data.model.SportsEventsResponse
import com.example.pehelper.data.model.TeacherPairsResponse
import com.example.pehelper.data.model.TeacherSubjectsResponse
import com.example.pehelper.data.model.StudentPairsResponse
import com.example.pehelper.data.model.AttendanceStatusResponse
import com.example.pehelper.data.model.AllAttendancesResponse
import com.example.pehelper.data.model.StudentEventsResponse
import com.example.pehelper.data.model.StudentApplicationResponse
import com.example.pehelper.data.model.CuratorApplicationsResponse
import com.example.pehelper.data.model.CuratorStudentProfileResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.http.PUT
import retrofit2.http.Query
import retrofit2.http.Multipart
import retrofit2.http.Part

interface PEAPI {
    @POST("/api/login")
    suspend fun login(@Body body: LoginUserModel): retrofit2.Response<AuthTokensModel>

    @POST("/api/refresh")
    suspend fun refresh(@Body body: RefreshTokenModel): retrofit2.Response<AuthTokensModel>

    @POST("/api/logout")
    suspend fun logout(): retrofit2.Response<Unit>

    @GET("/api/profile/student")
    suspend fun getStudentProfile(): retrofit2.Response<StudentProfileModel>

    @GET("/api/profile/teacher")
    suspend fun getTeacherProfile(): retrofit2.Response<TeacherProfileModel>

    @GET("/api/profile/curator")
    suspend fun getCuratorProfile(): retrofit2.Response<CuratorProfileModel>

    @GET("/api/profile/all-attendances")
    suspend fun getAllAttendances(): retrofit2.Response<AllAttendancesResponse>

    @GET("/api/session")
    suspend fun getSession(): retrofit2.Response<SessionResponse>

    @POST("/api/sports/events")
    suspend fun createSportsEvent(@Body request: CreateSportsEventRequest)

    @GET("/api/sports/events")
    suspend fun getSportsEvents(): SportsEventsResponse

    @GET("/api/sports/events/{id}")
    suspend fun getSportsEventById(@Path("id") id: String): SportsEventModel

    @DELETE("/api/sports/events/{id}")
    suspend fun deleteSportsEvent(@Path("id") id: String)

    @PUT("/api/sports/events/{id}/application")
    suspend fun updateAttendanceStatus(
        @Path("id") eventId: String,
        @Query("userId") userId: String,
        @Query("status") status: String
    )

    @GET("/api/teacher/pairs")
    suspend fun getTeacherPairs(): TeacherPairsResponse

    @GET("/api/teacher/subjects")
    suspend fun getTeacherSubjects(): TeacherSubjectsResponse

    @POST("/api/teacher/pairs")
    suspend fun createTeacherPair(@Query("subjectId") subjectId: String)

    @GET("/api/student/pairs")
    suspend fun getStudentPairs(): StudentPairsResponse

    @GET("/api/student/events")
    suspend fun getStudentEvents(): StudentEventsResponse

    @GET("/api/student/application/{id}")
    suspend fun getStudentApplication(@Path("id") eventId: String): StudentApplicationResponse

    @POST("/api/student/application/{id}")
    suspend fun createStudentApplication(@Path("id") eventId: String): retrofit2.Response<Unit>

    @DELETE("/api/student/application/{id}")
    suspend fun deleteStudentApplication(@Path("id") eventId: String): retrofit2.Response<Unit>

    @POST("/api/student/attendance/{pairId}")
    suspend fun markAttendance(@Path("pairId") pairId: String): retrofit2.Response<Unit>

    @DELETE("/api/student/attendance/{pairId}")
    suspend fun cancelAttendance(@Path("pairId") pairId: String): retrofit2.Response<Unit>

    @GET("/api/student/attendance/{pairId}")
    suspend fun getAttendanceStatus(@Path("pairId") pairId: String): AttendanceStatusResponse

    @Multipart
    @POST("/api/avatar")
    suspend fun uploadAvatar(
        @Query("id") userId: String,
        @Part avatar: okhttp3.MultipartBody.Part
    ): retrofit2.Response<Unit>

    @GET("/api/avatar")
    suspend fun getAvatar(@Query("id") userId: String): retrofit2.Response<okhttp3.ResponseBody>

    @GET("/api/curator/event/applications")
    suspend fun getCuratorApplications(): CuratorApplicationsResponse

    @PUT("/api/curator/event/check/{eventId}")
    suspend fun approveApplication(
        @Path("eventId") eventId: String,
        @Query("studentId") studentId: String
    ): retrofit2.Response<Unit>

    @DELETE("/api/curator/event/check/{eventId}")
    suspend fun rejectApplication(
        @Path("eventId") eventId: String,
        @Query("studentId") studentId: String
    ): retrofit2.Response<Unit>

    @GET("/api/curator/profile/{studentId}")
    suspend fun getCuratorStudentProfile(@Path("studentId") studentId: String): CuratorStudentProfileResponse
}

data class SessionResponse(
    val role: String?
) 