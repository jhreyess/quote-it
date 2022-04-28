package com.example.quoteit.data.network

import com.squareup.moshi.Json

data class UserLoginRequest(
    val email: String = "",
    val password: String = ""
)

data class UserRequest(
    val username: String = "",
    val email: String =  "",
    val password: String = ""
)

data class DatabaseResult(
    @Json(name = "success") val success: Boolean,
    @Json(name = "errors") var errors: Map<String, String>? = null,
)
