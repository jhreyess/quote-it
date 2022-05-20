package com.quoteit.android.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.quoteit.android.R
import com.quoteit.android.data.PreferencesDataStore
import com.quoteit.android.data.dataStore
import com.quoteit.android.databinding.FragmentProfileBinding
import com.quoteit.android.ui.EditImageActivity
import com.quoteit.android.ui.QuoteItApp
import com.quoteit.android.ui.utils.AdapterCallback
import com.quoteit.android.ui.utils.ConfirmDialog
import com.quoteit.android.ui.utils.PostAdapter

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
            override fun onDetailsClicked(view: View, canEdit: Boolean, id: Long) {
                val menu = if(canEdit) R.menu.post_creator_detail_menu else R.menu.post_detail_menu
                showPopUp(view, menu, id)
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
        model.isRefreshing.observe(viewLifecycleOwner) { binding.profileSwipeRefresh.isRefreshing = it}
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