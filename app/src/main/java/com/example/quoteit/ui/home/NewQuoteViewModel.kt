package com.example.quoteit.ui.home

import androidx.lifecycle.*
import com.example.quoteit.data.FolderQuoteRepository
import com.example.quoteit.data.FoldersRepository
import com.example.quoteit.data.QuotesRespository
import com.example.quoteit.data.local.DatabaseQuote
import kotlinx.coroutines.launch

class NewQuoteViewModel(
    private val foldersQuoteRepo: FolderQuoteRepository,
    private val quoteRepo: QuotesRespository,
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
                val quote = DatabaseQuote(author = author, content = content)
                val quoteId = quoteRepo.insert(quote)
                foldersQuoteRepo.insert(folderId, quoteId)
                foldersRepo.updateCount(folderId)
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
    private val repo2: QuotesRespository,
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