package com.application.sarmyatnhar.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.application.sarmyatnhar.data.entity.Library

@Dao
interface LibraryDao {
    @Insert
    suspend fun insert(library: Library): Long

    @Query("INSERT INTO library (user_id, book_id, is_downloaded) VALUES (:userId, :bookId, :isDownloaded)")
    suspend fun insertLibrary(userId: Int, bookId: Int, isDownloaded: Boolean)

    @Query("SELECT * FROM library WHERE user_id = :userId")
    suspend fun getByUser(userId: Int): List<Library>

    @Query("SELECT * FROM library WHERE user_id = :userId AND book_id = :bookId LIMIT 1")
    suspend fun getByUserAndBook(userId: Int, bookId: Int): Library?

    @Update
    suspend fun update(library: Library)

    @Query("DELETE FROM library WHERE id = :id")
    suspend fun deleteById(id: Int)
}
