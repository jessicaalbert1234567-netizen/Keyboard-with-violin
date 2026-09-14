package com.example.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.settings.EffectType
import com.example.data.settings.KeyboardSettings
import com.example.effects.EffectEngine
import com.example.effects.EffectOverlay

@Composable
fun EffectsStudioScreen(
    settings: KeyboardSettings,
    onUpdateSettings: (KeyboardSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    val previewEffectEngine = remember { EffectEngine().apply { this.settings = settings } }
    previewEffectEngine.settings = settings

    val effectTypes = listOf<Pair<EffectType, String>>(
        EffectType.NONE to "None",
        EffectType.FIRE to "🔥 Fire",
        EffectType.WATER to "💧 Water",
        EffectType.DARK_CHOCOLATE to "🍫 Chocolate",
        EffectType.ELECTRIC to "⚡ Electric",
        EffectType.SPARK to "✨ Spark",
        EffectType.SMOKE to "💨 Smoke",
        EffectType.MAGIC to "🪄 Magic",
        EffectType.CONFETTI to "🎉 Confetti"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Interactive Playground Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2E))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        // Trigger effect at center
                        previewEffectEngine.triggerKeyEffect(250f, 200f, 100f, 70f)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tap here to preview '${settings.effectType.displayName}'",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
                EffectOverlay(
                    engine = previewEffectEngine,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Effect Selector Grid
        Text(
            text = "Select Effect",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.height(180.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(effectTypes) { (type, label) ->
                val isSelected = settings.effectType == type
                Surface(
                    modifier = Modifier
                        .height(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            onUpdateSettings(settings.copy(effectType = type))
                            previewEffectEngine.triggerKeyEffect(250f, 200f, 100f, 70f)
                        }
                        .testTag("effect_${type.name}"),
                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = label,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Sliders Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Fine-Tune Parameters",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                // Intensity
                Text(text = "Intensity: ${(settings.effectIntensity * 100).toInt()}%", fontSize = 12.sp)
                Slider(
                    value = settings.effectIntensity,
                    onValueChange = { onUpdateSettings(settings.copy(effectIntensity = it)) },
                    valueRange = 0.3f..2.5f
                )

                // Particle Size
                Text(text = "Particle Size: ${(settings.effectSize * 100).toInt()}%", fontSize = 12.sp)
                Slider(
                    value = settings.effectSize,
                    onValueChange = { onUpdateSettings(settings.copy(effectSize = it)) },
                    valueRange = 0.5f..2.5f
                )

                // Duration
                Text(text = "Duration: ${settings.animationDurationMs} ms", fontSize = 12.sp)
                Slider(
                    value = settings.animationDurationMs.toFloat(),
                    onValueChange = { onUpdateSettings(settings.copy(animationDurationMs = it.toLong())) },
                    valueRange = 150f..1000f
                )

                // Opacity
                Text(text = "Opacity: ${(settings.effectOpacity * 100).toInt()}%", fontSize = 12.sp)
                Slider(
                    value = settings.effectOpacity,
                    onValueChange = { onUpdateSettings(settings.copy(effectOpacity = it)) },
                    valueRange = 0.2f..1.0f
                )
            }
        }
    }
}
