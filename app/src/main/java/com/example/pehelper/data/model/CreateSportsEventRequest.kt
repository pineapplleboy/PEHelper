package com.example.pehelper.data.model

data class CreateSportsEventRequest(
    val name: String,
    val classesAmount: Int,
    val description: String,
    val date: String
) 