package com.example.effects

import androidx.compose.ui.graphics.Color

class SmokeEffect(private val engine: EffectEngine) : KeyEffect {
    private val smokeColors = listOf(
        Color(0xFF90A4AE), // Cool Slate Smoke
        Color(0xFFB0BEC5), // Light Gray Mist
        Color(0xFF78909C), // Deep Ash
        Color(0xFFCFD8DC), // Whisper Smoke
        Color(0xFFECEFF1)  // Vapor White
    )

    override fun trigger(x: Float, y: Float, keyWidth: Float, keyHeight: Float) {
        val count = engine.getScaledParticleCount(10)
        val duration = engine.getDuration() * 1.2f

        for (i in 0 until count) {
            val p = engine.particlePool.obtain() ?: break
            p.x = x + (engine.random.nextFloat() - 0.5f) * keyWidth * 0.4f
            p.y = y
            p.vx = (engine.random.nextFloat() - 0.5f) * 40f
            p.vy = -(60f + engine.random.nextFloat() * 80f) * engine.settings.effectIntensity
            p.ax = (engine.random.nextFloat() - 0.5f) * 20f
            p.ay = -20f // Slow upward drift
            p.initialSize = (14f + engine.random.nextFloat() * 18f) * engine.settings.effectSize
            p.size = p.initialSize
            p.color = smokeColors[engine.random.nextInt(smokeColors.size)]
            p.alpha = (0.4f + engine.random.nextFloat() * 0.3f) * engine.settings.effectOpacity
            p.maxLife = duration
            p.shapeType = 0 // soft puffy circle
            p.rotation = 0f
            p.rotationSpeed = 0f
        }
    }
}
