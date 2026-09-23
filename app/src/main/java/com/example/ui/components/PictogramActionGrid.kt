package com.example.ui.components

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AppTextToSpeech

data class PictogramCard(
    val id: String,
    val icon: ImageVector,
    val emoji: String,
    val title: String,
    val spokenPrompt: String,
    val aiQuery: String,
    val color: Color
)

@Composable
fun PictogramActionGrid(
    tts: AppTextToSpeech,
    onSendPrompt: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        PictogramCard(
            id = "music",
            icon = Icons.Default.MusicNote,
            emoji = "🎵",
            title = "Cipta Lagu",
            spokenPrompt = "Saya sedang meminta AI mencipta sebuah lagu yang indah untuk anda.",
            aiQuery = "Tolong ciptakan sebuah lagu melodi santai yang menenangkan dengan lirik yang indah.",
            color = Color(0xFF06B6D4)
        ),
        PictogramCard(
            id = "talk",
            icon = Icons.Default.RecordVoiceOver,
            emoji = "🗣️",
            title = "Bual Suara",
            spokenPrompt = "Halo kawan! Apa khabar anda hari ini? Mari kita berbual.",
            aiQuery = "Halo AI! Tolong perkenalkan diri anda dan berikan kata-kata semangat yang ringkas untuk hari ini.",
            color = Color(0xFF22C55E)
        ),
        PictogramCard(
            id = "story",
            icon = Icons.Default.Book,
            emoji = "📖",
            title = "Cerita Rakyat",
            spokenPrompt = "Mari dengarkan sebuah cerita rakyat yang menarik.",
            aiQuery = "Tolong ceritakan satu kisah dongeng teladan yang pendek dan menarik untuk didengari.",
            color = Color(0xFFA855F7)
        ),
        PictogramCard(
            id = "weather",
            icon = Icons.Default.WbSunny,
            emoji = "🌤️",
            title = "Cuaca & Hari",
            spokenPrompt = "Memeriksa tip cuaca dan panduan harian anda.",
            aiQuery = "Bagaimanakah tips persediaan cuaca harian dan aktiviti terbaik untuk hari ini?",
            color = Color(0xFFF59E0B)
        ),
        PictogramCard(
            id = "health",
            icon = Icons.Default.Favorite,
            emoji = "🩺",
            title = "Tip Sihat",
            spokenPrompt = "Berikut adalah tip kesihatan ringkas untuk tubuh yang cergas.",
            aiQuery = "Berikan 3 tip kesihatan dan pemakanan mudah yang sangat baik diamalkan setiap hari.",
            color = Color(0xFFEF4444)
        ),
        PictogramCard(
            id = "idea",
            icon = Icons.Default.Lightbulb,
            emoji = "💡",
            title = "Idea Mudah",
            spokenPrompt = "Mencari idea kreatif untuk anda.",
            aiQuery = "Bolehkah anda beri saya beberapa idea santai dan menyeronokkan untuk mengisi masa lapang?",
            color = Color(0xFF3B82F6)
        )
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Akses Pantas Gambar (Sentuh & Dengar)",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        tts.speak("Ketik mana-mana kad bergambar berwarna-warni di bawah. Anda akan mendengar suaranya dan jawapan AI secara automatik tanpa perlu membaca.")
                    },
                color = Color(0xFF00E5FF).copy(alpha = 0.15f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = null,
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Dengar Arahan",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF00E5FF),
                        fontSize = 10.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            items(items) { card ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(74.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(
                            width = 1.5.dp,
                            color = card.color.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable {
                            tts.speak(card.spokenPrompt)
                            onSendPrompt(card.aiQuery)
                        }
                        .testTag("pictogram_${card.id}"),
                    color = card.color.copy(alpha = 0.12f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = card.emoji,
                            fontSize = 24.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = card.title,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
