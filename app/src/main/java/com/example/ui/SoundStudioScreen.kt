package com.example.ui

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundEngine
import com.example.data.settings.KeyboardSettings
import com.example.data.settings.SoundPackType

@Composable
fun SoundStudioScreen(
    settings: KeyboardSettings,
    onUpdateSettings: (KeyboardSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val soundEngine = SoundEngine.getInstance(context)

    val soundPacks = listOf(
        SoundPackType.PIANO to "🎹 Grand Piano",
        SoundPackType.VIOLIN to "🎻 Violin Ensemble",
        SoundPackType.MECHANICAL to "⌨️ Mechanical",
        SoundPackType.TYPEWRITER to "📜 Typewriter",
        SoundPackType.SOFT_TAP to "👆 Soft Tap",
        SoundPackType.BUBBLE to "🫧 Bubble Pop",
        SoundPackType.NONE to "🔇 Mute"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Master Sound Switch Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Keyboard Sound", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        text = "Plays individual notes & sounds on each key",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = settings.soundEnabled,
                    onCheckedChange = { onUpdateSettings(settings.copy(soundEnabled = it)) },
                    modifier = Modifier.testTag("sound_enabled_switch")
                )
            }
        }

        // Sound Pack Selector
        Text(
            text = "Select Sound Pack",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.height(230.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(soundPacks) { (type, label) ->
                val isSelected = settings.soundPack == type
                Surface(
                    modifier = Modifier
                        .height(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            onUpdateSettings(settings.copy(soundPack = type))
                            soundEngine.settings = settings.copy(soundPack = type, soundEnabled = true)
                            soundEngine.playForKey("a")
                        }
                        .testTag("sound_pack_${type.name}"),
                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = label,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Volume Control
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Key Sound Volume: ${(settings.soundVolume * 100).toInt()}%", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Slider(
                    value = settings.soundVolume,
                    onValueChange = {
                        onUpdateSettings(settings.copy(soundVolume = it))
                        soundEngine.settings = settings.copy(soundVolume = it)
                    },
                    valueRange = 0f..1f
                )
            }
        }

        // Interactive Instrument Note Tester
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "Melodic Notes Preview", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(
                    text = "Tap letters to preview Piano & Violin notes (sustains until next note):",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                val sampleLetters = listOf("A", "C", "E", "G", "J", "M", "Q", "Z")
                Text(text = "🎹 Grand Piano Scale:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    sampleLetters.forEach { letter ->
                        Button(
                            onClick = {
                                soundEngine.settings = settings.copy(soundEnabled = true)
                                soundEngine.pianoPack.playForKey(letter)
                            },
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                        ) {
                            Text(letter, fontSize = 12.sp)
                        }
                    }
                }

                Text(text = "🎻 Violin Ensemble Scale:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    sampleLetters.forEach { letter ->
                        FilledTonalButton(
                            onClick = {
                                soundEngine.settings = settings.copy(soundEnabled = true)
                                soundEngine.violinPack.playForKey(letter)
                            },
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                        ) {
                            Text(letter, fontSize = 12.sp)
                        }
                    }
                }

                OutlinedButton(
                    onClick = { soundEngine.stopSustain() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("⏹️ Stop Sound Sustain")
                }
            }
        }
    }
}
