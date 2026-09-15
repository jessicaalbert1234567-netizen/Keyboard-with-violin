package com.example.language

class TransliterationEngine {
    private val transliterators = mutableMapOf<String, LanguageTransliterator>()

    init {
        register(BengaliTransliterator())
        register(HindiTransliterator())
        register(ArabicTransliterator())
        register(RussianTransliterator())
        register(GreekTransliterator())
        register(JapaneseTransliterator())
    }

    fun register(transliterator: LanguageTransliterator) {
        transliterators[transliterator.languageCode.lowercase()] = transliterator
    }

    fun getTransliterator(languageCode: String): LanguageTransliterator? {
        return transliterators[languageCode.lowercase()]
    }

    fun transliterate(languageCode: String, input: String): String {
        val transliterator = getTransliterator(languageCode) ?: return input
        return transliterator.transliterate(input)
    }

    fun getCandidates(languageCode: String, input: String): List<String> {
        val transliterator = getTransliterator(languageCode) ?: return emptyList()
        return transliterator.getCandidates(input)
    }

    companion object {
        val instance = TransliterationEngine()
    }
}
