package com.quoteit.android.data.network

import com.quoteit.android.data.network.config.SessionManager
import com.quoteit.android.data.network.config.TokenAuthenticator
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://quote-it-app.herokuapp.com/api/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val tokenInterceptor = Interceptor { chain ->
    val request = chain.request()
        .newBuilder()
        .header("x-access-token", SessionManager.getAccessToken())
        .build()
    chain.proceed(request)
}

private val httpClient = OkHttpClient.Builder()
    .addInterceptor(tokenInterceptor)
    .authenticator(TokenAuthenticator())
    .readTimeout(20, TimeUnit.SECONDS)
    .connectTimeout(20, TimeUnit.SECONDS)
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .client(httpClient)
    .build()

// Endpoints ~ Api DataSource
interface DatabaseService {

    @DELETE("session")
    suspend fun deleteAccount() : LoginResponse

    @POST("session/password")
    suspend fun updateUserPassword(@Body body: UpdatePasswordRequest) : LoginResponse

    @GET("me/feed")
    suspend fun getUserPosts() : PostResponse

    @GET("me/likes")
    suspend fun getLikedPosts() : PostResponse

    @POST("feed")
    suspend fun insertPost(@Body body: NewPostRequest) : PostResponse

    @DELETE("feed/{postId}")
    suspend fun deletePost(@Path("postId") id: Long) : PostResponse

    @GET("feed")
    suspend fun getPosts() : PostResponse

    @POST("feed/{postId}/likes")
    suspend fun likePost(@Path("postId") id: Long) : PostResponse

    @DELETE("feed/{postId}/likes")
    suspend fun dislikePost(@Path("postId") id: Long) : PostResponse

    @POST("feed/sync")
    suspend fun insertLikes(@Body values: SyncPostRequest) : PostResponse
}

object DatabaseApi {
    val retrofitService: DatabaseService by lazy { retrofit.create(DatabaseService::class.java) }
}