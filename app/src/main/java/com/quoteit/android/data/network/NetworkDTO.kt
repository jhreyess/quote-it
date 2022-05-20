package com.quoteit.android.data.network

import com.squareup.moshi.Json

data class AuthorApi(
    @Json(name = "name") val name: String,
    @Json(name = "url") val url: String
)

data class QuoteApi(
    @Json(name = "content") val content: String,
    @Json(name = "originator") val author: AuthorApi
)