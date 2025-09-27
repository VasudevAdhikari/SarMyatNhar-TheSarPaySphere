package com.application.sarmyatnhar.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallets")
data class Wallet(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var user_id: Int, // FK → User
    val read_points: Int = 0,
    val income_points: Int = 0,
    val updated_at: Long = System.currentTimeMillis()
)