package com.quoteit.android.ui.newpost

import androidx.lifecycle.*
import com.quoteit.android.data.PostsRepository
import com.quoteit.android.data.network.Result
import com.quoteit.android.domain.models.Post
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NewPostViewModel(private val postsRepo: PostsRepository): ViewModel() {

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _result = MutableLiveData<Post>(null)
    fun getResult() = _result
    private fun setResult(post: Post) { _result.value = post }

    private val _error = MutableLiveData(false)
    val error: LiveData<Boolean> get() = _error

    fun upload(author: String, content: String){
        viewModelScope.launch {
            postsRepo.upload(author, content).collect { result ->
                when(result){
                    is Result.Success -> { setResult(result.data) }
                    is Result.Error -> { _error.value = true }
                    is Result.Loading -> { _isLoading.value = result.isLoading }
                }
            }
        }
    }
}

class NewPostViewModelFactory(
    private val repo: PostsRepository,
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewPostViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewPostViewModel(repo) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}