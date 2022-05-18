package com.example.quoteit.data.network

import com.example.quoteit.domain.models.NewPost
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
    @POST("session/register")
    suspend fun insertUser(@Body body: UserRegisterRequest) : LoginResponse

    @POST("session/login")
    suspend fun queryUser(@Body body: UserLoginRequest) : LoginResponse

    @POST("session/password")
    suspend fun updateUserPassword(@Body body: UpdatePasswordRequest,
        @Header("x-access-token") token: String) : LoginResponse

    @GET("me/feed")
    suspend fun getUserPosts(@Header("x-access-token") token: String) : PostResponse

    @GET("me/likes")
    suspend fun getLikedPosts(@Header("x-access-token") token: String) : PostResponse

    @POST("feed")
    suspend fun insertPost(@Body newPost: NewPost,
        @Header("x-access-token") token: String
    ) : PostResponse

    @GET("feed")
    suspend fun getPosts(@Header("x-access-token") token: String) : PostResponse

    @POST("feed/{postId}/likes")
    suspend fun likePost( @Path("postId") id: Long,
        @Header("x-access-token") token: String) : PostResponse

    @DELETE("feed/{postId}/likes")
    suspend fun dislikePost(@Path("postId") id: Long,
        @Header("x-access-token") token: String) : PostResponse

    @DELETE("feed/sync")
    suspend fun insertLikes(@Body values: List<Long>,
        @Header("x-access-token") token: String) : PostResponse
}

object DatabaseApi{
    private var JWT_TOKEN = ""
    fun setToken(token: String) { JWT_TOKEN = token }
    fun getToken() = JWT_TOKEN
    val retrofitService: DatabaseService by lazy { retrofit.create(DatabaseService::class.java) }
}