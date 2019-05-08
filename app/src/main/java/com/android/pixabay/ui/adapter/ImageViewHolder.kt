package com.android.pixabay.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.pixabay.model.Hit
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.image_item.view.*

class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(hit: Hit?) {
        if (hit != null) {
            itemView.user_name_textView.text = hit.user
            itemView.tag_textView.text =hit.tags.replace(","," /")
            Picasso.get().load(hit.previewURL).into(itemView.main_imageView)
        }
    }

    companion object {
        fun create(parent: ViewGroup): ImageViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(com.android.pixabay.R.layout.image_item, parent, false)
            return ImageViewHolder(view)
        }
    }
}