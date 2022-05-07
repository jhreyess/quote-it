package com.example.quoteit.data

import com.example.quoteit.domain.models.Folder
import com.example.quoteit.domain.models.Post
import com.example.quoteit.domain.models.Quote

object TestingDatasource {

    val posts: MutableList<Post> = mutableListOf(
        Post("John Doe", "To be or not to be", "Shakespear", 5),
        Post("Jane Doe", "The future belongs to those who believe in the beauty of their dreams ", "Eleanor Roosevelt", 10),
        Post("Richard Roe", "Try to be a rainbow in someone's cloud", "Maya Angelou", 2),
        Post("John Doe", "A mistake repeated more than once is a decision", "Paulo Coelho", 23),
        Post("Jane Doe", "The love you take is equal to the love you make", "Beatles", 38),
    )

}