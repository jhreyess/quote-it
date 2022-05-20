package com.example.quoteit.data.network

import android.provider.Settings
import com.example.quoteit.data.local.PostEntity
import com.squareup.moshi.Json
import java.text.SimpleDateFormat
import java.util.*

// Http Bodies
data class SyncPostRequest(
    val values: List<Long>
)

// Responses
sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    data class Loading(val isLoading: Boolean) : Result<Nothing>()
}

data class PostResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "error") val error: String? = null,
    @Json(name = "data") val data: List<DatabasePost> = listOf()
)

data class DatabasePost(
    val post_id: Long,
    val no_likes: Int = 0,
    val quote_author: String,
    val quote_desc: String,
    val post_by: String,
    val user_id: Long,
    val timestamp: String,
    val liked: Boolean = false,
    val creatorActions: Boolean
)

fun DatabasePost.asPostEntity(): PostEntity {
    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S", Locale.getDefault())
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    val localDate = formatter.parse(timestamp)?.time ?: 0L
    return PostEntity(post_id, post_by, user_id, no_likes,
        quote_desc, quote_author, localDate, liked, true, creatorActions)
}


