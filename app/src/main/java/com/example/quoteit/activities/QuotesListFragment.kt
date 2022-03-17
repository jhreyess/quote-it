package com.example.quoteit.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.quoteit.adapters.QuoteAdapter
import com.example.quoteit.databinding.FragmentQuotesListBinding

class QuotesListFragment : Fragment() {

    companion object{
        const val FOLDER = "folder"
    }

    private var _binding: FragmentQuotesListBinding? = null
    private val binding get() = _binding!!

    private lateinit var folderTitle: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve the FOLDER TITLE from the Fragment arguments
        arguments?.let {
            folderTitle = it.getString(FOLDER).toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuotesListBinding.inflate(inflater, container, false)

        // Bindings
        binding.backButton.setOnClickListener {findNavController().popBackStack()}
        binding.fragmentLabel.text = folderTitle

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Recycler Viewer
        binding.quotesRecycler.adapter = QuoteAdapter(folderTitle)
        binding.quotesRecycler.setHasFixedSize(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}