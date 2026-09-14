package com.example.effects

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import kotlinx.coroutines.isActive

@Composable
fun EffectOverlay(
    engine: EffectEngine,
    modifier: Modifier = Modifier
) {
    val frameTick = remember { mutableLongStateOf(0L) }

    LaunchedEffect(engine) {
        var lastTime = withFrameMillis { it }
        while (isActive) {
            val currentTime = withFrameMillis { it }
            val deltaMs = (currentTime - lastTime).coerceIn(1L, 40L).toFloat()
            lastTime = currentTime
            engine.particlePool.updateAll(deltaMs)
            frameTick.longValue = currentTime
        }
    }

    // Read frameTick to trigger recomposition during active animation
    val currentFrame = frameTick.longValue
    val activeParticles = remember(currentFrame) { engine.particlePool.getAllActive() }

    if (activeParticles.isNotEmpty()) {
        Canvas(modifier = modifier.fillMaxSize()) {
            for (p in activeParticles) {
                if (!p.active || p.alpha <= 0.01f) continue
                drawParticle(p)
            }
        }
    }
}

private fun DrawScope.drawParticle(p: Particle) {
    val colorWithAlpha = p.color.copy(alpha = p.alpha.coerceIn(0f, 1f))
    when (p.shapeType) {
        0 -> { // Circle (Fire, Smoke)
            drawCircle(
                color = colorWithAlpha,
                radius = p.size,
                center = Offset(p.x, p.y)
            )
        }
        1 -> { // 4-Point Star Spark (Spark, Magic, Electric)
            rotate(degrees = p.rotation, pivot = Offset(p.x, p.y)) {
                val s = p.size
                val path = Path().apply {
                    moveTo(p.x, p.y - s * 1.5f)
                    lineTo(p.x + s * 0.4f, p.y - s * 0.4f)
                    lineTo(p.x + s * 1.5f, p.y)
                    lineTo(p.x + s * 0.4f, p.y + s * 0.4f)
                    lineTo(p.x, p.y + s * 1.5f)
                    lineTo(p.x - s * 0.4f, p.y + s * 0.4f)
                    lineTo(p.x - s * 1.5f, p.y)
                    lineTo(p.x - s * 0.4f, p.y - s * 0.4f)
                    close()
                }
                drawPath(path, color = colorWithAlpha)
            }
        }
        2 -> { // Water / Chocolate Droplet Oval
            drawOval(
                color = colorWithAlpha,
                topLeft = Offset(p.x - p.size * 0.7f, p.y - p.size),
                size = Size(p.size * 1.4f, p.size * 2f)
            )
        }
        3 -> { // Confetti Ribbon / Rectangle
            rotate(degrees = p.rotation, pivot = Offset(p.x, p.y)) {
                drawRect(
                    color = colorWithAlpha,
                    topLeft = Offset(p.x - p.size * 0.8f, p.y - p.size * 0.5f),
                    size = Size(p.size * 1.6f, p.size)
                )
            }
        }
        4 -> { // Expanding Ripple Ring
            val rippleRadius = p.size * (1f + (p.age / p.maxLife) * 2f)
            drawCircle(
                color = colorWithAlpha,
                radius = rippleRadius,
                center = Offset(p.x, p.y),
                style = Stroke(width = (4f * (1f - p.age / p.maxLife)).coerceAtLeast(1f))
            )
        }
    }
}
