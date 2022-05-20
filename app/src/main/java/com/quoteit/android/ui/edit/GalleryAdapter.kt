package com.quoteit.android.ui.edit

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.quoteit.android.R

class GalleryAdapter(
    private val images: List<Int>,
    private val context: Context
): RecyclerView.Adapter<GalleryAdapter.FolderViewHolder>() {

    private var selectImageCallback: (() -> Unit?)? = null
    private var callback: ((drawable: Int) -> Unit?)? = null

    class FolderViewHolder(val view: View?): RecyclerView.ViewHolder(view!!) {
        val imageView: ImageView = view!!.findViewById(R.id.gallery_placeholder)
    }

    fun setOnImageSelectedListener(cb: (drawable: Int) -> Unit){
        callback = cb
    }

    fun setOnPickImageListener(cb: () -> Unit){
        selectImageCallback = cb
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.gallery_list_items, parent, false)

        return FolderViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val imageRes = images[position]
        holder.imageView.setImageDrawable(ContextCompat.getDrawable(context, imageRes))
        if (position == 0){
            holder.imageView.setOnClickListener { selectImageCallback?.invoke() }
        }else{
            holder.imageView.setOnClickListener { callback?.invoke(imageRes) }
        }
    }

}