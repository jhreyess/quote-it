package com.quoteit.android.ui.newpost

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.quoteit.android.R
import com.quoteit.android.databinding.FragmentNewPostBinding
import com.quoteit.android.domain.models.NewPost
import com.quoteit.android.ui.QuoteItApp

class NewPostFragment : Fragment() {

    private var _binding: FragmentNewPostBinding? = null
    private val binding get() = _binding!!

    private val model: NewPostViewModel by viewModels {
        NewPostViewModelFactory((requireActivity().application as QuoteItApp).postsRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bindings
        binding.postContentAuthor.editText?.addTextChangedListener(validate)
        binding.postContent.editText?.addTextChangedListener(validate)
        binding.postButton.setOnClickListener { uploadPost() }
        binding.backToPostsBtn.setOnClickListener { exitActivity() }
        model.getResult().observe(viewLifecycleOwner) { it?.let {
            val intent = Intent()
            intent.putExtra("RELOAD", true)
            requireActivity().setResult(Activity.RESULT_OK, intent)
            exitActivity()
        }}
    }

    private fun exitActivity(){
        activity?.finish()
        activity?.overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_bottom)
    }

    private val validate = object: TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val author = binding.postContentAuthor.editText!!.text
            val content = binding.postContent.editText!!.text
            binding.postButton.isEnabled = author.isNotBlank() and content.isNotBlank()        }
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun afterTextChanged(p0: Editable?) {}
    }

    private fun uploadPost() {
        val postAuthor = binding.postContentAuthor.editText?.text.toString()
        val postContent = binding.postContent.editText?.text.toString()
        val newPost = NewPost(postAuthor, postContent)
        model.upload(newPost)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}