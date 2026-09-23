package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.audio.AppTextToSpeech
import com.example.data.auth.AuthManager
import kotlinx.coroutines.launch

@Composable
fun AuthDialog(
    authManager: AuthManager,
    tts: AppTextToSpeech,
    onDismiss: () -> Unit
) {
    val currentUser by authManager.currentUser.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    var showSwitchAccount by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }
    var showEmailForm by remember { mutableStateOf(false) }

    // Dialog untuk akaun yang sudah log masuk
    if (currentUser != null && !showSwitchAccount) {
        val user = currentUser!!
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF22C55E).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF22C55E),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Akaun Sedang Aktif",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Log masuk berjaya disahkan",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    // Kad Maklumat Profil
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                            .padding(16.dp),
                        color = Color.Transparent
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when (user.authProvider) {
                                                    "google" -> Color(0xFFEA4335).copy(alpha = 0.2f)
                                                    "nasadef" -> Color(0xFF00E5FF).copy(alpha = 0.2f)
                                                    "microsoft" -> Color(0xFF00A4EF).copy(alpha = 0.2f)
                                                    else -> Color(0xFF22C55E).copy(alpha = 0.2f)
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = when (user.authProvider) {
                                                "google" -> "G"
                                                "nasadef" -> "N"
                                                "microsoft" -> "M"
                                                else -> user.username.take(1).uppercase()
                                            },
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = when (user.authProvider) {
                                                "google" -> Color(0xFFEA4335)
                                                "nasadef" -> Color(0xFF00E5FF)
                                                "microsoft" -> Color(0xFF00A4EF)
                                                else -> Color(0xFF22C55E)
                                            }
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = user.username,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = user.email,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                if (user.isVip) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFEAB308).copy(alpha = 0.2f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = null,
                                                tint = Color(0xFFEAB308),
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "VIP",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = Color(0xFFEAB308),
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Kaedah Log Masuk:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = when (user.authProvider) {
                                        "google" -> "Akaun Google"
                                        "nasadef" -> "Akaun Rasmi Nasadef"
                                        "microsoft" -> "Akaun Microsoft"
                                        "guest" -> "Akses Suara Tetamu"
                                        else -> "Emel & Kata Laluan"
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Butang Tukar Akaun
                    OutlinedButton(
                        onClick = { showSwitchAccount = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tukar ke Akaun Lain")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Butang Log Keluar
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                authManager.logout()
                                tts.speak("Anda telah log keluar.")
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Log Keluar", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text("Tutup")
                }
            }
        )
        return
    }

    // Dialog Pilihan Log Masuk Pelbagai Akaun
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF3B82F6).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = Color(0xFF3B82F6),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Pilih Cara Log Masuk",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Google, Nasadef, Microsoft atau Suara",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Panduan Suara
                Surface(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0xFF00E5FF).copy(alpha = 0.15f)),
                    color = Color.Transparent,
                    onClick = {
                        tts.speak("Sila pilih untuk log masuk menggunakan akaun Google, akaun rasmi Nasadef, akaun Microsoft, atau ketik butang hijau untuk masuk pantas sebagai tetamu.")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Panduan Suara",
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier
                            .padding(8.dp)
                            .size(18.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // 1. Log Masuk Akaun Google (1-Ketik Pantas)
                OAuthOptionCard(
                    title = "Teruskan dengan Akaun Google",
                    subtitle = "Log masuk mudah melalui profil Google anda",
                    badgeText = "GOOGLE",
                    iconColor = Color(0xFFEA4335),
                    containerBorderColor = Color(0xFFEA4335).copy(alpha = 0.5f),
                    iconContent = {
                        Text(
                            text = "G",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = Color(0xFFEA4335)
                        )
                    },
                    onClick = {
                        coroutineScope.launch {
                            isLoading = true
                            val success = authManager.loginWithGoogle(
                                emailInput = "laptoprazif@gmail.com",
                                displayName = "Pengguna Google"
                            )
                            isLoading = false
                            if (success) {
                                tts.speak("Log masuk dengan akaun Google berjaya!")
                                onDismiss()
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 2. Log Masuk Akaun Nasadef (1-Ketik Rasmi & Akses VIP)
                OAuthOptionCard(
                    title = "Log Masuk Akaun Nasadef",
                    subtitle = "Akses eksklusif ahli portal nasadef.com.my",
                    badgeText = "VIP RASMI",
                    iconColor = Color(0xFF00E5FF),
                    containerBorderColor = Color(0xFF00E5FF).copy(alpha = 0.6f),
                    iconContent = {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    onClick = {
                        coroutineScope.launch {
                            isLoading = true
                            val success = authManager.loginWithNasadef(
                                memberIdOrEmail = "member_nasadef@nasadef.com.my",
                                displayName = "Ahli Rasmi Nasadef"
                            )
                            isLoading = false
                            if (success) {
                                tts.speak("Selamat kembali ahli Nasadef. Mod VIP diaktifkan!")
                                onDismiss()
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 3. Log Masuk Akaun Microsoft (1-Ketik Pantas)
                OAuthOptionCard(
                    title = "Teruskan dengan Microsoft",
                    subtitle = "Outlook, Hotmail & akaun kerja Office 365",
                    badgeText = "MICROSOFT",
                    iconColor = Color(0xFF00A4EF),
                    containerBorderColor = Color(0xFF00A4EF).copy(alpha = 0.5f),
                    iconContent = {
                        MicrosoftLogoGrid()
                    },
                    onClick = {
                        coroutineScope.launch {
                            isLoading = true
                            val success = authManager.loginWithMicrosoft(
                                emailInput = "user@outlook.com",
                                displayName = "Pengguna Microsoft"
                            )
                            isLoading = false
                            if (success) {
                                tts.speak("Log masuk dengan akaun Microsoft berjaya!")
                                onDismiss()
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 4. Masuk Terus Sebagai Tetamu (Khas Bukan Pembaca / Akses Pantas Suara)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, Color(0xFF22C55E).copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                        .background(Color(0xFF22C55E).copy(alpha = 0.12f))
                        .clickable {
                            coroutineScope.launch {
                                isLoading = true
                                val success = authManager.loginAsGuest("Pengguna Suara")
                                isLoading = false
                                if (success) {
                                    tts.speak("Log masuk pantas suara berjaya!")
                                    onDismiss()
                                }
                            }
                        }
                        .padding(12.dp),
                    color = Color.Transparent
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF22C55E)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Akses Segera Suara (Tetamu)",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF22C55E)
                                )
                                Text(
                                    text = "1-ketik tanpa sebarang kata laluan",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Butang Pilihan Emel Tradisional (Boleh Kembang/Tutup)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showEmailForm = !showEmailForm }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (showEmailForm) "Sembunyikan log masuk emel" else "Atau gunakan emel & kata laluan tempatan",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (showEmailForm) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                AnimatedVisibility(visible = showEmailForm) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        TabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Tab(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0; localError = null },
                                text = { Text("Log Masuk", fontWeight = FontWeight.Bold) }
                            )
                            Tab(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1; localError = null },
                                text = { Text("Daftar Baru", fontWeight = FontWeight.Bold) }
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (selectedTab == 1) {
                            OutlinedTextField(
                                value = username,
                                onValueChange = { username = it },
                                label = { Text("Nama Pengguna") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_username_field"),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Alamat Emel") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_email_field"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Kata Laluan") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_password_field"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    isLoading = true
                                    val success = if (selectedTab == 0) {
                                        authManager.login(email, password)
                                    } else {
                                        authManager.signUp(username, email, password)
                                    }
                                    isLoading = false
                                    if (success) {
                                        onDismiss()
                                    } else {
                                        localError = authManager.authError.value ?: "Gagal memproses permohonan."
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_submit_button"),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            } else {
                                Text(if (selectedTab == 0) "Log Masuk" else "Daftar Akaun")
                            }
                        }
                    }
                }

                if (localError != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = localError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )
}

@Composable
private fun OAuthOptionCard(
    title: String,
    subtitle: String,
    badgeText: String,
    iconColor: Color,
    containerBorderColor: Color,
    iconContent: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, containerBorderColor, RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .clickable(onClick = onClick)
            .padding(12.dp),
        color = Color.Transparent
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    iconContent()
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(iconColor.copy(alpha = 0.18f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badgeText,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = iconColor,
                    fontSize = 9.sp
                )
            }
        }
    }
}

@Composable
private fun MicrosoftLogoGrid() {
    Column(
        modifier = Modifier.size(16.dp),
        verticalArrangement = Arrangement.spacedBy(1.5.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(1.5.dp)) {
            Box(modifier = Modifier.size(7.dp).background(Color(0xFFF25022))) // Red
            Box(modifier = Modifier.size(7.dp).background(Color(0xFF7FBA00))) // Green
        }
        Row(horizontalArrangement = Arrangement.spacedBy(1.5.dp)) {
            Box(modifier = Modifier.size(7.dp).background(Color(0xFF00A4EF))) // Blue
            Box(modifier = Modifier.size(7.dp).background(Color(0xFFFFB900))) // Yellow
        }
    }
}
