package com.application.sarmyatnhar.data.entity
import androidx.room.*;

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val email: String,
    val password: String,
    val role: String, // "admin" / "user"
    val status: String = "pending", // "pending", "approved", "blocked"
    val profile_url: String? = null,
    val phone: String? =null,
    val created_at: Long = System.currentTimeMillis()
)