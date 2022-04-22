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
import com.example.quoteit.network.Quote
import com.example.quoteit.network.QuotesApi
import kotlinx.coroutines.*
import java.lang.Exception

class QuoteViewModel : ViewModel() {

    private val quote: MutableLiveData<Quote> by lazy {
        MutableLiveData<Quote>().also { fetchQuote() }
    }

    fun getQuote(): LiveData<Quote> { return quote }
    private fun setQuote(data: Quote){ quote.value = data }

    fun fetchQuote(){
        viewModelScope.launch {
            try{
                val data = QuotesApi.retrofitService.getSingleQuote(BuildConfig.API_KEY, "es")
                setQuote(data)
            }catch (e: Exception){
                // TODO Show error message
                Log.i("Debug", "Error -> $e")
            }
        }
    }
}

class DiscoverFragment : Fragment() {

    private var _binding: FragmentDiscoverBinding? = null
    private val binding get() = _binding!!

    private lateinit var model: QuoteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiscoverBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model = ViewModelProvider(requireActivity())[QuoteViewModel::class.java]

        // Bindings
        model.getQuote().observe(viewLifecycleOwner, Observer {
            binding.quotePlaceholder.text = it.content
            binding.quoteAuthorPlaceholder.text = it.author.name
            binding.nextButton.isEnabled = true
            // TODO Remove loader
        })

        binding.nextButton.setOnClickListener { fetchQuote() }
    }

    private fun fetchQuote(){
        // TODO Show loader
        binding.nextButton.isEnabled = false
        model.fetchQuote()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

