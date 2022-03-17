package com.example.quoteit.network

import com.squareup.moshi.Json

data class Author(
    @Json(name = "name") val name: String,
    @Json(name = "url") val url: String
)

data class Quote(
    @Json(name = "content") val content: String,
    @Json(name = "originator") val author: Author
)