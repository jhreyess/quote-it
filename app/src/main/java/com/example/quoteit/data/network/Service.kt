package com.example.quoteit.data.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

private const val BASE_URL = "https://quotes15.p.rapidapi.com/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

// Endpoints
interface QuotesApiService {
    @Headers("X-RapidAPI-Host: quotes15.p.rapidapi.com")
    @GET("quotes/random/")
    suspend fun getSingleQuote(
        @Header("X-RapidAPI-Key") key: String,
        @Query("language_code") lang: String
    ) : QuoteApi
}

object QuotesApiNetwork{
    val retrofitService: QuotesApiService by lazy { retrofit.create(QuotesApiService::class.java) }
}