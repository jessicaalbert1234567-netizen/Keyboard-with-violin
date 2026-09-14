package com.example.suggestions

interface SuggestionEngine {
    suspend fun getSuggestions(
        currentWord: String,
        previousWord: String?,
        languageCode: String,
        limit: Int = 3
    ): List<String>

    suspend fun learnWord(word: String, languageCode: String)
}
