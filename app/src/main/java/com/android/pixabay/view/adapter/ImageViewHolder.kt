package com.android.pixabay.view.adapter

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.android.pixabay.model.Hit
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.image_item.view.*




class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(hit: Hit?) {
        if (hit != null) {
            itemView.user_name_textView.text = hit.user
            itemView.tag_textView.text =hit.tags.replace(","," /")
            Picasso.get().load(hit.previewURL)
                //.into(itemView.main_imageView)
                .into(getTarget(itemView.main_imageView, itemView.user_name_textView, itemView.tag_textView))
        }
    }

    companion object {
        fun create(parent: ViewGroup): ImageViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(com.android.pixabay.R.layout.image_item, parent, false)
            return ImageViewHolder(view)
        }
    }


    fun getTarget(view: ImageView, textView: TextView, textView2: TextView): Target {
        return object : Target {
            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}
            override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                view.setImageBitmap(bitmap)
                Palette.from(bitmap).generate {
                    val swatch: Palette.Swatch? = it?.vibrantSwatch
                    val tv1 = textView.findViewById(com.android.pixabay.R.id.user_name_textView) as TextView
                    val tv2= textView2.findViewById(com.android.pixabay.R.id.tag_textView) as TextView

                    if (swatch != null) {
                        //layout.getBackground().setAlpha(100)
                        tv1.setBackgroundColor(swatch.rgb)
                        tv2.setBackgroundColor(swatch.rgb)
                    } else{
                       // layout.setBackgroundColor(R.color.darkGray)
                    }
                }
            }
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
        }
    }
}