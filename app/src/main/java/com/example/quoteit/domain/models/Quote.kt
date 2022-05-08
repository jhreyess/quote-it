package com.example.quoteit.domain.models

data class Quote(
    val id: Long,
    val author: String,
    val quote: String,
    val isFavorite: Boolean
)