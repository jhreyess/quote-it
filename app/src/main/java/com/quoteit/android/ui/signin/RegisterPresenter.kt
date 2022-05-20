package com.quoteit.android.ui.signin

import android.app.Activity
import com.quoteit.android.data.network.Result
import com.quoteit.android.ui.QuoteItApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RegisterPresenter(
    override var view: RegisterContract.View?,
    activity: Activity,
) : RegisterContract.Presenter<RegisterContract.View> {

    private val usersDao = (activity.application as QuoteItApp).usersRepository

    override fun register(email: String, username: String, password: String, confirm: String) {

        when(validation(email, username, password, confirm)){
            Validation.EMPTY_FIELDS -> { view?.showEmptyFieldsError() }
            Validation.PASSWORDS_DO_NOT_MATCH -> { view?.showInvalidPasswordsError() }
            Validation.WEAK_PASSWORD -> { view?.showWeakPasswordError() }
            Validation.IS_VALID -> {
                CoroutineScope(Dispatchers.Main).launch {
                    usersDao.registerUser(username, email, password).collect { result ->
                        when(result) {
                            is Result.Success -> {
                                if (result.data.success) {
                                    view?.launchApp(result.data)
                                } else {
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