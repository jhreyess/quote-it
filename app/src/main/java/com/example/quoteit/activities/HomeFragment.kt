package com.example.quoteit.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.example.quoteit.R
import com.example.quoteit.adapters.FolderAdapter
import com.example.quoteit.data.Datasource
import com.example.quoteit.databinding.FragmentHomeBinding
import com.example.quoteit.data.models.Folder
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Bindings
        binding.createFolderButton.setOnClickListener { showDialog() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Recycler Viewer
        binding.folderRecycler.adapter = FolderAdapter(this, Datasource.folders)
        binding.folderRecycler.setHasFixedSize(true)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showDialog(){

        val view = layoutInflater.inflate(R.layout.new_folder_dialog, null)
        val editText = view.findViewById<EditText>(R.id.new_folder_name)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Nuevo").setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_dialog))
            .setView(view)
            .setPositiveButton(R.string.ok) { _,_ -> createFolder(editText.text.toString()) }
            .setNegativeButton(R.string.cancel) { dialog, _  -> dialog.cancel() }
            .show()
    }

    private fun createFolder(name: String){
        Datasource.addFolder(Folder(name))
        binding.folderRecycler.adapter?.notifyItemInserted(2)
    }

}

