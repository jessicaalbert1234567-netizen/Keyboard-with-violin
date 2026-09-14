package com.example.effects

import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin

class SparkEffect(private val engine: EffectEngine) : KeyEffect {
    private val sparkColors = listOf(
        Color(0xFFFFEB3B), // Bright Yellow
        Color(0xFFFFC107), // Amber Gold
        Color(0xFFFF5722), // Orange Spark
        Color(0xFFFFFFFF), // Pure White Core
        Color(0xFFFF9800)  // Gold
    )

    override fun trigger(x: Float, y: Float, keyWidth: Float, keyHeight: Float) {
        val count = engine.getScaledParticleCount(15)
        val duration = engine.getDuration()

        for (i in 0 until count) {
            val p = engine.particlePool.obtain() ?: break
            val angle = engine.random.nextFloat() * 2f * Math.PI.toFloat()
            val speed = (100f + engine.random.nextFloat() * 200f) * engine.settings.effectIntensity

            p.x = x
            p.y = y
            p.vx = cos(angle) * speed
            p.vy = sin(angle) * speed
            p.ax = -p.vx * 1.5f // friction deceleration
            p.ay = 120f // mild spark fall
            p.initialSize = (4f + engine.random.nextFloat() * 8f) * engine.settings.effectSize
            p.size = p.initialSize
            p.color = sparkColors[engine.random.nextInt(sparkColors.size)]
            p.alpha = engine.settings.effectOpacity
            p.maxLife = duration * (0.5f + engine.random.nextFloat() * 0.5f)
            p.shapeType = 1 // 4-point star spark
            p.rotation = engine.random.nextFloat() * 360f
            p.rotationSpeed = (engine.random.nextFloat() - 0.5f) * 360f
        }
    }
}
