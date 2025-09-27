package com.application.sarmyatnhar.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversion_requests")
data class ConversionRequest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val payment_method_id: Int?, // FK → PaymentMethod
    val type: String, // "income_to_read", "income_to_money"
    val amount_points: Int,
    val payout_proof_url: String? = null,
    val status: String = "pending", // reuse PaymentRequest.STATUS_CHOICES
    val message: String? = null,
    val created_at: Long = System.currentTimeMillis()
)