package com.application.sarmyatnhar.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "book_approvals")
data class BookApproval(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val book_id: Int, // FK → Book
    val status: String, // "approved", "rejected", "pending"
    val message: String? = null,
    val decision_date: Long = System.currentTimeMillis()
)