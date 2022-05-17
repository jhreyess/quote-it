package com.example.quoteit.data

import android.util.Log
import com.example.quoteit.data.network.*
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception

class UsersRepository(
    private val apiService: DatabaseService,
) {

    suspend fun loginUser(email: String, password: String): Flow<Result<LoginResponse>>{
        val body = UserLoginRequest(email, password)
        return flow {
            emit(Result.Loading(true))
            val response = try {
                apiService.queryUser(body)
            } catch (e: HttpException) {
                emit(Result.Error(e))
                null
            }
            response?.let { emit(Result.Success(data = response)) }
            emit(Result.Loading(false))
        }
    }

    suspend fun registerUser(username: String, email: String, password: String): Flow<Result<LoginResponse>> {
        val body = UserRegisterRequest(username, email, password)
        return flow {
            emit(Result.Loading(true))
            val response = try {
                apiService.insertUser(body)
            } catch (e: HttpException) {
                emit(Result.Error(e))
                null
            }
            response?.let { emit(Result.Success(response)) }
            emit(Result.Loading(false))
        }
    }

}