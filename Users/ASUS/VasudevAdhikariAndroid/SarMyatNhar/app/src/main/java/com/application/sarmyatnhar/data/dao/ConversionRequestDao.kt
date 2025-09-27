package com.application.sarmyatnhar.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.application.sarmyatnhar.data.entity.ConversionRequest

@Dao
interface ConversionRequestDao {
    @Insert
    suspend fun insert(conversion: ConversionRequest)

    @Query("SELECT * FROM conversion_requests WHERE type = :type")
    suspend fun getByType(type: String): List<ConversionRequest>

    @Query("UPDATE conversion_requests SET payout_proof_url = :proofUrl, message = :message, status = :status WHERE id = :id")
    suspend fun updateProofAndMessage(id: Int, proofUrl: String?, message: String?, status: String)
}