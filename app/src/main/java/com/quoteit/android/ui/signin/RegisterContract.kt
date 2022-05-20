package com.quoteit.android.ui.signin

import com.quoteit.android.ui.base.BasePresenter
import com.quoteit.android.ui.base.BaseView

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