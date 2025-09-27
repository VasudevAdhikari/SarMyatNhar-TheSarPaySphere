package com.application.sarmyatnhar.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.application.sarmyatnhar.data.entity.BookApproval

@Dao
interface BookApprovalDao {
    @Insert
    suspend fun insert(approval: BookApproval)

    @Query("SELECT * FROM book_approvals WHERE book_id = :bookId")
    suspend fun getByBook(bookId: Int): List<BookApproval>
}