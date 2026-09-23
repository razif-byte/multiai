package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.components.AmbassadorProfileDialog
import com.example.ui.components.AntigravityHud
import com.example.ui.components.AuthDialog
import com.example.ui.components.BackgroundMusicDialog
import com.example.ui.components.ChatBubble
import com.example.ui.components.DeviceInstallQrDialog
import com.example.ui.components.LanguageSelectorSheet
import com.example.ui.components.ModelSelectorSheet
import com.example.ui.components.MusicFloatingWidget
import com.example.ui.components.PaymentSubscriptionModal
import com.example.ui.components.PictogramActionGrid
import com.example.ui.components.UnsplashThemeDialog
import com.example.ui.components.WatermarkFooter
import com.example.ui.screens.EndingVideoScreen
import com.example.ui.viewmodel.ChatViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainChatScreen(
    viewModel: ChatViewModel
) {
    val showIntroVideo by viewModel.showIntroVideo.collectAsStateWithLifecycle()
    val showEndingVideo by viewModel.showEndingVideo.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // 1. Sekiranya kali pertama dibuka atau dipilih, paparkan video intro (MX Player / VLC / Internal)
    if (showIntroVideo) {
        IntroVideoScreen(
            tts = viewModel.tts,
            onContinue = { viewModel.completeIntroVideo() }
        )
        return
    }

    // 2. Sekiranya video ending dipanggil, paparkan video ending (MX Player / VLC / Internal)
    if (showEndingVideo) {
        EndingVideoScreen(
            tts = viewModel.tts,
            onReturnToChat = { viewModel.closeEndingVideo() },
            onExitApp = {
                viewModel.closeEndingVideo()
                (context as? Activity)?.finishAffinity()
            }
        )
        return
    }
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val selectedModel by viewModel.selectedModel.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val isRealtimeTranslationActive by viewModel.isRealtimeTranslationActive.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val isAutoVoiceOver by viewModel.isAutoVoiceOver.collectAsStateWithLifecycle()
    val isVoiceIlliterateMode by viewModel.isVoiceIlliterateMode.collectAsStateWithLifecycle()
    val antigravityStats by viewModel.antigravityStats.collectAsStateWithLifecycle()
    val darkThemeMode by viewModel.darkThemeMode.collectAsStateWithLifecycle()

    val isMusicPlaying by viewModel.musicManager.isPlaying.collectAsStateWithLifecycle()
    val isMusicLoading by viewModel.musicManager.isLoading.collectAsStateWithLifecycle()
    val currentTrack by viewModel.musicManager.currentTrack.collectAsStateWithLifecycle()
    val isMusicLooping by viewModel.musicManager.isLooping.collectAsStateWithLifecycle()
    val musicVolume by viewModel.musicManager.volume.collectAsStateWithLifecycle()

    val isSpeaking by viewModel.tts.isSpeaking.collectAsStateWithLifecycle()

    val currentUnsplash by viewModel.currentUnsplash.collectAsStateWithLifecycle()
    val unsplashDim by viewModel.unsplashDim.collectAsStateWithLifecycle()

    val showMusicConsentDialog by viewModel.showMusicConsentDialog.collectAsStateWithLifecycle()
    val showModelPicker by viewModel.showModelPicker.collectAsStateWithLifecycle()
    val showPaymentModal by viewModel.showPaymentModal.collectAsStateWithLifecycle()
    val showQrInstallModal by viewModel.showQrInstallModal.collectAsStateWithLifecycle()
    val showUnsplashDialog by viewModel.showUnsplashDialog.collectAsStateWithLifecycle()
    val showAuthDialog by viewModel.showAuthDialog.collectAsStateWithLifecycle()
    val showLanguageSheet by viewModel.showLanguageSheet.collectAsStateWithLifecycle()
    val showFloatingMusicBar by viewModel.showFloatingMusicBar.collectAsStateWithLifecycle()
    val showAmbassadorBackground by viewModel.showAmbassadorBackground.collectAsStateWithLifecycle()
    val ambassadorAlpha by viewModel.ambassadorAlpha.collectAsStateWithLifecycle()
    val showAmbassadorDialog by viewModel.showAmbassadorDialog.collectAsStateWithLifecycle()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val focusManager = LocalFocusManager.current
    var showMenu by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val langSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Speech-To-Text Recognizer Launcher (Akses mudah tanpa perlu menaip/membaca)
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                viewModel.sendMessage(spoken)
            }
        }
    }

    fun startVoiceInput() {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, currentLanguage.ttsLocaleTag)
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Sila bercakap sekarang... Kami sedang mendengar.")
            }
            speechRecognizerLauncher.launch(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "Pengecaman suara tidak disokong pada peranti ini.", Toast.LENGTH_SHORT).show()
        }
    }

    // Animasi denyutan mikrofon untuk pengguna akses suara
    val infiniteTransition = rememberInfiniteTransition(label = "micPulse")
    val micPulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.14f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "micScale"
    )

    // Auto-scroll bila mesej baharu tiba
    LaunchedEffect(messages.size, isGenerating) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { viewModel.setModelPickerVisible(true) }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFF00E5FF), RoundedCornerShape(8.dp))
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.app_logo),
                                contentDescription = "Logo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Multi AI",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF00E5FF).copy(alpha = 0.2f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "ZERO-G",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFF00E5FF),
                                        fontSize = 8.sp
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = selectedModel.displayName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = selectedModel.badgeColor,
                                    fontSize = 11.sp
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Pilih Model",
                                    tint = selectedModel.badgeColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                },
                actions = {
                    // Pilihan Bahasa Terjemahan Real-time
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { viewModel.setLanguageSheetVisible(true) }
                            .background(Color(0xFF3B82F6).copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("appbar_language_button"),
                        color = Color.Transparent
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = currentLanguage.flagEmoji, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = currentLanguage.code.uppercase(Locale.getDefault()),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF3B82F6),
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Model Duta AI Avatar
                    Surface(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, Color(0xFFEC4899), CircleShape)
                            .clickable { viewModel.setAmbassadorDialogVisible(true) }
                            .testTag("appbar_ambassador_button"),
                        color = Color.Transparent
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_ambassador),
                            contentDescription = "Model Duta Multi AI",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Mod Mesra Suara & Gambar (Untuk Bukan Pembaca)
                    IconButton(
                        onClick = { viewModel.toggleVoiceIlliterateMode() },
                        modifier = Modifier.testTag("appbar_voice_mode_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = "Mod Akses Suara",
                            tint = if (isVoiceIlliterateMode) Color(0xFF22C55E) else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Profil / Akaun Pengguna
                    IconButton(
                        onClick = { viewModel.setAuthDialogVisible(true) },
                        modifier = Modifier.testTag("appbar_auth_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Akaun",
                            tint = if (currentUser != null) Color(0xFF22C55E) else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Muzik Latar
                    IconButton(
                        onClick = { viewModel.openMusicConsentDialog() },
                        modifier = Modifier.testTag("appbar_music_button")
                    ) {
                        Icon(
                            imageVector = if (isMusicPlaying) Icons.Default.GraphicEq else Icons.Default.MusicNote,
                            contentDescription = "Muzik Latar",
                            tint = if (isMusicPlaying) Color(0xFF00E5FF) else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Menu Limpahan
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Model Duta & Gambar Latar") },
                                onClick = {
                                    showMenu = false
                                    viewModel.setAmbassadorDialogVisible(true)
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFEC4899))
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Langganan VIP / Sumbangan") },
                                onClick = {
                                    showMenu = false
                                    viewModel.setPaymentModalVisible(true)
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Diamond, contentDescription = null, tint = Color(0xFFEAB308))
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("QR Pasang di Telefon Lain") },
                                onClick = {
                                    showMenu = false
                                    viewModel.setQrInstallModalVisible(true)
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.QrCode, contentDescription = null, tint = Color(0xFF3B82F6))
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("🎬 Tonton Video Intro (MX/VLC/Dalaman)") },
                                onClick = {
                                    showMenu = false
                                    viewModel.replayIntroVideo()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Color(0xFFEF4444))
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("🏁 Tonton Video Ending (Video Penutup)") },
                                onClick = {
                                    showMenu = false
                                    viewModel.playEndingVideo()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Color(0xFFEAB308))
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Tukar Tema Gelap/Cerah") },
                                onClick = {
                                    showMenu = false
                                    viewModel.cycleThemeMode()
                                },
                                leadingIcon = {
                                    Icon(if (darkThemeMode == 2) Icons.Default.DarkMode else Icons.Default.LightMode, contentDescription = null)
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Tema Latar Unsplash") },
                                onClick = {
                                    showMenu = false
                                    viewModel.setUnsplashDialogVisible(true)
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Wallpaper, contentDescription = null, tint = Color(0xFF00E5FF))
                                }
                            )

                            DropdownMenuItem(
                                text = { Text(if (isAutoVoiceOver) "Bacaan Suara (TTS): ON" else "Bacaan Suara (TTS): OFF") },
                                onClick = {
                                    showMenu = false
                                    viewModel.toggleAutoVoiceOver()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.VolumeUp, contentDescription = null, tint = if (isAutoVoiceOver) Color(0xFF22C55E) else MaterialTheme.colorScheme.onSurface)
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Kosongkan Perbualan") },
                                onClick = {
                                    showMenu = false
                                    viewModel.clearAllMessages()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.ClearAll, contentDescription = null, tint = Color(0xFFEF4444))
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.97f))
            ) {
                // Floating music player if enabled
                AnimatedVisibility(visible = showFloatingMusicBar) {
                    MusicFloatingWidget(
                        isPlaying = isMusicPlaying,
                        isLoading = isMusicLoading,
                        currentTrack = currentTrack,
                        isLooping = isMusicLooping,
                        onTogglePlayPause = { viewModel.togglePlayMusic() },
                        onNextTrack = { viewModel.nextMusicTrack() },
                        onToggleLoop = { viewModel.musicManager.toggleLoop() },
                        onClose = { viewModel.closeFloatingMusicBar() },
                        onOpenDialog = { viewModel.openMusicConsentDialog() }
                    )
                }

                // Barisan Akses Kad Gambar untuk Pengguna Yang Tidak Tahu Membaca
                AnimatedVisibility(visible = isVoiceIlliterateMode) {
                    PictogramActionGrid(
                        tts = viewModel.tts,
                        onSendPrompt = { prompt ->
                            viewModel.sendMessage(prompt)
                        }
                    )
                }

                // Baris Prompt Pantas Ringkas
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        QuickChip("🎬 Intro Video", Color(0xFFEF4444)) {
                            viewModel.replayIntroVideo()
                        }
                    }
                    item {
                        QuickChip("🏁 Ending Video", Color(0xFFEAB308)) {
                            viewModel.playEndingVideo()
                        }
                    }
                    item {
                        QuickChip(
                            if (showAmbassadorBackground) "✨ Duta Latar (${(ambassadorAlpha * 100).toInt()}%)" else "✨ Duta Maya",
                            Color(0xFFEC4899)
                        ) {
                            viewModel.setAmbassadorDialogVisible(true)
                        }
                    }
                    item {
                        QuickChip("🎵 Cipta Muzik Melodi", Color(0xFF06B6D4)) {
                            viewModel.selectModel(com.example.data.model.AiModel.MUZIKGPT)
                            inputText = "Tolong cipta lirik dan melodi lagu pop moden tentang harapan dan mimpi."
                        }
                    }
                    item {
                        QuickChip(
                            if (isVoiceIlliterateMode) "👁️ Mod Gambar: ON" else "👁️ Kad Gambar & Suara",
                            Color(0xFF22C55E)
                        ) {
                            viewModel.toggleVoiceIlliterateMode()
                        }
                    }
                    item {
                        QuickChip("🌐 Terjemahan: ${currentLanguage.nameMalay}", Color(0xFF3B82F6)) {
                            viewModel.setLanguageSheetVisible(true)
                        }
                    }
                    item {
                        QuickChip("⚡ Antigravity Boost", Color(0xFF00E5FF)) {
                            inputText = "Bagaimanakah enjin Antigravity mengoptimumkan latensi transmisi model?"
                        }
                    }
                }

                // Input Row dengan Butang Mikrofon Besar Suara
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Butang Besar Mikrofon Pengecaman Suara (Mesra pengguna tidak membaca)
                    FloatingActionButton(
                        onClick = { startVoiceInput() },
                        modifier = Modifier
                            .size(48.dp)
                            .scale(if (isVoiceIlliterateMode) micPulseScale else 1.0f)
                            .testTag("voice_input_fab"),
                        shape = CircleShape,
                        containerColor = Color(0xFF22C55E),
                        contentColor = Color.Black
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Bercakap dengan AI",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field"),
                        placeholder = {
                            Text(
                                text = "Bercakap 🎤 atau taip...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        maxLines = 4,
                        shape = RoundedCornerShape(24.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (inputText.isNotBlank() && !isGenerating) {
                                    viewModel.sendMessage(inputText)
                                    inputText = ""
                                    focusManager.clearFocus()
                                }
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = selectedModel.badgeColor,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    FloatingActionButton(
                        onClick = {
                            if (inputText.isNotBlank() && !isGenerating) {
                                viewModel.sendMessage(inputText)
                                inputText = ""
                                focusManager.clearFocus()
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("chat_send_button"),
                        shape = CircleShape,
                        containerColor = if (isGenerating) MaterialTheme.colorScheme.surfaceVariant else selectedModel.badgeColor,
                        contentColor = Color.White
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.5.dp,
                                color = selectedModel.badgeColor
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Hantar Mesej",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Watermark footer
                WatermarkFooter()
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Latar Belakang Model Duta Separa Telus (Default) atau Unsplash
            if (showAmbassadorBackground) {
                Image(
                    painter = painterResource(id = R.drawable.img_ambassador),
                    contentDescription = "Model Duta Multi AI",
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(ambassadorAlpha),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.background.copy(alpha = 0.50f),
                                    MaterialTheme.colorScheme.background.copy(alpha = 0.72f)
                                )
                            )
                        )
                )
            } else if (currentUnsplash.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = currentUnsplash.imageUrl,
                    contentDescription = "Latar Belakang",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.background.copy(alpha = unsplashDim),
                                    MaterialTheme.colorScheme.background.copy(alpha = (unsplashDim + 0.05f).coerceAtMost(1f))
                                )
                            )
                        )
                )
            }

            Column(modifier = Modifier.fillMaxSize()) {
                // Banner Pengguna Log Masuk
                if (currentUser != null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF22C55E).copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "👤 Log masuk: ${currentUser?.username}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF22C55E)
                            )
                            Text(
                                text = "Tukar Akaun",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF3B82F6),
                                modifier = Modifier.clickable { viewModel.setAuthDialogVisible(true) }
                            )
                        }
                    }
                }

                // Antigravity Telemetry HUD
                AntigravityHud(stats = antigravityStats)

                // Message Thread
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(
                        items = messages,
                        key = { it.id }
                    ) { msg ->
                        ChatBubble(
                            message = msg,
                            isSpeaking = isSpeaking,
                            onSpeak = { viewModel.speakMessage(it) },
                            onStopSpeak = { viewModel.stopSpeaking() }
                        )
                    }

                    if (isGenerating) {
                        item {
                            Surface(
                                modifier = Modifier
                                    .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                                    .clip(RoundedCornerShape(14.dp)),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = selectedModel.badgeColor
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Antigravity & Terjemahan memproses respons...",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = selectedModel.badgeColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Sheets and Dialogs
    if (showAmbassadorDialog) {
        AmbassadorProfileDialog(
            tts = viewModel.tts,
            isEnabled = showAmbassadorBackground,
            alpha = ambassadorAlpha,
            onToggleEnabled = { viewModel.setAmbassadorBackground(it) },
            onAlphaChanged = { viewModel.setAmbassadorAlpha(it) },
            onDismiss = { viewModel.setAmbassadorDialogVisible(false) }
        )
    }

    if (showModelPicker) {
        ModelSelectorSheet(
            sheetState = sheetState,
            selectedModel = selectedModel,
            onModelSelected = { viewModel.selectModel(it) },
            onDismiss = { viewModel.setModelPickerVisible(false) }
        )
    }

    if (showLanguageSheet) {
        LanguageSelectorSheet(
            sheetState = langSheetState,
            currentLanguage = currentLanguage,
            isRealtimeTranslationActive = isRealtimeTranslationActive,
            onToggleRealtimeTranslation = { viewModel.toggleRealtimeTranslation() },
            onSelectLanguage = { viewModel.selectLanguage(it) },
            onDismiss = { viewModel.setLanguageSheetVisible(false) }
        )
    }

    if (showAuthDialog) {
        AuthDialog(
            authManager = viewModel.authManager,
            tts = viewModel.tts,
            onDismiss = { viewModel.setAuthDialogVisible(false) }
        )
    }

    if (showMusicConsentDialog) {
        BackgroundMusicDialog(
            selectedTrack = currentTrack,
            isLooping = isMusicLooping,
            initialVolume = musicVolume,
            onConfirmPlay = { track, loop, vol ->
                viewModel.confirmPlayMusic(track, loop, vol)
            },
            onDismiss = { viewModel.dismissMusicConsent() }
        )
    }

    if (showPaymentModal) {
        PaymentSubscriptionModal(
            onDismiss = { viewModel.setPaymentModalVisible(false) }
        )
    }

    if (showQrInstallModal) {
        DeviceInstallQrDialog(
            onDismiss = { viewModel.setQrInstallModalVisible(false) }
        )
    }

    if (showUnsplashDialog) {
        UnsplashThemeDialog(
            currentPreset = currentUnsplash,
            initialDimAlpha = unsplashDim,
            onSelectPreset = { preset, dim ->
                viewModel.setUnsplashTheme(preset, dim)
            },
            onDismiss = { viewModel.setUnsplashDialogVisible(false) }
        )
    }
}

@Composable
private fun QuickChip(
    text: String,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = color.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = color
            )
        }
    }
}
