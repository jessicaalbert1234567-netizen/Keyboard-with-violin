package com.example.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object ThemeEngine {
    val Classic = KeyboardTheme(
        id = "classic",
        name = "Classic Light",
        isDark = false,
        backgroundBrush = Brush.verticalGradient(listOf(Color(0xFFF1F5F9), Color(0xFFE2E8F0))),
        keyBackground = Color(0xFFFFFFFF),
        keyPressedBackground = Color(0xFFCBD5E1),
        keyTextColor = Color(0xFF0F172A),
        specialKeyBackground = Color(0xFFE2E8F0),
        specialKeyTextColor = Color(0xFF334155),
        accentColor = Color(0xFF2563EB),
        suggestionBarBackground = Color(0xFFF8FAFC),
        suggestionTextColor = Color(0xFF1E293B),
        suggestionDividerColor = Color(0xFFE2E8F0),
        keyBorderColor = Color(0x1A000000),
        keyShadowElevationDp = 2
    )

    val Dark = KeyboardTheme(
        id = "dark",
        name = "Material Dark",
        isDark = true,
        backgroundBrush = Brush.verticalGradient(listOf(Color(0xFF1E1E2E), Color(0xFF181825))),
        keyBackground = Color(0xFF313244),
        keyPressedBackground = Color(0xFF45475A),
        keyTextColor = Color(0xFFCDD6F4),
        specialKeyBackground = Color(0xFF262738),
        specialKeyTextColor = Color(0xFFBAC2DE),
        accentColor = Color(0xFF89B4FA),
        suggestionBarBackground = Color(0xFF181825),
        suggestionTextColor = Color(0xFFF5E0DC),
        suggestionDividerColor = Color(0xFF313244),
        keyBorderColor = Color(0x22FFFFFF),
        keyShadowElevationDp = 3
    )

    val AMOLED = KeyboardTheme(
        id = "amoled",
        name = "True AMOLED",
        isDark = true,
        backgroundBrush = Brush.verticalGradient(listOf(Color(0xFF000000), Color(0xFF000000))),
        keyBackground = Color(0xFF111111),
        keyPressedBackground = Color(0xFF2A2A2A),
        keyTextColor = Color(0xFFFFFFFF),
        specialKeyBackground = Color(0xFF181818),
        specialKeyTextColor = Color(0xFF38BDF8),
        accentColor = Color(0xFF00E5FF),
        suggestionBarBackground = Color(0xFF000000),
        suggestionTextColor = Color(0xFFFFFFFF),
        suggestionDividerColor = Color(0xFF222222),
        keyBorderColor = Color(0xFF262626),
        keyShadowElevationDp = 1
    )

    val Fire = KeyboardTheme(
        id = "fire",
        name = "Volcanic Fire",
        isDark = true,
        backgroundBrush = Brush.verticalGradient(listOf(Color(0xFF210904), Color(0xFF120302))),
        keyBackground = Color(0xFF3A1208),
        keyPressedBackground = Color(0xFFFF5722),
        keyTextColor = Color(0xFFFFCCBC),
        specialKeyBackground = Color(0xFF4E160A),
        specialKeyTextColor = Color(0xFFFFAB91),
        accentColor = Color(0xFFFF3D00),
        suggestionBarBackground = Color(0xFF1A0603),
        suggestionTextColor = Color(0xFFFFD180),
        suggestionDividerColor = Color(0xFF4E160A),
        keyBorderColor = Color(0x33FF5722),
        keyShadowElevationDp = 3
    )

    val Water = KeyboardTheme(
        id = "water",
        name = "Ocean Abyss",
        isDark = true,
        backgroundBrush = Brush.verticalGradient(listOf(Color(0xFF041C2C), Color(0xFF02101A))),
        keyBackground = Color(0xFF0A314A),
        keyPressedBackground = Color(0xFF0091EA),
        keyTextColor = Color(0xFFE1F5FE),
        specialKeyBackground = Color(0xFF0E3E5E),
        specialKeyTextColor = Color(0xFF80D8FF),
        accentColor = Color(0xFF00B0FF),
        suggestionBarBackground = Color(0xFF031622),
        suggestionTextColor = Color(0xFFB3E5FC),
        suggestionDividerColor = Color(0xFF0A314A),
        keyBorderColor = Color(0x3300B0FF),
        keyShadowElevationDp = 2
    )

    val Chocolate = KeyboardTheme(
        id = "chocolate",
        name = "Dark Cocoa",
        isDark = true,
        backgroundBrush = Brush.verticalGradient(listOf(Color(0xFF1C110C), Color(0xFF120B08))),
        keyBackground = Color(0xFF322019),
        keyPressedBackground = Color(0xFF4A3026),
        keyTextColor = Color(0xFFEDE0D4),
        specialKeyBackground = Color(0xFF261813),
        specialKeyTextColor = Color(0xFFDDB892),
        accentColor = Color(0xFFB08968),
        suggestionBarBackground = Color(0xFF170E0A),
        suggestionTextColor = Color(0xFFE6CCB2),
        suggestionDividerColor = Color(0xFF322019),
        keyBorderColor = Color(0x26B08968),
        keyShadowElevationDp = 2
    )

    val Neon = KeyboardTheme(
        id = "neon",
        name = "Neon Synth",
        isDark = true,
        backgroundBrush = Brush.verticalGradient(listOf(Color(0xFF0D0221), Color(0xFF070014))),
        keyBackground = Color(0xFF190938),
        keyPressedBackground = Color(0xFF35126B),
        keyTextColor = Color(0xFF00F0FF),
        specialKeyBackground = Color(0xFF220B4A),
        specialKeyTextColor = Color(0xFFFF007F),
        accentColor = Color(0xFFFF007F),
        suggestionBarBackground = Color(0xFF090117),
        suggestionTextColor = Color(0xFF00F0FF),
        suggestionDividerColor = Color(0xFF2B0E5A),
        keyBorderColor = Color(0x4D00F0FF),
        keyShadowElevationDp = 4
    )

    val Cyberpunk = KeyboardTheme(
        id = "cyberpunk",
        name = "Cyberpunk 2077",
        isDark = true,
        backgroundBrush = Brush.verticalGradient(listOf(Color(0xFF050505), Color(0xFF0E0E00))),
        keyBackground = Color(0xFF1A1A00),
        keyPressedBackground = Color(0xFFFFE600),
        keyTextColor = Color(0xFFFFE600),
        specialKeyBackground = Color(0xFF262600),
        specialKeyTextColor = Color(0xFF00FFFF),
        accentColor = Color(0xFFFF0055),
        suggestionBarBackground = Color(0xFF080800),
        suggestionTextColor = Color(0xFFFFE600),
        suggestionDividerColor = Color(0xFF333300),
        keyBorderColor = Color(0x80FFE600),
        keyShadowElevationDp = 3
    )

    val Piano = KeyboardTheme(
        id = "piano",
        name = "Ebony & Ivory",
        isDark = false,
        backgroundBrush = Brush.verticalGradient(listOf(Color(0xFF1A1A1A), Color(0xFF0A0A0A))),
        keyBackground = Color(0xFFFAFAFA),
        keyPressedBackground = Color(0xFFD4D4D4),
        keyTextColor = Color(0xFF0A0A0A),
        specialKeyBackground = Color(0xFF262626),
        specialKeyTextColor = Color(0xFFFAFAFA),
        accentColor = Color(0xFFEAB308),
        suggestionBarBackground = Color(0xFF141414),
        suggestionTextColor = Color(0xFFFAFAFA),
        suggestionDividerColor = Color(0xFF262626),
        keyBorderColor = Color(0x22000000),
        keyShadowElevationDp = 4
    )

    val Glass = KeyboardTheme(
        id = "glass",
        name = "Frosted Glass",
        isDark = true,
        backgroundBrush = Brush.verticalGradient(listOf(Color(0xCC1E293B), Color(0xEE0F172A))),
        keyBackground = Color(0x33FFFFFF),
        keyPressedBackground = Color(0x66FFFFFF),
        keyTextColor = Color(0xFFF8FAFC),
        specialKeyBackground = Color(0x22FFFFFF),
        specialKeyTextColor = Color(0xFF94A3B8),
        accentColor = Color(0xFF38BDF8),
        suggestionBarBackground = Color(0x22FFFFFF),
        suggestionTextColor = Color(0xFFF1F5F9),
        suggestionDividerColor = Color(0x1FFFFFFF),
        keyBorderColor = Color(0x33FFFFFF),
        keyShadowElevationDp = 0
    )

    val Minimal = KeyboardTheme(
        id = "minimal",
        name = "Minimalist Mono",
        isDark = false,
        backgroundBrush = Brush.verticalGradient(listOf(Color(0xFFFFFFFF), Color(0xFFFAFAFA))),
        keyBackground = Color(0xFFF5F5F5),
        keyPressedBackground = Color(0xFFE5E5E5),
        keyTextColor = Color(0xFF171717),
        specialKeyBackground = Color(0xFFEDEDED),
        specialKeyTextColor = Color(0xFF525252),
        accentColor = Color(0xFF171717),
        suggestionBarBackground = Color(0xFFFFFFFF),
        suggestionTextColor = Color(0xFF262626),
        suggestionDividerColor = Color(0xFFE5E5E5),
        keyBorderColor = Color(0x10000000),
        keyShadowElevationDp = 0
    )

    val Nature = KeyboardTheme(
        id = "nature",
        name = "Emerald Forest",
        isDark = true,
        backgroundBrush = Brush.verticalGradient(listOf(Color(0xFF062015), Color(0xFF03130C))),
        keyBackground = Color(0xFF0D3B27),
        keyPressedBackground = Color(0xFF14532D),
        keyTextColor = Color(0xFFDCFCE7),
        specialKeyBackground = Color(0xFF082D1D),
        specialKeyTextColor = Color(0xFF86EFAC),
        accentColor = Color(0xFF22C55E),
        suggestionBarBackground = Color(0xFF04170E),
        suggestionTextColor = Color(0xFFBBF7D0),
        suggestionDividerColor = Color(0xFF0D3B27),
        keyBorderColor = Color(0x2622C55E),
        keyShadowElevationDp = 2
    )

    val Space = KeyboardTheme(
        id = "space",
        name = "Cosmic Nebula",
        isDark = true,
        backgroundBrush = Brush.verticalGradient(listOf(Color(0xFF0B0E23), Color(0xFF050611))),
        keyBackground = Color(0xFF181B3D),
        keyPressedBackground = Color(0xFF2E2D68),
        keyTextColor = Color(0xFFE0E7FF),
        specialKeyBackground = Color(0xFF12142E),
        specialKeyTextColor = Color(0xFFA5B4FC),
        accentColor = Color(0xFF818CF8),
        suggestionBarBackground = Color(0xFF090A1B),
        suggestionTextColor = Color(0xFFC7D2FE),
        suggestionDividerColor = Color(0xFF181B3D),
        keyBorderColor = Color(0x33818CF8),
        keyShadowElevationDp = 3
    )

    val allThemes: List<KeyboardTheme> = listOf(
        Dark,
        Classic,
        AMOLED,
        Fire,
        Water,
        Chocolate,
        Neon,
        Cyberpunk,
        Piano,
        Glass,
        Minimal,
        Nature,
        Space
    )

    fun getThemeById(id: String): KeyboardTheme {
        return allThemes.find { it.id.equals(id, ignoreCase = true) } ?: Dark
    }
}
