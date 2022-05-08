package com.example.quoteit.data.local

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FolderQuoteDAO {

    @Transaction
    @Query("SELECT * FROM folder WHERE folderId = :id")
    fun getFolderWithQuotes(id: Long): LiveData<FolderWithQuotes>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFolderQuote(join: FolderQuoteCrossRef)

    @Delete
    fun deleteFolderQuote(join: FolderQuoteCrossRef)

}