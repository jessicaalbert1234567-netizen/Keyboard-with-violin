package com.example.ui

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundEngine
import com.example.clipboard.FXClipboardManager
import com.example.data.settings.KeyboardInputMode
import com.example.data.settings.KeyboardSettings
import com.example.effects.EffectEngine
import com.example.keyboard.KeyType
import com.example.keyboard.KeyboardComposable
import com.example.keyboard.LayoutViewMode
import com.example.language.TransliterationEngine
import com.example.suggestions.OfflineDictionarySuggestionEngine
import com.example.theme.ThemeEngine
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    settings: KeyboardSettings,
    onUpdateSettings: (KeyboardSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "FX Keyboard",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Keyboard, contentDescription = "Test") },
                    label = { Text("Test") },
                    modifier = Modifier.testTag("nav_test")
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Effects") },
                    label = { Text("Effects") },
                    modifier = Modifier.testTag("nav_effects")
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.VolumeUp, contentDescription = "Sound") },
                    label = { Text("Sound") },
                    modifier = Modifier.testTag("nav_sound")
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Palette, contentDescription = "Themes") },
                    label = { Text("Themes") },
                    modifier = Modifier.testTag("nav_themes")
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(Icons.Default.Language, contentDescription = "Languages") },
                    label = { Text("Langs") },
                    modifier = Modifier.testTag("nav_languages")
                )
                NavigationBarItem(
                    selected = selectedTab == 5,
                    onClick = { selectedTab = 5 },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Preferences") },
                    label = { Text("Settings") },
                    modifier = Modifier.testTag("nav_preferences")
                )
                NavigationBarItem(
                    selected = selectedTab == 6,
                    onClick = { selectedTab = 6 },
                    icon = { Icon(Icons.AutoMirrored.Filled.Help, contentDescription = "About") },
                    label = { Text("About") },
                    modifier = Modifier.testTag("nav_about")
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (selectedTab) {
                0 -> KeyboardTesterScreen(settings = settings, onUpdateSettings = onUpdateSettings)
                1 -> EffectsStudioScreen(settings = settings, onUpdateSettings = onUpdateSettings)
                2 -> SoundStudioScreen(settings = settings, onUpdateSettings = onUpdateSettings)
                3 -> ThemesScreen(settings = settings, onUpdateSettings = onUpdateSettings)
                4 -> LanguagesScreen(settings = settings, onUpdateSettings = onUpdateSettings)
                5 -> PreferencesScreen(settings = settings, onUpdateSettings = onUpdateSettings)
                6 -> AboutScreen()
            }
        }
    }
}

