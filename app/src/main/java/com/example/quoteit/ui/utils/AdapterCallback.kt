package com.example.quoteit.ui.utils

import android.view.View

interface AdapterCallback {
    fun onItemSelected(id: Long){}
    fun onFavoriteClicked(id: Long, b: Boolean) {}
    fun onDetailsClicked(view: View, canEdit: Boolean, id: Long){}
}