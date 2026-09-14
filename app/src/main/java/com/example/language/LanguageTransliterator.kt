package com.example.language

interface LanguageTransliterator {
    val languageCode: String
    val languageName: String

    fun transliterate(englishInput: String): String
    fun getCandidates(englishInput: String): List<String>
}
