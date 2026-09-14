package com.example.effects

import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin

class MagicEffect(private val engine: EffectEngine) : KeyEffect {
    private val magicColors = listOf(
        Color(0xFFE040FB), // Magenta
        Color(0xFF7C4DFF), // Deep Violet
        Color(0xFF00E5FF), // Cyan Star
        Color(0xFFFF4081), // Pink Glow
        Color(0xFFFFD700)  // Golden Glitter
    )

    override fun trigger(x: Float, y: Float, keyWidth: Float, keyHeight: Float) {
        val count = engine.getScaledParticleCount(15)
        val duration = engine.getDuration()

        for (i in 0 until count) {
            val p = engine.particlePool.obtain() ?: break
            val angle = engine.random.nextFloat() * 2f * Math.PI.toFloat()
            val speed = (70f + engine.random.nextFloat() * 150f) * engine.settings.effectIntensity

            p.x = x
            p.y = y
            p.vx = cos(angle) * speed
            p.vy = sin(angle) * speed - 50f
            p.ax = 0f
            p.ay = -30f // floats gently upwards
            p.initialSize = (5f + engine.random.nextFloat() * 8f) * engine.settings.effectSize
            p.size = p.initialSize
            p.color = magicColors[engine.random.nextInt(magicColors.size)]
            p.alpha = engine.settings.effectOpacity
            p.maxLife = duration * (0.7f + engine.random.nextFloat() * 0.4f)
            p.shapeType = 1 // star
            p.rotation = engine.random.nextFloat() * 360f
            p.rotationSpeed = (engine.random.nextFloat() - 0.5f) * 360f
        }
    }
}
