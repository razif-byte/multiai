package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.MainChatScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ChatViewModel

class MainActivity : ComponentActivity() {

    private val chatViewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val darkThemeMode by chatViewModel.darkThemeMode.collectAsStateWithLifecycle()
            val systemDark = isSystemInDarkTheme()

            val isDark = when (darkThemeMode) {
                1 -> true
                2 -> false
                else -> systemDark
            }

            MyApplicationTheme(darkTheme = isDark, dynamicColor = false) {
                MainChatScreen(viewModel = chatViewModel)
            }
        }
    }
}
