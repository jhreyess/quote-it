package com.example.quoteit.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quoteit.R
import com.example.quoteit.data.TestingDatasource

class QuoteAdapter(
    private val folderTitle: String
    ) : RecyclerView.Adapter<QuoteAdapter.QuoteViewHolder>() {

        private var quotes = TestingDatasource.folders.single { it.title == folderTitle }.quotes

        class QuoteViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
            val preview: TextView = view!!.findViewById(R.id.quote_preview)
            //val favorite = view.findViewById<ImageView>(R.id.favorite)
        }

    override fun getItemCount(): Int = quotes.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.quotes_list_items, parent, false)

        return QuoteViewHolder(layout)
    }

    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        println(quotes)
        val quote = quotes[position]
        holder.preview.text = quote.quote
    }

}