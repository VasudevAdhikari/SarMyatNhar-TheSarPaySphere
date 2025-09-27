package com.application.sarmyatnhar.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.application.sarmyatnhar.data.entity.BookPurchase

@Dao
interface BookPurchaseDao {
    @Insert
    suspend fun insert(purchase: BookPurchase)

    @Query("SELECT * FROM book_purchases WHERE wallet_id = :walletId")
    suspend fun getByWallet(walletId: Int): List<BookPurchase>
}