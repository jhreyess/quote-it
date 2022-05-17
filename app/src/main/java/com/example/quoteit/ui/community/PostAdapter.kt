package com.example.quoteit.ui.community

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.quoteit.R
import com.example.quoteit.domain.models.Folder
import com.example.quoteit.domain.models.Post
import com.example.quoteit.ui.utils.AdapterCallback

class PostAdapter(
    private val context: Context?,
    private val callback: AdapterCallback
): RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private var posts: List<Post> = listOf()

    class PostViewHolder(val view: View?): RecyclerView.ViewHolder(view!!) {
        // Declare and initialize all of the list item UI components
        val user: TextView = view!!.findViewById(R.id.post_user)
        val quote: TextView = view!!.findViewById(R.id.post_quote)
        val author: TextView = view!!.findViewById(R.id.post_quote_author)
        val likes: TextView = view!!.findViewById(R.id.post_likes)
        val isLiked: CheckBox = view!!.findViewById(R.id.favorite_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {

        // Inflate the layout
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.posts_list_items, parent, false)

        return PostViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int = posts.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        val resources = context?.resources

        holder.user.text = post.user
        holder.quote.text = post.quote
        holder.author.text = post.author
        holder.likes.text = resources?.getString(R.string.likes_count, post.likes)
        holder.isLiked.isChecked = post.isLiked
        holder.isLiked.setOnCheckedChangeListener { _, newState ->
            post.likes = post.likes.plus(if(newState) 1 else -1)
            post.isLiked = newState
            holder.likes.text = resources?.getString(R.string.likes_count, post.likes)
            callback.onFavoriteClicked(post.id, newState)
        }
    }

    fun setData(newData: List<Post>){
        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = posts.size
            override fun getNewListSize() = newData.size

            override fun areContentsTheSame(
                oldItemPosition: Int,
                newItemPosition: Int
            ): Boolean {
                val oldItem = posts[oldItemPosition]
                val newItem = newData[newItemPosition]
                return oldItem.id == newItem.id
                        && oldItem.user == newItem.user
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = posts[oldItemPosition]
                val newItem = newData[newItemPosition]
                return oldItem.id == newItem.id
            }
        })
        posts = newData
        result.dispatchUpdatesTo(this)
    }
}