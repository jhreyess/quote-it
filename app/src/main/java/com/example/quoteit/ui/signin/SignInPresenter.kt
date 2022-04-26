package com.example.quoteit.ui.signin

import com.example.quoteit.ui.base.BasePresenter

import android.app.Activity
import com.example.quoteit.ui.base.BaseView

class SignInUser(
    override var view: SignInContract.View?,
    private val activity: Activity) : SignInContract.Presenter<SignInContract.View> {

    override fun logIn(email: String, password: String) {

    }

}

