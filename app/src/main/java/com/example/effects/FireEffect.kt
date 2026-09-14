package com.example.effects

import androidx.compose.ui.graphics.Color

class FireEffect(private val engine: EffectEngine) : KeyEffect {
    private val fireColors = listOf(
        Color(0xFFFF3D00), // Red-Orange
        Color(0xFFFF9100), // Orange
        Color(0xFFFFD600), // Bright Yellow
        Color(0xFFFF6D00), // Deep Orange
        Color(0xFFFFF176)  // Bright Gold
    )

    override fun trigger(x: Float, y: Float, keyWidth: Float, keyHeight: Float) {
        val count = engine.getScaledParticleCount(16)
        val duration = engine.getDuration()

        for (i in 0 until count) {
            val p = engine.particlePool.obtain() ?: break
            val spawnX = x + (engine.random.nextFloat() - 0.5f) * keyWidth * 0.7f
            val spawnY = y + (engine.random.nextFloat() - 0.5f) * keyHeight * 0.4f

            p.x = spawnX
            p.y = spawnY
            // Upward flame movement with slight lateral swirl
            p.vx = (engine.random.nextFloat() - 0.5f) * 80f * engine.settings.effectIntensity
            p.vy = -(140f + engine.random.nextFloat() * 180f) * engine.settings.effectIntensity
            p.ax = (engine.random.nextFloat() - 0.5f) * 40f
            p.ay = -60f // buoyancy lifting upward
            p.initialSize = (10f + engine.random.nextFloat() * 14f) * engine.settings.effectSize
            p.size = p.initialSize
            p.color = fireColors[engine.random.nextInt(fireColors.size)]
            p.alpha = engine.settings.effectOpacity
            p.maxLife = duration * (0.6f + engine.random.nextFloat() * 0.5f)
            p.shapeType = 0 // circle
            p.rotation = 0f
            p.rotationSpeed = 0f
        }
    }
}
