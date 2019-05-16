package com.android.pixabay.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.pixabay.State
import com.android.pixabay.model.Hit

class ImageAdapter(private val retry: () -> Unit) : PagedListAdapter<Hit, RecyclerView.ViewHolder>(ImagesDiffCallback) {

    private val DATA_VIEW_TYPE = 1
    private val FOOTER_VIEW_TYPE = 2
    private var state = State.LOADING
    lateinit var mListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == DATA_VIEW_TYPE) ImageViewHolder.create(parent)
        else FooterViewHolder.create(retry, parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == DATA_VIEW_TYPE)
            (holder as ImageViewHolder).bind(getItem(position))
        else (holder as FooterViewHolder).bind(state)
        if (holder is ImageViewHolder){
            holder.itemView.setOnClickListener { v ->
                mListener.onItemClick(v, holder.layoutPosition)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < super.getItemCount()) DATA_VIEW_TYPE else FOOTER_VIEW_TYPE
    }

    companion object {
        val ImagesDiffCallback = object : DiffUtil.ItemCallback<Hit>() {
            override fun areItemsTheSame(oldItem: Hit, newItem: Hit): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: Hit, newItem: Hit): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasFooter()) 1 else 0
    }

    private fun hasFooter(): Boolean {
        return super.getItemCount() != 0 && (state == State.LOADING || state == State.ERROR)
    }

    fun setState(state: State) {
        this.state = state
        notifyItemChanged(super.getItemCount())
    }

    fun getHitAtPosition(position: Int): Hit? {
        return currentList?.get(position)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(v: View, pos: Int)
    }
}