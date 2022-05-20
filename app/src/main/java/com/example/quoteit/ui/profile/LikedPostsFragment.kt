package com.example.quoteit.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.quoteit.R
import com.example.quoteit.databinding.FragmentLikedPostsBinding
import com.example.quoteit.ui.EditImageActivity
import com.example.quoteit.ui.QuoteItApp
import com.example.quoteit.ui.utils.PostAdapter
import com.example.quoteit.ui.utils.AdapterCallback
import com.example.quoteit.ui.utils.ConfirmDialog

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

        // Bindings
        val adapter = PostAdapter(context, object: AdapterCallback {
            override fun onDetailsClicked(view: View, canEdit: Boolean, id: Long) {
                val menu = if(canEdit) R.menu.post_creator_detail_menu else R.menu.post_detail_menu
                showPopUp(view, menu, id)
            }

            override fun onFavoriteClicked(id: Long, b: Boolean) { model.likePost(id, b) }
        })
        binding.profileLikesSwipeRefresh.setOnRefreshListener { model.getLikedPosts(fetchFromRemote = true) }
        binding.backToProfileBtn.setOnClickListener { findNavController().popBackStack() }
        binding.likedPostsRecycler.adapter = adapter
        binding.likedPostsRecycler.setHasFixedSize(true)
        model.likedPosts.observe(viewLifecycleOwner) { adapter.setData(it) }
        model.isRefreshing.observe(viewLifecycleOwner) { binding.profileLikesSwipeRefresh.isRefreshing = it}
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