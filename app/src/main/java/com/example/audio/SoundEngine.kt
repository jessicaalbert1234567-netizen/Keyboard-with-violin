package com.example.audio

import android.content.Context
import android.media.AudioManager
import com.example.data.settings.KeyboardSettings
import com.example.data.settings.SoundPackType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.pow

class SoundEngine(private val context: Context) {
    private var pcmPlayer: PcmAudioPlayer? = null
    var settings: KeyboardSettings = KeyboardSettings()

    // 29 distinct notes: 0..25 for English letters A-Z, 26 for SPACE, 27 for ENTER, 28 for BACKSPACE
    private val totalMusicalNotes = 29
    private val pianoPcms = Array(totalMusicalNotes) { ShortArray(0) }
    private val violinPcms = Array(totalMusicalNotes) { ShortArray(0) }

    private val mechanicalPcms = mutableListOf<ShortArray>()
    private var typewriterSlugPcm = ShortArray(0)
    private var typewriterBellPcm = ShortArray(0)
    private var softTapPcm = ShortArray(0)
    private var bubblePcm = ShortArray(0)

    // Sound packs
    val pianoPack = PianoSoundPack(this)
    val violinPack = ViolinSoundPack(this)
    val mechanicalPack = MechanicalSoundPack(this)
    val typewriterPack = TypewriterSoundPack(this)
    val softTapPack = SoftTapSoundPack(this)
    val bubblePack = BubbleSoundPack(this)
    val nonePack = NoneSoundPack()

    init {
        pcmPlayer = PcmAudioPlayer(context)
        // Synthesize all musical & mechanical PCM buffers in background coroutine
        CoroutineScope(Dispatchers.Default).launch {
            synthesizePcmBuffers()
        }
    }

    private fun synthesizePcmBuffers() {
        // Musical note frequencies for all 26 English letters (A-Z) spanning G3 (196 Hz) to G#5 (830.6 Hz)
        // plus 3 dedicated harmonic notes for Space, Enter, Backspace
        val baseG3 = 196.0f
        for (i in 0 until 26) {
            // Equal temperament semitone steps
            val freq = (baseG3 * 2.0.pow(i / 12.0)).toFloat()
            pianoPcms[i] = AudioSynthesizer.createPianoPcm(freq)
            violinPcms[i] = AudioSynthesizer.createViolinPcm(freq)
        }
        // Note 26: SPACE (Deep warm C3 = 130.81 Hz)
        pianoPcms[26] = AudioSynthesizer.createPianoPcm(130.81f)
        violinPcms[26] = AudioSynthesizer.createViolinPcm(130.81f)

        // Note 27: ENTER (Bright triumphant C5 = 523.25 Hz)
        pianoPcms[27] = AudioSynthesizer.createPianoPcm(523.25f)
        violinPcms[27] = AudioSynthesizer.createViolinPcm(523.25f)

        // Note 28: BACKSPACE (Warm minor cadence E3 = 164.81 Hz)
        pianoPcms[28] = AudioSynthesizer.createPianoPcm(164.81f)
        violinPcms[28] = AudioSynthesizer.createViolinPcm(164.81f)

        // Mechanical keyboard clicks
        mechanicalPcms.clear()
        for (i in 0..1) {
            mechanicalPcms.add(AudioSynthesizer.createMechanicalClickPcm(i))
        }

        // Typewriter clicks & Bell
        typewriterSlugPcm = AudioSynthesizer.createTypewriterPcm(false)
        typewriterBellPcm = AudioSynthesizer.createTypewriterPcm(true)

        // Soft Tap
        softTapPcm = AudioSynthesizer.createSoftTapPcm()

        // Bubble pop
        bubblePcm = AudioSynthesizer.createBubblePopPcm()
    }

    fun playPianoNote(noteIndex: Int) {
        if (!settings.soundEnabled) {
            pcmPlayer?.stopSustain()
            return
        }
        val safeIndex = noteIndex.coerceIn(0, totalMusicalNotes - 1)
        val pcm = pianoPcms.getOrNull(safeIndex)
        if (pcm != null && pcm.isNotEmpty()) {
            pcmPlayer?.playSustained(pcm, settings.soundVolume)
        } else {
            pcmPlayer?.playSystemClick(settings.soundVolume)
        }
    }

