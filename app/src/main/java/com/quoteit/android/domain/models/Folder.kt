package com.quoteit.android.domain.models

data class Folder(
    val id: Long,
    val title: String,
    val quantity: Int = 0,
)

data class FolderWQuotes(
    val parentFolder: String,
    val quotes: List<Quote>
)