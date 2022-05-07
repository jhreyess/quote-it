package com.example.quoteit.domain.models

data class Post(
    val user: String,
    val quote: String,
    val author: String,
    val likes: Int
)