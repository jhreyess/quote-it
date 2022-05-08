package com.example.quoteit.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.quoteit.data.local.AppDatabase
import com.example.quoteit.data.local.DatabaseQuote
import com.example.quoteit.data.local.QuoteDAO
import com.example.quoteit.data.local.asQuoteDomainModel
import com.example.quoteit.domain.models.Quote
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuotesRespository(
    private val quoteDao: QuoteDAO,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    val favQuotes: LiveData<List<Quote>> = Transformations
        .map(quoteDao.getAllFavQuotes()){
            it.asQuoteDomainModel()
        }

    suspend fun insert(quote: DatabaseQuote): Long{
        return withContext(ioDispatcher){
            quoteDao.insertQuote(quote)
        }
    }

    suspend fun updateFavState(id: Long, b: Boolean){
        withContext(ioDispatcher){
            quoteDao.updateState(id, b)
        }
    }

}