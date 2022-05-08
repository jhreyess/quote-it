package com.example.quoteit.ui.utils

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import com.example.quoteit.R
import com.example.quoteit.databinding.FragmentSignInBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class BottomSheet(
    private val dialogTitle: String
) : BottomSheetDialogFragment() {

    private var mListener: DialogCallback? = null

    fun onActionCompleteListener(listener: DialogCallback){
        mListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.new_folder_dialog, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = view.findViewById<TextView>(R.id.dialog_title)
        val cancel = view.findViewById<MaterialButton>(R.id.cancel_button)
        val confirm = view.findViewById<MaterialButton>(R.id.confirm_button)
        val input = view.findViewById<TextInputLayout>(R.id.new_folder_name)

        title.text = dialogTitle

        input?.requestFocus()
        input?.editText?.apply {
            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        if(confirm.isEnabled) {
                            dismiss()
                            mListener?.onConfirm(input.editText?.text.toString())
                        }
                        true
                    }
                    else -> false
                }
            }
            setOnKeyListener { edit, i, keyEvent ->
                if(i == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_DOWN){
                    edit.clearFocus()
                    dismiss()
                }
                false
            }
            addTextChangedListener(object: TextWatcher{
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    confirm.isEnabled = s.toString().trim().isNotEmpty()
                }
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {}
            })
        }
        confirm?.setOnClickListener {
            dismiss()
            mListener?.onConfirm(input?.editText?.text.toString())
        }
        cancel?.setOnClickListener {
            dismiss()
            mListener?.onCancel()
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

}