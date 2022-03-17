package com.example.quoteit.data.models

data class Folder(
    val title: String,
    val quantity: Int = 0,
    val quotes: List<Quote> = listOf()
)