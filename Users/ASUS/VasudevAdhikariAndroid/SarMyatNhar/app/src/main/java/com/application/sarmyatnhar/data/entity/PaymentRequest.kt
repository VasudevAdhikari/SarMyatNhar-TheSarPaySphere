package com.application.sarmyatnhar.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payment_requests")
data class PaymentRequest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val payment_method_id: Int?, // FK → PaymentMethod
    val proof_image_url: String,
    val amount_money: Double,
    val coins: Int?,
    val status: String = "pending", // "pending", "approved", "rejected"
    val decision_date: Long? = null
)