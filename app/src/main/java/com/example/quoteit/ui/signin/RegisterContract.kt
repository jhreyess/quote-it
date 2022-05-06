package com.example.quoteit.ui.signin

import com.example.quoteit.ui.base.BasePresenter
import com.example.quoteit.ui.base.BaseView

class RegisterContract {

    interface Presenter <T: BaseView> : BasePresenter<T> {
        fun register(email: String, username: String, password: String, confirm: String)
    }

    interface View : BaseView, ValidationView {
        fun goToLogin()
        fun showInvalidPasswordsError()
        fun showWeakPasswordError()
    }
}