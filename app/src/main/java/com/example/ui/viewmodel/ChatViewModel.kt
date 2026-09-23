package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AppTextToSpeech
import com.example.audio.BackgroundMusicManager
import com.example.data.antigravity.AntigravityEngine
import com.example.data.api.AiApiManager
import com.example.data.auth.AuthManager
import com.example.data.local.ChatDatabase
import com.example.data.local.ChatMessageEntity
import com.example.data.model.AiModel
import com.example.data.model.AntigravityStats
import com.example.data.model.BackgroundTrack
import com.example.data.model.ChatMessage
import com.example.data.model.UnsplashPreset
import com.example.data.translation.RealtimeTranslationEngine
import com.example.data.translation.SupportedLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("multi_ai_prefs", Context.MODE_PRIVATE)

    private val db = ChatDatabase.getDatabase(application)
    private val chatDao = db.chatMessageDao()
    private val userDao = db.userDao()

    val authManager = AuthManager(userDao, viewModelScope)
    val translationEngine = RealtimeTranslationEngine()
    private val antigravityEngine = AntigravityEngine()
    private val aiApiManager = AiApiManager(antigravityEngine)

    val musicManager = BackgroundMusicManager(application)
    val tts = AppTextToSpeech(application)

    // Current User
    val currentUser = authManager.currentUser

    // Room DB Messages linked to active user
    val messages: StateFlow<List<ChatMessage>> = currentUser
        .flatMapLatest { user ->
            chatDao.getMessagesForUser(user?.id)
        }
        .map { list -> list.map { it.toDomain() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected AI Model (Default = Gemini 3.5 Pro)
    private val _selectedModel = MutableStateFlow(AiModel.GEMINI)
    val selectedModel: StateFlow<AiModel> = _selectedModel.asStateFlow()

    // Loading / Generation status
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    // Real-time Translation module
    private val _isRealtimeTranslationActive = MutableStateFlow(true)
    val isRealtimeTranslationActive: StateFlow<Boolean> = _isRealtimeTranslationActive.asStateFlow()

    private val _currentLanguage = MutableStateFlow(SupportedLanguage.fromCode("ms"))
    val currentLanguage: StateFlow<SupportedLanguage> = _currentLanguage.asStateFlow()

    // Auto Voice-over toggle & Illiterate / Non-reader mode
    private val _isAutoVoiceOver = MutableStateFlow(true) // Default true for voice accessibility
    val isAutoVoiceOver: StateFlow<Boolean> = _isAutoVoiceOver.asStateFlow()

    private val _isVoiceIlliterateMode = MutableStateFlow(false)
    val isVoiceIlliterateMode: StateFlow<Boolean> = _isVoiceIlliterateMode.asStateFlow()

    // Intro full-screen video
    private val currentIntroVideoId = "EvwdsI9G6-o"
    private val _showIntroVideo = MutableStateFlow(
        prefs.getString("seen_intro_video_id", "") != currentIntroVideoId
    )
    val showIntroVideo: StateFlow<Boolean> = _showIntroVideo.asStateFlow()

    // Dark Theme: 1 = Dark (default), 2 = Light, 0 = System
    private val _darkThemeMode = MutableStateFlow(1)
    val darkThemeMode: StateFlow<Int> = _darkThemeMode.asStateFlow()

    // Antigravity Stats
    val antigravityStats: StateFlow<AntigravityStats> = antigravityEngine.stats

    // Unsplash dynamic theme
    private val _currentUnsplash = MutableStateFlow(UnsplashPreset.PRESETS[0])
    val currentUnsplash: StateFlow<UnsplashPreset> = _currentUnsplash.asStateFlow()

    private val _unsplashDim = MutableStateFlow(0.85f)
    val unsplashDim: StateFlow<Float> = _unsplashDim.asStateFlow()

    // Music states
    private val _showMusicConsentDialog = MutableStateFlow(false)
    val showMusicConsentDialog: StateFlow<Boolean> = _showMusicConsentDialog.asStateFlow()

    private val _showFloatingMusicBar = MutableStateFlow(false)
    val showFloatingMusicBar: StateFlow<Boolean> = _showFloatingMusicBar.asStateFlow()

    // Ambassador Model Background & Profile
    private val _showAmbassadorBackground = MutableStateFlow(true)
    val showAmbassadorBackground: StateFlow<Boolean> = _showAmbassadorBackground.asStateFlow()

    private val _ambassadorAlpha = MutableStateFlow(0.32f)
    val ambassadorAlpha: StateFlow<Float> = _ambassadorAlpha.asStateFlow()

    private val _showAmbassadorDialog = MutableStateFlow(false)
    val showAmbassadorDialog: StateFlow<Boolean> = _showAmbassadorDialog.asStateFlow()

    // Modals
    private val _showModelPicker = MutableStateFlow(false)
    val showModelPicker: StateFlow<Boolean> = _showModelPicker.asStateFlow()

    private val _showPaymentModal = MutableStateFlow(false)
    val showPaymentModal: StateFlow<Boolean> = _showPaymentModal.asStateFlow()

    private val _showQrInstallModal = MutableStateFlow(false)
    val showQrInstallModal: StateFlow<Boolean> = _showQrInstallModal.asStateFlow()

    private val _showUnsplashDialog = MutableStateFlow(false)
    val showUnsplashDialog: StateFlow<Boolean> = _showUnsplashDialog.asStateFlow()

    private val _showAuthDialog = MutableStateFlow(false)
    val showAuthDialog: StateFlow<Boolean> = _showAuthDialog.asStateFlow()

    private val _showLanguageSheet = MutableStateFlow(false)
    val showLanguageSheet: StateFlow<Boolean> = _showLanguageSheet.asStateFlow()

    init {
        // Welcome message if database has no messages for this profile
        viewModelScope.launch {
            if (chatDao.getMessageCount() == 0) {
                val welcome = ChatMessage(
                    text = "Selamat datang ke **Multi AI Chatbot**! Enjin **Antigravity** aktif. Aplikasi ini kini dilengkapi modul terjemahan pintar 10+ bahasa dan mod mesra suara untuk memudahkan perbualan tanpa perlu membaca.",
                    isUser = false,
                    model = AiModel.GEMINI,
                    fallbackPath = "Antigravity Node Initialized",
                    userId = authManager.currentUser.value?.id
                )
                chatDao.insertMessage(ChatMessageEntity.fromDomain(welcome))
            }
        }
    }

    // Ending Video
    private val _showEndingVideo = MutableStateFlow(false)
    val showEndingVideo: StateFlow<Boolean> = _showEndingVideo.asStateFlow()

    fun completeIntroVideo() {
        prefs.edit()
            .putBoolean("is_first_launch_done", true)
            .putString("seen_intro_video_id", currentIntroVideoId)
            .apply()
        _showIntroVideo.value = false
        tts.speak("Selamat datang ke ruang sembang Multi AI. Ketik mikrofon untuk mula bercakap.")
    }

    fun replayIntroVideo() {
        _showIntroVideo.value = true
    }

    fun playEndingVideo() {
        _showEndingVideo.value = true
    }

    fun closeEndingVideo() {
        _showEndingVideo.value = false
    }

    fun sendMessage(promptText: String) {
        val trimmed = promptText.trim()
        if (trimmed.isBlank() || _isGenerating.value) return

        viewModelScope.launch {
            val activeUser = authManager.currentUser.value
            val userId = activeUser?.id

            // 1. Pengesanan bahasa masa nyata & pra-terjemahan jika perlu
            val translationPipeline = if (_isRealtimeTranslationActive.value) {
                translationEngine.processInputForModel(trimmed)
            } else {
                RealtimeTranslationEngine.TranslationPipelineResult(
                    detectedLanguage = _currentLanguage.value,
                    promptForModel = trimmed,
                    needsReverseTranslation = false
                )
            }

            // Simpan mesej pengguna ke Room DB
            val userMsg = ChatMessage(
                text = trimmed,
                isUser = true,
                model = _selectedModel.value,
                userId = userId,
                detectedLanguage = translationPipeline.detectedLanguage.code
            )
            chatDao.insertMessage(ChatMessageEntity.fromDomain(userMsg))

            _isGenerating.value = true

            try {
                // 2. Hantar melalui AiApiManager + Antigravity Middleware
                val response = aiApiManager.sendMessage(
                    rawPrompt = translationPipeline.promptForModel,
                    selectedModel = _selectedModel.value,
                    isAutoTranslate = false
                )

                // 3. Terjemah semula jawapan AI kepada bahasa asal pengguna jika dwiarah aktif
                val finalText = if (_isRealtimeTranslationActive.value && translationPipeline.needsReverseTranslation) {
                    val reverse = translationEngine.translateResponseToUserLanguage(
                        response.text,
                        translationPipeline.detectedLanguage
                    )
                    reverse.translatedToUserLanguage
                } else {
                    response.text
                }

                // 4. Simpan jawapan ke Room DB
                val assistantMsg = ChatMessage(
                    text = finalText,
                    isUser = false,
                    model = response.modelUsed,
                    latencyMs = response.latencyMs,
                    fallbackPath = response.fallbackPath,
                    isMusic = response.isMusic,
                    musicGenre = response.musicGenre,
                    musicLyrics = response.musicLyrics,
                    isError = response.isError,
                    userId = userId,
                    detectedLanguage = translationPipeline.detectedLanguage.code
                )
                val id = chatDao.insertMessage(ChatMessageEntity.fromDomain(assistantMsg))

                // 5. Suara TTS automatik jika mod auto suara atau mod buta huruf aktif
                if (_isAutoVoiceOver.value || _isVoiceIlliterateMode.value) {
                    tts.speak(finalText, id)
                }
            } catch (e: Exception) {
                val errorMsg = ChatMessage(
                    text = "Ralat semasa pemprosesan: ${e.localizedMessage}. Sila cuba model lain.",
                    isUser = false,
                    model = _selectedModel.value,
                    isError = true,
                    userId = userId
                )
                chatDao.insertMessage(ChatMessageEntity.fromDomain(errorMsg))
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun selectModel(model: AiModel) {
        _selectedModel.value = model
        viewModelScope.launch {
            authManager.updatePreferences(model.id, _currentLanguage.value.code)
        }
    }

    fun selectLanguage(lang: SupportedLanguage) {
        _currentLanguage.value = lang
        viewModelScope.launch {
            authManager.updatePreferences(_selectedModel.value.id, lang.code)
        }
    }

    fun toggleRealtimeTranslation() {
        _isRealtimeTranslationActive.value = !_isRealtimeTranslationActive.value
    }

    fun toggleAutoVoiceOver() {
        val newVal = !_isAutoVoiceOver.value
        _isAutoVoiceOver.value = newVal
        tts.speak(if (newVal) "Auto suara aktif." else "Auto suara dinyahaktifkan.")
    }

    fun toggleVoiceIlliterateMode() {
        val newVal = !_isVoiceIlliterateMode.value
        _isVoiceIlliterateMode.value = newVal
        if (newVal) {
            _isAutoVoiceOver.value = true
            tts.speak("Mod Akses Suara dan Gambar diaktifkan. Anda boleh menyentuh kad bergambar atau bercakap melalui mikrofon.")
        }
    }

    fun cycleThemeMode() {
        _darkThemeMode.value = when (_darkThemeMode.value) {
            1 -> 2 // to Light
            2 -> 1 // to Dark
            else -> 1
        }
    }

    fun clearAllMessages() {
        viewModelScope.launch {
            tts.stop()
            chatDao.clearMessagesForUser(authManager.currentUser.value?.id)
        }
    }

    fun speakMessage(message: ChatMessage) {
        tts.speak(message.text, message.id)
    }

    fun stopSpeaking() {
        tts.stop()
    }

    // Music Dialog Controls
    fun openMusicConsentDialog() {
        _showMusicConsentDialog.value = true
    }

    fun dismissMusicConsent() {
        _showMusicConsentDialog.value = false
    }

    fun confirmPlayMusic(track: BackgroundTrack, loop: Boolean, volume: Float) {
        _showMusicConsentDialog.value = false
        _showFloatingMusicBar.value = true
        if (loop != musicManager.isLooping.value) {
            musicManager.toggleLoop()
        }
        musicManager.setVolume(volume)
        musicManager.playTrack(track)
    }

    fun togglePlayMusic() {
        _showFloatingMusicBar.value = true
        musicManager.togglePlayPause()
    }

    fun nextMusicTrack() {
        val tracks = BackgroundTrack.TRACKS
        val currentIndex = tracks.indexOfFirst { it.id == musicManager.currentTrack.value.id }
        val nextIndex = (currentIndex + 1) % tracks.size
        musicManager.playTrack(tracks[nextIndex])
    }

    fun closeFloatingMusicBar() {
        _showFloatingMusicBar.value = false
        musicManager.stop()
    }

    // Modal Visibility Setters
    fun setModelPickerVisible(visible: Boolean) { _showModelPicker.value = visible }
    fun setPaymentModalVisible(visible: Boolean) { _showPaymentModal.value = visible }
    fun setQrInstallModalVisible(visible: Boolean) { _showQrInstallModal.value = visible }
    fun setUnsplashDialogVisible(visible: Boolean) { _showUnsplashDialog.value = visible }
    fun setAuthDialogVisible(visible: Boolean) { _showAuthDialog.value = visible }
    fun setLanguageSheetVisible(visible: Boolean) { _showLanguageSheet.value = visible }

    fun setUnsplashTheme(preset: UnsplashPreset, dimAlpha: Float) {
        _currentUnsplash.value = preset
        _unsplashDim.value = dimAlpha
    }

    fun setAmbassadorBackground(enabled: Boolean) {
        _showAmbassadorBackground.value = enabled
    }

    fun setAmbassadorAlpha(alpha: Float) {
        _ambassadorAlpha.value = alpha
    }

    fun setAmbassadorDialogVisible(visible: Boolean) {
        _showAmbassadorDialog.value = visible
    }

    override fun onCleared() {
        super.onCleared()
        musicManager.release()
        tts.release()
    }
}
