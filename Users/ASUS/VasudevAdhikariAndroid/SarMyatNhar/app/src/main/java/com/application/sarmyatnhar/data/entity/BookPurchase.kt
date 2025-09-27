package com.application.sarmyatnhar.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "book_purchases")
data class BookPurchase(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val wallet_id: Int, // FK → Wallet
    val book_id: Int,   // FK → Book
    val book_purchase_time: Long = System.currentTimeMillis()
)