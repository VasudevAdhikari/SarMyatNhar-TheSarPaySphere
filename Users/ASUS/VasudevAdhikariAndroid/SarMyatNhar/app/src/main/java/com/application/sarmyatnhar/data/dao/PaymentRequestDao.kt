package com.application.sarmyatnhar.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.application.sarmyatnhar.data.entity.PaymentRequest

@Dao
interface PaymentRequestDao {
    @Insert
    suspend fun insert(request: PaymentRequest)

    @Query("SELECT * FROM payment_requests WHERE status = :status")
    suspend fun getByStatus(status: String): List<PaymentRequest>

    @Query("UPDATE payment_requests SET status = :status, decision_date = :decisionDate WHERE id = :id")
    suspend fun updateStatus(id: Int, status: String, decisionDate: Long)
}