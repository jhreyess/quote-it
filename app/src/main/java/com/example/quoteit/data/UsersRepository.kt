package com.example.quoteit.data

import android.util.Log
import com.example.quoteit.data.network.*
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception

class UsersRepository(
    private val database: DatabaseApi,
) {

    suspend fun loginUser(email: String, password: String): Result<LoginResponse>{
        val body = UserLoginRequest(email, password)
        return try {
            val response = database.retrofitService.queryUser(body)
            Result.Success(response)
        } catch (e: HttpException) {
            when(e.code()){
                400 -> Result.Success(LoginResponse(false, "Entradas no válidas"))
                401 -> Result.Success(LoginResponse(false, "Correo o contraseña invalido"))
                else -> Result.Error(Exception("Algo salió mal, intenta de nuevo más tarde"))
            }
        }
    }

    suspend fun registerUser(username: String, email: String, password: String): Result<LoginResponse> {
        val body = UserRegisterRequest(username, email, password)
        return try {
            val response = database.retrofitService.insertUser(body)
            Result.Success(response)
        } catch (e: HttpException) {
            when(e.code()){
                400 -> Result.Success(LoginResponse(false, "Entradas no válidas"))
                409 -> Result.Success(LoginResponse(false,
                    "Ya existe una cuenta asociado a este correo o nombre de usuario"))
                else -> Result.Error(Exception("Algo salió mal, intenta de nuevo más tarde"))
            }
        }
    }

}