package com.application.sarmyatnhar.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.application.sarmyatnhar.data.entity.AuthorProfile
import com.application.sarmyatnhar.data.entity.User

@Dao
interface AuthorProfileDao {
    @Insert
    suspend fun insert(profile: AuthorProfile)
    @Query("SELECT * FROM author_profiles WHERE user_id = :userId") suspend fun getByUserId(userId: Int): AuthorProfile?

    @Query("SELECT * FROM author_profiles WHERE user_id = :user_id LIMIT 1")
    suspend fun getAuthorByUserId(user_id: Int): AuthorProfile?

    @Query("SELECT * FROM author_profiles WHERE id = :id LIMIT 1")
    suspend fun getAuthorById(id: Int): AuthorProfile?

    @Query("SELECT * FROM author_profiles WHERE user_id = :userId LIMIT 1")
    suspend fun getAuthorProfileByUserId(userId: Int): AuthorProfile?

    @Update
    suspend fun update(profile: AuthorProfile)

    @Transaction
    suspend fun getOrCreateAuthorProfile(userId: Int, penName: String, bio: String?): AuthorProfile {
        var profile = getByUserId(userId)
        if (profile == null) {
            val newProfile = AuthorProfile(user_id = userId, pen_name = penName, bio = bio)
            insert(newProfile)
            profile = getByUserId(userId)
        }
        return profile!!
    }
}