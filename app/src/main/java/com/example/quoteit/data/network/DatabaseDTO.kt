package com.example.quoteit.data.network

import com.squareup.moshi.Json

data class UserLoginRequest(
    val email: String = "",
    val password: String = ""
)

data class UserRegisterRequest(
    val username: String = "",
    val email: String =  "",
    val password: String = ""
)

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}

data class LoginResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "error") var error: String? = null,
)
