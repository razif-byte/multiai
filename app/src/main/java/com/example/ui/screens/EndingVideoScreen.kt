package com.example.ui.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.audio.AppTextToSpeech
import com.example.ui.components.WatermarkFooter
import com.example.ui.util.VideoPlayerHelper

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun EndingVideoScreen(
    tts: AppTextToSpeech,
    onReturnToChat: () -> Unit,
    onExitApp: () -> Unit
) {
    val context = LocalContext.current
    var selectedPlayer by remember { mutableStateOf(VideoPlayerHelper.PlayerType.INTERNAL) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    val videoEmbedUrl = VideoPlayerHelper.ENDING_VIDEO_URL
    val videoDirectUrl = VideoPlayerHelper.ENDING_DIRECT_URL

    LaunchedEffect(Unit) {
        tts.speak("Terima kasih telah menggunakan Multi AI. Video penutup kini dimainkan. Anda boleh kembali ke ruang sembang bila-bila masa atau menutup aplikasi.")
    }

    DisposableEffect(Unit) {
        onDispose {
            tts.stop()
            webViewInstance?.destroy()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("ending_video_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF0F172A)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEAB308))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ENDING VIDEO & OUTRO",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = Color.White
                            )
                        }

                        // Panduan Suara
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF00E5FF).copy(alpha = 0.25f)),
                            color = Color.Transparent,
                            onClick = {
                                tts.speak("Ketik butang biru untuk kembali ke perbualan, atau butang merah untuk menutup aplikasi sepenuhnya.")
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Panduan Suara",
                                    tint = Color(0xFF00E5FF),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Panduan Suara",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF00E5FF)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Player Selection Tabs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PlayerSelectBadge(
                            label = "📱 Dalaman",
                            isSelected = selectedPlayer == VideoPlayerHelper.PlayerType.INTERNAL,
                            activeColor = Color(0xFF00E5FF),
                            modifier = Modifier.weight(1f)
                        ) {
                            selectedPlayer = VideoPlayerHelper.PlayerType.INTERNAL
                            webViewInstance?.loadUrl(videoEmbedUrl)
                        }

                        PlayerSelectBadge(
                            label = "⚡ MX Player",
                            isSelected = selectedPlayer == VideoPlayerHelper.PlayerType.MX_PLAYER,
                            activeColor = Color(0xFF3B82F6),
                            modifier = Modifier.weight(1f)
                        ) {
                            selectedPlayer = VideoPlayerHelper.PlayerType.MX_PLAYER
                            VideoPlayerHelper.launchMxPlayer(context, videoDirectUrl, "Multi AI - Ending Video")
                        }

                        PlayerSelectBadge(
                            label = "🧡 VLC",
                            isSelected = selectedPlayer == VideoPlayerHelper.PlayerType.VLC,
                            activeColor = Color(0xFFFF9800),
                            modifier = Modifier.weight(1f)
                        ) {
                            selectedPlayer = VideoPlayerHelper.PlayerType.VLC
                            VideoPlayerHelper.launchVlcPlayer(context, videoDirectUrl, "Multi AI - Ending Video")
                        }

                        PlayerSelectBadge(
                            label = "🚀 Sistem",
                            isSelected = selectedPlayer == VideoPlayerHelper.PlayerType.SYSTEM_CHOOSER,
                            activeColor = Color(0xFF10B981),
                            modifier = Modifier.weight(1f)
                        ) {
                            selectedPlayer = VideoPlayerHelper.PlayerType.SYSTEM_CHOOSER
                            VideoPlayerHelper.launchSystemPlayer(context, videoDirectUrl, "Multi AI - Ending Video")
                        }
                    }
                }
            }

            // Video Player Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.Black)
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.apply {
                                javaScriptEnabled = true
                                mediaPlaybackRequiresUserGesture = false
                                domStorageEnabled = true
                                loadWithOverviewMode = true
                                useWideViewPort = true
                                cacheMode = WebSettings.LOAD_DEFAULT
                            }
                            webChromeClient = WebChromeClient()
                            webViewClient = WebViewClient()
                            loadUrl(videoEmbedUrl)
                            webViewInstance = this
                        }
                    }
                )

                if (selectedPlayer != VideoPlayerHelper.PlayerType.INTERNAL) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.85f))
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.VideoLibrary,
                                contentDescription = null,
                                tint = when (selectedPlayer) {
                                    VideoPlayerHelper.PlayerType.MX_PLAYER -> Color(0xFF3B82F6)
                                    VideoPlayerHelper.PlayerType.VLC -> Color(0xFFFF9800)
                                    else -> Color(0xFF10B981)
                                },
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Ending Video Dimainkan Melalui ${selectedPlayer.displayName}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Ketik butang di bawah untuk membuka semula di pemain luaran.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.LightGray,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = {
                                        when (selectedPlayer) {
                                            VideoPlayerHelper.PlayerType.MX_PLAYER ->
                                                VideoPlayerHelper.launchMxPlayer(context, videoDirectUrl, "Ending Video")
                                            VideoPlayerHelper.PlayerType.VLC ->
                                                VideoPlayerHelper.launchVlcPlayer(context, videoDirectUrl, "Ending Video")
                                            else ->
                                                VideoPlayerHelper.launchSystemPlayer(context, videoDirectUrl, "Ending Video")
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = when (selectedPlayer) {
                                            VideoPlayerHelper.PlayerType.MX_PLAYER -> Color(0xFF3B82F6)
                                            VideoPlayerHelper.PlayerType.VLC -> Color(0xFFFF9800)
                                            else -> Color(0xFF10B981)
                                        }
                                    )
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Buka Semula")
                                }

                                Button(
                                    onClick = {
                                        selectedPlayer = VideoPlayerHelper.PlayerType.INTERNAL
                                        webViewInstance?.loadUrl(videoEmbedUrl)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155))
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Pemain Dalaman")
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Actions: Return to Chat or Exit App
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF0A0F1D)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Terima kasih telah menggunakan Multi AI! ✨",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF00E5FF),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onReturnToChat,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "KEMBALI KE SEMBANG",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.Black
                            )
                        }

                        Button(
                            onClick = onExitApp,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .weight(0.9f)
                                .height(50.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "TUTUP APP",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    WatermarkFooter()
                }
            }
        }
    }
}
