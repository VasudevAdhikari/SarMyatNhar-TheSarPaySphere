package com.application.sarmyatnhar.ui.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.sarmyatnhar.R
import com.application.sarmyatnhar.ui.activities.BookDetailsActivity
import com.bumptech.glide.Glide

class BookAdapter(private val bookList: List<BookAdapter.DisplayBook>) :
    RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    data class DisplayBook(
        val thumbnailUrl: String,
        val title: String,
        val authorPenName: String,
        val id: Int
    )

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookImage: ImageView = itemView.findViewById(R.id.bookImage)
        val bookTitle: TextView = itemView.findViewById(R.id.bookTitle)
        val bookAuthor: TextView = itemView.findViewById(R.id.bookAuthor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = bookList[position]

        val filePath = book.thumbnailUrl
        val file = filePath
        // Load image from URL using Glide
        Glide.with(holder.bookImage.context)
            .load(file)
            .into(holder.bookImage)

        holder.bookTitle.text = book.title
        holder.bookAuthor.text = book.authorPenName

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, BookDetailsActivity::class.java).apply {
                putExtra("BOOK_ID", book.id)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = bookList.size
}