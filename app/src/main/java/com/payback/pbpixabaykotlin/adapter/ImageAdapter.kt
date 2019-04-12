package com.payback.pbpixabaykotlin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.payback.pbpixabaykotlin.R
import com.payback.pbpixabaykotlin.model.Hit
import com.squareup.picasso.Picasso

class ImageAdapter : RecyclerView.Adapter<ImageAdapter.ImageAdapterViewHolder>() {
    lateinit var mContext : Context
    var mImages : List<Hit>? = null
    lateinit var mClickListener: ClickListener

    inner class ImageAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        override fun onClick(v: View) {
            mClickListener.onItemClick(v, adapterPosition)
        }

        var userNameTextView : TextView
        var tagsTextView : TextView
        var posterImageView : ImageView

        init {
            itemView.tag = this
            userNameTextView = itemView.findViewById(R.id.user_name_textView)
            tagsTextView = itemView.findViewById(R.id.tag_textView)
            posterImageView = itemView.findViewById(R.id.main_imageView)

            itemView.setOnClickListener(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageAdapter.ImageAdapterViewHolder {
        mContext = parent.context
        val view : View = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false)
        return ImageAdapterViewHolder(view)
    }
    override fun onBindViewHolder(holder: ImageAdapter.ImageAdapterViewHolder, position: Int) {
        val hit: Hit? = mImages?.get(position)
        holder.userNameTextView.text = hit?.user
        holder.tagsTextView.text = hit?.tags?.replace(","," /")
        Picasso.with(mContext)
            .load(hit?.previewURL)
            .into(holder.posterImageView)
    }
    override fun getItemCount(): Int {
        if (mImages == null) return 0
        return mImages!!.size
    }
    fun setImageData(images: List<Hit>){
        mImages = images
        notifyDataSetChanged()
    }
    fun getHitAtPosition(position: Int): Hit? {
        return mImages?.get(position)
    }
    fun setOnItemClickListener(clickListener: ClickListener){
        mClickListener = clickListener
    }

    interface ClickListener {
        fun onItemClick(v: View, position: Int)
    }
}