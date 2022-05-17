package com.example.quoteit.ui.profile

import androidx.lifecycle.*
import com.example.quoteit.data.PostsRepository
import com.example.quoteit.domain.models.Post
import com.example.quoteit.ui.community.CommunityViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProfileViewModel(private val postsRepo: PostsRepository): ViewModel() {

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _posts

    private val _likedPosts = MutableLiveData<List<Post>>()
    val likedPosts: LiveData<List<Post>> get() = _likedPosts

    fun getUserPosts(user: String) {
        viewModelScope.launch {
            postsRepo.userPosts(user).collect {
                _posts.value = it
            }
        }
    }

    fun getLikedPosts(){
        viewModelScope.launch {
            postsRepo.likedPosts().collect {
                _likedPosts.value = it
            }
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
        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}