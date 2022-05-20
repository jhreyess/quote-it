package com.quoteit.android.ui.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.quoteit.android.databinding.FragmentDiscoverBinding

class DiscoverFragment : Fragment() {

    private var _binding: FragmentDiscoverBinding? = null
    private val binding get() = _binding!!

    private val model: QuoteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiscoverBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bindings
        model.getQuote().observe(viewLifecycleOwner, Observer {
            binding.quotePlaceholder.text = it.content
            binding.quoteAuthorPlaceholder.text = it.author.name
            binding.nextButton.isEnabled = true
            binding.loadingScreen.visibility = View.GONE
        })

        binding.nextButton.setOnClickListener { fetchQuote() }
    }

    private fun fetchQuote(){
        binding.loadingScreen.visibility = View.VISIBLE
        binding.quotePlaceholder.text = ""
        binding.quoteAuthorPlaceholder.text = ""
        binding.nextButton.isEnabled = false
        model.fetchQuote()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

