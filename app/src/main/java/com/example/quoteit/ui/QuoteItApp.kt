package com.example.quoteit.ui

import android.app.Application
import com.example.quoteit.data.*
import com.example.quoteit.data.local.AppDatabase
import com.example.quoteit.data.network.AuthenticationApi
import com.example.quoteit.data.network.DatabaseApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class QuoteItApp : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob())

    private val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val usersRepository by lazy { UsersRepository(AuthenticationApi.retrofitService) }
    val foldersRepository by lazy { FoldersRepository(database.folderDao()) }
    val foldersQuoteRepository by lazy { FolderQuoteRepository(database.folderQuoteDao()) }
    val quotesRepository by lazy { QuotesRepository(database.quoteDao()) }
    val postsRepository by lazy { PostsRepository(database.postDao(), DatabaseApi.retrofitService) }
}
