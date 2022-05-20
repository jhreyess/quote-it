package com.quoteit.android.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.quoteit.android.R
import com.quoteit.android.domain.models.Folder
import com.quoteit.android.ui.utils.AdapterCallback

class FolderAdapter(
    private val context: Context?,
    private val layout: Int = 0,
    private val callback: AdapterCallback
): RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {

    private var folders: List<Folder> = listOf()

    class FolderViewHolder(val view: View?): RecyclerView.ViewHolder(view!!) {
        // Declare and initialize all of the list item UI components
        val titleTextView: TextView = view!!.findViewById(R.id.folder_title)
        val quantityTextView: TextView = view!!.findViewById(R.id.folder_quantity)
        val cardButton: MaterialCardView = view!!.findViewById(R.id.card_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val layout = when(layout){
            0 -> R.layout.folder_list_items
            else -> R.layout.dialog_list_items
        }
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(layout, parent, false)

        return FolderViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int = folders.size

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folder = folders[position]
        val resources = context?.resources

        holder.titleTextView.text = folder.title
        holder.quantityTextView.text =  resources?.getString(R.string.folder_quantity, folder.quantity)

        // Assign onClickListener to each card
        holder.cardButton.setOnClickListener { callback.onItemSelected(folder.id) }
    }

    fun setData(newData: List<Folder>){
        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = folders.size
            override fun getNewListSize() = newData.size

            override fun areContentsTheSame(
                oldItemPosition: Int,
                newItemPosition: Int
            ): Boolean {
                val oldItem = folders[oldItemPosition]
                val newItem = newData[newItemPosition]
                return oldItem.id == newItem.id
                        && oldItem.title == newItem.title
                        && oldItem.quantity == oldItem.quantity
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = folders[oldItemPosition]
                val newItem = newData[newItemPosition]
                return oldItem.id == newItem.id
            }
        })
        folders = newData
        result.dispatchUpdatesTo(this)
    }

}