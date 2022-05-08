package com.example.quoteit.ui.utils

interface DialogCallback {
    fun onConfirm(str: String?)
    fun onCancel(){}
}