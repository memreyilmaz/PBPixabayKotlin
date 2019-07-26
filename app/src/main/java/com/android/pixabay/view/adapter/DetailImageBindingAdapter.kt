package com.android.pixabay.view.adapter

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.palette.graphics.Palette
import com.android.pixabay.R
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

object DetailImageBindingAdapter {

    @JvmStatic
    @BindingAdapter("largeImageURL")
    fun setImageUrl(view: ImageView, url: String){
        Picasso.get()
            .load(url)
            .placeholder(R.drawable.pixabaylogo)
            .error(R.drawable.pixabaylogo)
            .into(view)
            //.into(getTarget(view))
    }

}

    fun getTarget(view: ImageView): Target {
    return object : Target {
        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}
        override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
            view.setImageBitmap(bitmap)
            Palette.from(bitmap).generate(Palette.PaletteAsyncListener {
                val swatch: Palette.Swatch? = it?.vibrantSwatch
                val layout = R.id.detail_layout as ConstraintLayout
                if (swatch != null) {
                    layout.setBackgroundColor(swatch.bodyTextColor)
                }

            })
        }
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
    }
}
