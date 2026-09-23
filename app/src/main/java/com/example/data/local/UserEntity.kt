package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val username: String,
    val email: String,
    val passwordHash: String,
    val preferredModelId: String = "gemini",
    val preferredLanguageCode: String = "ms",
    val isVip: Boolean = false,
    val isLoggedIn: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
