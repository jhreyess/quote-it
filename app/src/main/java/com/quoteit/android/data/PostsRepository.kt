package com.quoteit.android.data

import com.quoteit.android.data.local.PostDao
import com.quoteit.android.data.local.PostEntity
import com.quoteit.android.data.local.asPostDomainModel
import com.quoteit.android.data.network.DatabaseService
import com.quoteit.android.data.network.Result
import com.quoteit.android.data.network.SyncPostRequest
import com.quoteit.android.data.network.asPostEntity
import com.quoteit.android.data.network.NewPostRequest
import com.quoteit.android.domain.models.Post
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class PostsRepository(
    private val postsDao: PostDao,
    private val apiService: DatabaseService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun get(id: Long): Post {
        val data = withContext(ioDispatcher){
            postsDao.getPost(id).asPostDomainModel()
        }
        return data
    }
    suspend fun getLikedPosts(
        fetchFromRemote: Boolean
    ): Flow<Result<List<Post>>> {
        return flow {
            emit(Result.Loading(true))

            val localLikedPosts = postsDao.getLikedPosts()
            emit(Result.Success(
                data = localLikedPosts.map { it.asPostDomainModel() }
            ))

            val isDbEmpty = localLikedPosts.isEmpty()
            val loadFromCache = !isDbEmpty && !fetchFromRemote

            if(loadFromCache){
                emit(Result.Loading(false))
                return@flow
            }

            // If no local liked posts
            val remoteLikedPosts = try {
                apiService.getLikedPosts()
            }catch (e: HttpException){
                e.printStackTrace()
                emit(Result.Error(Exception("Algo salió mal")))
                null
            }catch (e: IOException){
                e.printStackTrace()
                emit(Result.Error(Exception("Algo salió mal")))
                null
            }

            remoteLikedPosts?.let { posts ->
                postsDao.insertPosts(
                    posts.data.map { it.asPostEntity() }
                )
                emit(Result.Success(
                    data = postsDao.getLikedPosts()
                        .map { it.asPostDomainModel() }
                ))
                emit(Result.Loading(false))
            }
        }
    }

    suspend fun getUserPosts(
        fetchFromRemoteUser: Boolean,
        fromUser: Long
    ): Flow<Result<List<Post>>> {
        return flow {
            emit(Result.Loading(true))

            if(fetchFromRemoteUser){
                return@flow
            }

            val localUserPosts = postsDao.getAllUserPosts(fromUser)
            emit(Result.Success(
                data = localUserPosts.map { it.asPostDomainModel() }
            ))

            if(localUserPosts.isNotEmpty()){
                emit(Result.Loading(false))
                return@flow
            }

            val remoteUserPosts = try{
                apiService.getUserPosts()
            }catch (e: Exception){
                e.printStackTrace()
                emit(Result.Error(Exception("Algo salió mal")))
                null
            }
            remoteUserPosts?.let { posts ->
                postsDao.insertPosts(
                    posts.data.map { it.asPostEntity() }
                )
                emit(Result.Success(
                    data = postsDao.getAllUserPosts(fromUser)
                        .map { it.asPostDomainModel() }
                ))
            }
            emit(Result.Loading(false))
        }
    }

    suspend fun getPosts(
        fetchFromRemote: Boolean,
        appendContent: Boolean,
    ): Flow<Result<List<Post>>> {
        return flow {
            emit(Result.Loading(true))

            val now = System.currentTimeMillis()
            val dayMillis = 24 * 60 * 60 * 1000
            val yesterday = now.minus(dayMillis)
            val localPosts = postsDao.getFeedPosts(yesterday)
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
                apiService.getPosts()
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
                if(!appendContent) { postsDao.deleteAllFeed() }
                postsDao.insertPosts(
                    posts.data.map { it.asPostEntity() }
                )
                emit(Result.Success(
                    data = postsDao.getFeedPosts(yesterday)
                        .map { it.asPostDomainModel() }
                ))
            }
            emit(Result.Loading(false))
        }
    }

    suspend fun upload(author: String, content: String): Flow<Result<Post>>{
        return flow {
            emit(Result.Loading(true))

            val newPost = try {
                val post = NewPostRequest(author, content)
                apiService.insertPost(post)
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

    suspend fun delete(id: Long): Flow<Result<Boolean>>{
        return flow {
            emit(Result.Loading(true))

            val didDelete = try{
                apiService.deletePost(id)
            }catch(e: HttpException){
                e.printStackTrace()
                emit(Result.Error(Exception("Algo salió mal")))
                null
            }catch(e: IOException){
                e.printStackTrace()
                emit(Result.Error(Exception("Algo salió mal")))
                null
            }

            didDelete?.let {
                if(it.success) postsDao.deletePost(id)
                emit(Result.Success(it.success))
            }
            emit(Result.Loading(false))
        }
    }

    suspend fun likePost(id: Long, like: Boolean): Flow<Result<Boolean>> {
        return flow {
            emit(Result.Loading(true))
            emit(Result.Success( data = false))
            val result = try{
                if(like){
                    apiService.likePost(id)
                }else{
                    apiService.dislikePost(id)
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
                val unsyncedPost = postsDao.getPost(id).asPostDomainModel()
                if(unsyncedPost.isLiked != like){
                    postsDao.syncPost(id, like, synced, diff)
                    emit(Result.Success( data = true ))
                }
            }
            emit(Result.Loading(false))
        }
    }
    
    suspend fun getUnsyncedPosts(): List<PostEntity> {
        return postsDao.getUnsyncedPosts()
    }

    suspend fun syncPosts(posts: List<PostEntity>){
        try{
            val body = SyncPostRequest(posts.map { it.postId })
            apiService.insertLikes(body)
            posts.forEach { it.likeSynced = true }
            postsDao.syncListPost(posts)
        }catch (e: HttpException){
            e.printStackTrace()
        }catch (e: IOException){
            e.printStackTrace()
        }
    }

}