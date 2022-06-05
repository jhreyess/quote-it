package com.quoteit.android.ui.settings

import androidx.lifecycle.*
import com.quoteit.android.data.UsersRepository
import com.quoteit.android.data.network.Result
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DeleteAccountViewModel(private val usersRepo: UsersRepository): ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _success = MutableLiveData(false)
    val success: LiveData<Boolean> get() = _success

    private val _error = MutableLiveData("")
    val error: LiveData<String> get() = _error

    fun deleteAccount(){
        viewModelScope.launch {
            _error.value = ""
            usersRepo.deleteUserAccount().collect { result ->
                when(result){
                    is Result.Success -> { _success.value = result.data.success }
                    is Result.Error -> { _error.value = result.exception.message }
                    is Result.Loading -> { _isLoading.value = result.isLoading }
                }
            }
        }
    }
}

class DeleteAccountViewModelFactory(
    private val repo: UsersRepository,
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeleteAccountViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DeleteAccountViewModel(repo) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}