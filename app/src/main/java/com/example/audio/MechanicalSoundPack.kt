package com.example.audio

class MechanicalSoundPack(private val engine: SoundEngine) : KeySoundPack {
    override fun playForKey(key: String) {
        engine.playMechanicalClick()
    }
}
