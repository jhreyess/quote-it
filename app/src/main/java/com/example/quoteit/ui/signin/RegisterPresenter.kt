package com.example.quoteit.ui.signin

import android.app.Activity
import com.example.quoteit.data.UsersRepository
import com.example.quoteit.data.network.DatabaseApi
import com.example.quoteit.data.network.LoginResponse
import com.example.quoteit.data.network.Result
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

        when(validation(email, username, password, confirm)){
            Validation.EMPTY_FIELDS -> { view?.showEmptyFieldsError() }
            Validation.PASSWORDS_DO_NOT_MATCH -> { view?.showInvalidPasswordsError() }
            Validation.WEAK_PASSWORD -> { view?.showWeakPasswordError() }
            Validation.IS_VALID -> {
                CoroutineScope(Dispatchers.Main).launch {
                    view?.showLoadingScreen()
                    val result = try{
                        db.registerUser(username, email, password)
                    }catch (e: Exception){
                        Result.Error(Exception("Fallo en la solicitud..."))
                    }finally {
                        view?.hideLoadingScreen()
                    }

                    when (result) {
                        is Result.Success<LoginResponse> -> {
                            if(result.data.success){ view?.launchApp()
                            }else{ view?.showWrongCredentialsError(result.data.error) }
                        }
                        is Result.Error -> { view?.showExceptionError(result.exception)
                        }
                    }
                }
            }
        }
    }

    private fun validation(email: String, username: String, password: String, confirm: String): Validation {
        return when {
            email.isBlank() -> { Validation.EMPTY_FIELDS }
            username.isBlank() -> { Validation.EMPTY_FIELDS }
            password.isBlank() -> { Validation.EMPTY_FIELDS }
            confirm.isBlank() -> { Validation.EMPTY_FIELDS }
            (password.length < 6) -> { Validation.WEAK_PASSWORD }
            !password.equals(confirm, false) -> { Validation.PASSWORDS_DO_NOT_MATCH }
            else -> { Validation.IS_VALID }
        }
    }

    private enum class Validation{
        EMPTY_FIELDS,
        PASSWORDS_DO_NOT_MATCH,
        IS_VALID,
        WEAK_PASSWORD
    }

}