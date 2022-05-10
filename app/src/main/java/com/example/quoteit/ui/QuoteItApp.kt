package com.example.quoteit.ui

import android.app.Application
import com.example.quoteit.data.FolderQuoteRepository
import com.example.quoteit.data.FoldersRepository
import com.example.quoteit.data.QuotesRepository
import com.example.quoteit.data.UsersRepository
import com.example.quoteit.data.local.AppDatabase
import com.example.quoteit.data.network.DatabaseApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class QuoteItApp : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob())

    private val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val usersRepository by lazy { UsersRepository(DatabaseApi.retrofitService) }
    val foldersRepository by lazy { FoldersRepository(database.folderDao()) }
    val foldersQuoteRepository by lazy { FolderQuoteRepository(database.folderQuoteDao()) }
    val quotesRepository by lazy { QuotesRepository(database.quoteDao()) }
}
