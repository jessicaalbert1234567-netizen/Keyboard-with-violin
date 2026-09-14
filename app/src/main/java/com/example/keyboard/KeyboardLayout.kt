package com.example.keyboard

enum class LayoutViewMode {
    ALPHA,
    NUMERIC_SYMBOLS,
    MORE_SYMBOLS,
    EMOJI,
    CLIPBOARD
}

object KeyboardLayoutProvider {

    fun getEnglishAlphaRows(isShifted: Boolean, isCapsLock: Boolean, showNumberRow: Boolean): List<List<KeyDefinition>> {
        val rows = mutableListOf<List<KeyDefinition>>()

        // Optional Number Row
        if (showNumberRow) {
            val numRow = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0").map {
                KeyDefinition(label = it, output = it, weight = 1.0f)
            }
            rows.add(numRow)
        }

        // Row 1: Q W E R T Y U I O P
        val r1Chars = if (isShifted || isCapsLock) {
            listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P")
        } else {
            listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")
        }
        rows.add(r1Chars.map { KeyDefinition(label = it, output = it, weight = 1.0f) })

        // Row 2: A S D F G H J K L
        val r2Chars = if (isShifted || isCapsLock) {
            listOf("A", "S", "D", "F", "G", "H", "J", "K", "L")
        } else {
            listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")
        }
        rows.add(r2Chars.map { KeyDefinition(label = it, output = it, weight = 1.0f) })

        // Row 3: Shift, Z X C V B N M, Backspace
        val r3Chars = if (isShifted || isCapsLock) {
            listOf("Z", "X", "C", "V", "B", "N", "M")
        } else {
            listOf("z", "x", "c", "v", "b", "n", "m")
        }
        val r3 = mutableListOf<KeyDefinition>()
        r3.add(
            KeyDefinition(
                label = if (isCapsLock) "▲" else "⇧",
                output = "",
                type = KeyType.SHIFT,
                weight = 1.4f,
                isToggleActive = isShifted || isCapsLock
            )
        )
        r3.addAll(r3Chars.map { KeyDefinition(label = it, output = it, weight = 1.0f) })
        r3.add(KeyDefinition(label = "⌫", output = "", type = KeyType.BACKSPACE, weight = 1.4f))
        rows.add(r3)

        // Row 4: ?123, Emoji, Language, Space, Comma, Period, Enter
        val r4 = listOf(
            KeyDefinition(label = "?123", output = "", type = KeyType.SYMBOL_SWITCH, weight = 1.3f),
            KeyDefinition(label = "😊", output = "", type = KeyType.EMOJI_SWITCH, weight = 1.0f),
            KeyDefinition(label = "🌐", output = "", type = KeyType.LANGUAGE_SWITCH, weight = 1.0f),
            KeyDefinition(label = "Space", output = " ", type = KeyType.SPACE, weight = 3.6f),
            KeyDefinition(label = ",", output = ",", type = KeyType.COMMA, weight = 1.0f),
            KeyDefinition(label = ".", output = ".", type = KeyType.PERIOD, weight = 1.0f),
            KeyDefinition(label = "↵", output = "\n", type = KeyType.ENTER, weight = 1.5f)
        )
        rows.add(r4)

