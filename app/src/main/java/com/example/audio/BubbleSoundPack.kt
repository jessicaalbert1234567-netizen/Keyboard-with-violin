package com.example.audio

class BubbleSoundPack(private val engine: SoundEngine) : KeySoundPack {
    override fun playForKey(key: String) {
        engine.playBubble()
    }
}
