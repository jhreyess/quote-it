package com.example.quoteit.ui.signin

import com.example.quoteit.ui.base.BaseView

interface ValidationView : BaseView {
    fun launchApp()
    fun showLoadingScreen()
    fun showEmptyEmailError()
    fun showEmptyPasswordError()
    fun showEmptyFieldsError()
    fun showWrongCredentialsError()
}