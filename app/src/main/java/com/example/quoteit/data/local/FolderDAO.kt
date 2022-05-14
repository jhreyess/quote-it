package com.example.quoteit.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDAO {

    @Query("SELECT * FROM folders")
    fun getAllFolders(): Flow<List<FolderEntity>>

    @Insert
    fun insertFolder(folderEntity: FolderEntity)

    @Query("DELETE FROM folders WHERE folderId = :id")
    fun deleteFolder(id: Long)

    @Query("UPDATE folders SET folderQty = (SELECT COUNT(*) FROM folderquotecrossref WHERE folderId = :id) WHERE folderId = :id")
    fun updateCount(id: Long)

    @Query("UPDATE folders SET folderQty = (SELECT COUNT(*) FROM folderquotecrossref WHERE folderId = folders.folderId)")
    fun updateAllCount()

    @Query("UPDATE folders SET folderQty = (SELECT COUNT(*) FROM quotes WHERE quotes.isFavorite = 1) WHERE folderId = :id")
    fun updateFavCount(id: Long)

    @Query("UPDATE folders SET folderQty = (SELECT COUNT(*) FROM quotes) WHERE folderId = :id")
    fun updateUserCount(id: Long)

    @Query("UPDATE folders SET folderName = :newName WHERE folderId = :id")
    fun updateName(id: Long, newName: String)
}