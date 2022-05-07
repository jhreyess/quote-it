package com.example.quoteit.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.quoteit.databinding.FragmentHomeBinding
import com.example.quoteit.ui.QuoteItApp
import com.example.quoteit.ui.utils.BottomSheet
import com.example.quoteit.ui.utils.BottomSheetListener

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val model: HomeViewModel by viewModels {
        HomeViewModelFactory((requireActivity().application as QuoteItApp).foldersRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = FolderAdapter(this)

        // Bindings
        binding.createFolderButton.setOnClickListener { showDialog() }
        binding.folderRecycler.adapter = adapter
        binding.folderRecycler.setHasFixedSize(false)

        model.folders.observe(viewLifecycleOwner) { folders ->
            binding.emptyView.visibility = if (folders.isEmpty()) View.VISIBLE else View.GONE
            folders.let { adapter.setData(it) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showDialog(){
        val bottomSheet = BottomSheet()
        bottomSheet.onActionCompleteListener(object : BottomSheetListener {
            override fun onConfirm(str: String) { model.insertFolder(str) }
            override fun onCancel() {}
        })
        bottomSheet.show(parentFragmentManager, "new_folder_bottom_sheet")
    }

}

