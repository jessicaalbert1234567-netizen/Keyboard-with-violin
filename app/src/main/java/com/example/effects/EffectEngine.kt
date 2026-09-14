package com.example.effects

import androidx.compose.ui.graphics.Color
import com.example.data.settings.EffectType
import com.example.data.settings.KeyboardSettings
import kotlin.random.Random

class EffectEngine {
    val particlePool = ParticlePool(maxParticles = 300)

    var settings: KeyboardSettings = KeyboardSettings()
    val random = Random.Default

    val fireEffect = FireEffect(this)
    val waterEffect = WaterEffect(this)
    val chocolateEffect = ChocolateEffect(this)
    val electricEffect = ElectricEffect(this)
    val sparkEffect = SparkEffect(this)
    val smokeEffect = SmokeEffect(this)
    val magicEffect = MagicEffect(this)
    val confettiEffect = ConfettiEffect(this)
    val noneEffect = NoneEffect()

    fun getEffect(type: EffectType): KeyEffect {
        return when (type) {
            EffectType.FIRE -> fireEffect
            EffectType.WATER -> waterEffect
            EffectType.DARK_CHOCOLATE -> chocolateEffect
            EffectType.ELECTRIC -> electricEffect
            EffectType.SPARK -> sparkEffect
            EffectType.SMOKE -> smokeEffect
            EffectType.MAGIC -> magicEffect
            EffectType.CONFETTI -> confettiEffect
            EffectType.NONE -> noneEffect
        }
    }

    fun triggerKeyEffect(x: Float, y: Float, keyWidth: Float, keyHeight: Float) {
        if (!settings.effectsEnabled || settings.effectType == EffectType.NONE) return
        getEffect(settings.effectType).trigger(x, y, keyWidth, keyHeight)
    }

    fun getScaledParticleCount(baseCount: Int): Int {
        var count = (baseCount * settings.effectIntensity).toInt().coerceAtLeast(2)
        if (settings.performanceMode) {
            count = (count * 0.5f).toInt().coerceAtLeast(2)
        }
        return count
    }

    fun getDuration(): Float {
        return (settings.animationDurationMs.toFloat()).coerceIn(120f, 600f)
    }
}
