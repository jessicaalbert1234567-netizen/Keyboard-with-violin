package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log
import java.util.concurrent.Executors

/**
 * Direct PCM Audio Player using Android's native AudioTrack in streaming mode.
 * Completely bypasses MediaCodec, SoundPool, Stagefright, and CCodec to prevent
 * "Failed to query component interface for required system resources: 6" decoder errors.
 *
 * Supports both:
 * - One-shot key clicks (mechanical, typewriter, soft-tap, bubble)
 * - Continuous sustained melodic instruments (Grand Piano & Violin Ensemble) that sustain
 *   until the next key is pressed.
 */
class PcmAudioPlayer(private val context: Context) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private val executor = Executors.newSingleThreadExecutor()

    // One-shot track pool
    private val numTracks = 2
    private val tracks = arrayOfNulls<AudioTrack>(numTracks)
    private var currentTrackIndex = 0
    private var isAudioTrackAvailable = false

    // Sustained instrument track and state
    private var sustainTrack: AudioTrack? = null
    @Volatile private var currentSustainPcm: ShortArray? = null
    @Volatile private var currentSustainVolume: Float = 0.75f
    @Volatile private var isSustaining: Boolean = false
    private var sustainThread: Thread? = null
    private val sustainLock = Any()

    init {
        try {
            val sampleRate = AudioSynthesizer.SAMPLE_RATE
            val minBuf = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(4096)

            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val format = AudioFormat.Builder()
                .setSampleRate(sampleRate)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build()

            // Initialize one-shot tracks
            for (i in 0 until numTracks) {
                tracks[i] = AudioTrack.Builder()
                    .setAudioAttributes(attributes)
                    .setAudioFormat(format)
                    .setBufferSizeInBytes(minBuf * 2)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build().apply {
                        if (state == AudioTrack.STATE_INITIALIZED) {
                            play()
                        }
                    }
            }

            // Initialize sustain track
            sustainTrack = AudioTrack.Builder()
                .setAudioAttributes(attributes)
                .setAudioFormat(format)
                .setBufferSizeInBytes(minBuf * 2)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build().apply {
                    if (state == AudioTrack.STATE_INITIALIZED) {
                        play()
                    }
                }

            isAudioTrackAvailable = true
        } catch (e: Exception) {
            Log.w("PcmAudioPlayer", "AudioTrack stream unavailable on this device, using native sound effect fallback", e)
            isAudioTrackAvailable = false
        }
    }

    /**
     * Plays a continuous sustained instrument note (Piano or Violin) that continues
     * to sustain seamlessly until another key is pressed.
     */
    fun playSustained(pcm: ShortArray, volume: Float) {
        if (pcm.isEmpty()) return
        val clampedVol = volume.coerceIn(0f, 1f)
        if (clampedVol <= 0.01f) {
            stopSustain()
            return
        }

        if (!isAudioTrackAvailable) {
            playSystemClick(clampedVol)
            return
        }

        currentSustainPcm = pcm
        currentSustainVolume = clampedVol
        try {
            sustainTrack?.setVolume(clampedVol)
        } catch (_: Exception) {}

        synchronized(sustainLock) {
            if (!isSustaining) {
                isSustaining = true
                sustainThread = Thread({
                    try {
                        sustainTrack?.play()
                        val chunkSize = 1024
                        while (isSustaining) {
                            val activeBuf = currentSustainPcm ?: break
                            val track = sustainTrack ?: break
                            if (track.state != AudioTrack.STATE_INITIALIZED) break

                            var offset = 0
                            while (isSustaining && offset < activeBuf.size) {
                                // If the user touched another key, switch immediately to the new note!
                                val latestBuf = currentSustainPcm
                                if (latestBuf !== activeBuf) {
                                    break
                                }
                                val count = minOf(chunkSize, activeBuf.size - offset)
                                track.write(activeBuf, offset, count, AudioTrack.WRITE_BLOCKING)
                                offset += count
                            }
                        }
                    } catch (_: Exception) {
                    } finally {
                        try {
                            sustainTrack?.pause()
                            sustainTrack?.flush()
                        } catch (_: Exception) {}
                        isSustaining = false
                    }
                }, "PcmSustainThread").apply {
                    isDaemon = true
                    start()
                }
            }
        }
    }

    /**
     * Stops any ongoing sustained instrument sound.
     */
    fun stopSustain() {
        if (!isSustaining) return
        isSustaining = false
        currentSustainPcm = null
        try {
            sustainTrack?.pause()
            sustainTrack?.flush()
        } catch (_: Exception) {}
    }

    /**
     * Plays a one-shot PCM sound (clicks, pops, taps).
     */
    fun playPcm(pcm: ShortArray, volume: Float) {
        if (pcm.isEmpty()) return
        val clampedVol = volume.coerceIn(0f, 1f)
        if (clampedVol <= 0.01f) return

        if (!isAudioTrackAvailable) {
            playSystemClick(clampedVol)
            return
        }

        executor.execute {
            try {
                val track = synchronized(tracks) {
                    val t = tracks[currentTrackIndex]
                    currentTrackIndex = (currentTrackIndex + 1) % numTracks
                    t
                }

                if (track != null && track.state == AudioTrack.STATE_INITIALIZED) {
                    track.setVolume(clampedVol)
                    track.write(pcm, 0, pcm.size, AudioTrack.WRITE_NON_BLOCKING)
                } else {
                    playSystemClick(clampedVol)
                }
            } catch (_: Exception) {
                playSystemClick(clampedVol)
            }
        }
    }

    fun playSystemClick(volume: Float, fx: Int = AudioManager.FX_KEYPRESS_STANDARD) {
        try {
            audioManager?.playSoundEffect(fx, volume.coerceIn(0f, 1f))
        } catch (_: Exception) {}
    }

    fun release() {
        stopSustain()
        executor.shutdown()
        synchronized(tracks) {
            for (i in 0 until numTracks) {
                try {
                    tracks[i]?.stop()
                    tracks[i]?.release()
                    tracks[i] = null
                } catch (_: Exception) {}
            }
            try {
                sustainTrack?.stop()
                sustainTrack?.release()
                sustainTrack = null
            } catch (_: Exception) {}
            isAudioTrackAvailable = false
        }
    }
}
