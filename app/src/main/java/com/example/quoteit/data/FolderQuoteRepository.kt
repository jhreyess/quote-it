package com.example.quoteit.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.quoteit.data.local.AppDatabase
import com.example.quoteit.data.local.asDomainModel
import com.example.quoteit.domain.models.Folder
import com.example.quoteit.domain.models.FolderWQuotes
import com.example.quoteit.domain.models.Quote
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FolderQuoteRepository(
    private val localDatabase: AppDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun getFolderWithQuotes(id: Long): FolderWQuotes {
        return withContext(ioDispatcher){
            localDatabase.folderQuoteDao().getFolderWithQuotes(id)
                .first().asDomainModel()
        }
    }

}