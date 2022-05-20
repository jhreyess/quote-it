package com.quoteit.android.ui.community

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.quoteit.android.R
import com.quoteit.android.databinding.FragmentCommunityBinding
import com.quoteit.android.ui.EditImageActivity
import com.quoteit.android.ui.NewPostActivity
import com.quoteit.android.ui.QuoteItApp
import com.quoteit.android.ui.utils.AdapterCallback
import com.quoteit.android.ui.utils.ConfirmDialog
import com.quoteit.android.ui.utils.PostAdapter

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
            override fun onDetailsClicked(view: View, canEdit: Boolean, id: Long) {
                val menu = if(canEdit) R.menu.post_creator_detail_menu else R.menu.post_detail_menu
                showPopUp(view, menu, id)
            }
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
        model.isRefreshing.observe(viewLifecycleOwner) { binding.swipeRefresh.isRefreshing = it }
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

    private fun showPopUp(view: View, menu: Int, itemId: Long){
        PopupMenu(requireContext(), view).apply {
            setOnMenuItemClickListener { popUpMenuItem(it, itemId) }
            inflate(menu)
            show()
        }
    }

    private fun popUpMenuItem(item: MenuItem, postId: Long): Boolean {
        return when(item.itemId){
            R.id.delete_post -> { deletePost(postId) }
            R.id.share_post -> { sharePost(postId) }
            R.id.convert_post -> { pickImage(postId) }
            else -> false
        }
    }

    private fun showAlert(message: String, callback: () -> Unit){
        val dialog = ConfirmDialog(message)
        dialog.setOnConfirmListener(callback)
        dialog.show(parentFragmentManager, "confirm_delete_dialog")
    }

    private fun deletePost(id: Long): Boolean{
        showAlert(resources.getString(R.string.confirm_post_delete)) {
            model.deletePost(id)
        }
        return true
    }

    private fun pickImage(id: Long): Boolean{
        val intent = Intent(requireActivity(), EditImageActivity::class.java)
        intent.putExtra("post", id)
        startActivity(intent)
        activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        return true
    }

    private fun sharePost(id: Long): Boolean{
        model.getPost(id).observe(viewLifecycleOwner){
            val intent = Intent(Intent.ACTION_SEND);
            val shareBody = StringBuilder()
                .appendLine(it.quote).appendLine()
                .append("-").appendLine(it.author).appendLine(resources.getString(R.string.shared_label))
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, shareBody.toString())
            startActivity(Intent.createChooser(intent, resources.getString(R.string.share_with)))
        }
        return true
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}