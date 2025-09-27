package com.application.sarmyatnhar.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.application.sarmyatnhar.data.entity.Notification

@Dao
interface NotificationDao {
    @Insert
    suspend fun insert(notification: Notification)

    @Query("SELECT * FROM notifications WHERE user_id = :userId")
    suspend fun getByUser(userId: Int): List<Notification>
}