package com.example.quoteit.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.quoteit.domain.models.Quote

@Dao
interface QuoteDAO {

    @Query("SELECT * FROM quote")
    fun getAllQuotes(): List<DatabaseQuote>

    @Query("SELECT * FROM quote WHERE isFavorite = 1")
    fun getAllFavQuotes(): LiveData<List<DatabaseQuote>>

    @Insert
    suspend fun insertQuote(quote: DatabaseQuote): Long

    @Insert
    suspend fun insertAll(quotes: List<DatabaseQuote>)

    @Query("UPDATE quote SET isFavorite = :b WHERE quoteId = :id")
    suspend fun updateState(id: Long, b: Boolean)

    @Delete
    suspend fun deleteQuote(quote: DatabaseQuote)

}