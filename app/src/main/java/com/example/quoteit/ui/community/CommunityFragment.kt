package com.example.quoteit.ui.community

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.quoteit.R
import com.example.quoteit.databinding.FragmentCommunityBinding
import com.example.quoteit.ui.NewPostActivity
import com.example.quoteit.ui.QuoteItApp
import com.example.quoteit.ui.utils.AdapterCallback
import com.example.quoteit.ui.utils.PostAdapter

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
        val adapter = PostAdapter(context, object: AdapterCallback {
            override fun onFavoriteClicked(id: Long, b: Boolean) {
                model.likePost(id, b)
            }
        })
        binding.postsRecycler.adapter = adapter
        binding.postsRecycler.setHasFixedSize(true)
        binding.newPostBtn.setOnClickListener { createPost() }
        binding.swipeRefresh.setOnRefreshListener { model.getPosts(fetchFromRemote = true) }
        binding.postsRecycler.addOnScrollListener(feedBottom)
        model.posts.observe(viewLifecycleOwner) { adapter.setData(it) }
        model.isLoading.observe(viewLifecycleOwner) { binding.swipeRefresh.isRefreshing = it }
    }

    private val feedBottom = object: RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            val canScrollUp = recyclerView.canScrollVertically(-1)
            val canScrollDown = recyclerView.canScrollVertically(1)
            val letScroll = canScrollUp && !canScrollDown
            if(letScroll && newState == RecyclerView.SCROLL_STATE_IDLE){
                model.getPosts(fetchFromRemote = true, appendContent = true)
            }
        }
    }

    private fun createPost(){
        val intent = Intent(requireActivity(), NewPostActivity::class.java)
        createPostLauncher.launch(intent)
        activity?.overridePendingTransition(R.anim.slide_in_bottom, android.R.anim.fade_out)
    }

    private val createPostLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == Activity.RESULT_OK){
            it.data?.extras?.get("RELOAD").let { model.getPosts() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}