package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.example.data.model.BackgroundTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BackgroundMusicManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentTrack = MutableStateFlow(BackgroundTrack.TRACKS[0])
    val currentTrack: StateFlow<BackgroundTrack> = _currentTrack.asStateFlow()

    private val _isLooping = MutableStateFlow(true)
    val isLooping: StateFlow<Boolean> = _isLooping.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _volume = MutableStateFlow(0.7f)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun playTrack(track: BackgroundTrack) {
        _currentTrack.value = track
        _isLoading.value = true
        _errorMessage.value = null

        scope.launch(Dispatchers.IO) {
            try {
                mediaPlayer?.release()
                mediaPlayer = null

                val player = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource(context, Uri.parse(track.streamUrl))
                    isLooping = _isLooping.value
                    val vol = _volume.value
                    setVolume(vol, vol)

                    setOnPreparedListener { mp ->
                        _isLoading.value = false
                        mp.start()
                        _isPlaying.value = true
                        Log.d("MusicManager", "Playing ${track.title}")
                    }

                    setOnErrorListener { _, what, extra ->
                        Log.w("MusicManager", "MediaPlayer error $what / $extra, falling back gracefully")
                        _isLoading.value = false
                        _isPlaying.value = false
                        _errorMessage.value = "Streaming track ${track.id} dijeda (penimbalan Google Drive)."
                        true
                    }

                    setOnCompletionListener {
                        if (!_isLooping.value) {
                            _isPlaying.value = false
                        }
                    }

                    prepareAsync()
                }
                mediaPlayer = player
            } catch (e: Exception) {
                Log.e("MusicManager", "Failed to initialize player: ${e.message}", e)
                _isLoading.value = false
                _isPlaying.value = false
                _errorMessage.value = "Sambungan muzik drive gagal: ${e.localizedMessage}"
            }
        }
    }

    fun togglePlayPause() {
        val player = mediaPlayer
        if (player != null) {
            try {
                if (player.isPlaying) {
                    player.pause()
                    _isPlaying.value = false
                } else {
                    player.start()
                    _isPlaying.value = true
                }
            } catch (e: Exception) {
                Log.e("MusicManager", "Toggle error", e)
                playTrack(_currentTrack.value)
            }
        } else {
            playTrack(_currentTrack.value)
        }
    }

    fun toggleLoop() {
        val newLoop = !_isLooping.value
        _isLooping.value = newLoop
        mediaPlayer?.isLooping = newLoop
    }

    fun setVolume(vol: Float) {
        val clamped = vol.coerceIn(0f, 1f)
        _volume.value = clamped
        mediaPlayer?.setVolume(clamped, clamped)
    }

    fun stop() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (_: Exception) {}
        _isPlaying.value = false
        _isLoading.value = false
    }

    fun release() {
        stop()
    }
}
