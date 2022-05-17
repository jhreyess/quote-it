package com.example.quoteit.ui.newpost

import androidx.lifecycle.*
import com.example.quoteit.data.PostsRepository
import com.example.quoteit.data.network.Result
import com.example.quoteit.domain.models.NewPost
import com.example.quoteit.domain.models.Post
import com.example.quoteit.ui.community.CommunityViewModel
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

    fun upload(post: NewPost){
        viewModelScope.launch {
            postsRepo.upload(post).collect { result ->
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