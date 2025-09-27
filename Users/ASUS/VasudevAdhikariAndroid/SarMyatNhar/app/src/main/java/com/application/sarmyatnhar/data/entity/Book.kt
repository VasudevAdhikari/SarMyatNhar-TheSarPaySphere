package com.application.sarmyatnhar.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uploader_id: Int?, // FK → AuthorProfile
    val title: String,
    val category: String?= "",
    val file_url: String,
    val price_points: Int = 0,
    val description: String?= "No Description Provided Yet",
    val status: String = "pending", // "pending", "approved", "rejected"
    val created_at: Long = System.currentTimeMillis(),
    val thumbnail_url: String? = null
)