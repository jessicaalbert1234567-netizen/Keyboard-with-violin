package com.example.suggestions

import android.content.Context
import com.example.data.db.AppDatabase
import com.example.data.db.UserWord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OfflineDictionarySuggestionEngine(context: Context) : SuggestionEngine {
    private val db = AppDatabase.getInstance(context)
    private val userWordDao = db.userWordDao()

    // High-frequency English common words dictionary
    private val englishWords = listOf(
        "the", "be", "to", "of", "and", "a", "in", "that", "have", "I",
        "it", "for", "not", "on", "with", "he", "as", "you", "do", "at",
        "this", "but", "his", "by", "from", "they", "we", "say", "her", "she",
        "or", "an", "will", "my", "one", "all", "would", "there", "their", "what",
        "so", "up", "out", "if", "about", "who", "get", "which", "go", "me",
        "when", "make", "can", "like", "time", "no", "just", "him", "know", "take",
        "people", "into", "year", "your", "good", "some", "could", "them", "see", "other",
        "than", "then", "now", "look", "only", "come", "its", "over", "think", "also",
        "back", "after", "use", "two", "how", "our", "work", "first", "well", "way",
        "even", "new", "want", "because", "any", "these", "give", "day", "most", "us",
        "hello", "help", "held", "helicopter", "helmet", "hero", "here", "happy", "hope",
        "keyboard", "android", "awesome", "beautiful", "please", "thanks", "thank", "welcome"
    )

    // Common English next-word bigram predictions
    private val englishBigrams = mapOf(
        "how" to listOf("are", "is", "can", "to"),
        "how are" to listOf("you", "things", "we"),
        "thank" to listOf("you", "god", "heavens"),
        "i" to listOf("am", "will", "have", "want", "think"),
        "good" to listOf("morning", "evening", "night", "luck", "job"),
        "see" to listOf("you", "it", "more", "later"),
        "welcome" to listOf("to", "back", "home"),
        "what" to listOf("is", "are", "do", "happened")
    )

    // Bengali common words dictionary
    private val bengaliWords = listOf(
        "আমি", "তুমি", "আপনি", "আমরা", "তোমরা", "আপনারা", "সে", "তিনি", "তারা",
        "ভালো", "আছি", "কেমন", "আছো", "আছেন", "বাংলাদেশ", "বাংলা", "ঢাকা",
        "ধন্যবাদ", "খবর", "বন্ধু", "শুভ", "সকাল", "রাত", "ভাত", "খাবার",
        "অনেক", "সুন্দর", "দেশ", "কাজ", "সময়", "ভালোবাসি", "বাড়ি", "পানি"
    )

    private val bengaliBigrams = mapOf(
        "আমি" to listOf("ভালো", "আছি", "যাবো", "ভাত"),
        "কেমন" to listOf("আছো", "আছেন", "হলো"),
        "শুভ" to listOf("সকাল", "রাত্রি", "কামনা"),
        "অনেক" to listOf("ধন্যবাদ", "সুন্দর", "ভালো")
    )

    override suspend fun getSuggestions(
        currentWord: String,
        previousWord: String?,
        languageCode: String,
        limit: Int
    ): List<String> = withContext(Dispatchers.IO) {
        val result = LinkedHashSet<String>()
        val cleanCurrent = currentWord.trim().lowercase()

        // 1. If user is between words, predict next word from previous word
        if (cleanCurrent.isEmpty() && !previousWord.isNullOrBlank()) {
            val prevClean = previousWord.trim().lowercase()
            if (languageCode == "bn") {
                bengaliBigrams[previousWord]?.let { result.addAll(it.take(limit)) }
            } else {
                englishBigrams[prevClean]?.let { result.addAll(it.take(limit)) }
            }
            if (result.size >= limit) return@withContext result.take(limit)
        }

        if (cleanCurrent.isEmpty()) {
            // Default suggestions when field is empty
            return@withContext if (languageCode == "bn") {
                listOf("আমি", "কেমন", "ভালো")
            } else {
                listOf("I", "The", "Hello")
            }
        }

        // 2. Learned words from local Room database
        try {
            val userWords = userWordDao.getWordsStartingWith(cleanCurrent, languageCode, limit)
            result.addAll(userWords)
        } catch (_: Exception) {}

        // 3. Static dictionary completions
        val dict = if (languageCode == "bn") bengaliWords else englishWords
        for (w in dict) {
            if (w.lowercase().startsWith(cleanCurrent) && !result.contains(w)) {
                // Preserve capitalization if current word is capitalized
                if (currentWord.isNotEmpty() && currentWord[0].isUpperCase()) {
                    result.add(w.replaceFirstChar { it.uppercase() })
                } else {
                    result.add(w)
                }
                if (result.size >= limit) break
            }
        }

        // 4. Exact word fallback if no match found
        if (result.isEmpty()) {
            result.add(currentWord)
        }

        result.take(limit)
    }

    override suspend fun learnWord(word: String, languageCode: String) = withContext(Dispatchers.IO) {
        val trimmed = word.trim()
        if (trimmed.length < 2 || trimmed.length > 30) return@withContext
        try {
            val existing = userWordDao.findWord(trimmed, languageCode)
            if (existing != null) {
                userWordDao.update(
                    existing.copy(
                        frequency = existing.frequency + 1,
                        lastUsed = System.currentTimeMillis()
                    )
                )
            } else {
                userWordDao.insert(
                    UserWord(
                        word = trimmed,
                        languageCode = languageCode,
                        frequency = 1
                    )
                )
            }
        } catch (_: Exception) {}
    }
}
