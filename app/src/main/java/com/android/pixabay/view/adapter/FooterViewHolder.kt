package com.android.pixabay.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.pixabay.R
import com.android.pixabay.utils.NetworkState
import com.android.pixabay.utils.Status
import kotlinx.android.synthetic.main.image_list_footer.view.*

class FooterViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(networkState: NetworkState?) {
        itemView.progress_bar.visibility = if (networkState?.status == Status.LOADING) View.VISIBLE else View.INVISIBLE
        itemView.retry_button.visibility = if (networkState?.status == Status.ERROR) View.VISIBLE else View.INVISIBLE
        itemView.txt_error.visibility = if(networkState?.msg != null) View.VISIBLE else View.INVISIBLE
        itemView.txt_error.text = networkState?.msg
    }

    companion object {
        fun create(retry: () -> Unit, parent: ViewGroup): FooterViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.image_list_footer, parent, false)
            view.retry_button.setOnClickListener { retry() }
            return FooterViewHolder(view)
        }
    }
}