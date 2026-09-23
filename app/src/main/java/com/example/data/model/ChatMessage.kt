package com.example.data.model

data class ChatMessage(
    val id: Long = 0,
    val text: String,
    val isUser: Boolean,
    val model: AiModel = AiModel.GEMINI,
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
)
