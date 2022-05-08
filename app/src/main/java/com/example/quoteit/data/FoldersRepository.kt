package com.example.quoteit.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.quoteit.data.local.*
import com.example.quoteit.domain.models.Folder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FoldersRepository(
    private val folderDao: FolderDAO,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ){

    val folders: Flow<List<Folder>> = folderDao.getAllFolders().map {
            it.asFolderDomainModel()
        }

    suspend fun insert(folder: DatabaseFolder){
        withContext(ioDispatcher){
            folderDao.insertFolder(folder)
        }
    }

    suspend fun delete(folderId: Long){
        withContext(ioDispatcher) {
            folderDao.deleteFolder(folderId)
        }
    }

    suspend fun updateCount(folderId: Long){
        withContext(ioDispatcher){
            folderDao.updateCount(folderId)
        }
    }

    suspend fun updateName(folderId: Long, str: String){
        withContext(ioDispatcher){
            folderDao.updateName(folderId, str)
        }
    }

}