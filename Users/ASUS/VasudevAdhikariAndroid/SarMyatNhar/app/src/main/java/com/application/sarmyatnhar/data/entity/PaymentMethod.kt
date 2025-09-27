package com.application.sarmyatnhar.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payment_methods")
data class PaymentMethod(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var user_id: Int?, // FK → User
    val method_type: String, // "KPAY", "WAVEPAY", "AYAPAY"
    val account_num: String,
    val account_name: String,
    val created_at: Long = System.currentTimeMillis()
)