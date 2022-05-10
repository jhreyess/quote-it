package com.example.quoteit.data

import androidx.lifecycle.Transformations
import com.example.quoteit.data.local.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FolderQuoteRepository(
    private val folderQuotesDao: FolderQuoteDAO,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
){

    val foldersWithQuotes = { id: Long ->
        Transformations.map(folderQuotesDao.getFolderWithQuotes(id)){
            it.asFolderWQuotesDomainModel()
        }
    }

    suspend fun insert(folderId: Long, quoteId: Long ){
        withContext(ioDispatcher){
            folderQuotesDao.insertFolderQuote(FolderQuoteCrossRef(folderId, quoteId))
        }
    }

    suspend fun delete(folderId: Long, quoteId: Long){
        withContext(ioDispatcher){
            folderQuotesDao.deleteFolderQuote(FolderQuoteCrossRef(folderId, quoteId))
        }
    }

    suspend fun deleteAllFolder(folderId: Long){
        withContext(ioDispatcher){
            folderQuotesDao.deleteAllFromFolder(folderId)
        }
    }

    suspend fun deleteAllQuote(quoteId: Long){
        withContext(ioDispatcher){
            folderQuotesDao.deleteAllFromQuote(quoteId)
        }
    }
}