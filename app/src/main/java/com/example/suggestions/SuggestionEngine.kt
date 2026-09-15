package com.example.suggestions

interface SuggestionEngine {
    suspend fun getSuggestions(
        currentWord: String,
        previousWord: String?,
        languageCode: String,
        limit: Int = 3,
        personalDictionaryEnabled: Boolean = true
    ): List<String>

    fun getAutoCorrection(word: String, languageCode: String): String?

    suspend fun learnWord(word: String, languageCode: String)
}
