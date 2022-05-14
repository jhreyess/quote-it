package com.example.quoteit.data.local

import androidx.room.*
import com.example.quoteit.domain.models.Post

@Dao
interface PostDao {

    @Query("SELECT * FROM posts")
    suspend fun getAllPosts(): List<PostEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(listPost: List<PostEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewPost(post: PostEntity)

    @Query("DELETE FROM posts")
    suspend fun deleteAll()

}