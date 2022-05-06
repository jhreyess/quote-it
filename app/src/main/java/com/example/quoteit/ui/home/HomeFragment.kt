package com.example.quoteit.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.quoteit.data.TestingDatasource
import com.example.quoteit.databinding.FragmentHomeBinding
import com.example.quoteit.data.models.Folder
import com.example.quoteit.ui.utils.BottomSheet
import com.example.quoteit.ui.utils.BottomSheetListener

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
        binding.folderRecycler.setHasFixedSize(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showDialog(){
        val bottomSheet = BottomSheet()
        bottomSheet.onActionCompleteListener(object : BottomSheetListener {
            override fun onConfirm(str: String) { createFolder(str) }
            override fun onCancel() {}
        })
        bottomSheet.show(parentFragmentManager, "new_folder_bottom_sheet")
    }

    private fun createFolder(name: String){
        TestingDatasource.addFolder(Folder(name))
        val size = TestingDatasource.folders.size
        binding.folderRecycler.adapter?.notifyItemInserted(size)
    }

}

