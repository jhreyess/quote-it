package com.example.quoteit.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.example.quoteit.R
import com.example.quoteit.data.PreferencesDataStore
import com.example.quoteit.data.dataStore
import com.example.quoteit.databinding.FragmentProfileBinding
import com.example.quoteit.ui.QuoteItApp
import com.example.quoteit.ui.utils.AdapterCallback
import com.example.quoteit.ui.utils.PostAdapter
import kotlinx.coroutines.flow.collect

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var userId: Long = 0L

    private val model: ProfileViewModel by viewModels {
        ProfileViewModelFactory((requireActivity().application as QuoteItApp).postsRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val preferences = PreferencesDataStore(requireContext().dataStore)
        val adapter = PostAdapter(context, object: AdapterCallback {
            override fun onDetailsClicked(view: View, id: Long) {
                // TODO: POST SETTINGS
            }

            override fun onFavoriteClicked(id: Long, b: Boolean) { model.likePost(id, b) }
        })

        binding.userPostsRecycler.adapter = adapter
        binding.profileSwipeRefresh.setOnRefreshListener { model.getUserPosts(fromUser = userId) }
        preferences.preferenceFlow.asLiveData().observe(viewLifecycleOwner) {
            binding.profileToolbar.title = it.username
            userId = it.userId
            model.getUserPosts(fromUser = it.userId)
        }
        binding.likeFolder.setOnClickListener {
            val action = R.id.action_profileFragment_to_likedPostsFragment
            findNavController().navigate(action)
        }
        model.posts.observe(viewLifecycleOwner) {
            binding.emptyProfileView.visibility = if(it.isEmpty()) View.VISIBLE else View.GONE
            if(it.isNotEmpty()){ adapter.setData(it) }
        }
        model.isLoading.observe(viewLifecycleOwner) { binding.profileSwipeRefresh.isRefreshing = it}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}