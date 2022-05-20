package com.example.quoteit.ui.profile

import androidx.lifecycle.*
import com.example.quoteit.data.PostsRepository
import com.example.quoteit.data.network.Result
import com.example.quoteit.domain.models.Post
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProfileViewModel(private val postsRepo: PostsRepository): ViewModel() {

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _posts
    private fun setPosts(posts: List<Post>) { _posts.value = posts }

    private val _likedPosts: MutableLiveData<List<Post>> by lazy {
        MutableLiveData<List<Post>>().also { getLikedPosts(fetchFromRemote = true) }
    }
    val likedPosts: LiveData<List<Post>> get() = _likedPosts

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> get() = _isRefreshing

    fun getUserPosts(
        fetchFromRemoteUser: Boolean = false,
        fromUser: Long
    ) {
        viewModelScope.launch {
            postsRepo.getUserPosts(fetchFromRemoteUser, fromUser).collect { result ->
                when(result){
                    is Result.Success -> { setPosts(result.data) }
                    is Result.Loading -> { _isRefreshing.value = result.isLoading }
                }
            }
        }
    }

    fun getLikedPosts(
        fetchFromRemote: Boolean = false
    ){
        viewModelScope.launch {
            postsRepo.getLikedPosts(fetchFromRemote).collect { result ->
                when(result){
                    is Result.Success -> { _likedPosts.value = result.data.reversed() }
                    is Result.Loading -> { _isRefreshing.value = result.isLoading }
                }
            }
        }
    }

    fun deletePost(postId: Long){
        viewModelScope.launch {
            postsRepo.delete(postId).collect { result ->
                when(result){
                    is Result.Success -> { _posts.value = _posts.value }
                    is Result.Error -> {  }
                    is Result.Loading -> { _isLoading.value = result.isLoading }
                }
            }
        }
    }

    fun getPost(postId: Long): LiveData<Post>{
        val post = MutableLiveData<Post>()
        viewModelScope.launch {
            post.value = postsRepo.get(postId)
        }
        return post
    }

    fun likePost(postId: Long, like: Boolean){
        viewModelScope.launch {
            postsRepo.likePost(postId, like).collect()
        }
    }

}


class ProfileViewModelFactory(
    private val repo: PostsRepository,
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(repo) as T
        }
        throw IllegalArgumentException("Unable to construct viewModel")
    }
}