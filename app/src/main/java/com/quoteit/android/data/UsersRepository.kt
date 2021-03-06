package com.quoteit.android.data

import com.quoteit.android.data.network.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class UsersRepository(
    private val apiAuthService: AuthenticationService,
    private val apiDataService: DatabaseService
) {

    suspend fun loginUser(email: String, password: String): Flow<Result<LoginResponse>>{
        val body = UserLoginRequest(email, password)
        return flow {
            emit(Result.Loading(true))
            val response = try {
                apiAuthService.queryUser(body)
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
                apiAuthService.insertUser(body)
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
                apiDataService.updateUserPassword(body)
            }catch(e: Exception){
                emit(Result.Error(Exception("Algo salió mal, intenta de nuevo más tarde")))
                null
            }
            response?.let { emit(Result.Success(data = response)) }
            emit(Result.Loading(false))
        }
    }

    suspend fun deleteUserAccount(): Flow<Result<LoginResponse>>{
        return flow {
            emit(Result.Loading(true))
            val response = try {
                apiDataService.deleteAccount()
            }catch(e: Exception){
                emit(Result.Error(Exception("Algo salió mal, intenta de nuevo más tarde")))
                null
            }
            response?.let { emit(Result.Success(data = response)) }
            emit(Result.Loading(false))
        }
    }

}