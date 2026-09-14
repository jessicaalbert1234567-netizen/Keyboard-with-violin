package com.example.audio

class PianoSoundPack(private val engine: SoundEngine) : KeySoundPack {
    override fun playForKey(key: String) {
        val note = SoundEngine.letterToNoteIndex(key)
        engine.playPianoNote(note)
    }
}
