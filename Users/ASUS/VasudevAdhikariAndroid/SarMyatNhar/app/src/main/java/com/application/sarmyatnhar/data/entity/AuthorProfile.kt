package com.application.sarmyatnhar.data.entity
import androidx.room.*;

@Entity(tableName = "author_profiles")
data class AuthorProfile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var user_id: Int?, // FK → User
    val pen_name: String,
    val bio: String? = null,
    val joined_date: Long = System.currentTimeMillis()
)