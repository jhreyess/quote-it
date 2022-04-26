package com.example.quoteit.ui.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quoteit.BuildConfig
import com.example.quoteit.data.network.QuotesApiNetwork.retrofitService
import com.example.quoteit.data.network.QuoteApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

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

            }
        }
    }
}