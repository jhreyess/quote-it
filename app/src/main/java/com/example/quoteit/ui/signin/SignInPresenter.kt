package com.example.quoteit.ui.signin

import android.app.Activity
import android.util.Log
import com.example.quoteit.data.UsersRepository
import com.example.quoteit.data.network.DatabaseApi
import com.example.quoteit.data.network.LoginResponse
import com.example.quoteit.data.network.Result
import kotlinx.coroutines.*
import java.lang.Exception

class SignInPresenter(
    override var view: SignInContract.View?,
    private val activity: Activity,
    private val db: UsersRepository = UsersRepository(DatabaseApi)
) : SignInContract.Presenter<SignInContract.View> {

    override fun logIn(email: String, password: String){
        // TODO: Client-side validation
        CoroutineScope(Dispatchers.Main).launch {
            val result = try{
                db.loginUser(email, password)
            }catch (e: Exception){
                Result.Error(Exception(e.message))
            }
            Log.d("Debug", result.toString())
            when (result) {
                is Result.Success<LoginResponse> -> {
                    if(result.data.success){
                        view?.launchApp()
                    }else{
                        view?.showWrongCredentialsError()
                    }
                }
                else -> {
                    // TODO: Show toast of invalid request
                    view?.showWrongCredentialsError()
                }
            }
        }
    }


}

