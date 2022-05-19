package com.example.quoteit.data.network

import com.squareup.moshi.Json

// User HTTP Bodies
data class UserLoginRequest(
    val email: String = "",
    val password: String = ""
)

data class UserRegisterRequest(
    val username: String = "",
    val email: String =  "",
    val password: String = ""
)

data class UpdatePasswordRequest(
    val password: String = "",
)

data class RefreshTokenRequest(
    val token: String = ""
)

data class LoginResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "username") val username: String? = null,
    @Json(name = "userId") val userId: Long? = null,
    @Json(name = "token") val token: String? = null,
    @Json(name = "refreshToken") val refreshToken: String? = null,
    @Json(name = "error") val error: String? = null
)

data class TokenResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "token") val token: String
)



