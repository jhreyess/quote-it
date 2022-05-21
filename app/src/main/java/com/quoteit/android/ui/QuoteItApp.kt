package com.quoteit.android.ui

import android.app.Application
import com.quoteit.android.data.*
import com.quoteit.android.data.local.AppDatabase
import com.quoteit.android.data.network.AuthenticationApi
import com.quoteit.android.data.network.DatabaseApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class QuoteItApp : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob())

    private val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val usersRepository by lazy { UsersRepository(AuthenticationApi.retrofitService, DatabaseApi.retrofitService) }
    val foldersRepository by lazy { FoldersRepository(database.folderDao()) }
    val foldersQuoteRepository by lazy { FolderQuoteRepository(database.folderQuoteDao()) }
    val quotesRepository by lazy { QuotesRepository(database.quoteDao()) }
    val postsRepository by lazy { PostsRepository(database.postDao(), DatabaseApi.retrofitService) }

    fun clearDatabase(){
        database.clearAllTables()
        AppDatabase.populateDatabase(database.folderDao())
    }
}
