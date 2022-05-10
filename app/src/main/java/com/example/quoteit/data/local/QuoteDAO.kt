package com.example.quoteit.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.quoteit.domain.models.Quote
import kotlinx.coroutines.flow.Flow

@Dao
interface QuoteDAO {

    @Query("SELECT * FROM quote")
    fun getAllQuotes(): Flow<List<DatabaseQuote>>

    @Query("SELECT * FROM quote WHERE isFavorite = 1")
    fun getAllFavQuotes(): Flow<List<DatabaseQuote>>

    @Insert
    suspend fun insertQuote(quote: DatabaseQuote): Long

    @Insert
    suspend fun insertAll(quotes: List<DatabaseQuote>)

    @Query("UPDATE quote SET isFavorite = :b WHERE quoteId = :id")
    suspend fun updateState(id: Long, b: Boolean)

    @Query("DELETE FROM quote WHERE quoteId = :id")
    suspend fun deleteQuote(id: Long)

}