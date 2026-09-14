package com.example.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "fx_keyboard_settings")

class SettingsRepository(private val context: Context) {

    private object PreferencesKeys {
        val KEYBOARD_HEIGHT = intPreferencesKey("keyboard_height")
        val KEY_SPACING = intPreferencesKey("key_spacing")
        val FONT_SIZE = intPreferencesKey("font_size")
        val KEY_OPACITY = floatPreferencesKey("key_opacity")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val VIBRATION_DURATION = longPreferencesKey("vibration_duration")
        val AUTO_CAPITALIZATION = booleanPreferencesKey("auto_capitalization")
        val DOUBLE_SPACE_PERIOD = booleanPreferencesKey("double_space_period")
        val SHOW_NUMBER_ROW = booleanPreferencesKey("show_number_row")

        val EFFECTS_ENABLED = booleanPreferencesKey("effects_enabled")
        val EFFECT_TYPE = stringPreferencesKey("effect_type")
        val EFFECT_INTENSITY = floatPreferencesKey("effect_intensity")
        val PARTICLE_COUNT = intPreferencesKey("particle_count")
        val ANIMATION_DURATION = longPreferencesKey("animation_duration")
        val EFFECT_SIZE = floatPreferencesKey("effect_size")
        val EFFECT_OPACITY = floatPreferencesKey("effect_opacity")
        val PERFORMANCE_MODE = booleanPreferencesKey("performance_mode")

        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val SOUND_PACK = stringPreferencesKey("sound_pack")
        val SOUND_VOLUME = floatPreferencesKey("sound_volume")
        val PIANO_MODE = booleanPreferencesKey("piano_mode")

        val THEME_ID = stringPreferencesKey("theme_id")
        val KEY_CORNER_RADIUS = intPreferencesKey("key_corner_radius")

        val CURRENT_LANGUAGE = stringPreferencesKey("current_language")
        val CURRENT_INPUT_MODE = stringPreferencesKey("current_input_mode")

        val SUGGESTIONS_ENABLED = booleanPreferencesKey("suggestions_enabled")
        val AUTO_CORRECTION = booleanPreferencesKey("auto_correction")
        val NEXT_WORD_PREDICTION = booleanPreferencesKey("next_word_prediction")

        val CLIPBOARD_HISTORY_ENABLED = booleanPreferencesKey("clipboard_history_enabled")
        val MAX_CLIPBOARD_ITEMS = intPreferencesKey("max_clipboard_items")

        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }

    val settingsFlow: Flow<KeyboardSettings> = context.dataStore.data.map { prefs ->
        val effectTypeName = prefs[PreferencesKeys.EFFECT_TYPE] ?: EffectType.FIRE.name
        val effectType = try {
            EffectType.valueOf(effectTypeName)
        } catch (_: Exception) {
            EffectType.FIRE
        }

        val soundPackName = prefs[PreferencesKeys.SOUND_PACK] ?: SoundPackType.PIANO.name
        val soundPack = try {
            SoundPackType.valueOf(soundPackName)
        } catch (_: Exception) {
            SoundPackType.PIANO
        }

        val inputModeName = prefs[PreferencesKeys.CURRENT_INPUT_MODE] ?: KeyboardInputMode.ENGLISH.name
        val inputMode = try {
            KeyboardInputMode.valueOf(inputModeName)
        } catch (_: Exception) {
            KeyboardInputMode.ENGLISH
        }

        KeyboardSettings(
            keyboardHeightDp = prefs[PreferencesKeys.KEYBOARD_HEIGHT] ?: 270,
            keySpacingDp = prefs[PreferencesKeys.KEY_SPACING] ?: 4,
            fontSizeSp = prefs[PreferencesKeys.FONT_SIZE] ?: 18,
            keyOpacity = prefs[PreferencesKeys.KEY_OPACITY] ?: 0.95f,
            vibrationEnabled = prefs[PreferencesKeys.VIBRATION_ENABLED] ?: true,
            vibrationDurationMs = prefs[PreferencesKeys.VIBRATION_DURATION] ?: 18L,
            autoCapitalization = prefs[PreferencesKeys.AUTO_CAPITALIZATION] ?: true,
            doubleSpacePeriod = prefs[PreferencesKeys.DOUBLE_SPACE_PERIOD] ?: true,
            showNumberRow = prefs[PreferencesKeys.SHOW_NUMBER_ROW] ?: true,

            effectsEnabled = prefs[PreferencesKeys.EFFECTS_ENABLED] ?: true,
            effectType = effectType,
            effectIntensity = prefs[PreferencesKeys.EFFECT_INTENSITY] ?: 1.0f,
            particleCount = prefs[PreferencesKeys.PARTICLE_COUNT] ?: 14,
            animationDurationMs = prefs[PreferencesKeys.ANIMATION_DURATION] ?: 320L,
            effectSize = prefs[PreferencesKeys.EFFECT_SIZE] ?: 1.0f,
            effectOpacity = prefs[PreferencesKeys.EFFECT_OPACITY] ?: 0.9f,
            performanceMode = prefs[PreferencesKeys.PERFORMANCE_MODE] ?: false,

            soundEnabled = prefs[PreferencesKeys.SOUND_ENABLED] ?: true,
            soundPack = soundPack,
            soundVolume = prefs[PreferencesKeys.SOUND_VOLUME] ?: 0.75f,
            pianoMode = prefs[PreferencesKeys.PIANO_MODE] ?: true,

            themeId = prefs[PreferencesKeys.THEME_ID] ?: "dark",
            keyCornerRadiusDp = prefs[PreferencesKeys.KEY_CORNER_RADIUS] ?: 8,

            currentLanguageId = prefs[PreferencesKeys.CURRENT_LANGUAGE] ?: "en",
            currentInputMode = inputMode,

            suggestionsEnabled = prefs[PreferencesKeys.SUGGESTIONS_ENABLED] ?: true,
            autoCorrectionEnabled = prefs[PreferencesKeys.AUTO_CORRECTION] ?: true,
            nextWordPredictionEnabled = prefs[PreferencesKeys.NEXT_WORD_PREDICTION] ?: true,

            clipboardHistoryEnabled = prefs[PreferencesKeys.CLIPBOARD_HISTORY_ENABLED] ?: true,
            maxClipboardItems = prefs[PreferencesKeys.MAX_CLIPBOARD_ITEMS] ?: 25,

            isOnboardingCompleted = prefs[PreferencesKeys.ONBOARDING_COMPLETED] ?: false
        )
    }

