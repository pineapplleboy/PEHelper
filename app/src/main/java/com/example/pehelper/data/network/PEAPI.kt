package com.example.pehelper.data.network

import com.example.pehelper.data.model.AuthTokensModel
import com.example.pehelper.data.model.CuratorProfileModel
import com.example.pehelper.data.model.LoginUserModel
import com.example.pehelper.data.model.RefreshTokenModel
import com.example.pehelper.data.model.StudentProfileModel
import com.example.pehelper.data.model.TeacherProfileModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

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

    @GET("/api/session")
    suspend fun getSession(): retrofit2.Response<SessionResponse>
}

data class SessionResponse(
    val role: String?
) 