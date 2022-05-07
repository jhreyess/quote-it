package com.example.quoteit.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface FolderQuoteDAO {

    @Transaction
    @Query("SELECT * FROM folder WHERE folderId = :id")
    suspend fun getFolderWithQuotes(id: Long): List<FolderWithQuotes>

}