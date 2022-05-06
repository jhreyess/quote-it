package com.example.quoteit.ui.utils

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import com.example.quoteit.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

interface BottomSheetListener{
    fun onConfirm(str: String)
    fun onCancel()
}

class BottomSheet : BottomSheetDialogFragment() {

    private lateinit var mListener: BottomSheetListener

    fun onActionCompleteListener(listener: BottomSheetListener){
        mListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.new_folder_dialog, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cancel = view.findViewById<MaterialButton>(R.id.cancel_button)
        val confirm = view.findViewById<MaterialButton>(R.id.confirm_button)
        val input = view.findViewById<TextInputLayout>(R.id.new_folder_name)

        input?.requestFocus()
        input?.editText?.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    val text = input.editText?.text.toString()
                    if(text.isNotBlank()) {
                        dismiss()
                        mListener.onConfirm(input.editText?.text.toString())
                    }
                    true
                }
                else -> false
            }
        }
        input?.editText?.setOnKeyListener { edit, i, keyEvent ->
            if(i == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_DOWN){
                edit.clearFocus()
                dismiss()
            }
            false
        }
        input?.editText?.doOnTextChanged { _, _, _, count ->
            confirm?.isEnabled = count != 0
        }
        confirm?.setOnClickListener {
            dismiss()
            mListener.onConfirm(input?.editText?.text.toString())
        }
        cancel?.setOnClickListener {
            dismiss()
            mListener.onCancel()
        }
    }

}