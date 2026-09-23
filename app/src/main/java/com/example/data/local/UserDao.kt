package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    fun getActiveUserFlow(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    suspend fun getActiveUser(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET isLoggedIn = 0")
    suspend fun setAllLoggedOut()

    @Query("UPDATE users SET isLoggedIn = 1 WHERE id = :userId")
    suspend fun setLoggedIn(userId: String)

    @Query("UPDATE users SET preferredModelId = :modelId WHERE id = :userId")
    suspend fun updatePreferredModel(userId: String, modelId: String)

    @Query("UPDATE users SET preferredLanguageCode = :langCode WHERE id = :userId")
    suspend fun updatePreferredLanguage(userId: String, langCode: String)
}
