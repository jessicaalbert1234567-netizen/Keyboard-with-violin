package com.example.audio

import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

object AudioSynthesizer {
    const val SAMPLE_RATE = 44100

    /**
     * Synthesizes a rich grand piano note with hammer strike attack and warm sustaining resonance.
     * Engineered with crossfaded loop boundaries so it can sustain continuously without pops.
     */
    fun createPianoPcm(frequency: Float, durationSec: Float = 0.75f): ShortArray {
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val pcm = ShortArray(numSamples)
        val crossfadeLen = 0.035f

        for (i in 0 until numSamples) {
            val t = i.toFloat() / SAMPLE_RATE
            val phase = 2.0 * PI * frequency * t

            // Harmonics with acoustic decay ratios: fundamental + 2nd + 3rd + 4th + detuned string chorus
            val h1 = sin(phase)
            val h2 = 0.55 * sin(2.0 * phase)
            val h3 = 0.28 * sin(3.0 * phase)
            val h4 = 0.14 * sin(4.0 * phase)
            // Multi-string chorus effect (slightly detuned secondary string)
            val chorus = 0.22 * sin(2.0 * PI * (frequency + 0.6) * t)

            val tone = (h1 + h2 + h3 + h4 + chorus) / 2.0

            // Initial hammer strike pop in first 18ms
            val hammerPop = if (t < 0.018f) (1.0 - (t.toDouble() / 0.018)) * 0.35 else 0.0
            val attack = if (t < 0.02f) (t.toDouble() / 0.02) else 1.0

            // Natural piano acoustic decay
            val decay = exp(-t.toDouble() * 1.6)

            // Loop crossfade envelope at boundaries to allow infinite smooth sustain
            val loopWindow = when {
                t < crossfadeLen -> (t.toDouble() / crossfadeLen)
                t > durationSec - crossfadeLen -> ((durationSec - t).toDouble() / crossfadeLen)
                else -> 1.0
            }

            val sample = ((tone * attack * decay + hammerPop) * loopWindow * 0.75 * Short.MAX_VALUE).toInt()
            pcm[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return pcm
    }

    /**
     * Synthesizes an expressive bowed Violin note with realistic vibrato, bow friction, and wood resonance.
     * Engineered with crossfaded loop boundaries so it sustains smoothly until another key is pressed.
     */
    fun createViolinPcm(frequency: Float, durationSec: Float = 0.85f): ShortArray {
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val pcm = ShortArray(numSamples)
        val crossfadeLen = 0.04f

        // Classic violin vibrato: ~5.6 Hz rate, 1.8% pitch modulation depth
        val vibratoRate = 5.6
        val vibratoDepth = 0.018

        for (i in 0 until numSamples) {
            val t = i.toFloat() / SAMPLE_RATE

            // Vibrato frequency modulation
            val instantFreq = frequency * (1.0 + vibratoDepth * sin(2.0 * PI * vibratoRate * t))
            val phase = 2.0 * PI * instantFreq * t

            // Bowed string harmonic series (Helmholtz motion gives rich 1/n harmonic spectra)
            val h1 = sin(phase)
            val h2 = 0.65 * sin(2.0 * phase)
            val h3 = 0.45 * sin(3.0 * phase)
            val h4 = 0.30 * sin(4.0 * phase)
            val h5 = 0.20 * sin(5.0 * phase)
            val h6 = 0.12 * sin(6.0 * phase)

            // Violin wood body formant boost (~2600 Hz resonance)
            val woodResonance = 0.16 * sin(2.0 * PI * 2600.0 * t) * exp(-((instantFreq - 650.0) / 450.0).let { it * it })

            // Subtle bow-hair friction texture
            val bowTexture = (Math.random() * 2.0 - 1.0) * 0.035

            val rawWave = (h1 + h2 + h3 + h4 + h5 + h6 + woodResonance + bowTexture) / 2.7

            // Gentle bow onset attack in first 40ms
            val bowAttack = if (t < 0.04f) (t.toDouble() / 0.04) else 1.0

            // Loop crossfade envelope at edges
            val loopWindow = when {
                t < crossfadeLen -> (t.toDouble() / crossfadeLen)
                t > durationSec - crossfadeLen -> ((durationSec - t).toDouble() / crossfadeLen)
                else -> 1.0
            }

            val sample = (rawWave * bowAttack * loopWindow * 0.75 * Short.MAX_VALUE).toInt()
            pcm[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return pcm
    }

    fun createMechanicalClickPcm(variant: Int = 0): ShortArray {
        val durationSec = 0.06f
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val pcm = ShortArray(numSamples)
        val baseFreq = if (variant == 0) 1200f else 1800f
        for (i in 0 until numSamples) {
            val t = i.toFloat() / SAMPLE_RATE
            val noise = (Math.random() * 2.0 - 1.0) * exp(-t * 140.0)
            val tone = sin(2.0 * PI * baseFreq * t) * exp(-t * 80.0)
            val sample = ((noise * 0.7 + tone * 0.5) * Short.MAX_VALUE).toInt()
            pcm[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return pcm
    }

    fun createTypewriterPcm(isEnter: Boolean = false): ShortArray {
        val durationSec = if (isEnter) 0.22f else 0.05f
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val pcm = ShortArray(numSamples)
        if (isEnter) {
            val bellFreq = 2400f
            for (i in 0 until numSamples) {
                val t = i.toFloat() / SAMPLE_RATE
                val bell = sin(2.0 * PI * bellFreq * t) * exp(-t * 14.0)
                val sample = (bell * 0.7 * Short.MAX_VALUE).toInt()
                pcm[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
        } else {
            for (i in 0 until numSamples) {
                val t = i.toFloat() / SAMPLE_RATE
                val slug = (Math.random() * 2.0 - 1.0) * exp(-t * 160.0)
                val metallic = sin(2.0 * PI * 850f * t) * exp(-t * 100.0)
                val sample = ((slug * 0.6 + metallic * 0.4) * Short.MAX_VALUE).toInt()
                pcm[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
        }
        return pcm
    }

    fun createSoftTapPcm(): ShortArray {
        val durationSec = 0.035f
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val pcm = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toFloat() / SAMPLE_RATE
            val thud = sin(2.0 * PI * 180f * t) * exp(-t * 110.0)
            val sample = (thud * 0.6 * Short.MAX_VALUE).toInt()
            pcm[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return pcm
    }

    fun createBubblePopPcm(): ShortArray {
        val durationSec = 0.09f
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val pcm = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toFloat() / SAMPLE_RATE
            val freq = 400f + t * 7000f
            val pop = sin(2.0 * PI * freq * t) * exp(-t * 32.0)
            val sample = (pop * 0.7 * Short.MAX_VALUE).toInt()
            pcm[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return pcm
    }
}
