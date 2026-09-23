package com.example.ui.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast

object VideoPlayerHelper {

    enum class PlayerType(val displayName: String, val iconLabel: String) {
        INTERNAL("Pemain Dalaman", "📱 Internal"),
        MX_PLAYER("MX Player", "⚡ MX Player"),
        VLC("VLC Player", "🧡 VLC Player"),
        SYSTEM_CHOOSER("Pemain Sistem", "🚀 Sistem")
    }

    const val INTRO_VIDEO_URL = "https://www.youtube.com/embed/EvwdsI9G6-o?autoplay=1&playsinline=1&controls=1&rel=0&fs=1"
    const val INTRO_DIRECT_URL = "https://youtu.be/EvwdsI9G6-o?si=ldt9ysqbIqEDisQh"

    const val ENDING_VIDEO_URL = "https://www.youtube.com/embed/Cbjt_34t2O8?autoplay=1&playsinline=1&controls=1&rel=0&fs=1"
    const val ENDING_DIRECT_URL = "https://youtu.be/Cbjt_34t2O8"

    fun isPackageInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun launchMxPlayer(context: Context, videoUrl: String, title: String) {
        val mxAdPackage = "com.mxtech.videoplayer.ad"
        val mxProPackage = "com.mxtech.videoplayer.pro"

        val targetPackage = when {
            isPackageInstalled(context, mxAdPackage) -> mxAdPackage
            isPackageInstalled(context, mxProPackage) -> mxProPackage
            else -> null
        }

        if (targetPackage != null) {
            try {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(Uri.parse(videoUrl), "video/*")
                    setPackage(targetPackage)
                    putExtra("title", title)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (_: Exception) {
                // Cuba buka URL terus
                openUrlInMx(context, targetPackage, videoUrl)
            }
        } else {
            // Beri makluman & tawarkan pautan muat turun MX Player dari Play Store
            Toast.makeText(context, "MX Player tidak dijumpai. Membuka Play Store...", Toast.LENGTH_SHORT).show()
            openPlayStore(context, mxAdPackage)
        }
    }

    private fun openUrlInMx(context: Context, packageName: String, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                setPackage(packageName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal melancarkan MX Player: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun launchVlcPlayer(context: Context, videoUrl: String, title: String) {
        val vlcPackage = "org.videolan.vlc"

        if (isPackageInstalled(context, vlcPackage)) {
            try {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(Uri.parse(videoUrl), "video/*")
                    setPackage(vlcPackage)
                    putExtra("title", title)
                    putExtra("from_start", true)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (_: Exception) {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl)).apply {
                        setPackage(vlcPackage)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Gagal melancarkan VLC Player: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "VLC Player tidak dijumpai. Membuka Play Store...", Toast.LENGTH_SHORT).show()
            openPlayStore(context, vlcPackage)
        }
    }

    fun launchSystemPlayer(context: Context, videoUrl: String, title: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl)).apply {
                setDataAndType(Uri.parse(videoUrl), "video/*")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val chooser = Intent.createChooser(intent, "Main Video Dengan:")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (_: Exception) {
            // Fallback buka URL
            try {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(browserIntent)
            } catch (e: Exception) {
                Toast.makeText(context, "Tidak dapat memainkan video: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun openPlayStore(context: Context, packageName: String) {
        try {
            val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(marketIntent)
        } catch (_: Exception) {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
        }
    }
}
