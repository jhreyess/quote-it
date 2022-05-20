package com.quoteit.android.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuoteDAO {

    @Query("SELECT * FROM quotes")
    fun getAllQuotes(): Flow<List<QuoteEntity>>

    @Query("SELECT * FROM quotes WHERE isFavorite = 1")
    fun getAllFavQuotes(): Flow<List<QuoteEntity>>

    @Query("SELECT * FROM quotes WHERE quoteId = :id")
    fun getQuote(id: Long): QuoteEntity

    @Insert
    suspend fun insertQuote(quoteEntity: QuoteEntity): Long

    @Insert
    suspend fun insertAll(quoteEntities: List<QuoteEntity>)

    @Query("UPDATE quotes SET isFavorite = :b WHERE quoteId = :id")
    suspend fun updateState(id: Long, b: Boolean)

    @Query("DELETE FROM quotes WHERE quoteId = :id")
    suspend fun deleteQuote(id: Long)

}