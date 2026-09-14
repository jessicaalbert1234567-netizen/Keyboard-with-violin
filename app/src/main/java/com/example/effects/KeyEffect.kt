package com.example.effects

interface KeyEffect {
    fun trigger(
        x: Float,
        y: Float,
        keyWidth: Float,
        keyHeight: Float
    )
}
