package com.example.quoteit.data.network

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

}

object DatabaseApi{
    val retrofitService: DatabaseService by lazy { retrofit.create(DatabaseService::class.java) }
}