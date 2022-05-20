package com.quoteit.android.data.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://quote-it-app.herokuapp.com/api/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val httpClient = OkHttpClient.Builder()
    .readTimeout(20, TimeUnit.SECONDS)
    .connectTimeout(20, TimeUnit.SECONDS)
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .client(httpClient)
    .build()

// Endpoints ~ Api DataSource
interface AuthenticationService {
    @POST("session/register")
    suspend fun insertUser(@Body body: UserRegisterRequest) : LoginResponse

    @POST("session/login")
    suspend fun queryUser(@Body body: UserLoginRequest) : LoginResponse

    @POST("session/refresh")
    suspend fun refreshToken(@Body body: RefreshTokenRequest) : TokenResponse
}

object AuthenticationApi {
    val retrofitService: AuthenticationService by lazy { retrofit.create(AuthenticationService::class.java) }
}