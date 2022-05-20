package com.quoteit.android.data

import com.quoteit.android.data.local.FolderDAO
import com.quoteit.android.data.local.FolderEntity
import com.quoteit.android.data.local.asFolderDomainModel
import com.quoteit.android.domain.models.Folder
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

    suspend fun insert(folderEntity: FolderEntity){
        withContext(ioDispatcher){
            folderDao.insertFolder(folderEntity)
        }
    }

    suspend fun delete(folderId: Long){
        withContext(ioDispatcher) {
            folderDao.deleteFolder(folderId)
        }
    }

    suspend fun updateCount(folderId: Long){
        withContext(ioDispatcher){
            when(folderId) {
                1L -> folderDao.updateFavCount(folderId)
                2L -> folderDao.updateUserCount(folderId)
                else -> folderDao.updateCount(folderId)
            }
        }
    }

    suspend fun updateAllCount(){
        withContext(ioDispatcher){
            folderDao.updateAllCount()
        }
    }

    suspend fun updateName(folderId: Long, str: String){
        withContext(ioDispatcher){
            folderDao.updateName(folderId, str)
        }
    }

}