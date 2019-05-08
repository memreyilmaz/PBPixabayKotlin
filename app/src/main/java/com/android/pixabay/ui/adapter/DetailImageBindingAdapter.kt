package com.android.pixabay.ui.adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.android.pixabay.R
import com.squareup.picasso.Picasso

object DetailImageBindingAdapter {

    @JvmStatic
    @BindingAdapter("largeImageURL")
    fun setImageUrl(view: ImageView, url: String){
        Picasso.get()
            .load(url)
            .placeholder(R.drawable.pixabay)
            .error(R.drawable.pixabay)
            .into(view)
    }
}