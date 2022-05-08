package com.example.quoteit.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.quoteit.databinding.FragmentNewQuoteBinding
import com.example.quoteit.ui.QuoteItApp
import kotlinx.coroutines.launch

class NewQuoteFragment : Fragment() {

    private var _binding: FragmentNewQuoteBinding? = null
    private val binding get() = _binding!!

    private var folderId: Long = 0L

    companion object{
        const val FOLDER = "folder"
    }

    private val model: NewQuoteViewModel by viewModels {
        val repos = (requireActivity().application as QuoteItApp)
        NewQuoteViewModelFactory(repos.foldersQuoteRepository, repos.quotesRepository, repos.foldersRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve folderID
        folderId = arguments?.getLong(FOLDER) ?: 0L
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewQuoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bindings
        binding.backToFolderBtn.setOnClickListener { findNavController().popBackStack() }
        binding.confirmQuoteBtn.setOnClickListener {
            val author = binding.quoteAuthor.editText!!.text.toString()
            val content = binding.quoteContent.editText!!.text.toString()
            model.createQuote(author, content, folderId)
        }

        model.isLoading.observe(viewLifecycleOwner) { loading ->
            if(!loading){ findNavController().popBackStack() }
        }

        model.isValidInput.observe(viewLifecycleOwner) { valid ->
            if(!valid) { /* TODO: Show error message */}
        }
        // TODO: Hide tip if showTipPrefs is false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}