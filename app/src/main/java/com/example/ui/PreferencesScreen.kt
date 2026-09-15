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

        // Keyboard Appearance Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "Keyboard Appearance", fontWeight = FontWeight.Bold, fontSize = 16.sp)

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

                // Key Border Size
                Text(text = "Key Border Size: ${settings.keyBorderSizePercent}%", fontSize = 13.sp)
                Slider(
                    value = settings.keyBorderSizePercent.toFloat(),
                    onValueChange = { onUpdateSettings(settings.copy(keyBorderSizePercent = it.toInt())) },
                    valueRange = 0f..100f
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

        // Suggestions & Corrections Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(text = "Suggestions & Correction", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                // Suggestions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Word Suggestions", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("Show predictive suggestions strip above keyboard", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = settings.suggestionsEnabled,
                        onCheckedChange = { onUpdateSettings(settings.copy(suggestionsEnabled = it)) },
                        modifier = Modifier.testTag("switch_suggestions")
                    )
                }

                // Auto Correction
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Auto Correction", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("Automatically correct misspelled words on space", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = settings.autoCorrectionEnabled,
                        onCheckedChange = { onUpdateSettings(settings.copy(autoCorrectionEnabled = it)) },
                        modifier = Modifier.testTag("switch_autocorrect")
                    )
                }

                // Spell Correction
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Intelligent Spell Check", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("Damerau-Levenshtein edit-distance spell engine", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = settings.spellCorrectionEnabled,
                        onCheckedChange = { onUpdateSettings(settings.copy(spellCorrectionEnabled = it)) },
                        modifier = Modifier.testTag("switch_spell_check")
                    )
                }

                // Personal Dictionary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Personal Dictionary", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("Learn custom words and prioritize them in suggestions", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = settings.personalDictionaryEnabled,
                        onCheckedChange = { onUpdateSettings(settings.copy(personalDictionaryEnabled = it)) },
                        modifier = Modifier.testTag("switch_personal_dict")
                    )
                }

                // Next-word Prediction
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Next-word Prediction", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("Predict likely next words and phrases from context", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = settings.nextWordPredictionEnabled,
                        onCheckedChange = { onUpdateSettings(settings.copy(nextWordPredictionEnabled = it)) },
                        modifier = Modifier.testTag("switch_next_word")
                    )
                }

                // Phonetic Input Engine
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Phonetic Input Engine", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("Convert transliterated typing into native script", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = settings.phoneticInputEnabled,
                        onCheckedChange = { onUpdateSettings(settings.copy(phoneticInputEnabled = it)) },
                        modifier = Modifier.testTag("switch_phonetic")
                    )
                }
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

                // Auto Correction
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Auto Spell Correction", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("Automatically fix common typos on space", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = settings.autoCorrectionEnabled,
                        onCheckedChange = { onUpdateSettings(settings.copy(autoCorrectionEnabled = it)) },
                        modifier = Modifier.testTag("switch_autocorrect")
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
