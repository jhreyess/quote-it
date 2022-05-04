package com.example.quoteit.data

import android.util.Log
import com.example.quoteit.data.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.lang.Exception

class UsersRepository(
    private val database: DatabaseApi,
) {

    suspend fun loginUser(email: String, password: String): Result<LoginResponse>{
        val body = UserLoginRequest(email, password)
        return withContext(Dispatchers.IO){
            try {
                val response = database.retrofitService.queryUser(body)
                Result.Success(response)
            } catch (e: HttpException) {
                Log.d("Debug", e.message.toString())
                Result.Error(Exception("Fallo en la solicitud..."))
            }
        }
    }

    suspend fun registerUser(email: String, username: String, password: String): Result<LoginResponse> {
        val body = UserRegisterRequest(username, email, password)
        return withContext(Dispatchers.IO){
            try {
                val response = database.retrofitService.insertUser(body)
                Result.Success(response)
            } catch (e: HttpException) {
                Result.Error(Exception("Fallo en la solicitud..."))
            }
        }
    }

}