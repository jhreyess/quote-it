package com.example.quoteit.ui.base

interface BasePresenter<T : BaseView> {
    var view: T?
    fun onDestroy(){ view = null }
}