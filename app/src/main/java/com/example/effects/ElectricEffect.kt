package com.example.effects

import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin

class ElectricEffect(private val engine: EffectEngine) : KeyEffect {
    private val electricColors = listOf(
        Color(0xFF00FFFF), // Electric Cyan
        Color(0xFF80D8FF), // Neon Blue
        Color(0xFFE040FB), // Electric Purple Arc
        Color(0xFFFFFFFF), // Lightning White
        Color(0xFF64FFDA)  // Fluorescent Mint
    )

    override fun trigger(x: Float, y: Float, keyWidth: Float, keyHeight: Float) {
        val count = engine.getScaledParticleCount(16)
        val duration = engine.getDuration() * 0.75f // Electric is snappy and energetic

        for (i in 0 until count) {
            val p = engine.particlePool.obtain() ?: break
            val angle = engine.random.nextFloat() * 2f * Math.PI.toFloat()
            val speed = (120f + engine.random.nextFloat() * 220f) * engine.settings.effectIntensity

            p.x = x
            p.y = y
            p.vx = cos(angle) * speed
            p.vy = sin(angle) * speed
            // Rapid chaotic zig-zag jitter acceleration
            p.ax = (engine.random.nextFloat() - 0.5f) * 600f
            p.ay = (engine.random.nextFloat() - 0.5f) * 600f
            p.initialSize = (5f + engine.random.nextFloat() * 9f) * engine.settings.effectSize
            p.size = p.initialSize
            p.color = electricColors[engine.random.nextInt(electricColors.size)]
            p.alpha = engine.settings.effectOpacity
            p.maxLife = duration * (0.5f + engine.random.nextFloat() * 0.5f)
            p.shapeType = 1 // star spark / zig-zag flash
            p.rotation = engine.random.nextFloat() * 360f
            p.rotationSpeed = (engine.random.nextFloat() - 0.5f) * 720f
        }
    }
}
