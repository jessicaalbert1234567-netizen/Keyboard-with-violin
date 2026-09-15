package com.example.language

class GreekTransliterator : LanguageTransliterator {
    override val languageCode: String = "el"
    override val languageName: String = "Greek"

    private val dictionary = mapOf(
        "kalimera" to "καλημέρα",
        "efharisto" to "ευχαριστώ",
        "efcharisto" to "ευχαριστώ",
        "geia" to "γεια",
        "parakalo" to "παρακαλώ",
        "kalispera" to "καλησπέρα",
        "kalinychta" to "καληνύχτα",
        "nai" to "ναι",
        "ochi" to "όχι",
        "ti" to "τι",
        "kaneis" to "κάνεις"
    )

    private val candidateSuggestions = mapOf(
        "kalimera" to listOf("καλημέρα", "καλημέρα σας", "καλησπέρα"),
        "efharisto" to listOf("ευχαριστώ", "ευχαριστώ πολύ", "παρακαλώ"),
        "geia" to listOf("γεια", "γεια σου", "γεια σας")
    )

    private val digraphs = listOf(
        "th" to "θ", "ch" to "χ", "ps" to "ψ", "ks" to "ξ"
    )

    private val letterMap = mapOf(
        'a' to "α", 'b' to "β", 'g' to "γ", 'd' to "δ", 'e' to "ε",
        'z' to "ζ", 'i' to "ι", 'k' to "κ", 'l' to "λ", 'm' to "μ",
        'n' to "ν", 'o' to "ο", 'p' to "π", 'r' to "ρ", 's' to "σ",
        't' to "τ", 'u' to "υ", 'f' to "φ", 'w' to "ω", 'y' to "υ",
        'x' to "ξ"
    )

    override fun transliterate(englishInput: String): String {
        if (englishInput.isBlank()) return ""
        val lower = englishInput.trim().lowercase()
        dictionary[lower]?.let { return it }

        val sb = StringBuilder()
        var i = 0
        while (i < lower.length) {
            var matched = false
            for ((pat, rep) in digraphs) {
                if (lower.startsWith(pat, i)) {
                    sb.append(rep)
                    i += pat.length
                    matched = true
                    break
                }
            }
            if (matched) continue
            val char = lower[i]
            val mapped = if (char == 's' && i == lower.length - 1) "ς" else letterMap[char] ?: char.toString()
            sb.append(mapped)
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
