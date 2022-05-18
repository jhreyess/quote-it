package com.example.quoteit.ui.signin

import com.example.quoteit.data.network.LoginResponse
import com.example.quoteit.ui.base.BaseView

interface ValidationView : BaseView {
    fun launchApp(loginCredentials: LoginResponse)
    fun showLoadingScreen(loading: Boolean)
    fun showEmptyFieldsError()
    fun showWrongCredentialsError(error: String?)
    fun showExceptionError(exception: Exception)
}