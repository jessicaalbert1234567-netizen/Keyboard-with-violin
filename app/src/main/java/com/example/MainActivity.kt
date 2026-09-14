package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.data.settings.KeyboardSettings
import com.example.data.settings.SettingsRepository
import com.example.ui.MainAppScreen
import com.example.ui.OnboardingScreen
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val settingsRepo = SettingsRepository.getInstance(this)

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val settings by settingsRepo.settingsFlow.collectAsState(initial = KeyboardSettings())
                    val coroutineScope = rememberCoroutineScope()

                    if (!settings.isOnboardingCompleted) {
                        OnboardingScreen(
                            onCompleteOnboarding = {
                                coroutineScope.launch {
                                    settingsRepo.setOnboardingCompleted(true)
                                }
                            }
                        )
                    } else {
                        MainAppScreen(
                            settings = settings,
                            onUpdateSettings = { newSettings ->
                                coroutineScope.launch {
                                    settingsRepo.updateSettings(newSettings)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
