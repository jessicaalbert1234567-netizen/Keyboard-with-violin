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
        "khana" to "खाना"
    )

    override fun transliterate(englishInput: String): String {
        val lower = englishInput.lowercase().trim()
        return dictionary[lower] ?: englishInput
    }

    override fun getCandidates(englishInput: String): List<String> {
        val lower = englishInput.lowercase().trim()
        val result = mutableListOf<String>()
        dictionary[lower]?.let { result.add(it) }
        for ((k, v) in dictionary) {
            if (k.startsWith(lower) && k != lower && !result.contains(v)) {
                result.add(v)
                if (result.size >= 4) break
            }
        }
        if (result.isEmpty()) {
            result.add(englishInput)
        }
        return result
    }
}
