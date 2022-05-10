package com.example.quoteit.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDAO {

    @Query("SELECT * FROM folder")
    fun getAllFolders(): Flow<List<DatabaseFolder>>

    @Insert
    fun insertFolder(folder: DatabaseFolder)

    @Query("DELETE FROM folder WHERE folderId = :id")
    fun deleteFolder(id: Long)

    @Query("UPDATE folder SET folderQty = (SELECT COUNT(*) FROM folderquotecrossref WHERE folderId = :id) WHERE folderId = :id")
    fun updateCount(id: Long)

    @Query("UPDATE folder SET folderQty = (SELECT COUNT(*) FROM folderquotecrossref WHERE folderId = folder.folderId)")
    fun updateAllCount()

    @Query("UPDATE folder SET folderQty = (SELECT COUNT(*) FROM quote WHERE quote.isFavorite = 1) WHERE folderId = :id")
    fun updateFavCount(id: Long)

    @Query("UPDATE folder SET folderQty = (SELECT COUNT(*) FROM quote) WHERE folderId = :id")
    fun updateUserCount(id: Long)

    @Query("UPDATE folder SET folderName = :newName WHERE folderId = :id")
    fun updateName(id: Long, newName: String)
}