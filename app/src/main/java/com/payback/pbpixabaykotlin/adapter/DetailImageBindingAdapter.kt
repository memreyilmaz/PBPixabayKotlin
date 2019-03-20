package com.payback.pbpixabaykotlin.adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.payback.pbpixabaykotlin.R
import com.squareup.picasso.Picasso

object  DetailImageBindingAdapter {

    @JvmStatic
    @BindingAdapter("largeImageURL")
    fun setImageUrl(view: ImageView, url: String){
        Picasso.with(view.context)
            .load(url)
            .placeholder(R.drawable.pixabay)
            .error(R.drawable.pixabay)
            .into(view)
    }
}