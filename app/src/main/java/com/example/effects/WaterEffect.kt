package com.example.effects

import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin

class WaterEffect(private val engine: EffectEngine) : KeyEffect {
    private val waterColors = listOf(
        Color(0xFF00E5FF), // Cyan
        Color(0xFF2979FF), // Bright Blue
        Color(0xFF80D8FF), // Light Sky Blue
        Color(0xFFE0F7FA), // Frosted Water White
        Color(0xFF00B0FF)  // Deep Azure
    )

    override fun trigger(x: Float, y: Float, keyWidth: Float, keyHeight: Float) {
        val count = engine.getScaledParticleCount(18)
        val duration = engine.getDuration()

        // 1. Water ripple burst
        val ripple = engine.particlePool.obtain()
        if (ripple != null) {
            ripple.x = x
            ripple.y = y
            ripple.vx = 0f
            ripple.vy = 0f
            ripple.ax = 0f
            ripple.ay = 0f
            ripple.initialSize = keyWidth * 0.4f * engine.settings.effectSize
            ripple.size = ripple.initialSize
            ripple.color = Color(0x9900E5FF)
            ripple.alpha = 0.8f * engine.settings.effectOpacity
            ripple.maxLife = duration * 0.8f
            ripple.shapeType = 4 // ripple arc
        }

        // 2. Radial droplet splash with gravity
        for (i in 0 until count) {
            val p = engine.particlePool.obtain() ?: break
            val angle = (engine.random.nextFloat() * 2f * Math.PI.toFloat())
            val speed = (80f + engine.random.nextFloat() * 160f) * engine.settings.effectIntensity

            p.x = x + (engine.random.nextFloat() - 0.5f) * keyWidth * 0.3f
            p.y = y + (engine.random.nextFloat() - 0.5f) * keyHeight * 0.3f
            p.vx = cos(angle) * speed
            p.vy = sin(angle) * speed - 40f // initial upward pop
            p.ax = 0f
            p.ay = 280f // gravity pulling water droplets downward
            p.initialSize = (6f + engine.random.nextFloat() * 8f) * engine.settings.effectSize
            p.size = p.initialSize
            p.color = waterColors[engine.random.nextInt(waterColors.size)]
            p.alpha = engine.settings.effectOpacity
            p.maxLife = duration * (0.7f + engine.random.nextFloat() * 0.5f)
            p.shapeType = 2 // droplet oval
        }
    }
}
