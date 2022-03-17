package com.example.quoteit.activities

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.*
import com.example.quoteit.BuildConfig
import com.example.quoteit.databinding.FragmentDiscoverBinding
import com.example.quoteit.network.QuotesApi
import kotlinx.coroutines.*
import java.lang.Exception

class DiscoverFragment : Fragment() {

    private var _binding: FragmentDiscoverBinding? = null
    private val binding get() = _binding!!

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
        binding.nextButton.setOnClickListener { fetchQuote() }


        // Fetch on init
        fetchQuote()
    }

    private fun fetchQuote(){
        // TODO Show loader
        binding.nextButton.isEnabled = false
        viewLifecycleOwner.lifecycleScope.launch {
            try{
                val quote = QuotesApi.retrofitService.getSingleQuote(BuildConfig.API_KEY, "es")
                binding.quotePlaceholder.text = quote.content
                binding.quoteAuthorPlaceholder.text = quote.author.name
            }catch (e: Exception){
                // TODO Show error message
                Log.i("Debug", "Error -> $e")
            }
        }
        binding.nextButton.isEnabled = true
        // TODO Remove loader
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

