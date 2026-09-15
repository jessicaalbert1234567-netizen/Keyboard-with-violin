package com.example.data.settings

enum class EffectType(val displayName: String) {
    FIRE("Fire"),
    WATER("Water Splash"),
    DARK_CHOCOLATE("Dark Chocolate"),
    ELECTRIC("Electric Arc"),
    SPARK("Sparks"),
    SMOKE("Mystic Smoke"),
    MAGIC("Magic Dust"),
    CONFETTI("Confetti"),
    NONE("None")
}

enum class SoundPackType(val displayName: String) {
    PIANO("Grand Piano"),
    VIOLIN("Violin Ensemble"),
    MECHANICAL("Mechanical Click"),
    TYPEWRITER("Typewriter"),
    SOFT_TAP("Soft Tap"),
    BUBBLE("Bubble Pop"),
    NONE("None")
}

enum class KeyboardInputMode(val displayName: String) {
    ENGLISH("English QWERTY"),
    NATIVE("Native Script"),
    PHONETIC("Phonetic Transliteration")
}

data class KeyboardSettings(
    // Keyboard Layout & Typing
    val keyboardHeightDp: Int = 270,
    val keySpacingDp: Int = 4,
    val fontSizeSp: Int = 18,
    val keyOpacity: Float = 0.95f,
    val vibrationEnabled: Boolean = true,
    val vibrationDurationMs: Long = 18L,
    val autoCapitalization: Boolean = true,
    val doubleSpacePeriod: Boolean = true,
    val showNumberRow: Boolean = true,

    // Visual Effects
    val effectsEnabled: Boolean = true,
    val effectType: EffectType = EffectType.FIRE,
    val effectIntensity: Float = 1.0f,
    val particleCount: Int = 14,
    val animationDurationMs: Long = 320L,
    val effectSize: Float = 1.0f,
    val effectOpacity: Float = 0.9f,
    val performanceMode: Boolean = false,

    // Audio & Sound
    val soundEnabled: Boolean = true,
    val soundPack: SoundPackType = SoundPackType.PIANO,
    val soundVolume: Float = 0.75f,
    val pianoMode: Boolean = true,

    // Appearance & Theme
    val themeId: String = "dark",
    val keyCornerRadiusDp: Int = 8,
    val keyBorderSizePercent: Int = 0,

    // Languages
    val currentLanguageId: String = "en",
    val currentInputMode: KeyboardInputMode = KeyboardInputMode.ENGLISH,

    // Suggestions & Autocorrect
    val suggestionsEnabled: Boolean = true,
    val autoCorrectionEnabled: Boolean = true,
    val spellCorrectionEnabled: Boolean = true,
    val personalDictionaryEnabled: Boolean = true,
    val nextWordPredictionEnabled: Boolean = true,
    val phoneticInputEnabled: Boolean = true,

    // Clipboard
    val clipboardHistoryEnabled: Boolean = true,
    val maxClipboardItems: Int = 25,

    // Onboarding
    val isOnboardingCompleted: Boolean = false
)
