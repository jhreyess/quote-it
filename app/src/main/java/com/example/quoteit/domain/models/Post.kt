package com.example.quoteit.domain.models

data class Post(
    val id: Long,
    val user: String,
    val quote: String,
    val author: String,
    var likes: Int,
    val isLiked: Boolean
)

data class NewPost(
    val quoteAuthor: String,
    val quoteDesc: String,
)