package com.quoteit.android.ui.base

interface BasePresenter<T : BaseView> {
    var view: T?
    fun onDestroy(){ view = null }
}