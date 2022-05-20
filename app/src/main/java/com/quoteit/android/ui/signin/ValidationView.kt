package com.quoteit.android.ui.signin

import com.quoteit.android.data.network.LoginResponse
import com.quoteit.android.ui.base.BaseView

interface ValidationView : BaseView {
    fun launchApp(loginCredentials: LoginResponse)
    fun showLoadingScreen(loading: Boolean)
    fun showEmptyFieldsError()
    fun showWrongCredentialsError(error: String?)
    fun showExceptionError(exception: Exception)
}