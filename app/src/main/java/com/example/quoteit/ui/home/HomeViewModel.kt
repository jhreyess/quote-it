package com.example.quoteit.ui.home

import androidx.lifecycle.*
import com.example.quoteit.data.FolderQuoteRepository
import com.example.quoteit.data.FoldersRepository
import com.example.quoteit.data.QuotesRepository
import com.example.quoteit.data.local.FolderEntity
import com.example.quoteit.domain.models.Folder
import com.example.quoteit.domain.models.FolderWQuotes
import com.example.quoteit.domain.models.Quote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val foldersRepo: FoldersRepository,
    private val foldersQuoteRepo: FolderQuoteRepository,
    private val quotesRepo: QuotesRepository
) : ViewModel() {

    fun getFolders(): Flow<List<Folder>> = foldersRepo.folders
    fun getQuotes(): Flow<List<Quote>> = quotesRepo.quotes
    fun getFavQuotes(): Flow<List<Quote>> = quotesRepo.favQuotes

    fun insertFolder(name: String) {
        viewModelScope.launch {
            foldersRepo.insert(FolderEntity(folderName = name))
        }
    }

    fun getFolderQuotes(id: Long): LiveData<FolderWQuotes> {
        return foldersQuoteRepo.foldersWithQuotes(id)
    }

    fun updateFavQuote(quoteId: Long, newState: Boolean){
        viewModelScope.launch {
            quotesRepo.updateFavState(quoteId, newState)
            foldersRepo.updateCount(1L)
        }
    }

    fun deleteFolder(folderId: Long){
        viewModelScope.launch {
            foldersRepo.delete(folderId)
            foldersQuoteRepo.deleteAllFolder(folderId)
        }
    }

    fun modifyFolder(folderId: Long, newName: String){
        viewModelScope.launch {
            foldersRepo.updateName(folderId, newName)
        }
    }

    fun deleteQuoteFromFolder(quoteId: Long, folderId: Long){
        viewModelScope.launch {
            if(folderId == 2L) {
                quotesRepo.delete(quoteId)
                foldersQuoteRepo.deleteAllQuote(quoteId)
                foldersRepo.updateAllCount()
            }else{
                foldersQuoteRepo.delete(folderId, quoteId)
            }
            foldersRepo.updateCount(folderId)
        }
    }

    fun addQuoteToFolder(quoteId: Long, folderId: Long){
        viewModelScope.launch {
            foldersQuoteRepo.insert(folderId, quoteId)
            foldersRepo.updateCount(folderId)
        }
    }

    fun getQuote(quoteId: Long): LiveData<Quote>{
        val quote = MutableLiveData<Quote>()
        viewModelScope.launch {
            quote.value = quotesRepo.get(quoteId)
        }
        return quote
    }

}

class HomeViewModelFactory(
    private val repo: FoldersRepository,
    private val repo2: FolderQuoteRepository,
    private val repo3: QuotesRepository
    ) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repo, repo2, repo3) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}