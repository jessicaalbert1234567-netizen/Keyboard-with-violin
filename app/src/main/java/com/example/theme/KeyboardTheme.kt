package com.example.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

data class KeyboardTheme(
    val id: String,
    val name: String,
    val isDark: Boolean,
    val backgroundBrush: Brush,
    val keyBackground: Color,
    val keyPressedBackground: Color,
    val keyTextColor: Color,
    val specialKeyBackground: Color,
    val specialKeyTextColor: Color,
    val accentColor: Color,
    val suggestionBarBackground: Color,
    val suggestionTextColor: Color,
    val suggestionDividerColor: Color,
    val keyBorderColor: Color = Color.Transparent,
    val keyShadowElevationDp: Int = 2
)
