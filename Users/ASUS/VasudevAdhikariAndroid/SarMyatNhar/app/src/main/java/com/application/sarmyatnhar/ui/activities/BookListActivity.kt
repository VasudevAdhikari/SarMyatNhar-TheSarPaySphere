package com.application.sarmyatnhar.ui.activities

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.sarmyatnhar.R
import com.application.sarmyatnhar.data.entity.AuthorProfile
import com.application.sarmyatnhar.data.entity.Book
import com.application.sarmyatnhar.ui.adapters.*
import kotlinx.coroutines.runBlocking

class BookListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BookAdapter
    private lateinit var goBack: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_list)
        recyclerView = findViewById(R.id.recyclerViewBooks)
        recyclerView.layoutManager = LinearLayoutManager(this)
        goBack = findViewById(R.id.imageButton)
        goBack.setOnClickListener { this.finish() }
        val db = com.application.sarmyatnhar.data.AppDatabase.getDatabase(this)
        val bookDao = db.bookDao()
        val parameter = intent.getStringExtra("PARAMETER")
        val (booksWithAuthor, titleText) = when {
            parameter == null -> runBlocking { bookDao.getAllBooksWithAuthor() } to "All Books"
            parameter == "newly_added" -> runBlocking { bookDao.getNewlyAddedBooks() } to "Newly Added Books"
            parameter == "free_books" -> runBlocking { bookDao.getFreeBooks() } to "Free Books"
            parameter == "collections" -> runBlocking { bookDao.getCollections() } to "Collections"
            parameter == "popular_books" -> runBlocking { bookDao.getPopularBooks() } to "Popular Books"
            parameter == "top_rated_books" -> runBlocking { bookDao.getTopRatedBooks() } to "Top Rated Books"
            parameter.startsWith("search:") -> {
                val query = parameter.removePrefix("search:")
                runBlocking { bookDao.getBooksWithAuthorBySearchLike(query) } to "Search Results for '$query'"
            }
            else -> runBlocking { bookDao.getBooksWithAuthorByCategoryLike(parameter) } to "Books in '$parameter'"
        }
        val displayBooks = booksWithAuthor.map { book ->
            BookAdapter.DisplayBook(
                id = book.id,
                thumbnailUrl = book.thumbnail_url ?: "",
                title = book.title,
                authorPenName = book.pen_name
            )
        }
        val titleView = findViewById<TextView>(R.id.bookListTitle)
        titleView?.text = titleText
        adapter = BookAdapter(displayBooks)
        recyclerView.adapter = adapter
    }
}