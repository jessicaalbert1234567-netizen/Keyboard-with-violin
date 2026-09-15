package com.example.language

class ArabicTransliterator : LanguageTransliterator {
    override val languageCode: String = "ar"
    override val languageName: String = "Arabic"

    private val dictionary = mapOf(
        "marhaba" to "مرحبا",
        "salam" to "سلام",
        "shukran" to "شكرا",
        "ahlan" to "أهلا",
        "naam" to "نعم",
        "la" to "لا",
        "kayf" to "كيف",
        "tamam" to "تمام",
        "habibi" to "حبيبي",
        "afwan" to "عفوا",
        "sabah" to "صباح",
        "masa" to "مساء",
        "khayr" to "خير",
        "inshallah" to "إن شاء الله",
        "alhamdulillah" to "الحمد لله",
        "yalla" to "يلا",
        "mumtaz" to "ممتاز",
        "jamal" to "جميل"
    )

    private val candidateSuggestions = mapOf(
        "marhaba" to listOf("مرحبا", "مرحبا بك", "مرحبا جميعا"),
        "salam" to listOf("سلام", "السلام عليكم", "سلامات"),
        "shukran" to listOf("شكرا", "شكرا جزيلا", "شكرا لك"),
        "ahlan" to listOf("أهلا", "أهلا وسهلا", "أهلا بك")
    )

    private val letterMap = mapOf(
        'a' to "ا", 'b' to "ب", 't' to "ت", 'j' to "ج", 'h' to "ه",
        'k' to "ك", 'l' to "ل", 'm' to "م", 'n' to "ن", 's' to "س",
        'r' to "ر", 'd' to "د", 'z' to "ز", 'f' to "ف", 'q' to "ق",
        'w' to "و", 'y' to "ي", 'u' to "و", 'i' to "ي", 'e' to "ي",
        'o' to "و", 'c' to "ك", 'x' to "كس", 'p' to "ب", 'g' to "ج"
    )

    override fun transliterate(englishInput: String): String {
        if (englishInput.isBlank()) return ""
        val lower = englishInput.trim().lowercase()
        dictionary[lower]?.let { return it }

        val sb = StringBuilder()
        var i = 0
        while (i < lower.length) {
            if (lower.startsWith("sh", i)) {
                sb.append("ش")
                i += 2
                continue
            }
            if (lower.startsWith("th", i)) {
                sb.append("ث")
                i += 2
                continue
            }
            if (lower.startsWith("kh", i)) {
                sb.append("خ")
                i += 2
                continue
            }
            if (lower.startsWith("dh", i)) {
                sb.append("ذ")
                i += 2
                continue
            }
            if (lower.startsWith("gh", i)) {
                sb.append("غ")
                i += 2
                continue
            }
            val char = lower[i]
            sb.append(letterMap[char] ?: char.toString())
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
