package com.example.quoteit.ui.edit

import android.content.ClipData
import androidx.lifecycle.*
import com.example.quoteit.BuildConfig
import com.example.quoteit.data.FolderQuoteRepository
import com.example.quoteit.data.FoldersRepository
import com.example.quoteit.data.QuotesRepository
import com.example.quoteit.data.local.DatabaseQuote
import com.example.quoteit.data.network.QuoteApi
import com.example.quoteit.data.network.QuotesApi
import com.example.quoteit.domain.models.Quote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class ImageEditViewModel(
    private val quoteRepo: QuotesRepository
    ) : ViewModel() {

    private val _quote = MutableLiveData<Quote>()
    val quote: LiveData<Quote> get() = _quote

    private val _selectedDrawable = MutableLiveData<Int>()
    val selectedDrawable get() = _selectedDrawable

    private val _selectedImage = MutableLiveData<String>()
    val selectedImage get() = _selectedImage

    fun getQuote(id: Long) {
        viewModelScope.launch {
            _quote.value = quoteRepo.get(id)
        }
    }

    fun addDrawable(res: Int){ _selectedDrawable.value = res }

    fun addUri(uri: String){ _selectedImage.value = uri }
}

class ImageEditViewModelFactory(
    private val repo: QuotesRepository,
    ) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ImageEditViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ImageEditViewModel(repo) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}