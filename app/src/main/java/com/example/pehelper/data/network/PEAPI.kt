package com.example.pehelper.data.network

import com.example.pehelper.data.model.LoginUserModel
import com.example.pehelper.data.model.RefreshTokenModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PEAPI {
    @POST("/api/login")
    suspend fun login(@Body body: LoginUserModel): retrofit2.Response<Unit>

    @POST("/api/refresh")
    suspend fun refresh(@Body body: RefreshTokenModel): retrofit2.Response<Unit>

    @POST("/api/logout")
    suspend fun logout(): retrofit2.Response<Unit>

    @GET("/api/profile/student")
    suspend fun getStudentProfile(): retrofit2.Response<Unit>

    @GET("/api/profile/teacher")
    suspend fun getTeacherProfile(): retrofit2.Response<Unit>

    @GET("/api/profile/curator")
    suspend fun getCuratorProfile(): retrofit2.Response<Unit>
} 