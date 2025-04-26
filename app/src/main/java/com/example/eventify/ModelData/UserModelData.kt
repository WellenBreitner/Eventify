package com.example.eventify.ModelData

data class UserModelData(
    val username: String,
    val userID: String = "",
    val email: String = "",
    val phone: String? = "",
    val role: String = ""
)
