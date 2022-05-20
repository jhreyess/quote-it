package com.quoteit.android.domain.models

data class Post(
    val id: Long,
    val user: String,
    val quote: String,
    val author: String,
    var likes: Int,
    var isLiked: Boolean,
    val creatorActions: Boolean
)

data class NewPost(
    val quoteAuthor: String,
    val quoteDesc: String,
)