    fun playViolinNote(noteIndex: Int) {
        if (!settings.soundEnabled) {
            pcmPlayer?.stopSustain()
            return
        }
        val safeIndex = noteIndex.coerceIn(0, totalMusicalNotes - 1)
        val pcm = violinPcms.getOrNull(safeIndex)
        if (pcm != null && pcm.isNotEmpty()) {
            pcmPlayer?.playSustained(pcm, settings.soundVolume)
        } else {
            pcmPlayer?.playSystemClick(settings.soundVolume)
        }
    }

    fun playMechanicalClick() {
        pcmPlayer?.stopSustain()
        if (!settings.soundEnabled) return
        val pcm = mechanicalPcms.randomOrNull()
        if (pcm != null && pcm.isNotEmpty()) {
            pcmPlayer?.playPcm(pcm, settings.soundVolume)
        } else {
            pcmPlayer?.playSystemClick(settings.soundVolume)
        }
    }

    fun playTypewriter(isEnter: Boolean) {
        pcmPlayer?.stopSustain()
        if (!settings.soundEnabled) return
        val pcm = if (isEnter) typewriterBellPcm else typewriterSlugPcm
        if (pcm.isNotEmpty()) {
            pcmPlayer?.playPcm(pcm, settings.soundVolume)
        } else {
            pcmPlayer?.playSystemClick(
                settings.soundVolume,
                if (isEnter) AudioManager.FX_KEYPRESS_RETURN else AudioManager.FX_KEYPRESS_STANDARD
            )
        }
    }

    fun playSoftTap() {
        pcmPlayer?.stopSustain()
        if (!settings.soundEnabled) return
        if (softTapPcm.isNotEmpty()) {
            pcmPlayer?.playPcm(softTapPcm, settings.soundVolume)
        } else {
            pcmPlayer?.playSystemClick(settings.soundVolume)
        }
    }

    fun playBubble() {
        pcmPlayer?.stopSustain()
        if (!settings.soundEnabled) return
        if (bubblePcm.isNotEmpty()) {
            pcmPlayer?.playPcm(bubblePcm, settings.soundVolume)
        } else {
            pcmPlayer?.playSystemClick(settings.soundVolume)
        }
    }

    fun playForKey(key: String) {
        if (!settings.soundEnabled || settings.soundPack == SoundPackType.NONE) {
            pcmPlayer?.stopSustain()
            return
        }
        val pack: KeySoundPack = when (settings.soundPack) {
            SoundPackType.PIANO -> pianoPack
            SoundPackType.VIOLIN -> violinPack
            SoundPackType.MECHANICAL -> mechanicalPack
            SoundPackType.TYPEWRITER -> typewriterPack
            SoundPackType.SOFT_TAP -> softTapPack
            SoundPackType.BUBBLE -> bubblePack
            SoundPackType.NONE -> nonePack
        }
        pack.playForKey(key)
    }

    fun stopSustain() {
        pcmPlayer?.stopSustain()
    }

    fun release() {
        pcmPlayer?.release()
        pcmPlayer = null
    }

    companion object {
        @Volatile
        private var instance: SoundEngine? = null

        fun getInstance(context: Context): SoundEngine {
            return instance ?: synchronized(this) {
                instance ?: SoundEngine(context.applicationContext).also { instance = it }
            }
        }

        /**
         * Maps every single English letter (A-Z, uppercase or lowercase) to an individual distinct note (0..25),
         * and special keys (Space=26, Enter=27, Backspace=28).
         */
        fun letterToNoteIndex(key: String): Int {
            if (key.isEmpty()) return 0
            val trimmed = key.trim()
            if (trimmed.equals("SPACE", ignoreCase = true) || key == " ") return 26
            if (trimmed.equals("ENTER", ignoreCase = true) || key == "\n") return 27
            if (trimmed.equals("BACKSPACE", ignoreCase = true) || trimmed.equals("DEL", ignoreCase = true)) return 28

            val firstChar = trimmed.first()
            val upper = firstChar.uppercaseChar()
            return when (upper) {
                in 'A'..'Z' -> upper - 'A' // 0..25: strictly separate for each English letter
                in '0'..'9' -> ((upper - '0') * 2) % 26
                else -> (key.hashCode() and 0x7FFFFFFF) % 26
            }
        }
    }
}
