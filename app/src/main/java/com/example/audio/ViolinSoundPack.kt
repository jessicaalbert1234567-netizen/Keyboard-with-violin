package com.example.audio

class ViolinSoundPack(private val engine: SoundEngine) : KeySoundPack {
    override fun playForKey(key: String) {
        val note = SoundEngine.letterToNoteIndex(key)
        engine.playViolinNote(note)
    }
}
