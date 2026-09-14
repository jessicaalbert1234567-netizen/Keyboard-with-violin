package com.example.audio

class TypewriterSoundPack(private val engine: SoundEngine) : KeySoundPack {
    override fun playForKey(key: String) {
        val isEnter = key.equals("ENTER", ignoreCase = true) || key == "\n"
        engine.playTypewriter(isEnter)
    }
}