@Composable
private fun KeyboardTesterScreen(
    settings: KeyboardSettings,
    onUpdateSettings: (KeyboardSettings) -> Unit
) {
    val context = LocalContext.current
    var textInput by remember { mutableStateOf("") }
    val theme = ThemeEngine.getThemeById(settings.themeId)

    val soundEngine = remember { SoundEngine.getInstance(context) }
    soundEngine.settings = settings

    val effectEngine = remember { EffectEngine() }
    effectEngine.settings = settings

    val suggestionEngine = remember { OfflineDictionarySuggestionEngine(context) }
    val transliterationEngine = remember { TransliterationEngine.instance }
    val clipboardManager = remember { FXClipboardManager.getInstance(context) }

    var layoutMode by remember { mutableStateOf(LayoutViewMode.ALPHA) }
    var isShifted by remember { mutableStateOf(false) }
    var isCapsLock by remember { mutableStateOf(false) }
    var suggestions by remember { mutableStateOf<List<String>>(emptyList()) }

    var isEnabledInSettings by remember { mutableStateOf(checkIsKeyboardEnabled(context)) }
    var isSelectedAsDefault by remember { mutableStateOf(checkIsKeyboardSelected(context)) }

    DisposableEffect(Unit) {
        onDispose {
            soundEngine.stopSustain()
        }
    }

    // Periodic check for system IME status
    LaunchedEffect(Unit) {
        while (true) {
            isEnabledInSettings = checkIsKeyboardEnabled(context)
            isSelectedAsDefault = checkIsKeyboardSelected(context)
            delay(3000L)
        }
    }

    // Refresh suggestions when textInput changes
    LaunchedEffect(textInput, settings.currentLanguageId, settings.currentInputMode) {
        val lastWord = textInput.substringAfterLast(' ').substringAfterLast('\n')
        if (lastWord.isNotBlank() && settings.suggestionsEnabled) {
            if (settings.currentLanguageId == "bn" && settings.currentInputMode == KeyboardInputMode.PHONETIC) {
                suggestions = transliterationEngine.getCandidates("bn", lastWord)
            } else {
                suggestions = suggestionEngine.getSuggestions(lastWord, null, settings.currentLanguageId, 4)
            }
        } else {
            suggestions = emptyList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Scrollable Top Header & Test Field
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // System IME Activation Banner
            if (!isEnabledInSettings) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("1. Enable FX Keyboard", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onErrorContainer)
                            Text("Required for keyboard to appear in YouTube, Chrome, Notes & other apps.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onErrorContainer)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { context.startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Enable", fontSize = 12.sp)
                        }
                    }
                }
            } else if (!isSelectedAsDefault) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("2. Set as Default Keyboard", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                            Text("Select FX Keyboard as your active system typing tool.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                                imm?.showInputMethodPicker()
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Select", fontSize = 12.sp)
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                ) {
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Active System Keyboard: Ready in YouTube, Chrome & All Apps!", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }

            // Input Display Field
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = { Text("Type here with FX Keyboard...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .testTag("interactive_test_field"),
                shape = RoundedCornerShape(12.dp)
            )

            // IME Switcher and Clear Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                        imm?.showInputMethodPicker()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.filledTonalButtonColors()
                ) {
                    Text("Switch Keyboard", fontSize = 12.sp)
                }

                Button(
                    onClick = { textInput = "" },
                    modifier = Modifier.weight(0.5f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.filledTonalButtonColors()
                ) {
                    Text("Clear", fontSize = 12.sp)
                }
            }
        }

        // Live Embedded Interactive FX Keyboard
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height((settings.keyboardHeightDp + 48).dp)
        ) {
            KeyboardComposable(
                settings = settings,
                theme = theme,
                effectEngine = effectEngine,
                layoutMode = layoutMode,
                isShifted = isShifted,
                isCapsLock = isCapsLock,
                suggestions = suggestions,
                clipboardEntries = emptyList(),
                onKeyAction = { keyDef, center, size ->
                    // Trigger effect
                    effectEngine.triggerKeyEffect(center.x, center.y, size.width, size.height)

                    // Trigger sound
                    soundEngine.playForKey(keyDef.output)

                    // Handle typing actions
                    when (keyDef.type) {
                        KeyType.CHARACTER -> {
                            var charToType = keyDef.output
                            if (isShifted || isCapsLock) {
                                charToType = charToType.uppercase()
                            }
                            textInput += charToType
                            if (isShifted && !isCapsLock) {
                                isShifted = false
                            }
                        }
                        KeyType.SPACE -> {
                            textInput += " "
                        }
                        KeyType.BACKSPACE -> {
                            if (textInput.isNotEmpty()) {
                                textInput = textInput.dropLast(1)
                            }
                        }
                        KeyType.ENTER -> {
                            textInput += "\n"
                        }
                        KeyType.COMMA -> textInput += ","
                        KeyType.PERIOD -> textInput += "."
                        KeyType.SHIFT -> {
                            if (isShifted) {
                                isCapsLock = !isCapsLock
                                isShifted = isCapsLock
                            } else {
                                isShifted = true
                            }
                        }
                        KeyType.SYMBOL_SWITCH -> layoutMode = LayoutViewMode.NUMERIC_SYMBOLS
                        KeyType.MORE_SYMBOLS_SWITCH -> layoutMode = LayoutViewMode.MORE_SYMBOLS
                        KeyType.LETTER_SWITCH -> layoutMode = LayoutViewMode.ALPHA
                        KeyType.EMOJI_SWITCH -> layoutMode = LayoutViewMode.EMOJI
                        KeyType.CLIPBOARD_SWITCH -> layoutMode = LayoutViewMode.CLIPBOARD
                        KeyType.LANGUAGE_SWITCH -> {
                            val newLang = if (settings.currentLanguageId == "en") "bn" else "en"
                            val newMode = if (newLang == "bn") KeyboardInputMode.NATIVE else KeyboardInputMode.ENGLISH
                            onUpdateSettings(settings.copy(currentLanguageId = newLang, currentInputMode = newMode))
                        }
                        else -> {}
                    }
                },
                onSuggestionClick = { selectedWord ->
                    val words = textInput.split(' ', '\n').toMutableList()
                    if (words.isNotEmpty()) {
                        words[words.size - 1] = selectedWord
                        textInput = words.joinToString(" ") + " "
                    } else {
                        textInput = "$selectedWord "
                    }
                },
                onClipboardPaste = { text ->
                    textInput += text
                    layoutMode = LayoutViewMode.ALPHA
                },
                onClipboardPin = {},
                onClipboardDelete = {},
                onClipboardClearAll = {},
                onOpenSettings = {},
                onModeChange = { layoutMode = it }
            )
        }
    }
}

private fun checkIsKeyboardEnabled(context: Context): Boolean {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return false
    val enabledMethods = imm.enabledInputMethodList
    return enabledMethods.any { it.packageName == context.packageName }
}

private fun checkIsKeyboardSelected(context: Context): Boolean {
    val currentIme = Settings.Secure.getString(context.contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD)
    return currentIme?.contains(context.packageName) == true
}
