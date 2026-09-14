package com.example.keyboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.ClipboardEntry
import com.example.data.settings.KeyboardInputMode
import com.example.data.settings.KeyboardSettings
import com.example.effects.EffectEngine
import com.example.effects.EffectOverlay
import com.example.theme.KeyboardTheme

@Composable
fun KeyboardComposable(
    settings: KeyboardSettings,
    theme: KeyboardTheme,
    effectEngine: EffectEngine,
    layoutMode: LayoutViewMode,
    isShifted: Boolean,
    isCapsLock: Boolean,
    suggestions: List<String>,
    clipboardEntries: List<ClipboardEntry>,
    onKeyAction: (KeyDefinition, Offset, Size) -> Unit,
    onSuggestionClick: (String) -> Unit,
    onClipboardPaste: (String) -> Unit,
    onClipboardPin: (ClipboardEntry) -> Unit,
    onClipboardDelete: (Long) -> Unit,
    onClipboardClearAll: () -> Unit,
    onOpenSettings: () -> Unit,
    onModeChange: (LayoutViewMode) -> Unit,
    modifier: Modifier = Modifier
) {
    var rootCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var activeKeyPreview by remember { mutableStateOf<KeyPopupPreviewData?>(null) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height((settings.keyboardHeightDp + 48).dp)
            .background(theme.backgroundBrush)
            .onGloballyPositioned { rootCoordinates = it }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Suggestion Bar
            SuggestionBar(
                suggestions = suggestions,
                theme = theme,
                onSuggestionClick = onSuggestionClick,
                onOpenClipboard = { onModeChange(LayoutViewMode.CLIPBOARD) },
                onOpenSettings = onOpenSettings,
                currentInputMode = settings.currentInputMode,
                currentLanguageId = settings.currentLanguageId
            )

            // Keyboard Body
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                when (layoutMode) {
                    LayoutViewMode.ALPHA -> {
                        val rows = if (settings.currentLanguageId == "bn" && settings.currentInputMode == KeyboardInputMode.NATIVE) {
                            KeyboardLayoutProvider.getBengaliNativeRows(isShifted)
                        } else {
                            KeyboardLayoutProvider.getEnglishAlphaRows(isShifted, isCapsLock, settings.showNumberRow)
                        }
                        KeyboardGrid(
                            rows = rows,
                            settings = settings,
                            theme = theme,
                            rootCoordinates = rootCoordinates,
                            onShowKeyPreview = { activeKeyPreview = it },
                            onHideKeyPreview = { activeKeyPreview = null },
                            onKeyAction = onKeyAction
                        )
                    }
                    LayoutViewMode.NUMERIC_SYMBOLS -> {
                        KeyboardGrid(
                            rows = KeyboardLayoutProvider.getSymbolsPage1Rows(),
                            settings = settings,
                            theme = theme,
                            rootCoordinates = rootCoordinates,
                            onShowKeyPreview = { activeKeyPreview = it },
                            onHideKeyPreview = { activeKeyPreview = null },
                            onKeyAction = onKeyAction
                        )
                    }
                    LayoutViewMode.MORE_SYMBOLS -> {
                        KeyboardGrid(
                            rows = KeyboardLayoutProvider.getSymbolsPage2Rows(),
                            settings = settings,
                            theme = theme,
                            rootCoordinates = rootCoordinates,
                            onShowKeyPreview = { activeKeyPreview = it },
                            onHideKeyPreview = { activeKeyPreview = null },
                            onKeyAction = onKeyAction
                        )
                    }
                    LayoutViewMode.EMOJI -> {
                        EmojiPanel(
                            theme = theme,
                            onEmojiSelected = { emoji ->
                                onKeyAction(KeyDefinition(label = emoji, output = emoji), Offset.Zero, Size.Zero)
                            },
                            onBackToKeyboard = { onModeChange(LayoutViewMode.ALPHA) }
                        )
                    }
                    LayoutViewMode.CLIPBOARD -> {
                        ClipboardPanel(
                            entries = clipboardEntries,
                            theme = theme,
                            onPaste = onClipboardPaste,
                            onPin = onClipboardPin,
                            onDelete = onClipboardDelete,
                            onClearAll = onClipboardClearAll,
                            onBackToKeyboard = { onModeChange(LayoutViewMode.ALPHA) }
                        )
                    }
                }

                // Effect Overlay rendering particles on top of keys
                EffectOverlay(
                    engine = effectEngine,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Google Keyboard-style Key Popup Preview displayed upwards above pressed character
        activeKeyPreview?.let { preview ->
            GboardKeyPopup(
                preview = preview,
                theme = theme,
                settings = settings
            )
        }
    }
}

@Composable
private fun SuggestionBar(
    suggestions: List<String>,
    theme: KeyboardTheme,
    onSuggestionClick: (String) -> Unit,
    onOpenClipboard: () -> Unit,
    onOpenSettings: () -> Unit,
    currentInputMode: KeyboardInputMode,
    currentLanguageId: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
        color = theme.suggestionBarBackground
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mode Indicator
            Text(
                text = when {
                    currentLanguageId == "bn" && currentInputMode == KeyboardInputMode.NATIVE -> "বাংলা"
                    currentLanguageId == "bn" && currentInputMode == KeyboardInputMode.PHONETIC -> "Phonetic"
                    else -> "EN"
                },
                color = theme.accentColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp)
            )

            // Suggestions List
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (suggestions.isEmpty()) {
                    Text(
                        text = "FX Keyboard",
                        color = theme.suggestionTextColor.copy(alpha = 0.5f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    suggestions.forEachIndexed { index, suggestion ->
                        if (index > 0) {
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(16.dp)
                                    .background(theme.suggestionDividerColor)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { onSuggestionClick(suggestion) }
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = suggestion,
                                color = if (index == 0) theme.accentColor else theme.suggestionTextColor,
                                fontSize = 14.sp,
                                fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Quick Actions: Clipboard & Settings
            IconButton(onClick = onOpenClipboard, modifier = Modifier.padding(2.dp)) {
                Icon(
                    imageVector = Icons.Default.ContentPaste,
                    contentDescription = "Clipboard",
                    tint = theme.suggestionTextColor.copy(alpha = 0.8f)
                )
            }
            IconButton(onClick = onOpenSettings, modifier = Modifier.padding(2.dp)) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = theme.suggestionTextColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun KeyboardGrid(
    rows: List<List<KeyDefinition>>,
    settings: KeyboardSettings,
    theme: KeyboardTheme,
    rootCoordinates: LayoutCoordinates?,
    onShowKeyPreview: (KeyPopupPreviewData) -> Unit,
    onHideKeyPreview: () -> Unit,
    onKeyAction: (KeyDefinition, Offset, Size) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(settings.keySpacingDp.dp)
    ) {
        for (row in rows) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(settings.keySpacingDp.dp)
            ) {
                for (keyDef in row) {
                    KeyView(
                        keyDef = keyDef,
                        settings = settings,
                        theme = theme,
                        rootCoordinates = rootCoordinates,
                        onShowKeyPreview = onShowKeyPreview,
                        onHideKeyPreview = onHideKeyPreview,
                        modifier = Modifier.weight(keyDef.weight),
                        onKeyAction = onKeyAction
                    )
                }
            }
        }
    }
}

@Composable
private fun KeyView(
    keyDef: KeyDefinition,
    settings: KeyboardSettings,
    theme: KeyboardTheme,
    rootCoordinates: LayoutCoordinates?,
    onShowKeyPreview: (KeyPopupPreviewData) -> Unit,
    onHideKeyPreview: () -> Unit,
    modifier: Modifier = Modifier,
    onKeyAction: (KeyDefinition, Offset, Size) -> Unit
) {
    var keyCenter by remember { mutableStateOf(Offset.Zero) }
    var keySize by remember { mutableStateOf(Size.Zero) }
    var localCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var isPressed by remember { mutableStateOf(false) }

    val isSpecial = keyDef.type != KeyType.CHARACTER &&
            keyDef.type != KeyType.COMMA &&
            keyDef.type != KeyType.PERIOD &&
            keyDef.type != KeyType.SPACE

    val bgColor = when {
        isPressed -> theme.keyPressedBackground
        keyDef.isToggleActive -> theme.accentColor.copy(alpha = 0.85f)
        isSpecial -> theme.specialKeyBackground
        else -> theme.keyBackground
    }

    val textColor = when {
        keyDef.isToggleActive -> Color.White
        isSpecial -> theme.specialKeyTextColor
        else -> theme.keyTextColor
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                localCoordinates = coordinates
                val bounds = coordinates.boundsInWindow()
                keyCenter = Offset(bounds.left + bounds.width / 2f, bounds.top + bounds.height / 2f)
                keySize = Size(bounds.width, bounds.height)
            }
            .shadow(
                elevation = theme.keyShadowElevationDp.dp,
                shape = RoundedCornerShape(settings.keyCornerRadiusDp.dp)
            )
            .clip(RoundedCornerShape(settings.keyCornerRadiusDp.dp))
            .background(bgColor.copy(alpha = settings.keyOpacity))
            .pointerInput(keyDef, rootCoordinates) {
                detectTapGestures(
                    onPress = { offset ->
                        isPressed = true
                        val coords = localCoordinates
                        val rootCoords = rootCoordinates
                        if (coords != null && coords.isAttached && rootCoords != null && rootCoords.isAttached) {
                            val posInRoot = rootCoords.localPositionOf(coords, Offset.Zero)
                            val isLetterOrChar = keyDef.type == KeyType.CHARACTER ||
                                    keyDef.type == KeyType.COMMA ||
                                    keyDef.type == KeyType.PERIOD ||
                                    (keyDef.label.length == 1 && keyDef.type != KeyType.SPACE)
                            if (isLetterOrChar && keyDef.label.isNotBlank()) {
                                onShowKeyPreview(
                                    KeyPopupPreviewData(
                                        label = keyDef.label,
                                        positionInRoot = posInRoot,
                                        keySize = Size(coords.size.width.toFloat(), coords.size.height.toFloat())
                                    )
                                )
                            }
                        }
                        // Calculate absolute offset within key
                        val tapCenter = Offset(keyCenter.x - (keySize.width / 2f) + offset.x, keyCenter.y - (keySize.height / 2f) + offset.y)
                        onKeyAction(keyDef, tapCenter, keySize)
                        tryAwaitRelease()
                        isPressed = false
                        onHideKeyPreview()
                    }
                )
            }
            .testTag("key_${keyDef.label}"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = keyDef.label,
            color = textColor,
            fontSize = if (keyDef.label.length > 2) (settings.fontSizeSp - 4).sp else settings.fontSizeSp.sp,
            fontWeight = if (keyDef.type == KeyType.ENTER || keyDef.type == KeyType.SHIFT) FontWeight.Bold else FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EmojiPanel(
    theme: KeyboardTheme,
    onEmojiSelected: (String) -> Unit,
    onBackToKeyboard: () -> Unit
) {
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    val categories = EmojiData.categories

    Column(modifier = Modifier.fillMaxSize()) {
        // Category Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .background(theme.specialKeyBackground.copy(alpha = 0.5f))
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onBackToKeyboard() }
                    .background(theme.specialKeyBackground)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text("ABC", color = theme.accentColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            categories.forEachIndexed { index, cat ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { selectedCategoryIndex = index }
                        .background(if (selectedCategoryIndex == index) theme.accentColor.copy(alpha = 0.3f) else Color.Transparent)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(cat.icon, fontSize = 18.sp)
                }
            }
        }

        // Emoji Grid
        val currentEmojis = categories[selectedCategoryIndex].emojis
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 42.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(4.dp)
        ) {
            items(currentEmojis) { emoji ->
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onEmojiSelected(emoji) }
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 24.sp)
                }
            }
        }
    }
}

