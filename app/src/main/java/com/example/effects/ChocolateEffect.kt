package com.example.effects

import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin

class ChocolateEffect(private val engine: EffectEngine) : KeyEffect {
    private val chocolateColors = listOf(
        Color(0xFF3E2723), // Deep melted cocoa
        Color(0xFF4E342E), // Dark rich chocolate
        Color(0xFF5D4037), // Warm silky chocolate
        Color(0xFF6D4C41), // Milk chocolate swirl
        Color(0xFF795548), // Amber cocoa crema
        Color(0xFF2E1A16)  // 90% Pure dark cacao
    )

    override fun trigger(x: Float, y: Float, keyWidth: Float, keyHeight: Float) {
        val count = engine.getScaledParticleCount(14)
        val duration = engine.getDuration()

        for (i in 0 until count) {
            val p = engine.particlePool.obtain() ?: break
            val angle = (engine.random.nextFloat() * Math.PI.toFloat()) + Math.PI.toFloat() // upwards semi-circle
            val speed = (50f + engine.random.nextFloat() * 110f) * engine.settings.effectIntensity

            p.x = x + (engine.random.nextFloat() - 0.5f) * keyWidth * 0.5f
            p.y = y + (engine.random.nextFloat() - 0.5f) * keyHeight * 0.3f
            p.vx = cos(angle) * speed
            p.vy = sin(angle) * speed - 60f // viscous pop
            p.ax = -p.vx * 0.4f // viscous drag
            p.ay = 340f // heavier gravity for melted chocolate
            p.initialSize = (8f + engine.random.nextFloat() * 12f) * engine.settings.effectSize
            p.size = p.initialSize
            p.color = chocolateColors[engine.random.nextInt(chocolateColors.size)]
            p.alpha = engine.settings.effectOpacity
            p.maxLife = duration * (0.8f + engine.random.nextFloat() * 0.4f)
            p.shapeType = 2 // viscous droplet
        }
    }
}
