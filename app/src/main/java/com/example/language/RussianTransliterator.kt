package com.example.language

class RussianTransliterator : LanguageTransliterator {
    override val languageCode: String = "ru"
    override val languageName: String = "Russian"

    private val dictionary = mapOf(
        "privet" to "привет",
        "spasibo" to "спасибо",
        "da" to "да",
        "net" to "нет",
        "kak" to "как",
        "dela" to "дела",
        "khorosho" to "хорошо",
        "pozhaluysta" to "пожалуйста",
        "dobroye" to "доброе",
        "utro" to "утро",
        "den" to "день",
        "vecher" to "вечер",
        "poka" to "пока",
        "drug" to "друг",
        "rossiya" to "Россия",
        "moskva" to "Москва"
    )

    private val candidateSuggestions = mapOf(
        "privet" to listOf("привет", "привет как дела", "приветствую"),
        "spasibo" to listOf("спасибо", "большое спасибо", "спасибо вам"),
        "kak" to listOf("как", "как дела", "как вы"),
        "khorosho" to listOf("хорошо", "очень хорошо", "хорошего дня")
    )

    private val cyrillicRules = listOf(
        "shch" to "щ", "sch" to "щ", "ch" to "ч", "sh" to "ш",
        "zh" to "ж", "kh" to "х", "ts" to "ц", "ya" to "я",
        "yu" to "ю", "yo" to "ё", "ye" to "е"
    )

    private val singleMap = mapOf(
        'a' to "а", 'b' to "б", 'v' to "в", 'g' to "г", 'd' to "д",
        'e' to "е", 'z' to "з", 'i' to "и", 'j' to "й", 'k' to "к",
        'l' to "л", 'm' to "м", 'n' to "н", 'o' to "о", 'p' to "п",
        'r' to "р", 's' to "с", 't' to "т", 'u' to "у", 'f' to "ф",
        'h' to "х", 'c' to "ц", 'y' to "ы"
    )

    override fun transliterate(englishInput: String): String {
        if (englishInput.isBlank()) return ""
        val lower = englishInput.trim().lowercase()
        dictionary[lower]?.let { return it }

        val sb = StringBuilder()
        var i = 0
        while (i < lower.length) {
            var matched = false
            for ((pat, rep) in cyrillicRules) {
                if (lower.startsWith(pat, i)) {
                    sb.append(rep)
                    i += pat.length
                    matched = true
                    break
                }
            }
            if (matched) continue
            val char = lower[i]
            sb.append(singleMap[char] ?: char.toString())
            i++
        }
        return sb.toString()
    }

    override fun getCandidates(englishInput: String): List<String> {
        if (englishInput.isBlank()) return emptyList()
        val lower = englishInput.trim().lowercase()
        val result = LinkedHashSet<String>()

        candidateSuggestions[lower]?.let { result.addAll(it) }
        dictionary[lower]?.let { result.add(it) }

        val ruleBased = transliterate(englishInput)
        if (ruleBased.isNotBlank()) result.add(ruleBased)

        for ((k, v) in dictionary) {
            if (k.startsWith(lower) && k != lower) {
                result.add(v)
                if (result.size >= 3) break
            }
        }
        return result.take(3).toList()
    }
}
