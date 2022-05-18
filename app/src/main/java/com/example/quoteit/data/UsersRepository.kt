package com.example.quoteit.data

import com.example.quoteit.data.network.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception

class UsersRepository(
    private val apiService: DatabaseService,
) {

    private val token = DatabaseApi.getToken()

    suspend fun loginUser(email: String, password: String): Flow<Result<LoginResponse>>{
        val body = UserLoginRequest(email, password)
        return flow {
            emit(Result.Loading(true))
            val response = try {
                apiService.queryUser(body)
            } catch (e: HttpException) {
                when(e.code()){
                    400 -> emit(Result.Success(LoginResponse(false, error = "Entradas no válidas")))
                    401 -> emit(Result.Success(LoginResponse(false, error = "Correo o contraseña no válido")))
                    else -> emit(Result.Error(Exception("Algo salió mal, intenta de nuevo más tarde")))
                }
                null
            } catch (e: IOException){
                emit(Result.Error(Exception("Algo salió mal, intenta de nuevo más tarde")))
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
                when(e.code()){
                    400 -> emit(Result.Success(LoginResponse(false, error = "Entradas no válidas")))
                    409 -> emit(Result.Success(LoginResponse(false,
                                error = "Ya existe una cuenta asociado a este correo o nombre de usuario"
                            )))
                    else -> emit(Result.Error(Exception("Algo salió mal, intenta de nuevo más tarde")))
                }
                null
            } catch(e: IOException){
                emit(Result.Error(Exception("Algo salió mal, intenta de nuevo más tarde")))
                null
            }
            response?.let { emit(Result.Success(response)) }
            emit(Result.Loading(false))
        }
    }

    suspend fun updateUserPassword(newPassword: String): Flow<Result<LoginResponse>>{
        val body = UpdatePasswordRequest(newPassword)
        return flow {
            emit(Result.Loading(true))
            val response = try {
                apiService.updateUserPassword(body, token)
            }catch(e: Exception){
                emit(Result.Error(Exception("Algo salió mal, intenta de nuevo más tarde")))
                null
            }
            response?.let { emit(Result.Success(data = response)) }
            emit(Result.Loading(false))
        }
    }

}