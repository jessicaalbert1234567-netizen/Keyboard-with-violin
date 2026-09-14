package com.example.audio

class SoftTapSoundPack(private val engine: SoundEngine) : KeySoundPack {
    override fun playForKey(key: String) {
        engine.playSoftTap()
    }
}
