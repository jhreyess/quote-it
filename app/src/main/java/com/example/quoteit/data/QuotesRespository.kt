package com.example.quoteit.data

import com.example.quoteit.data.local.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuotesRespository(
    private val localDatabase: AppDatabase
) {
//    suspend fun getAllQuotes(){
//        withContext(Dispatchers.IO) {
//            localDatabase.quoteDao().insertAll()
//        }
//    }
}