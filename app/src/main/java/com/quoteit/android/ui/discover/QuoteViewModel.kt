package com.quoteit.android.ui.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quoteit.android.BuildConfig
import com.quoteit.android.data.network.QuoteApi
import com.quoteit.android.data.network.QuotesApi.retrofitService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuoteViewModel : ViewModel() {

    private val quote: MutableLiveData<QuoteApi> by lazy {
        MutableLiveData<QuoteApi>().also { fetchQuote() }
    }

    fun getQuote(): LiveData<QuoteApi> { return quote }
    private fun setQuote(data: QuoteApi){ quote.value = data }

    fun fetchQuote(){
        viewModelScope.launch {
            try {
                val data = withContext(Dispatchers.IO){
                    retrofitService.getSingleQuote(BuildConfig.API_KEY, "es")
                }
                setQuote(data)
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }
}