package com.example.ime

import android.content.Context
import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.MainActivity
import com.example.audio.SoundEngine
import com.example.clipboard.FXClipboardManager
import com.example.data.db.ClipboardEntry
import com.example.data.settings.KeyboardInputMode
import com.example.data.settings.KeyboardSettings
import com.example.data.settings.SettingsRepository
import com.example.effects.EffectEngine
import com.example.keyboard.KeyDefinition
import com.example.keyboard.KeyType
import com.example.keyboard.KeyboardComposable
import com.example.keyboard.LayoutViewMode
import com.example.language.LanguagePack
import com.example.language.LanguagePackManager
import com.example.language.TransliterationEngine
import com.example.suggestions.OfflineDictionarySuggestionEngine
import com.example.theme.ThemeEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FXInputMethodService : InputMethodService(),
    LifecycleOwner,
    ViewModelStoreOwner,
    SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private val vmStore = ViewModelStore()

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry
    override val viewModelStore: ViewModelStore get() = vmStore

    private val serviceScope = CoroutineScope(Dispatchers.Main)
    private lateinit var settingsRepo: SettingsRepository
    private lateinit var soundEngine: SoundEngine
    private lateinit var clipboardManager: FXClipboardManager
    private lateinit var suggestionEngine: OfflineDictionarySuggestionEngine
    private lateinit var packManager: LanguagePackManager
    private val effectEngine = EffectEngine()
    private val transliterationEngine = TransliterationEngine.instance

    private var vibrator: Vibrator? = null
    private var hasVibratorHardware: Boolean = false

    // Typing State
    private var currentLayoutViewMode by mutableStateOf(LayoutViewMode.ALPHA)
    private var isShifted by mutableStateOf(false)
    private var isCapsLock by mutableStateOf(false)
    private var suggestions by mutableStateOf<List<String>>(emptyList())
    private var isPasswordField by mutableStateOf(false)
    private var installedLanguages by mutableStateOf<List<LanguagePack>>(emptyList())

    // Current word composing buffer
    private val composingBuffer = StringBuilder()
    private var previousWord: String? = null

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        settingsRepo = SettingsRepository.getInstance(this)
        soundEngine = SoundEngine.getInstance(this)
        clipboardManager = FXClipboardManager.getInstance(this)
        suggestionEngine = OfflineDictionarySuggestionEngine(this)
        packManager = LanguagePackManager.getInstance(this)

        initVibrator()

        // Sync settings with effectEngine and soundEngine
        serviceScope.launch {
            settingsRepo.settingsFlow.collect { settings ->
                effectEngine.settings = settings
                soundEngine.settings = settings
            }
        }

        // Live observation of installed languages
        serviceScope.launch {
            packManager.installedPacksFlow.collect { packs ->
                installedLanguages = packs
            }
        }
    }

    private fun initVibrator() {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vm?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
        hasVibratorHardware = try {
            vibrator?.hasVibrator() == true
        } catch (_: Exception) {
            false
        }
    }

    override fun onConfigureWindow(win: android.view.Window, isFullscreen: Boolean, isCandidatesOnly: Boolean) {
        super.onConfigureWindow(win, isFullscreen, isCandidatesOnly)
        win.decorView.let { decor ->
            decor.setViewTreeLifecycleOwner(this)
            decor.setViewTreeSavedStateRegistryOwner(this)
            decor.setViewTreeViewModelStoreOwner(this)
        }
    }

    override fun onEvaluateInputViewShown(): Boolean {
        super.onEvaluateInputViewShown()
        return true
    }

    override fun onShowInputRequested(flags: Int, configChange: Boolean): Boolean {
        return true
    }

    override fun onEvaluateFullscreenMode(): Boolean {
        return false
    }

    override fun onWindowShown() {
        super.onWindowShown()
        if (lifecycleRegistry.currentState == Lifecycle.State.INITIALIZED ||
            lifecycleRegistry.currentState == Lifecycle.State.CREATED) {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        }
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onWindowHidden() {
        super.onWindowHidden()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    override fun onCreateInputView(): View {
        window?.window?.decorView?.let { decorView ->
            decorView.setViewTreeLifecycleOwner(this)
            decorView.setViewTreeSavedStateRegistryOwner(this)
            decorView.setViewTreeViewModelStoreOwner(this)
        }

        val composeView = ComposeView(this).apply {
            layoutParams = android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnLifecycleDestroyed(this@FXInputMethodService))
            setViewTreeLifecycleOwner(this@FXInputMethodService)
            setViewTreeSavedStateRegistryOwner(this@FXInputMethodService)
            setViewTreeViewModelStoreOwner(this@FXInputMethodService)

            setContent {
                val settings by settingsRepo.settingsFlow.collectAsState(initial = KeyboardSettings())
                effectEngine.settings = settings
                soundEngine.settings = settings
                val theme = ThemeEngine.getThemeById(settings.themeId)
                val clipboardEntries by clipboardManager.entriesFlow.collectAsState(initial = emptyList())

                KeyboardComposable(
                    settings = settings,
                    theme = theme,
                    effectEngine = effectEngine,
                    layoutMode = currentLayoutViewMode,
                    isShifted = isShifted,
                    isCapsLock = isCapsLock,
                    suggestions = if (isPasswordField) emptyList() else suggestions,
                    clipboardEntries = if (isPasswordField) emptyList() else clipboardEntries,
                    installedLanguages = installedLanguages,
                    onKeyAction = { keyDef, center, size ->
                        handleKeyPress(keyDef, center, size, settings)
                    },
                    onSuggestionClick = { selectedSuggestion ->
                        commitSuggestion(selectedSuggestion, settings)
                    },
                    onClipboardPaste = { clipText ->
                        currentInputConnection?.commitText(clipText, 1)
                        currentLayoutViewMode = LayoutViewMode.ALPHA
                    },
                    onClipboardPin = { entry ->
                        clipboardManager.pinEntry(entry)
                    },
                    onClipboardDelete = { id ->
                        clipboardManager.deleteEntry(id)
                    },
                    onClipboardClearAll = {
                        clipboardManager.clearAll()
                    },
                    onOpenSettings = {
                        val intent = Intent(this@FXInputMethodService, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(intent)
                    },
                    onModeChange = { newMode ->
                        currentLayoutViewMode = newMode
                    }
                )
            }
        }
        return composeView
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        if (lifecycleRegistry.currentState == Lifecycle.State.INITIALIZED ||
            lifecycleRegistry.currentState == Lifecycle.State.CREATED) {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        }
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        setCandidatesViewShown(false)
        try {
            showWindow(true)
        } catch (_: Exception) {}

        // Ensure installed language packs are up to date
        serviceScope.launch {
            val packs = packManager.getInstalledPacks()
            if (packs.isNotEmpty()) {
                installedLanguages = packs
            }
        }

        // Check if input field is password
        val inputType = info?.inputType ?: 0
        val variation = inputType and InputType.TYPE_MASK_VARIATION
        isPasswordField = variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
                variation == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD ||
                inputType and InputType.TYPE_MASK_CLASS == InputType.TYPE_CLASS_NUMBER &&
                variation == InputType.TYPE_NUMBER_VARIATION_PASSWORD

        composingBuffer.clear()
        previousWord = null
        currentLayoutViewMode = LayoutViewMode.ALPHA
        isShifted = false
        updateSuggestions(KeyboardSettings())
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        soundEngine.stopSustain()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        composingBuffer.clear()
    }

    private fun handleKeyPress(keyDef: KeyDefinition, center: Offset, size: Size, settings: KeyboardSettings) {
        effectEngine.settings = settings
        soundEngine.settings = settings

        // Trigger visual effect & sound immediately (non-blocking)
        if (!isPasswordField) {
            effectEngine.triggerKeyEffect(center.x, center.y, size.width, size.height)
        }
        soundEngine.playForKey(keyDef.label)
        triggerHaptic(settings)

        val ic = currentInputConnection ?: return

        when (keyDef.type) {
            KeyType.CHARACTER -> {
                val charToOutput = keyDef.output
                val isPhonetic = settings.currentInputMode == KeyboardInputMode.PHONETIC
                if (isPhonetic) {
                    // Single source of truth: phonetic composing buffer
                    composingBuffer.append(charToOutput)
                    val transliterated = transliterationEngine.transliterate(settings.currentLanguageId, composingBuffer.toString())
                    ic.setComposingText(transliterated, 1)
                } else {
                    ic.commitText(charToOutput, 1)
                    composingBuffer.append(charToOutput)
                }
                // Auto reset shift after typing letter
                if (isShifted && !isCapsLock) {
                    isShifted = false
                }
                updateSuggestions(settings)
            }

            KeyType.SHIFT -> {
                if (isShifted && !isCapsLock) {
                    isCapsLock = true
                } else if (isCapsLock) {
                    isShifted = false
                    isCapsLock = false
                } else {
                    isShifted = true
                }
            }

            KeyType.BACKSPACE -> {
                val selected = ic.getSelectedText(0)
                val isPhonetic = settings.currentInputMode == KeyboardInputMode.PHONETIC
                if (!selected.isNullOrEmpty()) {
                    ic.commitText("", 1)
                    if (composingBuffer.isNotEmpty()) composingBuffer.clear()
                } else if (isPhonetic) {
                    if (composingBuffer.isNotEmpty()) {
                        composingBuffer.deleteCharAt(composingBuffer.length - 1)
                        if (composingBuffer.isEmpty()) {
                            // Completely clear active composing text in the editor
                            ic.setComposingText("", 1)
                            ic.finishComposingText()
                        } else {
                            val transliterated = transliterationEngine.transliterate(settings.currentLanguageId, composingBuffer.toString())
                            ic.setComposingText(transliterated, 1)
                        }
                    } else {
                        ic.deleteSurroundingText(1, 0)
                    }
                } else {
                    if (composingBuffer.isNotEmpty()) {
                        composingBuffer.deleteCharAt(composingBuffer.length - 1)
                    }
                    ic.deleteSurroundingText(1, 0)
                }
                updateSuggestions(settings)
            }

            KeyType.ENTER -> {
                if (composingBuffer.isNotEmpty()) {
                    commitComposingWord(settings)
                }
                val imeOptions = currentInputEditorInfo?.imeOptions ?: 0
                val actionId = imeOptions and EditorInfo.IME_MASK_ACTION
                if (actionId != EditorInfo.IME_ACTION_NONE && actionId != EditorInfo.IME_ACTION_UNSPECIFIED) {
                    ic.performEditorAction(actionId)
                } else {
                    ic.commitText("\n", 1)
                }
            }

            KeyType.SPACE -> {
                if (composingBuffer.isNotEmpty()) {
                    commitComposingWord(settings)
                }
                ic.commitText(" ", 1)
                composingBuffer.clear()
                updateSuggestions(settings)
            }

            KeyType.COMMA -> {
                if (composingBuffer.isNotEmpty()) commitComposingWord(settings)
                ic.commitText(",", 1)
            }

            KeyType.PERIOD -> {
                if (composingBuffer.isNotEmpty()) commitComposingWord(settings)
                ic.commitText(".", 1)
            }

            KeyType.SYMBOL_SWITCH -> {
                currentLayoutViewMode = LayoutViewMode.NUMERIC_SYMBOLS
            }

            KeyType.MORE_SYMBOLS_SWITCH -> {
                currentLayoutViewMode = LayoutViewMode.MORE_SYMBOLS
            }

            KeyType.LETTER_SWITCH -> {
                currentLayoutViewMode = LayoutViewMode.ALPHA
            }

            KeyType.EMOJI_SWITCH -> {
                currentLayoutViewMode = LayoutViewMode.EMOJI
            }

            KeyType.CLIPBOARD_SWITCH -> {
                currentLayoutViewMode = LayoutViewMode.CLIPBOARD
            }

            KeyType.LANGUAGE_SWITCH -> {
                // Dynamically cycle through all installed languages and their supported modes
                serviceScope.launch {
                    val packs = if (installedLanguages.isNotEmpty()) installedLanguages else packManager.getInstalledPacks()
                    val availableModes = mutableListOf<Pair<String, KeyboardInputMode>>()
                    for (pack in packs) {
                        if (pack.id == "en") {
                            availableModes.add(Pair("en", KeyboardInputMode.ENGLISH))
                        } else {
                            if (pack.hasNativeLayout) {
                                availableModes.add(Pair(pack.id, KeyboardInputMode.NATIVE))
                            }
                            if (pack.hasPhoneticMode) {
                                availableModes.add(Pair(pack.id, KeyboardInputMode.PHONETIC))
                            }
                        }
                    }
                    if (availableModes.isEmpty()) {
                        availableModes.add(Pair("en", KeyboardInputMode.ENGLISH))
                        availableModes.add(Pair("bn", KeyboardInputMode.NATIVE))
                        availableModes.add(Pair("bn", KeyboardInputMode.PHONETIC))
                    }

                    val currentPair = Pair(settings.currentLanguageId, settings.currentInputMode)
                    val currentIndex = availableModes.indexOfFirst { it.first == currentPair.first && it.second == currentPair.second }
                    val nextIndex = if (currentIndex != -1) (currentIndex + 1) % availableModes.size else 0
                    val nextMode = availableModes[nextIndex]

                    settingsRepo.setCurrentLanguage(nextMode.first)
                    settingsRepo.setCurrentInputMode(nextMode.second)
                }
            }

            KeyType.SETTINGS_SWITCH -> {
                val intent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
            }
        }
    }

    private fun commitComposingWord(settings: KeyboardSettings) {
        val ic = currentInputConnection ?: return
        if (composingBuffer.isEmpty()) return

        val raw = composingBuffer.toString()
        val isPhonetic = settings.currentInputMode == KeyboardInputMode.PHONETIC
        if (isPhonetic) {
            val textToCommit = transliterationEngine.transliterate(settings.currentLanguageId, raw)
            // commitText atomically replaces any active composing text in the editor.
            // Do NOT call finishComposingText() beforehand as that leaves duplicate text!
            ic.commitText(textToCommit, 1)
            if (!isPasswordField && textToCommit.isNotBlank()) {
                serviceScope.launch {
                    suggestionEngine.learnWord(textToCommit, settings.currentLanguageId)
                }
            }
            previousWord = textToCommit
        } else {
            // English or other native mode:
            // The characters were already inserted into the editor.
            // Check if English auto spell correction is enabled and applicable:
            if (settings.autoCorrectionEnabled && settings.currentLanguageId == "en") {
                val correction = com.example.suggestions.EnglishSpellCorrector.getCorrection(raw)
                if (correction != null && correction != raw) {
                    ic.deleteSurroundingText(raw.length, 0)
                    ic.commitText(correction, 1)
                    previousWord = correction
                } else {
                    previousWord = raw
                    if (!isPasswordField && raw.isNotBlank()) {
                        serviceScope.launch {
                            suggestionEngine.learnWord(raw, "en")
                        }
                    }
                }
            } else {
                previousWord = raw
                if (!isPasswordField && raw.isNotBlank()) {
                    serviceScope.launch {
                        suggestionEngine.learnWord(raw, settings.currentLanguageId)
                    }
                }
            }
        }
        composingBuffer.clear()
    }

    private fun commitSuggestion(suggestion: String, settings: KeyboardSettings) {
        val ic = currentInputConnection ?: return
        val isPhonetic = settings.currentInputMode == KeyboardInputMode.PHONETIC
        if (isPhonetic) {
            // In phonetic mode, commitText replaces the current composing text directly
            ic.commitText("$suggestion ", 1)
        } else {
            if (composingBuffer.isNotEmpty()) {
                ic.deleteSurroundingText(composingBuffer.length, 0)
            }
            ic.commitText("$suggestion ", 1)
        }
        if (!isPasswordField) {
            serviceScope.launch {
                suggestionEngine.learnWord(suggestion, settings.currentLanguageId)
            }
        }
        previousWord = suggestion
        composingBuffer.clear()
        updateSuggestions(settings)
    }

    private fun updateSuggestions(settings: KeyboardSettings) {
        if (isPasswordField || !settings.suggestionsEnabled) {
            suggestions = emptyList()
            return
        }
        serviceScope.launch {
            val currentWord = composingBuffer.toString()
            val isPhonetic = settings.currentInputMode == KeyboardInputMode.PHONETIC
            if (isPhonetic) {
                val phoneticCandidates = transliterationEngine.getCandidates(settings.currentLanguageId, currentWord)
                suggestions = phoneticCandidates
            } else {
                val list = suggestionEngine.getSuggestions(
                    currentWord = currentWord,
                    previousWord = previousWord,
                    languageCode = settings.currentLanguageId,
                    limit = 4,
                    personalDictionaryEnabled = settings.personalDictionaryEnabled
                )
                suggestions = list
            }
        }
    }

    private fun triggerHaptic(settings: KeyboardSettings) {
        if (!settings.vibrationEnabled || !hasVibratorHardware) return
        val vib = vibrator ?: return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vib.vibrate(VibrationEffect.createOneShot(settings.vibrationDurationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(settings.vibrationDurationMs)
            }
        } catch (_: Exception) {}
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        vmStore.clear()
        serviceScope.cancel()
        soundEngine.release()
    }
}
