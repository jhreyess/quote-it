package com.example.quoteit.data.network

import com.example.quoteit.domain.models.NewPost
import com.example.quoteit.domain.models.Post
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private const val BASE_URL = "https://quote-it-app.herokuapp.com/api/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

// Endpoints ~ Api DataSource
interface DatabaseService {
    @POST("users/register")
    suspend fun insertUser(@Body body: UserRegisterRequest) : LoginResponse

    @POST("users/login")
    suspend fun queryUser(@Body body: UserLoginRequest) : LoginResponse

    @POST("posts")
    suspend fun insertPost(@Body newPost: NewPost,
        @Header("x-access-token") token: String
    ) : PostResponse

    @GET("posts")
    suspend fun getPosts(@Header("x-access-token") token: String) : PostResponse

    @POST("posts/{postId}/likes")
    suspend fun likePost( @Path("postId") id: Long,
        @Header("x-access-token") token: String) : PostResponse

    @DELETE("posts/{postId}/likes")
    suspend fun dislikePost(@Path("postId") id: Long,
        @Header("x-access-token") token: String) : PostResponse

    @DELETE("posts/sync")
    suspend fun insertLikes(@Body values: List<Long>,
        @Header("x-access-token") token: String) : PostResponse
}

object DatabaseApi{
    var JWT_TOKEN = ""
    fun setToken(token: String) { JWT_TOKEN = token }
    fun getToken() = JWT_TOKEN
    val retrofitService: DatabaseService by lazy { retrofit.create(DatabaseService::class.java) }
}