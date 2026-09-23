package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.security.MessageDigest

/**
 * Komponen menjana visual QR Matrix resolusi tinggi secara dinamik
 * tanpa memerlukan perpustakaan pihak ketiga yang berat.
 */
@Composable
fun QrCodeCanvas(
    content: String,
    modifier: Modifier = Modifier,
    darkColor: Color = Color(0xFF0F172A),
    lightColor: Color = Color.White
) {
    val matrix = remember(content) {
        generateQrMatrix(content, 25)
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(lightColor)
            .padding(12.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val size = matrix.size
            val cellSize = this.size.width / size

            for (r in 0 until size) {
                for (c in 0 until size) {
                    if (matrix[r][c]) {
                        drawRoundRect(
                            color = darkColor,
                            topLeft = Offset(c * cellSize, r * cellSize),
                            size = Size(cellSize * 1.02f, cellSize * 1.02f),
                            cornerRadius = CornerRadius(cellSize * 0.18f, cellSize * 0.18f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Menjana grid matriks QR standard (25x25) berasaskan hashing deterministik & pola penanda QR.
 */
private fun generateQrMatrix(data: String, size: Int): Array<BooleanArray> {
    val grid = Array(size) { BooleanArray(size) { false } }

    fun drawFinder(startX: Int, startY: Int) {
        for (r in 0 until 7) {
            for (c in 0 until 7) {
                val isOuter = r == 0 || r == 6 || c == 0 || c == 6
                val isInner = r in 2..4 && c in 2..4
                if (startX + r < size && startY + c < size) {
                    grid[startX + r][startY + c] = isOuter || isInner
                }
            }
        }
    }

    // Tiga penanda posisi sudut (Finder Patterns)
    drawFinder(0, 0)
    drawFinder(0, size - 7)
    drawFinder(size - 7, 0)

    // Garisan penentuan masa (Timing Patterns)
    for (i in 7 until size - 7) {
        grid[6][i] = (i % 2 == 0)
        grid[i][6] = (i % 2 == 0)
    }

    // Corak penjajaran kecil (Alignment pattern di sudut bawah kanan)
    val alignX = size - 7
    val alignY = size - 7
    for (r in -2..2) {
        for (c in -2..2) {
            val isBorder = kotlin.math.abs(r) == 2 || kotlin.math.abs(c) == 2
            val isCenter = r == 0 && c == 0
            val gx = alignX + r
            val gy = alignY + c
            if (gx in 0 until size && gy in 0 until size) {
                grid[gx][gy] = isBorder || isCenter
            }
        }
    }

    // Menjana data bit berasaskan hash kandungan
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(data.toByteArray())

    var bitIndex = 0
    for (r in 0 until size) {
        for (c in 0 until size) {
            val inTopLeftFinder = r < 8 && c < 8
            val inTopRightFinder = r < 8 && c >= size - 8
            val inBottomLeftFinder = r >= size - 8 && c < 8
            val inAlignment = r in (size - 9)..(size - 5) && c in (size - 9)..(size - 5)
            val inTiming = r == 6 || c == 6

            if (!inTopLeftFinder && !inTopRightFinder && !inBottomLeftFinder && !inAlignment && !inTiming) {
                val byteVal = digest[bitIndex % digest.size].toInt()
                val bitVal = (byteVal shr (bitIndex % 8)) and 1
                grid[r][c] = (bitVal == 1)
                bitIndex++
            }
        }
    }

    return grid
}
