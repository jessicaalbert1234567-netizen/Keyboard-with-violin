package com.example.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.settings.KeyboardSettings

@Composable
fun PreferencesScreen(
    settings: KeyboardSettings,
    onUpdateSettings: (KeyboardSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Keyboard Preferences",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        // Dimensions Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "Layout & Sizing", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                // Keyboard Height
                Text(text = "Keyboard Height: ${settings.keyboardHeightDp} dp", fontSize = 13.sp)
                Slider(
                    value = settings.keyboardHeightDp.toFloat(),
                    onValueChange = { onUpdateSettings(settings.copy(keyboardHeightDp = it.toInt())) },
                    valueRange = 200f..350f
                )

                // Key Corner Radius
                Text(text = "Key Corner Rounding: ${settings.keyCornerRadiusDp} dp", fontSize = 13.sp)
                Slider(
                    value = settings.keyCornerRadiusDp.toFloat(),
                    onValueChange = { onUpdateSettings(settings.copy(keyCornerRadiusDp = it.toInt())) },
                    valueRange = 0f..24f
                )

                // Key Spacing
                Text(text = "Key Gap Spacing: ${settings.keySpacingDp} dp", fontSize = 13.sp)
                Slider(
                    value = settings.keySpacingDp.toFloat(),
                    onValueChange = { onUpdateSettings(settings.copy(keySpacingDp = it.toInt())) },
                    valueRange = 1f..10f
                )

                // Font Size
                Text(text = "Key Label Font Size: ${settings.fontSizeSp} sp", fontSize = 13.sp)
                Slider(
                    value = settings.fontSizeSp.toFloat(),
                    onValueChange = { onUpdateSettings(settings.copy(fontSizeSp = it.toInt())) },
                    valueRange = 14f..28f
                )
            }
        }

        // Behavior & Features Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(text = "Features & Feedback", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                // Number Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Dedicated Number Row", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("Display 1-0 numbers above the letters", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = settings.showNumberRow,
                        onCheckedChange = { onUpdateSettings(settings.copy(showNumberRow = it)) },
                        modifier = Modifier.testTag("switch_number_row")
                    )
                }

                // Vibration / Haptics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Haptic Feedback on Press", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("Vibrate gently when keys are pressed", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = settings.vibrationEnabled,
                        onCheckedChange = { onUpdateSettings(settings.copy(vibrationEnabled = it)) },
                        modifier = Modifier.testTag("switch_vibration")
                    )
                }

                // Suggestions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Word Suggestions & Autocomplete", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("Show offline predictions above keyboard", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = settings.suggestionsEnabled,
                        onCheckedChange = { onUpdateSettings(settings.copy(suggestionsEnabled = it)) },
                        modifier = Modifier.testTag("switch_suggestions")
                    )
                }

                // Clipboard History
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Clipboard History", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("Keep recent copied texts for instant paste", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = settings.clipboardHistoryEnabled,
                        onCheckedChange = { onUpdateSettings(settings.copy(clipboardHistoryEnabled = it)) },
                        modifier = Modifier.testTag("switch_clipboard")
                    )
                }
            }
        }
    }
}
