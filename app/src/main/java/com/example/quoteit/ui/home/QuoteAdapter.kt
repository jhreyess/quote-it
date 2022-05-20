package com.example.quoteit.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.quoteit.R
import com.example.quoteit.domain.models.Quote
import com.example.quoteit.ui.utils.AdapterCallback

class QuoteAdapter(
    private val callback: AdapterCallback
) : RecyclerView.Adapter<QuoteAdapter.QuoteViewHolder>() {

    private var quotes: List<Quote> = listOf()

    class QuoteViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        val preview: TextView = view!!.findViewById(R.id.quote_preview)
        val favorite: CheckBox = view!!.findViewById(R.id.favorite_button)
        val more: ImageButton = view!!.findViewById(R.id.more_actions_button)
    }

    override fun getItemCount(): Int = quotes.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.quotes_list_items, parent, false)

        return QuoteViewHolder(layout)
    }

    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        val quote = quotes[position]
        holder.preview.text = quote.quote
        holder.favorite.isChecked = quote.isFavorite
        holder.favorite.setOnCheckedChangeListener { _, b -> callback.onFavoriteClicked(quote.id, b) }
        holder.more.setOnClickListener { callback.onDetailsClicked(it, true, quote.id) }
    }

    fun setData(newData: List<Quote>){
        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = quotes.size
            override fun getNewListSize() = newData.size

            override fun areContentsTheSame(
                oldItemPosition: Int,
                newItemPosition: Int
            ): Boolean {
                val oldItem = quotes[oldItemPosition]
                val newItem = newData[newItemPosition]
                return oldItem.id == newItem.id
                        && oldItem.author == newItem.author
                        && oldItem.quote == oldItem.quote
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = quotes[oldItemPosition]
                val newItem = newData[newItemPosition]
                return oldItem.id == newItem.id
            }
        })
        quotes = newData
        result.dispatchUpdatesTo(this)
    }

}