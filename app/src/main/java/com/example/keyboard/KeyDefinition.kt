package com.example.keyboard

enum class KeyType {
    CHARACTER,
    SHIFT,
    BACKSPACE,
    ENTER,
    SPACE,
    SYMBOL_SWITCH,
    LETTER_SWITCH,
    MORE_SYMBOLS_SWITCH,
    EMOJI_SWITCH,
    LANGUAGE_SWITCH,
    SETTINGS_SWITCH,
    CLIPBOARD_SWITCH,
    COMMA,
    PERIOD
}

data class KeyDefinition(
    val label: String,
    val subLabel: String? = null,
    val output: String = label,
    val type: KeyType = KeyType.CHARACTER,
    val weight: Float = 1.0f,
    val isToggleActive: Boolean = false
)
