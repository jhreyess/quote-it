package com.example.quoteit.ui.community

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quoteit.R
import com.example.quoteit.data.TestingDatasource

class PostAdapter(
    private val context: CommunityFragment?,
): RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private val posts = TestingDatasource.posts

    class PostViewHolder(val view: View?): RecyclerView.ViewHolder(view!!) {
        // Declare and initialize all of the list item UI components
        val user: TextView = view!!.findViewById(R.id.post_user)
        val quote: TextView = view!!.findViewById(R.id.post_quote)
        val author: TextView = view!!.findViewById(R.id.post_quote_author)
        val likes: TextView = view!!.findViewById(R.id.post_likes)
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
    }
}