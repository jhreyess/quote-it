package com.quoteit.android.ui.utils

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.quoteit.android.R

class ConfirmDialog(
    private val title: String
): DialogFragment() {

    private var confirmListener: (() -> Unit)? = null

    fun setOnConfirmListener(callback: () -> Unit){
        confirmListener = callback
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage(title)
                .setPositiveButton(R.string.confirm) { _, _ ->
                    confirmListener?.invoke()
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onDetach() {
        confirmListener = null
        super.onDetach()
    }
}