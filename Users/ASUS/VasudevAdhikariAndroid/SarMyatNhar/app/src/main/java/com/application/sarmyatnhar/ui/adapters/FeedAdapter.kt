package com.application.sarmyatnhar.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.sarmyatnhar.R
import com.application.sarmyatnhar.data.entity.Book

class FeedAdapter(private val feeds: List<Book>) :
    RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {

    inner class FeedViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val ivCover: ImageView = view.findViewById(R.id.ivBookCover)
//        val tvTitle: TextView = view.findViewById(R.id.tvBookTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book_vertical, parent, false)
        return FeedViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val feed = feeds[position]
//        holder.ivCover.setImageResource(feed.imageRes)
//        holder.tvTitle.text = feed.title
    }

    override fun getItemCount(): Int = feeds.size
}
