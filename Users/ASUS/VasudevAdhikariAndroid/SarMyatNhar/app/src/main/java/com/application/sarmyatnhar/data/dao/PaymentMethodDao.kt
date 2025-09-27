package com.application.sarmyatnhar.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.application.sarmyatnhar.data.entity.PaymentMethod

@Dao
interface PaymentMethodDao {
    @Insert
    suspend fun insert(method: PaymentMethod)

    @Query("SELECT * FROM payment_methods WHERE user_id = :userId")
    suspend fun getByUser(userId: Int): PaymentMethod?

    @Query("SELECT * FROM payment_methods WHERE user_id = :userId LIMIT 1")
    suspend fun getPaymentMethodByUserId(userId: Int): PaymentMethod?

    @Query("UPDATE payment_methods SET account_name = :accountName, account_num = :accountNum WHERE user_id = :userId")
    suspend fun updatePaymentMethod(userId: Int, accountName: String, accountNum: String)

    @Update
    suspend fun update(method: PaymentMethod)
}