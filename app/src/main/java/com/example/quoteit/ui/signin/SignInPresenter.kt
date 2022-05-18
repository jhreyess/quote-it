package com.example.quoteit.ui.signin

import android.app.Activity
import com.example.quoteit.data.network.Result
import com.example.quoteit.ui.QuoteItApp
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

class SignInPresenter(
    override var view: SignInContract.View?,
    activity: Activity,
) : SignInContract.Presenter<SignInContract.View> {

    private val usersDao = (activity.application as QuoteItApp).usersRepository

    override fun logIn(email: String, password: String){
        when(validation(email, password)){
            Validation.EMPTY_FIELDS -> { view?.showEmptyFieldsError() }
            Validation.EMPTY_EMAIL -> { view?.showEmptyEmailError() }
            Validation.EMPTY_PASSWORD -> { view?.showEmptyPasswordError() }
            Validation.IS_VALID -> {
                CoroutineScope(Dispatchers.Main).launch {
                    usersDao.loginUser(email, password).collect { result ->
                        when (result) {
                            is Result.Success -> {
                                if(result.data.success){
                                    view?.launchApp(result.data)
                                }else{
                                    view?.showWrongCredentialsError(result.data.error)
                                }
                            }
                            is Result.Error -> { view?.showExceptionError(result.exception) }
                            is Result.Loading -> { view?.showLoadingScreen(result.isLoading) }
                        }
                    }
                }
            }
        }
    }

    private fun validation(email: String, password: String): Validation {
        return when {
            (email.isBlank() && password.isBlank()) -> { Validation.EMPTY_FIELDS }
            password.isBlank() -> { Validation.EMPTY_PASSWORD }
            email.isBlank() -> { Validation.EMPTY_PASSWORD }
            else -> { Validation.IS_VALID }
        }
    }

    private enum class Validation{
        EMPTY_FIELDS,
        EMPTY_EMAIL,
        EMPTY_PASSWORD,
        IS_VALID
    }

}

