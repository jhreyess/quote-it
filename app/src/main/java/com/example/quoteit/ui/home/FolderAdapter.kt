package com.example.quoteit.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.quoteit.R
import com.example.quoteit.domain.models.Folder
import com.google.android.material.card.MaterialCardView

class FolderAdapter(
    private val context: HomeFragment?,
): RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {

    private var folders: List<Folder> = listOf()

    class FolderViewHolder(val view: View?): RecyclerView.ViewHolder(view!!) {
        // Declare and initialize all of the list item UI components
        val titleTextView: TextView = view!!.findViewById(R.id.folder_title)
        val quantityTextView: TextView = view!!.findViewById(R.id.folder_quantity)
        val cardButton: MaterialCardView = view!!.findViewById(R.id.card_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {

        // Inflate the layout
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.folder_list_items, parent, false)

        return FolderViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int = folders.size

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folder = folders[position]
        val resources = context?.resources

        holder.titleTextView.text = folder.title
        holder.quantityTextView.text =  resources?.getString(R.string.folder_quantity, folder.quantity)

        // Assign onClickListener to each card
        holder.cardButton.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToQuotesListFragment(folder = folder.title)
            holder.view?.findNavController()?.navigate(action)
        }
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