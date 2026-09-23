package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.sin

@Composable
fun ChatBubble(
    message: ChatMessage,
    isSpeaking: Boolean,
    onSpeak: (ChatMessage) -> Unit,
    onStopSpeak: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isMelodyPlaying by remember { mutableStateOf(false) }
    var showTranslation by remember { mutableStateOf(false) }

    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val formattedTime = remember(message.timestamp) { timeFormat.format(Date(message.timestamp)) }

    fun copyText(txt: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Chat Message", txt)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Mesej disalin!", Toast.LENGTH_SHORT).show()
    }

    /**
     * Memainkan melodi kord sintesis sebenar melalui AudioTrack Android
     * bagi respons yang dijana oleh MuzikGPT.
     */
    fun playSyntheticMelody() {
        if (isMelodyPlaying) return
        isMelodyPlaying = true
        coroutineScope.launch(Dispatchers.Default) {
            try {
                val sampleRate = 22050
                // Melodi arpeggio C Major: C4, E4, G4, C5, B4, G4, E4, C4
                val notes = doubleArrayOf(261.63, 329.63, 392.00, 523.25, 493.88, 392.00, 329.63, 261.63)
                val durationMs = 280
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()

                val track = AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    numSamples * 2,
                    AudioTrack.MODE_STREAM
                )
                track.play()

                for (freq in notes) {
                    val buffer = ShortArray(numSamples)
                    for (i in 0 until numSamples) {
                        val angle = 2.0 * Math.PI * i / (sampleRate / freq)
                        // Smooth envelope decay
                        val envelope = (numSamples - i).toDouble() / numSamples.toDouble()
                        buffer[i] = (sin(angle) * 32767.0 * 0.6 * envelope).toInt().toShort()
                    }
                    track.write(buffer, 0, buffer.size)
                }

                track.stop()
                track.release()
            } catch (_: Exception) {}
            isMelodyPlaying = false
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!message.isUser) {
            // Multi AI / Model Avatar
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, message.model.badgeColor, CircleShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = "Multi AI",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            // Model Header for Assistant
            if (!message.isUser) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(message.model.badgeColor.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = message.model.displayName,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = message.model.badgeColor,
                            fontSize = 10.sp
                        )
                    }

                    if (message.latencyMs > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "Antigravity Latency",
                                tint = Color(0xFF00E5FF),
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "${message.latencyMs}ms",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            // Message Bubble Card
            Surface(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (message.isUser) 16.dp else 4.dp,
                            bottomEnd = if (message.isUser) 4.dp else 16.dp
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = if (message.isUser) {
                            Color(0xFF3B82F6).copy(alpha = 0.4f)
                        } else {
                            message.model.badgeColor.copy(alpha = 0.25f)
                        },
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (message.isUser) 16.dp else 4.dp,
                            bottomEnd = if (message.isUser) 4.dp else 16.dp
                        )
                    )
                    .testTag("chat_bubble_${message.id}"),
                color = if (message.isUser) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
                },
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Fallback Badge if applicable
                    if (!message.isUser && message.fallbackPath != null) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp),
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "⚡ ${message.fallbackPath}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF00E5FF),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    // Main Text
                    Text(
                        text = if (showTranslation && message.translatedText != null) message.translatedText else message.text,
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                        color = if (message.isUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Music Generator Section
                    if (message.isMusic) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF06B6D4).copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF06B6D4).copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.MusicNote,
                                            contentDescription = null,
                                            tint = Color(0xFF06B6D4),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = message.musicGenre ?: "Muzik Melodi",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color(0xFF06B6D4)
                                        )
                                    }

                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .clickable { playSyntheticMelody() }
                                            .background(Color(0xFF06B6D4).copy(alpha = 0.25f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        color = Color.Transparent
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = if (isMelodyPlaying) Icons.Default.GraphicEq else Icons.Default.PlayArrow,
                                                contentDescription = "Main Melodi",
                                                tint = Color(0xFF06B6D4),
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (isMelodyPlaying) "Bermain..." else "Dengar Melodi",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = Color(0xFF06B6D4),
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Footer actions row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formattedTime,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            fontSize = 10.sp
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (!message.isUser) {
                                // Speak button
                                IconButton(
                                    onClick = {
                                        if (isSpeaking) onStopSpeak() else onSpeak(message)
                                    },
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                                        contentDescription = if (isSpeaking) "Henti Suara" else "Dengar Suara",
                                        tint = if (isSpeaking) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }

                            // Copy button
                            IconButton(
                                onClick = { copyText(message.text) },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Salin Teks",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (message.isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Pengguna",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
