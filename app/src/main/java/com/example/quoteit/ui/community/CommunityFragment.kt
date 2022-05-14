package com.example.quoteit.ui.community

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.quoteit.R
import com.example.quoteit.databinding.FragmentCommunityBinding
import com.example.quoteit.domain.models.NewPost
import com.example.quoteit.domain.models.Post
import com.example.quoteit.ui.MainActivity
import com.example.quoteit.ui.QuoteItApp

class CommunityFragment : Fragment() {

    private var _binding: FragmentCommunityBinding? = null
    private val binding get() = _binding!!

    private val model: CommunityViewModel by viewModels {
        CommunityViewModelFactory((requireActivity().application as QuoteItApp).postsRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommunityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = binding.communityToolbar

        toolbar.title = resources.getString(R.string.community_label)
        toolbar.navigationIcon = null

        // Recycler Viewer
        val adapter = PostAdapter(this)
        binding.postsRecycler.adapter = adapter
        binding.postsRecycler.setHasFixedSize(true)
        binding.postContentAuthor.editText?.addTextChangedListener(validate)
        binding.postContent.editText?.addTextChangedListener(validate)
        binding.swipeRefresh.setOnChildScrollUpCallback { _, _ -> scrollUp() }
        binding.postButton.setOnClickListener { uploadPost() }
        model.posts.observe(viewLifecycleOwner) {
            adapter.setData(it)
        }
    }

    private fun scrollUp(): Boolean{
        model.getPosts(fetchFromRemote = true)
        return true
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