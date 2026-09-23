package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.AiModel
import com.example.data.model.ChatMessage

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val isUser: Boolean,
    val modelId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val latencyMs: Long = 0,
    val fallbackPath: String? = null,
    val isMusic: Boolean = false,
    val musicGenre: String? = null,
    val musicLyrics: String? = null,
    val translatedText: String? = null,
    val isError: Boolean = false,
    val antigravityOptimized: Boolean = true,
    val userId: String? = null,
    val detectedLanguage: String? = null
) {
    fun toDomain(): ChatMessage {
        return ChatMessage(
            id = id,
            text = text,
            isUser = isUser,
            model = AiModel.fromId(modelId),
            timestamp = timestamp,
            latencyMs = latencyMs,
            fallbackPath = fallbackPath,
            isMusic = isMusic,
            musicGenre = musicGenre,
            musicLyrics = musicLyrics,
            translatedText = translatedText,
            isError = isError,
            antigravityOptimized = antigravityOptimized,
            userId = userId,
            detectedLanguage = detectedLanguage
        )
    }

    companion object {
        fun fromDomain(msg: ChatMessage): ChatMessageEntity {
            return ChatMessageEntity(
                id = msg.id,
                text = msg.text,
                isUser = msg.isUser,
                modelId = msg.model.id,
                timestamp = msg.timestamp,
                latencyMs = msg.latencyMs,
                fallbackPath = msg.fallbackPath,
                isMusic = msg.isMusic,
                musicGenre = msg.musicGenre,
                musicLyrics = msg.musicLyrics,
                translatedText = msg.translatedText,
                isError = msg.isError,
                antigravityOptimized = msg.antigravityOptimized,
                userId = msg.userId,
                detectedLanguage = msg.detectedLanguage
            )
        }
    }
}