        return rows
    }

    fun getBengaliNativeRows(isShifted: Boolean): List<List<KeyDefinition>> {
        val rows = mutableListOf<List<KeyDefinition>>()

        // Bengali Vowels & Consonants layout
        val r1 = if (!isShifted) {
            listOf("ক", "খ", "গ", "ঘ", "ঙ", "চ", "ছ", "জ", "ঝ", "ঞ")
        } else {
            listOf("অ", "আ", "ই", "ঈ", "উ", "ঊ", "ঋ", "এ", "ঐ", "ও")
        }
        rows.add(r1.map { KeyDefinition(label = it, output = it, weight = 1.0f) })

        val r2 = if (!isShifted) {
            listOf("ট", "ঠ", "ড", "ঢ", "ণ", "ত", "থ", "দ", "ধ", "ন")
        } else {
            listOf("া", "ি", "ী", "ু", "ূ", "ৃ", "ে", "ৈ", "ো", "ৌ")
        }
        rows.add(r2.map { KeyDefinition(label = it, output = it, weight = 1.0f) })

        val r3 = mutableListOf<KeyDefinition>()
        r3.add(KeyDefinition(label = "স্বর", output = "", type = KeyType.SHIFT, weight = 1.3f, isToggleActive = isShifted))
        val r3Chars = if (!isShifted) {
            listOf("প", "ফ", "ব", "ভ", "ম", "য", "র", "ল")
        } else {
            listOf("শ", "ষ", "স", "হ", "ড়", "ঢ়", "য়", "ৎ")
        }
        r3.addAll(r3Chars.map { KeyDefinition(label = it, output = it, weight = 1.0f) })
        r3.add(KeyDefinition(label = "⌫", output = "", type = KeyType.BACKSPACE, weight = 1.3f))
        rows.add(r3)

        val r4 = listOf(
            KeyDefinition(label = "?123", output = "", type = KeyType.SYMBOL_SWITCH, weight = 1.3f),
            KeyDefinition(label = "্", output = "্", weight = 1.0f), // Hasant (conjunct builder)
            KeyDefinition(label = "🌐", output = "", type = KeyType.LANGUAGE_SWITCH, weight = 1.0f),
            KeyDefinition(label = "স্পেস", output = " ", type = KeyType.SPACE, weight = 3.6f),
            KeyDefinition(label = "।", output = "।", weight = 1.0f), // Bengali Dari (period)
            KeyDefinition(label = "ং", output = "ং", weight = 1.0f),
            KeyDefinition(label = "↵", output = "\n", type = KeyType.ENTER, weight = 1.5f)
        )
        rows.add(r4)

        return rows
    }

    fun getSymbolsPage1Rows(): List<List<KeyDefinition>> {
        val rows = mutableListOf<List<KeyDefinition>>()
        rows.add(listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0").map { KeyDefinition(it, output = it) })
        rows.add(listOf("@", "#", "$", "_", "&", "-", "+", "(", ")", "/").map { KeyDefinition(it, output = it) })
        val r3 = mutableListOf<KeyDefinition>()
        r3.add(KeyDefinition(label = "=/<", output = "", type = KeyType.MORE_SYMBOLS_SWITCH, weight = 1.3f))
        r3.addAll(listOf("*", "\"", "'", ":", ";", "!", "?").map { KeyDefinition(it, output = it) })
        r3.add(KeyDefinition(label = "⌫", output = "", type = KeyType.BACKSPACE, weight = 1.3f))
        rows.add(r3)
        val r4 = listOf(
            KeyDefinition(label = "ABC", output = "", type = KeyType.LETTER_SWITCH, weight = 1.4f),
            KeyDefinition(label = "📋", output = "", type = KeyType.CLIPBOARD_SWITCH, weight = 1.0f),
            KeyDefinition(label = ",", output = ",", type = KeyType.COMMA, weight = 1.0f),
            KeyDefinition(label = "Space", output = " ", type = KeyType.SPACE, weight = 3.6f),
            KeyDefinition(label = ".", output = ".", type = KeyType.PERIOD, weight = 1.0f),
            KeyDefinition(label = "↵", output = "\n", type = KeyType.ENTER, weight = 1.5f)
        )
        rows.add(r4)
        return rows
    }

    fun getSymbolsPage2Rows(): List<List<KeyDefinition>> {
        val rows = mutableListOf<List<KeyDefinition>>()
        rows.add(listOf("~", "`", "|", "•", "√", "π", "÷", "×", "¶", "∆").map { KeyDefinition(it, output = it) })
        rows.add(listOf("£", "€", "¥", "¢", "^", "°", "=", "{", "}", "\\").map { KeyDefinition(it, output = it) })
        val r3 = mutableListOf<KeyDefinition>()
        r3.add(KeyDefinition(label = "123", output = "", type = KeyType.SYMBOL_SWITCH, weight = 1.3f))
        r3.addAll(listOf("%", "©", "®", "™", "✓", "[", "]").map { KeyDefinition(it, output = it) })
        r3.add(KeyDefinition(label = "⌫", output = "", type = KeyType.BACKSPACE, weight = 1.3f))
        rows.add(r3)
        val r4 = listOf(
            KeyDefinition(label = "ABC", output = "", type = KeyType.LETTER_SWITCH, weight = 1.4f),
            KeyDefinition(label = "<", output = "<", weight = 1.0f),
            KeyDefinition(label = ">", output = ">", weight = 1.0f),
            KeyDefinition(label = "Space", output = " ", type = KeyType.SPACE, weight = 3.6f),
            KeyDefinition(label = "«", output = "«", weight = 1.0f),
            KeyDefinition(label = "»", output = "»", weight = 1.0f),
            KeyDefinition(label = "↵", output = "\n", type = KeyType.ENTER, weight = 1.5f)
        )
        rows.add(r4)
        return rows
    }
}
