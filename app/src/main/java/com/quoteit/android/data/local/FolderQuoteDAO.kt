package com.quoteit.android.data.local

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FolderQuoteDAO {

    @Transaction
    @Query("SELECT * FROM folders WHERE folderId = :id")
    fun getFolderWithQuotes(id: Long): LiveData<FolderWithQuotes>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFolderQuote(join: FolderQuoteCrossRef)

    @Delete
    fun deleteFolderQuote(join: FolderQuoteCrossRef)

    @Query("DELETE FROM folderquotecrossref WHERE folderId = :id")
    fun deleteAllFromFolder(id: Long)

    @Query("DELETE FROM folderquotecrossref WHERE quoteId = :id")
    fun deleteAllFromQuote(id: Long)
}