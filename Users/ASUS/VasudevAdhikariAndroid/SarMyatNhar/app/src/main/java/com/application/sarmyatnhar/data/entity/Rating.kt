package com.application.sarmyatnhar.data.entity

import androidx.room.Entity

@Entity(
    tableName = "ratings",
    primaryKeys = ["user_id", "book_id"] // unique together
)
data class Rating(
    val user_id: Int, // FK → User
    val book_id: Int, // FK → Book
    val rating: Int = 0,
    val rated_on: Long = System.currentTimeMillis()
)