package com.example.quoteit.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import com.example.quoteit.R
import com.example.quoteit.data.TestingDatasource
import com.example.quoteit.databinding.FragmentHomeBinding
import com.example.quoteit.data.models.Folder
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Bindings
        binding.createFolderButton.setOnClickListener { showDialog() }
        binding.folderRecycler.adapter = FolderAdapter(this, TestingDatasource.folders)
        binding.folderRecycler.setHasFixedSize(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showDialog(){
        val bottomSheet = BottomSheetDialog(requireContext())
        bottomSheet.setContentView(R.layout.new_folder_dialog)
        bottomSheet.behavior.isHideable = true

        val cancel = bottomSheet.findViewById<MaterialButton>(R.id.cancel_button)
        val confirm = bottomSheet.findViewById<MaterialButton>(R.id.confirm_button)
        val input = bottomSheet.findViewById<TextInputLayout>(R.id.new_folder_name)

        input?.editText?.doOnTextChanged { _, _, _, count ->
            confirm?.isEnabled = count != 0
        }
        confirm?.setOnClickListener { createFolder(input?.editText?.text.toString()) }
        cancel?.setOnClickListener { bottomSheet.dismiss() }

        bottomSheet.show()
    }

    private fun createFolder(name: String){
        TestingDatasource.addFolder(Folder(name))
        val size = TestingDatasource.folders.size
        binding.folderRecycler.adapter?.notifyItemInserted(size - 1)
    }

}

