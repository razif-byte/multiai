package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class AiModel(
    val id: String,
    val displayName: String,
    val provider: String,
    val endpoint: String,
    val badgeColor: Color,
    val description: String,
    val isDefault: Boolean = false
) {
    GEMINI(
        id = "gemini",
        displayName = "Gemini 3.5 Pro",
        provider = "Google DeepMind",
        endpoint = "https://api.google.com/gemini",
        badgeColor = Color(0xFF4285F4),
        description = "Model utama pantas dengan pemikiran mendalam & multimodal.",
        isDefault = true
    ),
    COPILOT(
        id = "copilot",
        displayName = "Microsoft Copilot",
        provider = "Microsoft",
        endpoint = "https://api.microsoft.com/copilot",
        badgeColor = Color(0xFF00A4EF),
        description = "AI pembantu produktiviti & kod dengan integrasi Azure."
    ),
    CHATGPT(
        id = "chatgpt",
        displayName = "ChatGPT (GPT-4o)",
        provider = "OpenAI",
        endpoint = "https://api.openai.com/chatgpt",
        badgeColor = Color(0xFF10A37F),
        description = "AI perbualan fleksibel, logik mantap dan kefahaman luas."
    ),
    CLAUDE(
        id = "claude",
        displayName = "Claude 3.5 Sonnet",
        provider = "Anthropic",
        endpoint = "https://api.anthropic.com/claude",
        badgeColor = Color(0xFFD97706),
        description = "Model beretika tinggi, penulisan mendalam & analisis sastera."
    ),
    DEEPSITE(
        id = "deepsite",
        displayName = "DeepSite AI",
        provider = "DeepSite Intelligence",
        endpoint = "https://api.deepsite.ai",
        badgeColor = Color(0xFF8B5CF6),
        description = "Enjin penalaran mendalam dan penyelesaian masalah teknikal."
    ),
    FLUX(
        id = "flux",
        displayName = "Flux Neural Fast",
        provider = "Black Forest Labs / Flux",
        endpoint = "https://api.flux.ai",
        badgeColor = Color(0xFFEC4899),
        description = "Pemprosesan pantas, visual deskriptif dan idea kreatif."
    ),
    MUZIKGPT(
        id = "muzikgpt",
        displayName = "MuzikGPT Audio",
        provider = "MuzikGPT Core",
        endpoint = "https://api.muzikgpt.com",
        badgeColor = Color(0xFF06B6D4),
        description = "Penjana lirik, melodi, kord dan komposisi lagu interaktif."
    ),
    AI_STUDIO(
        id = "aistudio",
        displayName = "Google AI Studio",
        provider = "Google Cloud AI",
        endpoint = "https://aistudio.google.com/api",
        badgeColor = Color(0xFF34A853),
        description = "Akses terus ke generative models dengan talaan latensi rendah."
    );

    companion object {
        fun fromId(id: String): AiModel {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: GEMINI
        }
    }
}
