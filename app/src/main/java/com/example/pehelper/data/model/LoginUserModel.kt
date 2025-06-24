package com.example.pehelper.data.model

data class LoginUserModel(
    val email: String,
    val password: String
)

data class AuthTokensModel(
    val accessToken: String?,
    val refreshToken: String?
) 