package com.application.sarmyatnhar.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.sarmyatnhar.R
import com.application.sarmyatnhar.data.entity.Book
import com.bumptech.glide.Glide

class HomeBookAdapter(
    private val bookList: List<Book>,
    private val isVertical: Boolean = false
) : RecyclerView.Adapter<HomeBookAdapter.BookViewHolder>() {

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookImage: ImageView = itemView.findViewById(R.id.bookImage)
        val bookTitle: TextView = itemView.findViewById(R.id.bookTitle)
        val bookAuthor: TextView? = itemView.findViewById(R.id.bookAuthor) // only used in vertical
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val layout = if (isVertical) R.layout.item_book_vertical else R.layout.item_book_horizontal
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = bookList[position]

        // Load cover
        Glide.with(holder.bookImage.context)
            .load(book.thumbnail_url)
            .placeholder(R.drawable.book1) // fallback image
            .into(holder.bookImage)

        // Title
        holder.bookTitle.text = book.title

        // Only vertical layout shows author (for now mock it, or fetch uploader later)
        holder.bookAuthor?.text = "By Author #${book.uploader_id ?: 0}"
    }

    override fun getItemCount(): Int = bookList.size
}
