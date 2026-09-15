package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.util.Log
import com.example.data.settings.KeyboardSettings
import com.example.data.settings.SoundPackType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.pow

class SoundEngine(private val context: Context) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    var settings: KeyboardSettings = KeyboardSettings()

    private val totalMusicalNotes = 29
    private var soundPool: SoundPool? = null

    // Preloaded sound IDs for each sound pack
    private val pianoSoundIds = IntArray(totalMusicalNotes)
    private val violinSoundIds = IntArray(totalMusicalNotes)
    private val mechanicalSoundIds = mutableListOf<Int>()
    private var typewriterSlugId = 0
    private var typewriterBellId = 0
    private var softTapId = 0
    private var bubbleId = 0

    @Volatile
    private var isLoaded = false

    // Sound packs
    val pianoPack = PianoSoundPack(this)
    val violinPack = ViolinSoundPack(this)
    val mechanicalPack = MechanicalSoundPack(this)
    val typewriterPack = TypewriterSoundPack(this)
    val softTapPack = SoftTapSoundPack(this)
    val bubblePack = BubbleSoundPack(this)
    val nonePack = NoneSoundPack()

    init {
        try {
            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(24)
                .setAudioAttributes(attributes)
                .build()
        } catch (e: Exception) {
            Log.e("SoundEngine", "Failed to initialize SoundPool", e)
        }

        CoroutineScope(Dispatchers.IO).launch {
            loadAllSounds()
        }
    }

    private fun loadAllSounds() {
        val pool = soundPool ?: return
        val cacheDir = File(context.cacheDir, "keyboard_sounds").apply { mkdirs() }

        try {
            // Synthesize and load Piano & Violin notes (A-Z, space, enter, backspace)
            val baseG3 = 196.0f
            for (i in 0 until 26) {
                val freq = (baseG3 * 2.0.pow(i / 12.0)).toFloat()
                val pianoPcm = AudioSynthesizer.createPianoPcm(freq, durationSec = 0.5f)
                val violinPcm = AudioSynthesizer.createViolinPcm(freq, durationSec = 0.5f)

                val pianoFile = File(cacheDir, "piano_$i.wav")
                writePcmToWav(pianoPcm, pianoFile)
                pianoSoundIds[i] = pool.load(pianoFile.absolutePath, 1)

                val violinFile = File(cacheDir, "violin_$i.wav")
                writePcmToWav(violinPcm, violinFile)
                violinSoundIds[i] = pool.load(violinFile.absolutePath, 1)
            }

            // Note 26: Space (130.81 Hz)
            val pianoSpace = File(cacheDir, "piano_26.wav")
            writePcmToWav(AudioSynthesizer.createPianoPcm(130.81f, 0.4f), pianoSpace)
            pianoSoundIds[26] = pool.load(pianoSpace.absolutePath, 1)

            val violinSpace = File(cacheDir, "violin_26.wav")
            writePcmToWav(AudioSynthesizer.createViolinPcm(130.81f, 0.4f), violinSpace)
            violinSoundIds[26] = pool.load(violinSpace.absolutePath, 1)

            // Note 27: Enter (523.25 Hz)
            val pianoEnter = File(cacheDir, "piano_27.wav")
            writePcmToWav(AudioSynthesizer.createPianoPcm(523.25f, 0.4f), pianoEnter)
            pianoSoundIds[27] = pool.load(pianoEnter.absolutePath, 1)

            val violinEnter = File(cacheDir, "violin_27.wav")
            writePcmToWav(AudioSynthesizer.createViolinPcm(523.25f, 0.4f), violinEnter)
            violinSoundIds[27] = pool.load(violinEnter.absolutePath, 1)

            // Note 28: Backspace (164.81 Hz)
            val pianoDel = File(cacheDir, "piano_28.wav")
            writePcmToWav(AudioSynthesizer.createPianoPcm(164.81f, 0.35f), pianoDel)
            pianoSoundIds[28] = pool.load(pianoDel.absolutePath, 1)

            val violinDel = File(cacheDir, "violin_28.wav")
            writePcmToWav(AudioSynthesizer.createViolinPcm(164.81f, 0.35f), violinDel)
            violinSoundIds[28] = pool.load(violinDel.absolutePath, 1)

            // Mechanical clicks (2 variants)
            mechanicalSoundIds.clear()
            for (i in 0..1) {
                val mechFile = File(cacheDir, "mech_$i.wav")
                writePcmToWav(AudioSynthesizer.createMechanicalClickPcm(i), mechFile)
                mechanicalSoundIds.add(pool.load(mechFile.absolutePath, 1))
            }

            // Typewriter (slug and bell)
            val typeSlugFile = File(cacheDir, "typewriter_slug.wav")
            writePcmToWav(AudioSynthesizer.createTypewriterPcm(false), typeSlugFile)
            typewriterSlugId = pool.load(typeSlugFile.absolutePath, 1)

            val typeBellFile = File(cacheDir, "typewriter_bell.wav")
            writePcmToWav(AudioSynthesizer.createTypewriterPcm(true), typeBellFile)
            typewriterBellId = pool.load(typeBellFile.absolutePath, 1)

            // Soft tap
            val softTapFile = File(cacheDir, "soft_tap.wav")
            writePcmToWav(AudioSynthesizer.createSoftTapPcm(), softTapFile)
            softTapId = pool.load(softTapFile.absolutePath, 1)

            // Bubble pop
            val bubbleFile = File(cacheDir, "bubble_pop.wav")
            writePcmToWav(AudioSynthesizer.createBubblePopPcm(), bubbleFile)
            bubbleId = pool.load(bubbleFile.absolutePath, 1)

            isLoaded = true
        } catch (e: Exception) {
            Log.e("SoundEngine", "Error preloading sound files into SoundPool", e)
        }
    }

    private fun writePcmToWav(pcm: ShortArray, file: File, sampleRate: Int = AudioSynthesizer.SAMPLE_RATE) {
        val totalAudioLen = (pcm.size * 2).toLong()
        val totalDataLen = totalAudioLen + 36
        val channels = 1
        val byteRate = (16 * sampleRate * channels / 8).toLong()

        val header = ByteArray(44)
        header[0] = 'R'.code.toByte()
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        header[4] = (totalDataLen and 0xffL).toByte()
        header[5] = ((totalDataLen shr 8) and 0xffL).toByte()
        header[6] = ((totalDataLen shr 16) and 0xffL).toByte()
        header[7] = ((totalDataLen shr 24) and 0xffL).toByte()
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte()
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()
        header[16] = 16
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1
        header[21] = 0
        header[22] = channels.toByte()
        header[23] = 0
        header[24] = (sampleRate and 0xff).toByte()
        header[25] = ((sampleRate shr 8) and 0xff).toByte()
        header[26] = ((sampleRate shr 16) and 0xff).toByte()
        header[27] = ((sampleRate shr 24) and 0xff).toByte()
        header[28] = (byteRate and 0xffL).toByte()
        header[29] = ((byteRate shr 8) and 0xffL).toByte()
        header[30] = ((byteRate shr 16) and 0xffL).toByte()
        header[31] = ((byteRate shr 24) and 0xffL).toByte()
        header[32] = (channels * 2).toByte()
        header[33] = 0
        header[34] = 16
        header[35] = 0
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        header[40] = (totalAudioLen and 0xffL).toByte()
        header[41] = ((totalAudioLen shr 8) and 0xffL).toByte()
        header[42] = ((totalAudioLen shr 16) and 0xffL).toByte()
        header[43] = ((totalAudioLen shr 24) and 0xffL).toByte()

        FileOutputStream(file).use { out ->
            out.write(header)
            val byteBuf = ByteBuffer.allocate(pcm.size * 2).order(ByteOrder.LITTLE_ENDIAN)
            for (s in pcm) {
                byteBuf.putShort(s)
            }
            out.write(byteBuf.array())
        }
    }

    private fun playSoundId(soundId: Int, volume: Float) {
        val vol = volume.coerceIn(0f, 1f)
        if (vol <= 0.01f || !settings.soundEnabled) return
        val pool = soundPool
        if (pool != null && soundId > 0) {
            pool.play(soundId, vol, vol, 1, 0, 1.0f)
        } else {
            playSystemClick(vol)
        }
    }

    fun playPianoNote(noteIndex: Int) {
        if (!settings.soundEnabled) return
        val safeIndex = noteIndex.coerceIn(0, totalMusicalNotes - 1)
        val id = pianoSoundIds.getOrNull(safeIndex) ?: 0
        playSoundId(id, settings.soundVolume)
    }

    fun playViolinNote(noteIndex: Int) {
        if (!settings.soundEnabled) return
        val safeIndex = noteIndex.coerceIn(0, totalMusicalNotes - 1)
        val id = violinSoundIds.getOrNull(safeIndex) ?: 0
        playSoundId(id, settings.soundVolume)
    }

    fun playMechanicalClick() {
        if (!settings.soundEnabled) return
        val id = mechanicalSoundIds.randomOrNull() ?: 0
        playSoundId(id, settings.soundVolume)
    }

    fun playTypewriter(isEnter: Boolean) {
        if (!settings.soundEnabled) return
        val id = if (isEnter) typewriterBellId else typewriterSlugId
        playSoundId(id, settings.soundVolume)
    }

    fun playSoftTap() {
        if (!settings.soundEnabled) return
        playSoundId(softTapId, settings.soundVolume)
    }

    fun playBubble() {
        if (!settings.soundEnabled) return
        playSoundId(bubbleId, settings.soundVolume)
    }

    fun playForKey(key: String) {
        if (!settings.soundEnabled || settings.soundPack == SoundPackType.NONE) {
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
        // SoundPool streams naturally manage decay without blocking
    }

    fun playSystemClick(volume: Float, fx: Int = AudioManager.FX_KEYPRESS_STANDARD) {
        try {
            audioManager?.playSoundEffect(fx, volume.coerceIn(0f, 1f))
        } catch (_: Exception) {}
    }

    fun release() {
        try {
            soundPool?.release()
            soundPool = null
        } catch (_: Exception) {}
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
            if (trimmed.equals("BACKSPACE", ignoreCase = true) || trimmed.equals("DEL", ignoreCase = true) || key == "⌫") return 28

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
