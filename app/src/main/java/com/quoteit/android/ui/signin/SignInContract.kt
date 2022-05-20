package com.quoteit.android.ui.signin

import com.quoteit.android.ui.base.BasePresenter
import com.quoteit.android.ui.base.BaseView

interface SignInContract {

    interface Presenter <T: BaseView> : BasePresenter<T>{
        fun logIn(email: String, password: String)
    }

    interface View : BaseView, ValidationView {
        fun goToRegister()
        fun showEmptyEmailError()
        fun showEmptyPasswordError()
    }
}