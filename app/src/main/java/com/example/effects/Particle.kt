package com.example.effects

import androidx.compose.ui.graphics.Color

data class Particle(
    var active: Boolean = false,
    var x: Float = 0f,
    var y: Float = 0f,
    var vx: Float = 0f,
    var vy: Float = 0f,
    var ax: Float = 0f,
    var ay: Float = 0f,
    var size: Float = 8f,
    var initialSize: Float = 8f,
    var color: Color = Color.White,
    var secondaryColor: Color = Color.Yellow,
    var alpha: Float = 1f,
    var maxLife: Float = 300f,
    var age: Float = 0f,
    var rotation: Float = 0f,
    var rotationSpeed: Float = 0f,
    var shapeType: Int = 0 // 0 = circle, 1 = star/spark, 2 = droplet/oval, 3 = rect/confetti, 4 = arc
) {
    fun update(deltaMs: Float) {
        if (!active) return
        age += deltaMs
        if (age >= maxLife) {
            active = false
            return
        }
        val progress = age / maxLife
        alpha = (1f - progress).coerceIn(0f, 1f)
        vx += ax * (deltaMs / 1000f)
        vy += ay * (deltaMs / 1000f)
        x += vx * (deltaMs / 1000f)
        y += vy * (deltaMs / 1000f)
        rotation += rotationSpeed * (deltaMs / 1000f)
        size = (initialSize * (1f - progress * 0.5f)).coerceAtLeast(1f)
    }
}

class ParticlePool(maxParticles: Int = 250) {
    private val pool = Array(maxParticles) { Particle() }

    fun obtain(): Particle? {
        for (i in pool.indices) {
            if (!pool[i].active) {
                pool[i].active = true
                pool[i].age = 0f
                return pool[i]
            }
        }
        return null
    }

    fun getAllActive(): List<Particle> {
        val list = ArrayList<Particle>(pool.size)
        for (p in pool) {
            if (p.active) list.add(p)
        }
        return list
    }

    fun updateAll(deltaMs: Float) {
        for (p in pool) {
            if (p.active) {
                p.update(deltaMs)
            }
        }
    }

    fun clear() {
        for (p in pool) {
            p.active = false
        }
    }
}
