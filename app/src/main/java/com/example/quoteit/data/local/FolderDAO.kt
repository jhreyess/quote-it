package com.example.quoteit.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FolderDAO {

    @Query("SELECT * FROM folder")
    fun getAllFolders(): LiveData<List<DatabaseFolder>>

    @Insert
    fun insertFolder(folder: DatabaseFolder)

    @Delete
    fun deleteFolder(folder: DatabaseFolder)
}