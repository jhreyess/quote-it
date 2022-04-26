package com.example.quoteit.ui.signin

import com.example.quoteit.ui.base.BasePresenter
import com.example.quoteit.ui.base.BaseView

interface SignInContract {

    interface Presenter <T: BaseView> : BasePresenter<T>{
        fun logIn(email: String, password: String)
    }

    interface View : BaseView {
        fun launchApp()
        fun showLoadingScreen()
        fun showEmptyEmailError()
        fun showEmptyPasswordError()
        fun showEmptyFieldsError()
        fun showWrongCredentialsError()
        fun goToRegister()
    }
}