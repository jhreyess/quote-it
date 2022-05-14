package com.example.quoteit.data

import com.example.quoteit.data.local.PostDao
import com.example.quoteit.data.local.asPostDomainModel
import com.example.quoteit.data.network.*
import com.example.quoteit.domain.models.NewPost
import com.example.quoteit.domain.models.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception

class PostsRepository(
    private val postsDao: PostDao,
    private val apiService: DatabaseService,
) {

    private val token = DatabaseApi.getToken()

    suspend fun getPosts(
        fetchFromRemote: Boolean,
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

            // If no local posts
            val remotePosts = try {
                apiService.getPosts(token)
            }catch (e: HttpException){
                e.printStackTrace()
                //TODO: HANDLE HTTP EXCEPTIONS
                null
            }catch (e: IOException){
                e.printStackTrace()
                emit(Result.Error(Exception("Algo salió mal")))
                null
            }

            remotePosts?.let { posts ->
                postsDao.deleteAll()
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
            val newPost = apiService.insertPost(token, post)
            val localPost = newPost.data.first().asPostEntity()
            postsDao.insertNewPost(localPost)
            emit(Result.Success(
                data = localPost.asPostDomainModel()
            ))
            emit(Result.Loading(false))
        }
    }

}