package com.quoteit.android.domain.models

data class Quote(
    val id: Long,
    val author: String,
    val quote: String,
    val isFavorite: Boolean
)