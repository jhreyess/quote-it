package com.example.quoteit.ui.signin

import com.example.quoteit.ui.base.BasePresenter

import android.app.Activity
import com.example.quoteit.data.UsersRepository
import com.example.quoteit.data.network.DatabaseApi
import com.example.quoteit.data.network.LoginResponse
import com.example.quoteit.data.network.Result
import com.example.quoteit.ui.base.BaseView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class RegisterPresenter(
    override var view: RegisterContract.View?,
    private val activity: Activity,
    private val db: UsersRepository = UsersRepository(DatabaseApi)
) : RegisterContract.Presenter<RegisterContract.View> {

    override fun register(email: String, username: String, password: String, confirm: String) {
        // TODO: Client-side validation
        CoroutineScope(Dispatchers.Main).launch {
            val result = try{
                db.registerUser(email, username, password)
            }catch (e: Exception){
                Result.Error(Exception("Fallo en la solicitud..."))
            }
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
                }
            }
        }
    }

}