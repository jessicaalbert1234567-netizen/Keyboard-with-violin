package com.example.language

class HindiTransliterator : LanguageTransliterator {
    override val languageCode: String = "hi"
    override val languageName: String = "Hindi"

    private val dictionary = mapOf(
        "namaste" to "नमस्ते",
        "aap" to "आप",
        "tum" to "तुम",
        "main" to "मैं",
        "hum" to "हम",
        "kya" to "क्या",
        "kyun" to "क्यों",
        "kaise" to "कैसे",
        "accha" to "अच्छा",
        "theek" to "ठीक",
        "dhanyavaad" to "धन्यवाद",
        "shukriya" to "शुक्रिया",
        "bharat" to "भारत",
        "dilli" to "दिल्ली",
        "ghar" to "घर",
        "paani" to "पानी",
        "khana" to "खाना",
        "bhai" to "भाई",
        "dost" to "दोस्त",
        "shubh" to "शुभ",
        "din" to "दिन",
        "kripya" to "कृपया",
        "haan" to "हाँ",
        "nahi" to "नहीं",
        "aaj" to "आज",
        "kal" to "कल",
        "samay" to "समय",
        "kaam" to "काम"
    )

    private val consonantRules = listOf(
        "kkh" to "क्ष", "ksh" to "क्ष", "ggy" to "ज्ञ", "gy" to "ज्ञ",
        "tr" to "त्र", "sh" to "श", "ss" to "ष", "ch" to "छ",
        "kh" to "ख", "gh" to "घ", "jh" to "झ", "th" to "थ",
        "dh" to "ध", "ph" to "फ", "bh" to "भ",
        "k" to "क", "g" to "ग", "c" to "च", "j" to "ज",
        "t" to "त", "d" to "द", "p" to "प", "b" to "ब",
        "m" to "म", "y" to "य", "r" to "र", "l" to "ल",
        "v" to "व", "w" to "व", "s" to "स", "h" to "ह",
        "n" to "न"
    )

    private val vowelMapInitial = mapOf(
        "aa" to "आ", "a" to "आ", "ee" to "ई", "i" to "इ",
        "oo" to "ऊ", "u" to "उ", "e" to "ए", "ai" to "ऐ",
        "o" to "ओ", "au" to "औ", "ou" to "औ", "ri" to "ऋ"
    )

    private val vowelMapMatra = mapOf(
        "aa" to "ा", "a" to "ा", "ee" to "ी", "i" to "ि",
        "oo" to "ू", "u" to "ु", "e" to "े", "ai" to "ै",
        "o" to "ो", "au" to "ौ", "ou" to "ौ", "ri" to "ृ"
    )

    private val contextualCandidates = mapOf(
        "namaste" to listOf("नमस्ते", "नमस्ते जी", "नमस्कार"),
        "aap" to listOf("आप", "आपका", "आपको"),
        "tum" to listOf("तुम", "तुम्हारा", "तुम्हें"),
        "kaise" to listOf("कैसे", "कैसे हो", "कैसे हैं"),
        "kya" to listOf("क्या", "क्या हाल", "क्या बात"),
        "bharat" to listOf("भारत", "भारतीय", "भारत माता"),
        "shukriya" to listOf("शुक्रिया", "बहुत शुक्रिया", "धन्यवाद"),
        "dhanyavaad" to listOf("धन्यवाद", "बहुत धन्यवाद", "शुक्रिया")
    )

    override fun transliterate(englishInput: String): String {
        if (englishInput.isBlank()) return ""
        val trimmed = englishInput.trim()
        val lower = trimmed.lowercase()

        dictionary[lower]?.let { return it }

        return ruleBasedTransliterate(trimmed)
    }

    private fun ruleBasedTransliterate(input: String): String {
        val sb = StringBuilder()
        var i = 0
        var prevWasConsonant = false

        while (i < input.length) {
            var matchedConsonant = false
            for ((pat, replacement) in consonantRules) {
                if (input.startsWith(pat, i, ignoreCase = true)) {
                    sb.append(replacement)
                    i += pat.length
                    prevWasConsonant = true
                    matchedConsonant = true
                    break
                }
            }
            if (matchedConsonant) continue

            var matchedVowel = false
            for (len in 2 downTo 1) {
                if (i + len <= input.length) {
                    val sub = input.substring(i, i + len).lowercase()
                    if (prevWasConsonant) {
                        vowelMapMatra[sub]?.let { matra ->
                            sb.append(matra)
                            i += len
                            prevWasConsonant = false
                            matchedVowel = true
                        }
                    } else {
                        vowelMapInitial[sub]?.let { initial ->
                            sb.append(initial)
                            i += len
                            prevWasConsonant = false
                            matchedVowel = true
                        }
                    }
                    if (matchedVowel) break
                }
            }
            if (matchedVowel) continue

            sb.append(input[i])
            prevWasConsonant = false
            i++
        }
        return sb.toString()
    }

    override fun getCandidates(englishInput: String): List<String> {
        if (englishInput.isBlank()) return emptyList()
        val lower = englishInput.lowercase().trim()
        val result = LinkedHashSet<String>()
        contextualCandidates[lower]?.let { result.addAll(it) }
        dictionary[lower]?.let { result.add(it) }
        val ruleBased = ruleBasedTransliterate(englishInput.trim())
        if (ruleBased.isNotBlank()) {
            result.add(ruleBased)
        }
        for ((k, v) in dictionary) {
            if (k.startsWith(lower) && k != lower && !result.contains(v)) {
                result.add(v)
                if (result.size >= 3) break
            }
        }
        if (result.isEmpty()) {
            result.add(englishInput)
        }
        return result.take(3).toList()
    }
}
