package com.example.effects

import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin

class ConfettiEffect(private val engine: EffectEngine) : KeyEffect {
    private val confettiColors = listOf(
        Color(0xFFFF1744), // Crimson Red
        Color(0xFFFF9100), // Vibrant Orange
        Color(0xFFFFEA00), // Sunshine Yellow
        Color(0xFF00E676), // Bright Green
        Color(0xFF00B0FF), // Sky Blue
        Color(0xFFD500F9)  // Neon Purple
    )

    override fun trigger(x: Float, y: Float, keyWidth: Float, keyHeight: Float) {
        val count = engine.getScaledParticleCount(18)
        val duration = engine.getDuration() * 1.1f

        for (i in 0 until count) {
            val p = engine.particlePool.obtain() ?: break
            val angle = (engine.random.nextFloat() * Math.PI.toFloat()) + Math.PI.toFloat() // upwards pop
            val speed = (100f + engine.random.nextFloat() * 200f) * engine.settings.effectIntensity

            p.x = x + (engine.random.nextFloat() - 0.5f) * keyWidth * 0.4f
            p.y = y
            p.vx = cos(angle) * speed
            p.vy = sin(angle) * speed - 80f
            p.ax = (engine.random.nextFloat() - 0.5f) * 80f // flutter sway
            p.ay = 300f // gravity
            p.initialSize = (7f + engine.random.nextFloat() * 9f) * engine.settings.effectSize
            p.size = p.initialSize
            p.color = confettiColors[engine.random.nextInt(confettiColors.size)]
            p.alpha = engine.settings.effectOpacity
            p.maxLife = duration
            p.shapeType = 3 // rectangle confetti
            p.rotation = engine.random.nextFloat() * 360f
            p.rotationSpeed = (engine.random.nextFloat() - 0.5f) * 720f
        }
    }
}
