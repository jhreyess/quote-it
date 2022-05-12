package com.example.quoteit.workers

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.example.quoteit.data.PreferencesDataStore
import com.example.quoteit.data.dataStore
import com.example.quoteit.data.network.LoginResponse
import com.example.quoteit.data.network.Result as DatabaseResult
import com.example.quoteit.ui.QuoteItApp
import retrofit2.HttpException
import java.lang.Exception

class SyncDataWorker(ctx: Context,params: WorkerParameters) : CoroutineWorker(ctx, params){

    private val userRepo = (applicationContext as QuoteItApp).usersRepository

    override suspend fun doWork(): Result {

        val email = inputData.getString("email") ?: ""
        val pass = inputData.getString("pass") ?: ""

        return try{
            val response = userRepo.loginUser(email, pass)
            Log.d("Debug", response.toString())
            when(response){
                is DatabaseResult.Success<LoginResponse> -> {
                    if(response.data.success){
                        makeNotification("Data synced successfully", applicationContext)
                        Result.success()
                    }else{
                        makeNotification("Failed to login", applicationContext)
                        logout()
                        Result.failure()
                    }
                }
                else -> {
                    makeNotification("Failed to login", applicationContext)
                    logout()
                    Result.failure()
                }
            }
        }catch (e: Exception){
            logout()
            Result.failure()
        }
    }

    private suspend fun logout(){
        val prefs = PreferencesDataStore(applicationContext.dataStore)
        prefs.saveLogInPreference(false, applicationContext)
        makeNotification("Login failed", applicationContext)
    }
}