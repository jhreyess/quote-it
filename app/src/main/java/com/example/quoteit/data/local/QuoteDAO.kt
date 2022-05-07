package com.example.quoteit.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.quoteit.domain.models.Quote

@Dao
interface QuoteDAO {

    @Query("SELECT * FROM quote")
    suspend fun getAllQuotes(): List<DatabaseQuote>

    @Insert
    suspend fun insertQuote(quote: DatabaseQuote)

    @Insert
    suspend fun insertAll(quotes: List<DatabaseQuote>)

    @Delete
    suspend fun deleteQuote(quote: DatabaseQuote)

}