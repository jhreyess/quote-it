package com.example.quoteit.data

import com.example.quoteit.data.local.PostDao
import com.example.quoteit.data.local.PostEntity
import com.example.quoteit.data.local.asFolderDomainModel
import com.example.quoteit.data.local.asPostDomainModel
import com.example.quoteit.data.network.*
import com.example.quoteit.domain.models.Folder
import com.example.quoteit.domain.models.NewPost
import com.example.quoteit.domain.models.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception

class PostsRepository(
    private val postsDao: PostDao,
    private val apiService: DatabaseService,
) {

    fun userPosts(user: String): Flow<List<Post>> {
        return postsDao.getAllUserPosts(user).map { it.asPostDomainModel() }
    }

    fun likedPosts(): Flow<List<Post>> {
        return postsDao.getLikedPosts().map { it.asPostDomainModel() }
    }

    suspend fun getPosts(
        fetchFromRemote: Boolean,
        appendContent: Boolean
    ): Flow<Result<List<Post>>> {
        return flow {
            emit(Result.Loading(true))

            val localPosts = postsDao.getAllPosts()
            emit(Result.Success(
                data = localPosts.map { it.asPostDomainModel() }
            ))
            val isDbEmpty = localPosts.isEmpty()
            val loadFromCache = !isDbEmpty && !fetchFromRemote

            if(loadFromCache){
                emit(Result.Loading(false))
                return@flow
            }

            // If no local posts or fetchFromRemote
            val remotePosts = try {
                apiService.getPosts(DatabaseApi.getToken())
            }catch (e: HttpException){
                e.printStackTrace()
                emit(Result.Error(Exception("Algo salió mal")))
                null
            }catch (e: IOException){
                e.printStackTrace()
                emit(Result.Error(Exception("Algo salió mal")))
                null
            }

            remotePosts?.let { posts ->
                if(!appendContent) { postsDao.deleteAll() }
                postsDao.insertPosts(
                    posts.data.map { it.asPostEntity() }
                )
                emit(Result.Success(
                    data = postsDao.getAllPosts()
                        .map { it.asPostDomainModel() }
                ))
                emit(Result.Loading(false))
            }
        }
    }

    suspend fun upload(post: NewPost): Flow<Result<Post>>{
        return flow {
            emit(Result.Loading(true))

            val newPost = try {
                val token = DatabaseApi.getToken()
                apiService.insertPost(post, token)
            }catch (e: HttpException){
                e.printStackTrace()
                emit(Result.Error(Exception("Algo salió mal")))
                null
            }catch (e: IOException){
                e.printStackTrace()
                emit(Result.Error(Exception("Algo salió mal")))
                null
            }

            newPost?.let {
                val localPost = it.data.first().asPostEntity()
                postsDao.insertNewPost(localPost)
                emit(Result.Success(
                    data = localPost.asPostDomainModel()
                ))
            }
            emit(Result.Loading(false))
        }
    }

    suspend fun likePost(id: Long, like: Boolean): Flow<Result<Post>> {
        return flow {
            emit(Result.Loading(true))
            val token = DatabaseApi.getToken()
            val result = try{
                if(like){
                    apiService.likePost(id, token)
                }else{
                    apiService.dislikePost(id, token)
                }
            }catch (e: HttpException){
                e.printStackTrace()
                emit(Result.Error(Exception("Algo salió mal")))
                null
            }catch (e: IOException){
                e.printStackTrace()
                emit(Result.Error(Exception("Algo salió mal")))
                null
            }

            result.let {
                val synced = it?.success ?: false
                val diff = if(like) 1 else -1
                val syncedPost = postsDao.getPost(
                    postsDao.syncPost(id, like, synced, diff).toLong()
                )
                emit(Result.Success( data = syncedPost.first().asPostDomainModel() ))
            }
            emit(Result.Loading(false))
        }
    }
    
    suspend fun getUnsyncedPosts(): List<PostEntity> {
        return postsDao.getUnsyncedPosts()
    }

    suspend fun syncPosts(posts: List<PostEntity>){
        try{
            val token = DatabaseApi.getToken()
            apiService.insertLikes(posts.map { it.asPostDomainModel().id }, token)
            posts.forEach { it.likeSynced = true }
            postsDao.syncPost(posts)
        }catch (e: HttpException){
            e.printStackTrace()
        }catch (e: IOException){
            e.printStackTrace()
        }
    }

}