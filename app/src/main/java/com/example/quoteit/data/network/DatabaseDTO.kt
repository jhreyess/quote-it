package com.example.quoteit.data.network

import com.example.quoteit.data.local.PostEntity
import com.example.quoteit.domain.models.Post
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


// Responses
sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    data class Loading(val isLoading: Boolean) : Result<Nothing>()
}

data class LoginResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "username") val username: String? = null,
    @Json(name = "error") var error: String? = null,
    @Json(name = "token") var token: String? = null
)

data class PostResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "error") var error: String? = null,
    @Json(name = "data") var data: List<DatabasePost> = listOf()
)

data class DatabasePost(
    val post_id: Long,
    val no_likes: Int = 0,
    val quote_author: String,
    val quote_desc: String,
    val post_by: String,
    val liked: Boolean = false
)

fun DatabasePost.asPostEntity(): PostEntity {
    return PostEntity(post_id, post_by, no_likes, quote_desc, quote_author, liked, true)
}


