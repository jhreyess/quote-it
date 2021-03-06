package com.quoteit.android.ui.home

import androidx.lifecycle.*
import com.quoteit.android.data.FolderQuoteRepository
import com.quoteit.android.data.FoldersRepository
import com.quoteit.android.data.QuotesRepository
import com.quoteit.android.data.local.QuoteEntity
import kotlinx.coroutines.launch

class NewQuoteViewModel(
    private val foldersQuoteRepo: FolderQuoteRepository,
    private val quoteRepo: QuotesRepository,
    private val foldersRepo: FoldersRepository
    ) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> get() = _isLoading

    private val _isValidInput = MutableLiveData<Boolean>()
    val isValidInput : LiveData<Boolean> get() = _isValidInput

    fun createQuote(author: String, content: String, folderId: Long){
        _isValidInput.value = validate(author, content)
        val isValid = _isValidInput.value!!
        if(isValid){
            viewModelScope.launch {
                _isLoading.value = true
                val quote = QuoteEntity(author = author, content = content)
                val quoteId = quoteRepo.insert(quote)
                foldersQuoteRepo.insert(folderId, quoteId)
                foldersRepo.updateCount(folderId)
                foldersRepo.updateCount(2L)
                _isLoading.value = false
            }
        }
    }

    private fun validate(author: String, content: String): Boolean {
        return (author.isNotBlank() and content.isNotBlank())
    }
}

class NewQuoteViewModelFactory(
    private val repo: FolderQuoteRepository,
    private val repo2: QuotesRepository,
    private val repo3: FoldersRepository
    ) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewQuoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewQuoteViewModel(repo, repo2, repo3) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}