    suspend fun updateKeyboardHeight(height: Int) = context.dataStore.edit { it[PreferencesKeys.KEYBOARD_HEIGHT] = height }
    suspend fun updateKeySpacing(spacing: Int) = context.dataStore.edit { it[PreferencesKeys.KEY_SPACING] = spacing }
    suspend fun updateFontSize(size: Int) = context.dataStore.edit { it[PreferencesKeys.FONT_SIZE] = size }
    suspend fun updateKeyOpacity(opacity: Float) = context.dataStore.edit { it[PreferencesKeys.KEY_OPACITY] = opacity }
    suspend fun setVibrationEnabled(enabled: Boolean) = context.dataStore.edit { it[PreferencesKeys.VIBRATION_ENABLED] = enabled }
    suspend fun setAutoCapitalization(enabled: Boolean) = context.dataStore.edit { it[PreferencesKeys.AUTO_CAPITALIZATION] = enabled }
    suspend fun setDoubleSpacePeriod(enabled: Boolean) = context.dataStore.edit { it[PreferencesKeys.DOUBLE_SPACE_PERIOD] = enabled }
    suspend fun setShowNumberRow(enabled: Boolean) = context.dataStore.edit { it[PreferencesKeys.SHOW_NUMBER_ROW] = enabled }

    suspend fun setEffectsEnabled(enabled: Boolean) = context.dataStore.edit { it[PreferencesKeys.EFFECTS_ENABLED] = enabled }
    suspend fun setEffectType(type: EffectType) = context.dataStore.edit { it[PreferencesKeys.EFFECT_TYPE] = type.name }
    suspend fun setEffectIntensity(intensity: Float) = context.dataStore.edit { it[PreferencesKeys.EFFECT_INTENSITY] = intensity }
    suspend fun setParticleCount(count: Int) = context.dataStore.edit { it[PreferencesKeys.PARTICLE_COUNT] = count }
    suspend fun setAnimationDuration(duration: Long) = context.dataStore.edit { it[PreferencesKeys.ANIMATION_DURATION] = duration }
    suspend fun setPerformanceMode(enabled: Boolean) = context.dataStore.edit { it[PreferencesKeys.PERFORMANCE_MODE] = enabled }

    suspend fun setSoundEnabled(enabled: Boolean) = context.dataStore.edit { it[PreferencesKeys.SOUND_ENABLED] = enabled }
    suspend fun setSoundPack(pack: SoundPackType) = context.dataStore.edit { it[PreferencesKeys.SOUND_PACK] = pack.name }
    suspend fun setSoundVolume(volume: Float) = context.dataStore.edit { it[PreferencesKeys.SOUND_VOLUME] = volume }
    suspend fun setPianoMode(enabled: Boolean) = context.dataStore.edit { it[PreferencesKeys.PIANO_MODE] = enabled }

    suspend fun setThemeId(themeId: String) = context.dataStore.edit { it[PreferencesKeys.THEME_ID] = themeId }
    suspend fun setKeyCornerRadius(radius: Int) = context.dataStore.edit { it[PreferencesKeys.KEY_CORNER_RADIUS] = radius }

