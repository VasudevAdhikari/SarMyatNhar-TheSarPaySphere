package com.application.sarmyatnhar.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.application.sarmyatnhar.data.entity.Book

@Dao
interface BookDao {
    @Insert
    suspend fun insert(book: Book)

    @Query ("SELECT * FROM books")
    suspend fun getAllBooks(): List<Book>

    @Query ("SELECT * FROM books WHERE status= 'approved'")
    suspend fun getAllApprovedBooks(): List<Book>

    @Query("SELECT * FROM books WHERE status = :status")
    suspend fun getBooksByStatus(status: String): List<Book>

    @Query("SELECT b.* FROM books b INNER JOIN author_profiles ap ON b.uploader_id = ap.id INNER JOIN users u ON ap.user_id = u.id WHERE u.email = :email")
    suspend fun getBooksByAuthorEmail(email: String): List<Book>

    @Query("DELETE FROM books WHERE id = :id")
    suspend fun deleteBookById(id: Int)

    @Query("SELECT b.*, ap.pen_name, u.phone FROM books b INNER JOIN author_profiles ap ON b.uploader_id = ap.id INNER JOIN users u ON ap.user_id = u.id WHERE b.status = 'pending'")
    suspend fun getPendingBooksForApproval(): List<BookWithAuthor>

    @Query("UPDATE books SET status = :status WHERE id = :id")
    suspend fun updateBookStatus(id: Int, status: String)

    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getBookById(id: Int): Book

    @Query("SELECT b.*, ap.pen_name FROM books b INNER JOIN author_profiles ap ON b.uploader_id = ap.id WHERE b.category LIKE '%' || :category || '%' AND b.status = 'approved'")
    suspend fun getBooksWithAuthorByCategoryLike(category: String): List<BookWithAuthor>

    @Query("SELECT b.*, ap.pen_name FROM books b INNER JOIN author_profiles ap ON b.uploader_id = ap.id WHERE b.title LIKE '%' || :query || '%' OR ap.pen_name LIKE '%' || :query || '%'")
    suspend fun getBooksWithAuthorBySearch(query: String): List<BookWithAuthor>

    @Query("SELECT b.*, ap.pen_name FROM books b INNER JOIN author_profiles ap ON b.uploader_id = ap.id WHERE (b.title LIKE '%' || :query || '%' OR ap.pen_name LIKE '%' || :query || '%' OR b.category LIKE '%' || :query || '%') AND b.status = 'approved'")
    suspend fun getBooksWithAuthorBySearchLike(query: String): List<BookWithAuthor>

    @Query("SELECT b.*, ap.pen_name FROM books b INNER JOIN author_profiles ap ON b.uploader_id = ap.id")
    suspend fun getAllBooksWithAuthor(): List<BookWithAuthor>

    @Query("SELECT b.*, ap.pen_name FROM books b INNER JOIN author_profiles ap ON b.uploader_id = ap.id WHERE b.status = 'approved' ORDER BY b.created_at DESC")
    suspend fun getNewlyAddedBooks(): List<BookWithAuthor>

    @Query("SELECT b.*, ap.pen_name FROM books b INNER JOIN author_profiles ap ON b.uploader_id = ap.id WHERE b.status = 'approved' AND b.price_points = 0")
    suspend fun getFreeBooks(): List<BookWithAuthor>

    @Query("SELECT b.*, ap.pen_name FROM books b INNER JOIN author_profiles ap ON b.uploader_id = ap.id WHERE b.status = 'approved' AND b.title LIKE '%Collection%'")
    suspend fun getCollections(): List<BookWithAuthor>

    @Query("SELECT b.*, ap.pen_name FROM books b INNER JOIN author_profiles ap ON b.uploader_id = ap.id WHERE b.status = 'approved' ORDER BY b.price_points DESC")
    suspend fun getPopularBooks(): List<BookWithAuthor>

    @Query("SELECT b.*, ap.pen_name FROM books b INNER JOIN author_profiles ap ON b.uploader_id = ap.id WHERE b.status = 'approved' LIMIT 10")
    suspend fun getTopRatedBooks(): List<BookWithAuthor>
}

// Helper data class for book with author info
data class BookWithAuthor(
    val id: Int,
    val title: String,
    val pen_name: String,
    val phone: String?,
    val price_points: Int,
    val thumbnail_url: String?
)