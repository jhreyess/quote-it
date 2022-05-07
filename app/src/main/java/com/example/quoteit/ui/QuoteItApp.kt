package com.example.quoteit.ui

import android.app.Application
import com.example.quoteit.data.FoldersRepository
import com.example.quoteit.data.UsersRepository
import com.example.quoteit.data.local.AppDatabase
import com.example.quoteit.data.network.DatabaseApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class QuoteItApp : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    private val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val foldersRepository by lazy { FoldersRepository(database.folderDao()) }
    val usersRepository by lazy { UsersRepository(DatabaseApi.retrofitService) }

}
