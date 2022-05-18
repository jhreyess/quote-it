package com.example.quoteit.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.quoteit.databinding.FragmentLikedPostsBinding
import com.example.quoteit.ui.QuoteItApp
import com.example.quoteit.ui.utils.PostAdapter
import com.example.quoteit.ui.utils.AdapterCallback

class LikedPostsFragment : Fragment() {

    private var _binding: FragmentLikedPostsBinding? = null
    private val binding get() = _binding!!

    private val model: ProfileViewModel by viewModels {
        ProfileViewModelFactory((requireActivity().application as QuoteItApp).postsRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLikedPostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.getLikedPosts()

        // Bindings
        val adapter = PostAdapter(context, object: AdapterCallback {
            override fun onDetailsClicked(view: View, id: Long) {
                // TODO: Settings
            }

            override fun onFavoriteClicked(id: Long, b: Boolean) { model.likePost(id, b) }
        })
        binding.backToProfileBtn.setOnClickListener { findNavController().popBackStack() }
        binding.likedPostsRecycler.adapter = adapter
        binding.likedPostsRecycler.setHasFixedSize(true)
        model.likedPosts.observe(viewLifecycleOwner) { adapter.setData(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}