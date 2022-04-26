package com.example.quoteit.ui.signin

import com.example.quoteit.ui.base.BasePresenter

import android.app.Activity
import com.example.quoteit.ui.base.BaseView

class RegisterPresenter(
    override var view: RegisterContract.View?,
    private val activity: Activity) : RegisterContract.Presenter<RegisterContract.View> {

    override fun register(email: String, password: String) {
        TODO("Not yet implemented")
    }

}