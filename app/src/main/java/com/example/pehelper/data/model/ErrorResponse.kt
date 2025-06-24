package com.example.pehelper.data.model

data class ErrorResponse(
    val message: String? = null,
    val title: String? = null,
    val detail: String? = null,
    val errors: Map<String, List<String>>? = null
) 