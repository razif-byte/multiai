package com.example.data.translation

data class SupportedLanguage(
    val code: String,
    val nameMalay: String,
    val nativeName: String,
    val flagEmoji: String,
    val ttsLocaleTag: String
) {
    companion object {
        val ALL = listOf(
            SupportedLanguage("ms", "Bahasa Melayu", "Bahasa Melayu", "🇲🇾", "ms-MY"),
            SupportedLanguage("en", "Bahasa Inggeris", "English", "🇺🇸", "en-US"),
            SupportedLanguage("id", "Bahasa Indonesia", "Bahasa Indonesia", "🇮🇩", "id-ID"),
            SupportedLanguage("zh", "Bahasa Mandarin", "中文 (简体)", "🇨🇳", "zh-CN"),
            SupportedLanguage("ar", "Bahasa Arab", "العربية", "🇸🇦", "ar-SA"),
            SupportedLanguage("ta", "Bahasa Tamil", "தமிழ்", "🇮🇳", "ta-IN"),
            SupportedLanguage("ja", "Bahasa Jepun", "日本語", "🇯🇵", "ja-JP"),
            SupportedLanguage("es", "Bahasa Sepanyol", "Español", "🇪🇸", "es-ES"),
            SupportedLanguage("fr", "Bahasa Perancis", "Français", "🇫🇷", "fr-FR"),
            SupportedLanguage("de", "Bahasa Jerman", "Deutsch", "🇩🇪", "de-DE"),
            SupportedLanguage("ru", "Bahasa Rusia", "Русский", "🇷🇺", "ru-RU"),
            SupportedLanguage("th", "Bahasa Thai", "ไทย", "🇹🇭", "th-TH")
        )

        fun fromCode(code: String): SupportedLanguage {
            return ALL.find { it.code.equals(code, ignoreCase = true) } ?: ALL[0]
        }
    }
}
