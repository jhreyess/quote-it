package com.quoteit.android.data

import com.quoteit.android.data.local.QuoteDAO
import com.quoteit.android.data.local.QuoteEntity
import com.quoteit.android.data.local.asQuoteDomainModel
import com.quoteit.android.domain.models.Quote
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class QuotesRepository(
    private val quoteDao: QuoteDAO,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    val quotes: Flow<List<Quote>> = quoteDao.getAllQuotes().map {
            it.asQuoteDomainModel()
        }

    val favQuotes: Flow<List<Quote>> = quoteDao.getAllFavQuotes().map {
        it.asQuoteDomainModel()
    }

    suspend fun insert(quoteEntity: QuoteEntity): Long{
        return withContext(ioDispatcher){
            quoteDao.insertQuote(quoteEntity)
        }
    }

    suspend fun get(id: Long): Quote {
        val data = withContext(ioDispatcher){
            quoteDao.getQuote(id).asQuoteDomainModel()
        }
        return data
    }

    suspend fun delete(id: Long){
        withContext(ioDispatcher){
            quoteDao.deleteQuote(id)
        }
    }

    suspend fun updateFavState(id: Long, b: Boolean){
        withContext(ioDispatcher){
            quoteDao.updateState(id, b)
        }
    }

}