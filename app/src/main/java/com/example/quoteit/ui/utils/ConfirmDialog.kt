package com.example.quoteit.ui.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.quoteit.R

class ConfirmDialog(
    private val title: String
): DialogFragment() {

    private var mListener: DialogCallback? = null

    fun onActionCompleteListener(listener: DialogCallback){
        mListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage(title)
                .setPositiveButton(R.string.confirm) { _, _ ->
                    mListener?.onConfirm(null)
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }
}