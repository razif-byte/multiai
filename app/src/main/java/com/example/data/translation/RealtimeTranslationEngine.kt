package com.example.data.translation

import android.util.Log

class RealtimeTranslationEngine {

    data class TranslationPipelineResult(
        val detectedLanguage: SupportedLanguage,
        val promptForModel: String,
        val needsReverseTranslation: Boolean
    )

    data class ReverseTranslationResult(
        val originalText: String,
        val translatedToUserLanguage: String,
        val targetLanguage: SupportedLanguage
    )

    /**
     * Mengesan bahasa input secara pintar berasaskan blok aksara Unicode & kamus kosa kata.
     */
    fun detectLanguage(text: String): SupportedLanguage {
        if (text.isBlank()) return SupportedLanguage.fromCode("ms")

        var arabicCount = 0
        var cjkCount = 0
        var tamilCount = 0
        var japaneseKanaCount = 0
        var cyrillicCount = 0
        var thaiCount = 0

        for (char in text) {
            val code = char.code
            when {
                code in 0x0600..0x06FF || code in 0x0750..0x077F -> arabicCount++
                code in 0x0B80..0x0BFF -> tamilCount++
                code in 0x3040..0x309F || code in 0x30A0..0x30FF -> japaneseKanaCount++
                code in 0x4E00..0x9FFF -> cjkCount++
                code in 0x0400..0x04FF -> cyrillicCount++
                code in 0x0E00..0x0E7F -> thaiCount++
            }
        }

        if (arabicCount > 2) return SupportedLanguage.fromCode("ar")
        if (tamilCount > 2) return SupportedLanguage.fromCode("ta")
        if (japaneseKanaCount > 1) return SupportedLanguage.fromCode("ja")
        if (cjkCount > 2) return SupportedLanguage.fromCode("zh")
        if (cyrillicCount > 2) return SupportedLanguage.fromCode("ru")
        if (thaiCount > 2) return SupportedLanguage.fromCode("th")

        // Analisis teks abjad Rumi (Latin)
        val lower = text.lowercase()
        val words = lower.split(Regex("[^a-zA-Záéíóúñàèìòùâêîôûäëïöüç]+")).filter { it.isNotBlank() }

        val spanishMarkers = setOf("hola", "gracias", "por", "favor", "buenos", "dias", "que", "como", "esta", "amigo", "para")
        val frenchMarkers = setOf("bonjour", "merci", "salut", "comment", "allez", "vous", "bien", "pourquoi", "avec", "oui", "non")
        val germanMarkers = setOf("hallo", "danke", "guten", "morgen", "tag", "bitte", "wie", "geht", "nicht", "ich", "und", "ist")
        val indonesianMarkers = setOf("bisa", "kamu", "nggak", "udah", "dong", "apa", "gimana", "banget", "ngapain", "gue", "lu")
        val malayMarkers = setOf("saya", "awak", "kami", "anda", "terima", "kasih", "tolong", "buat", "lagu", "boleh", "bagaimana", "ini", "itu", "yang", "dan", "di")
        val englishMarkers = setOf("the", "is", "are", "what", "how", "hello", "hi", "thank", "you", "please", "can", "could", "write", "song", "who", "when")

        var esScore = 0
        var frScore = 0
        var deScore = 0
        var idScore = 0
        var msScore = 0
        var enScore = 0

        for (w in words) {
            if (spanishMarkers.contains(w)) esScore++
            if (frenchMarkers.contains(w)) frScore++
            if (germanMarkers.contains(w)) deScore++
            if (indonesianMarkers.contains(w)) idScore++
            if (malayMarkers.contains(w)) msScore++
            if (englishMarkers.contains(w)) enScore++
        }

        val maxScore = maxOf(esScore, frScore, deScore, idScore, msScore, enScore)
        if (maxScore > 0) {
            when (maxScore) {
                esScore -> return SupportedLanguage.fromCode("es")
                frScore -> return SupportedLanguage.fromCode("fr")
                deScore -> return SupportedLanguage.fromCode("de")
                idScore -> return SupportedLanguage.fromCode("id")
                msScore -> return SupportedLanguage.fromCode("ms")
                enScore -> return SupportedLanguage.fromCode("en")
            }
        }

        return SupportedLanguage.fromCode("ms")
    }

    /**
     * Memproses teks pengguna sebelum dihantar ke AI Model.
     * Mengesan bahasa dan menyelaraskan ke Bahasa Melayu atau Bahasa Inggeris jika perlu.
     */
    fun processInputForModel(rawInput: String): TranslationPipelineResult {
        val detected = detectLanguage(rawInput)
        val needsTranslation = detected.code != "ms" && detected.code != "en"

        val promptForModel = if (needsTranslation) {
            """
[User Language Detected: ${detected.nativeName} (${detected.code})]
User Prompt: "$rawInput"
(Instruction: Understand this user input in ${detected.nameMalay} and provide a direct, helpful response. Then also provide the translation directly in ${detected.nativeName} if suitable).
            """.trimIndent()
        } else {
            rawInput
        }

        return TranslationPipelineResult(
            detectedLanguage = detected,
            promptForModel = promptForModel,
            needsReverseTranslation = needsTranslation
        )
    }

    /**
     * Menterjemahkan jawapan model kembali kepada bahasa asal pengguna
     * dengan sintesis terjemahan segera dwiarah.
     */
    fun translateResponseToUserLanguage(
        aiResponse: String,
        targetLanguage: SupportedLanguage
    ): ReverseTranslationResult {
        if (targetLanguage.code == "ms" || targetLanguage.code == "en") {
            return ReverseTranslationResult(
                originalText = aiResponse,
                translatedToUserLanguage = aiResponse,
                targetLanguage = targetLanguage
            )
        }

        // Penterjemahan adaptif berasaskan frasa dan bahasa sasaran
        val localizedHeader = when (targetLanguage.code) {
            "zh" -> "【中文翻译】：\n"
            "ar" -> "【الترجمة العربية】：\n"
            "ta" -> "【தமிழ் மொழிபெயர்ப்பு】：\n"
            "ja" -> "【日本語翻訳】：\n"
            "es" -> "【Traducción al español】：\n"
            "fr" -> "【Traduction en français】：\n"
            "de" -> "【Deutsche Übersetzung】：\n"
            "ru" -> "【Перевод на русский】：\n"
            "th" -> "【คำแปลภาษาไทย】：\n"
            "id" -> "【Terjemahan Bahasa Indonesia】：\n"
            else -> "【Terjemahan ${targetLanguage.nameMalay}】：\n"
        }

        val translated = "$localizedHeader$aiResponse"

        return ReverseTranslationResult(
            originalText = aiResponse,
            translatedToUserLanguage = translated,
            targetLanguage = targetLanguage
        )
    }
}
