package com.example.quoteit.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quoteit.data.FolderQuoteRepository
import com.example.quoteit.data.FoldersRepository
import com.example.quoteit.data.QuotesRespository
import com.example.quoteit.data.local.DatabaseFolder
import com.example.quoteit.domain.models.Folder
import com.example.quoteit.domain.models.FolderWQuotes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val foldersRepo: FoldersRepository,
    private val foldersQuoteRepo: FolderQuoteRepository,
    private val quotesRepo: QuotesRespository
) : ViewModel() {

    fun getFolders(): Flow<List<Folder>> = foldersRepo.folders

    fun insertFolder(name: String) {
        viewModelScope.launch {
            foldersRepo.insert(DatabaseFolder(folderName = name))
        }
    }

    fun getQuotes(id: Long): LiveData<FolderWQuotes> {
        return foldersQuoteRepo.foldersWithQuotes(id)
    }

    fun updateFavQuote(quoteId: Long, newState: Boolean){
        viewModelScope.launch {
            quotesRepo.updateFavState(quoteId, newState)
            if(newState){
                foldersQuoteRepo.insert(1L, quoteId)
                foldersRepo.updateCount(1L)
            }else{
                foldersQuoteRepo.delete(1L, quoteId)
                foldersRepo.updateCount(1L)
            }
        }
    }

    fun deleteFolder(folderId: Long){
        viewModelScope.launch {
            foldersRepo.delete(folderId)
        }
    }

    fun modifyFolder(folderId: Long, newName: String){
        viewModelScope.launch {
            foldersRepo.updateName(folderId, newName)
        }
    }

}

class HomeViewModelFactory(
    private val repo: FoldersRepository,
    private val repo2: FolderQuoteRepository,
    private val repo3: QuotesRespository
    ) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repo, repo2, repo3) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}