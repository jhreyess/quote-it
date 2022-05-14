package com.example.quoteit.ui.community

import androidx.lifecycle.*
import com.example.quoteit.data.FolderQuoteRepository
import com.example.quoteit.data.FoldersRepository
import com.example.quoteit.data.PostsRepository
import com.example.quoteit.data.QuotesRepository
import com.example.quoteit.data.network.Result
import com.example.quoteit.domain.models.NewPost
import com.example.quoteit.domain.models.Post
import com.example.quoteit.ui.home.HomeViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CommunityViewModel(private val postsRepo: PostsRepository) : ViewModel() {

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData(false)
    val error: LiveData<Boolean> get() = _error

    private val _posts: MutableLiveData<List<Post>> by lazy {
        MutableLiveData<List<Post>>().also { getPosts() }
    }
    val posts: LiveData<List<Post>> get() = _posts

    fun getPosts(fetchFromRemote: Boolean = false) {
        viewModelScope.launch {
            _error.value = false
            postsRepo.getPosts(fetchFromRemote).collect { result ->
                when(result){
                    is Result.Success -> { _posts.value = result.data }
                    is Result.Error -> { _error.value = true }
                    is Result.Loading -> { _isLoading.value = result.isLoading }
                }
            }
        }
    }

    fun upload(post: NewPost){
        viewModelScope.launch {
            postsRepo.upload(post).collect { result ->
                when(result){
                    is Result.Success -> {
                        _posts.value = _posts.value?.plus(result.data) ?: listOf(result.data)
                    }
                    is Result.Error -> { _error.value = true }
                    is Result.Loading -> { _isLoading.value = result.isLoading }
                }
            }
        }
    }

}

class CommunityViewModelFactory(
    private val repo: PostsRepository,
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommunityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CommunityViewModel(repo) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}