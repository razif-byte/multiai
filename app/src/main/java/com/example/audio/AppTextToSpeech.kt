package com.example.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class AppTextToSpeech(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _currentUtteranceId = MutableStateFlow<String?>(null)
    val currentUtteranceId: StateFlow<String?> = _currentUtteranceId.asStateFlow()

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            val msLocale = Locale("ms", "MY")
            val result = tts?.setLanguage(msLocale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.language = Locale.ENGLISH
            }
            tts?.setPitch(1.0f)
            tts?.setSpeechRate(1.0f)

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                    _currentUtteranceId.value = utteranceId
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                    _currentUtteranceId.value = null
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                    _currentUtteranceId.value = null
                }
            })
            Log.d("AppTTS", "TTS Initialized successfully")
        } else {
            Log.e("AppTTS", "TTS Initialization failed: $status")
        }
    }

    fun speak(text: String, messageId: Long = 0) {
        if (!isInitialized) return
        val utteranceId = "msg_$messageId"
        if (_isSpeaking.value && _currentUtteranceId.value == utteranceId) {
            stop()
            return
        }
        stop()

        // Clean markdown characters like *, #, etc. for cleaner speech
        val cleaned = text
            .replace(Regex("[*#_`~>\\[\\]()]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()

        // Choose language based on simple Malay detection
        val lower = cleaned.lowercase()
        if (lower.contains("saya") || lower.contains("dan") || lower.contains("yang") || lower.contains("ini") || lower.contains("anda")) {
            tts?.language = Locale("ms", "MY")
        } else {
            tts?.language = Locale.ENGLISH
        }

        tts?.speak(cleaned, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
        _currentUtteranceId.value = null
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
