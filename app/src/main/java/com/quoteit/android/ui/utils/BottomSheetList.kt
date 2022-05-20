package com.quoteit.android.ui.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.quoteit.android.R
import com.quoteit.android.ui.home.FolderAdapter

class BottomSheetList(
    private val dialogTitle: String,
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dialog_list, container, false)

    private var mAdapter: FolderAdapter? = null

    fun setList(adapter: FolderAdapter){
        mAdapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = view.findViewById<TextView>(R.id.dialog_title)
        val cancel = view.findViewById<MaterialButton>(R.id.cancel_button)

        val recycler = view.findViewById<RecyclerView>(R.id.dialog_list)
        mAdapter?.let { recycler.adapter = it }
        recycler.setHasFixedSize(true)
        title.text = dialogTitle

        cancel?.setOnClickListener {
            dismiss()
        }
    }

}