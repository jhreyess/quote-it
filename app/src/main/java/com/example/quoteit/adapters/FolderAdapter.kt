package com.example.quoteit.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quoteit.R
import com.example.quoteit.activities.HomeFragment
import com.example.quoteit.models.Folder

class FolderAdapter(
    private val context: HomeFragment?,
    private val folders: List<Folder>
): RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {

    class FolderViewHolder(view: View?): RecyclerView.ViewHolder(view!!) {
        // Declare and initialize all of the list item UI components
        val titleTextView: TextView = view!!.findViewById(R.id.folder_title)
        val quantityTextView: TextView = view!!.findViewById(R.id.folder_quantity)
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
    }
}