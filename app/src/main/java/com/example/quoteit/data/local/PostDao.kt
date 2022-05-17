package com.example.quoteit.data.local

import androidx.room.*
import com.example.quoteit.domain.models.Post
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {

    @Query("SELECT * FROM posts")
    suspend fun getAllPosts(): List<PostEntity>

    @Query("SELECT * FROM posts WHERE isLiked = 1")
    fun getLikedPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE postBy = :username")
    fun getAllUserPosts(username: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE likeSynced = 0")
    suspend fun getUnsyncedPosts(): List<PostEntity>

    @Query("SELECT * FROM posts WHERE postId = :id")
    suspend fun getPost(id: Long): List<PostEntity>

    @Query("UPDATE posts SET noLikes = noLikes +:diff ,isLiked = :like, likeSynced = :sync WHERE postId = :id")
    suspend fun syncPost(id: Long, like: Boolean, sync: Boolean, diff: Int): Int

    @Update
    suspend fun syncPost(posts: List<PostEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPosts(listPost: List<PostEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewPost(post: PostEntity)

    @Query("DELETE FROM posts")
    suspend fun deleteAll()

}