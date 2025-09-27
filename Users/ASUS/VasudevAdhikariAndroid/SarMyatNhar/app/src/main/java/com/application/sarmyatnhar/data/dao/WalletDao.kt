package com.application.sarmyatnhar.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.application.sarmyatnhar.data.entity.Wallet

@Dao
interface WalletDao {
    @Insert
    suspend fun insert(wallet: Wallet)

    @Query("SELECT * FROM wallets WHERE user_id = :userId")
    suspend fun getByUser(userId: Int): Wallet?

    @Query("UPDATE wallets SET read_points = :readPoints, updated_at = :updatedAt WHERE user_id = :userId")
    suspend fun updateReadPoints(userId: Int, readPoints: Int, updatedAt: Long)

    @Query("UPDATE wallets SET income_points = :incomePoints, updated_at = :updatedAt WHERE user_id = :userId")
    suspend fun updateIncomePoints(userId: Int, incomePoints: Int, updatedAt: Long)
}