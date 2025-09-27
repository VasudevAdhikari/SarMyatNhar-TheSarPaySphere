package com.application.sarmyatnhar.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.application.sarmyatnhar.data.entity.Rating

@Dao
interface RatingDao {
    @Insert
    suspend fun insert(rating: Rating)

    @Query("SELECT * FROM ratings WHERE book_id = :bookId")
    suspend fun getRatingsForBook(bookId: Int): List<Rating>
}