@Composable
private fun ClipboardPanel(
    entries: List<ClipboardEntry>,
    theme: KeyboardTheme,
    onPaste: (String) -> Unit,
    onPin: (ClipboardEntry) -> Unit,
    onDelete: (Long) -> Unit,
    onClearAll: () -> Unit,
    onBackToKeyboard: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onBackToKeyboard() }
                    .background(theme.specialKeyBackground)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text("← Keyboard", color = theme.accentColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Text("Clipboard History", color = theme.keyTextColor, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)

            if (entries.isNotEmpty()) {
                IconButton(onClick = onClearAll) {
                    Icon(imageVector = Icons.Default.ClearAll, contentDescription = "Clear All", tint = Color.Red.copy(alpha = 0.8f))
                }
            }
        }

        if (entries.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No copied items yet", color = theme.keyTextColor.copy(alpha = 0.5f), fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(entries, key = { it.id }) { entry ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onPaste(entry.text) },
                        color = theme.keyBackground.copy(alpha = 0.9f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = entry.text,
                                color = theme.keyTextColor,
                                fontSize = 13.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { onPin(entry) }) {
                                Icon(
                                    imageVector = if (entry.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                    contentDescription = "Pin",
                                    tint = if (entry.isPinned) theme.accentColor else theme.keyTextColor.copy(alpha = 0.4f)
                                )
                            }
                            IconButton(onClick = { onDelete(entry.id) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = theme.keyTextColor.copy(alpha = 0.4f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Data describing the key being previewed with an elevated Google Keyboard-style callout.
 */
data class KeyPopupPreviewData(
    val label: String,
    val positionInRoot: Offset,
    val keySize: Size
)

/**
 * Elevated Google Keyboard (Gboard) style popup magnifier bubble displayed above the pressed key.
 */
@Composable
private fun GboardKeyPopup(
    preview: KeyPopupPreviewData,
    theme: KeyboardTheme,
    settings: KeyboardSettings
) {
    val density = LocalDensity.current
    val keyW = preview.keySize.width
    val keyH = preview.keySize.height
    val keyX = preview.positionInRoot.x
    val keyY = preview.positionInRoot.y

    // Calculate dimensions of the Google Keyboard style elevated popup bubble
    val popupWidth = with(density) { maxOf(keyW * 1.35f, 54.dp.toPx()) }
    val popupHeight = with(density) { 62.dp.toPx() }

    // Position horizontally centered above the pressed key
    val rawX = keyX + (keyW - popupWidth) / 2f
    val clampedX = rawX.coerceAtLeast(6f)

    // Position vertically upwards from the pressed key
    val rawY = keyY - popupHeight - with(density) { 6.dp.toPx() }
    val clampedY = rawY.coerceAtLeast(2f)

    Box(
        modifier = Modifier
            .offset { IntOffset(clampedX.toInt(), clampedY.toInt()) }
            .size(
                width = with(density) { popupWidth.toDp() },
                height = with(density) { popupHeight.toDp() }
            )
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = 10.dp,
                    bottomEnd = 10.dp
                ),
                spotColor = theme.accentColor.copy(alpha = 0.5f)
            )
            .clip(
                RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = 10.dp,
                    bottomEnd = 10.dp
                )
            )
            .background(
                Brush.verticalGradient(
                    listOf(
                        theme.keyBackground,
                        theme.keyPressedBackground
                    )
                )
            )
            .border(
                width = 1.5.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        theme.accentColor.copy(alpha = 0.9f),
                        theme.accentColor.copy(alpha = 0.35f)
                    )
                ),
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = 10.dp,
                    bottomEnd = 10.dp
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = preview.label,
            color = theme.accentColor,
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
    }
}

