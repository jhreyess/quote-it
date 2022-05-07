package com.example.quoteit.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quoteit.data.FoldersRepository
import com.example.quoteit.data.local.DatabaseFolder
import kotlinx.coroutines.launch

class HomeViewModel(repository: FoldersRepository) : ViewModel() {

    private val foldersRepository = repository
    val folders = foldersRepository.folders

    fun insertFolder(name: String) = viewModelScope.launch {
        foldersRepository.insert(DatabaseFolder(folderName = name))
    }

}

class HomeViewModelFactory(private val repo: FoldersRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repo) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}