    suspend fun setCurrentLanguage(langId: String) = context.dataStore.edit { it[PreferencesKeys.CURRENT_LANGUAGE] = langId }
    suspend fun setCurrentInputMode(mode: KeyboardInputMode) = context.dataStore.edit { it[PreferencesKeys.CURRENT_INPUT_MODE] = mode.name }

    suspend fun setSuggestionsEnabled(enabled: Boolean) = context.dataStore.edit { it[PreferencesKeys.SUGGESTIONS_ENABLED] = enabled }
    suspend fun setAutoCorrectionEnabled(enabled: Boolean) = context.dataStore.edit { it[PreferencesKeys.AUTO_CORRECTION] = enabled }
    suspend fun setNextWordPrediction(enabled: Boolean) = context.dataStore.edit { it[PreferencesKeys.NEXT_WORD_PREDICTION] = enabled }

    suspend fun setClipboardHistoryEnabled(enabled: Boolean) = context.dataStore.edit { it[PreferencesKeys.CLIPBOARD_HISTORY_ENABLED] = enabled }
    suspend fun setMaxClipboardItems(max: Int) = context.dataStore.edit { it[PreferencesKeys.MAX_CLIPBOARD_ITEMS] = max }

    suspend fun setOnboardingCompleted(completed: Boolean) = context.dataStore.edit { it[PreferencesKeys.ONBOARDING_COMPLETED] = completed }

    suspend fun updateSettings(s: KeyboardSettings) = context.dataStore.edit { prefs ->
        prefs[PreferencesKeys.KEYBOARD_HEIGHT] = s.keyboardHeightDp
        prefs[PreferencesKeys.KEY_SPACING] = s.keySpacingDp
        prefs[PreferencesKeys.FONT_SIZE] = s.fontSizeSp
        prefs[PreferencesKeys.KEY_OPACITY] = s.keyOpacity
        prefs[PreferencesKeys.VIBRATION_ENABLED] = s.vibrationEnabled
        prefs[PreferencesKeys.VIBRATION_DURATION] = s.vibrationDurationMs
        prefs[PreferencesKeys.AUTO_CAPITALIZATION] = s.autoCapitalization
        prefs[PreferencesKeys.DOUBLE_SPACE_PERIOD] = s.doubleSpacePeriod
        prefs[PreferencesKeys.SHOW_NUMBER_ROW] = s.showNumberRow
        prefs[PreferencesKeys.EFFECTS_ENABLED] = s.effectsEnabled
        prefs[PreferencesKeys.EFFECT_TYPE] = s.effectType.name
        prefs[PreferencesKeys.EFFECT_INTENSITY] = s.effectIntensity
        prefs[PreferencesKeys.PARTICLE_COUNT] = s.particleCount
        prefs[PreferencesKeys.ANIMATION_DURATION] = s.animationDurationMs
        prefs[PreferencesKeys.EFFECT_SIZE] = s.effectSize
        prefs[PreferencesKeys.EFFECT_OPACITY] = s.effectOpacity
        prefs[PreferencesKeys.PERFORMANCE_MODE] = s.performanceMode
        prefs[PreferencesKeys.SOUND_ENABLED] = s.soundEnabled
        prefs[PreferencesKeys.SOUND_PACK] = s.soundPack.name
        prefs[PreferencesKeys.SOUND_VOLUME] = s.soundVolume
        prefs[PreferencesKeys.PIANO_MODE] = s.pianoMode
        prefs[PreferencesKeys.THEME_ID] = s.themeId
        prefs[PreferencesKeys.KEY_CORNER_RADIUS] = s.keyCornerRadiusDp
        prefs[PreferencesKeys.CURRENT_LANGUAGE] = s.currentLanguageId
        prefs[PreferencesKeys.CURRENT_INPUT_MODE] = s.currentInputMode.name
        prefs[PreferencesKeys.SUGGESTIONS_ENABLED] = s.suggestionsEnabled
        prefs[PreferencesKeys.AUTO_CORRECTION] = s.autoCorrectionEnabled
        prefs[PreferencesKeys.NEXT_WORD_PREDICTION] = s.nextWordPredictionEnabled
        prefs[PreferencesKeys.CLIPBOARD_HISTORY_ENABLED] = s.clipboardHistoryEnabled
        prefs[PreferencesKeys.MAX_CLIPBOARD_ITEMS] = s.maxClipboardItems
        prefs[PreferencesKeys.ONBOARDING_COMPLETED] = s.isOnboardingCompleted
    }

    companion object {
        @Volatile
        private var instance: SettingsRepository? = null

        fun getInstance(context: Context): SettingsRepository {
            return instance ?: synchronized(this) {
                instance ?: SettingsRepository(context.applicationContext).also { instance = it }
            }
        }
    }
}
