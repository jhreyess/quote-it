package com.example.quoteit.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.room.Database
import com.example.quoteit.data.local.AppDatabase
import com.example.quoteit.data.local.DatabaseFolder
import com.example.quoteit.data.local.FolderDAO
import com.example.quoteit.data.local.asDomainModel
import com.example.quoteit.data.network.DatabaseApi
import com.example.quoteit.data.network.DatabaseService
import com.example.quoteit.domain.models.Folder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class FoldersRepository(
    private val folderDao: FolderDAO,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {

    val folders: LiveData<List<Folder>> = Transformations
        .map(folderDao.getAllFolders()){
            it.asDomainModel()
    }

    suspend fun insert(folder: DatabaseFolder){
        withContext(ioDispatcher){
            folderDao.insertFolder(folder)
        }
    }

    suspend fun delete(folder: DatabaseFolder){
        withContext(ioDispatcher) {
            folderDao.deleteFolder(folder)
        }
    }

}