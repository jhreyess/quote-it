package com.example.quoteit.data

import com.example.quoteit.data.models.Folder
import com.example.quoteit.data.models.Post
import com.example.quoteit.data.models.Quote

object TestingDatasource {

    /* TODO: TESTING */
    private val quotes: MutableList<Quote> = mutableListOf(
        Quote("Shakespeare", "To be or not to be"),
        Quote("Shakespeare", "To be or not to be"),
        Quote("Shakespeare", "To be or not to be"),
        Quote("Shakespeare", "To be or not to be"),
        Quote("Shakespeare", "To be or not to be"),
        Quote("Shakespeare", "To be or not to be"),
        Quote("Shakespeare", "To be or not to be"),
        Quote("Shakespeare", "To be or not to be")
    )

    val folders: MutableList<Folder> = mutableListOf(
        Folder("Mis favoritos", 13, quotes),
        Folder("Hechos por mí", 6, quotes),
        Folder("Amor", 2, quotes),
        Folder("Energía", 5, quotes),
        Folder("Estados de ánimo", 3, quotes)
    )

    val posts: MutableList<Post> = mutableListOf(
        Post("John Doe", "To be or not to be", "Shakespear", 5),
        Post("Jane Doe", "The future belongs to those who believe in the beauty of their dreams ", "Eleanor Roosevelt", 10),
        Post("Richard Roe", "Try to be a rainbow in someone's cloud", "Maya Angelou", 2),
        Post("John Doe", "A mistake repeated more than once is a decision", "Paulo Coelho", 23),
        Post("Jane Doe", "The love you take is equal to the love you make", "Beatles", 38),
    )

    val addFolder = {x: Folder -> folders.add(2, x)}
}