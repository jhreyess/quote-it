package com.example.quoteit.ui.signin

import com.example.quoteit.ui.base.BasePresenter
import com.example.quoteit.ui.base.BaseView
import kotlinx.coroutines.CoroutineScope

interface SignInContract {

    interface Presenter <T: BaseView> : BasePresenter<T>{
        fun logIn(email: String, password: String)
    }

    interface View : BaseView, ValidationView {
        fun goToRegister()
    }
}