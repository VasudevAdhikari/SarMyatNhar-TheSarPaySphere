package com.application.sarmyatnhar.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val user_id: Int, // FK → User
    val message: String,
    val type: String, // "book", "payment", "conversion", "system"
    val created_at: Long = System.currentTimeMillis(),
    val is_read: Boolean = false
)