package com.application.sarmyatnhar.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "library")
data class Library(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val user_id: Int,
    val book_id: Int,
    val is_downloaded: Boolean = false
)
