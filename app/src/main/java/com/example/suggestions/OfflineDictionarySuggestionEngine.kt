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
        "keyboard", "android", "awesome", "beautiful", "please", "thanks", "thank", "welcome",
        "receive", "separate", "definitely", "grammar", "tomorrow", "weather", "until", "something",
        "nothing", "anything", "everything", "important", "different", "together", "always", "never"
    )

    // Common English next-word bigram predictions & phrase completions
    private val englishBigrams = mapOf(
        "how" to listOf("how are", "how do", "how is"),
        "how ar" to listOf("how are", "how", "what"),
        "thank" to listOf("thank you", "thanks", "thank you so much"),
        "i" to listOf("I am", "I will", "I have"),
        "good" to listOf("good morning", "good night", "good luck"),
        "see" to listOf("see you", "see you later", "see it"),
        "welcome" to listOf("welcome to", "welcome back", "welcome home"),
        "what" to listOf("what is", "what are", "what do"),
        "where" to listOf("where are", "where is", "where do")
    )

    // Bengali common words dictionary
    private val bengaliWords = listOf(
        "আমি", "তুমি", "আপনি", "আমরা", "তোমরা", "আপনারা", "সে", "তিনি", "তারা",
        "ভালো", "আছি", "কেমন", "আছো", "আছেন", "বাংলাদেশ", "বাংলা", "ঢাকা",
        "ধন্যবাদ", "খবর", "বন্ধু", "শুভ", "সকাল", "রাত", "ভাত", "খাবার",
        "অনেক", "সুন্দর", "দেশ", "কাজ", "সময়", "ভালোবাসি", "বাড়ি", "পানি", "কোথায়"
    )

    private val bengaliBigrams = mapOf(
        "আমি" to listOf("আমি ভালো", "আমি আছি", "আমার"),
        "কেমন" to listOf("কেমন আছো", "কেমন আছেন", "কেমন হলো"),
        "শুভ" to listOf("শুভ সকাল", "শুভ রাত্রি", "শুভ কামনা"),
        "অনেক" to listOf("অনেক ধন্যবাদ", "অনেক সুন্দর", "অনেক ভালো"),
        "কোথায়" to listOf("কোথায় আছো", "কোথায় যাবেন", "কোথায় যাবে")
    )

    // Hindi common words & bigrams
    private val hindiWords = listOf(
        "नमस्ते", "आप", "तुम", "मैं", "हम", "क्या", "क्यों", "कैसे", "अच्छा", "ठीक",
        "धन्यवाद", "शुक्रिया", "भारत", "दिल्ली", "घर", "पानी", "खाना", "भाई", "दोस्त"
    )

    private val hindiBigrams = mapOf(
        "नमस्ते" to listOf("नमस्ते जी", "नमस्कार", "नमस्ते आप"),
        "क्या" to listOf("क्या हाल", "क्या बात", "क्या हुआ"),
        "कैसे" to listOf("कैसे हो", "कैसे हैं", "कैसे किया")
    )

    // Arabic common words
    private val arabicWords = listOf(
        "مرحبا", "سلام", "شكرا", "أهلا", "نعم", "لا", "كيف", "تمام", "حبيبي", "عفوا"
    )

    // Russian common words
    private val russianWords = listOf(
        "привет", "спасибо", "да", "нет", "как", "дела", "хорошо", "пожалуйста", "друг"
    )

    override suspend fun getSuggestions(
        currentWord: String,
        previousWord: String?,
        languageCode: String,
        limit: Int,
        personalDictionaryEnabled: Boolean
    ): List<String> = withContext(Dispatchers.IO) {
        val result = LinkedHashSet<String>()
        val cleanCurrent = currentWord.trim().lowercase()

        // 1. Check bigram / context phrase prediction
        if (cleanCurrent.isNotEmpty()) {
            val comboKey = if (!previousWord.isNullOrBlank()) "${previousWord.trim().lowercase()} $cleanCurrent" else cleanCurrent
            if (languageCode == "en") {
                englishBigrams[comboKey]?.let { result.addAll(it) }
                englishBigrams[cleanCurrent]?.let { result.addAll(it) }
            } else if (languageCode == "bn") {
                bengaliBigrams[cleanCurrent]?.let { result.addAll(it) }
            } else if (languageCode == "hi") {
                hindiBigrams[cleanCurrent]?.let { result.addAll(it) }
            }
        } else if (!previousWord.isNullOrBlank()) {
            val prevClean = previousWord.trim().lowercase()
            when (languageCode) {
                "bn" -> bengaliBigrams[previousWord]?.let { result.addAll(it.take(limit)) }
                "hi" -> hindiBigrams[previousWord]?.let { result.addAll(it.take(limit)) }
                else -> englishBigrams[prevClean]?.let { result.addAll(it.take(limit)) }
            }
            if (result.size >= limit) return@withContext result.take(limit).toList()
        }

        if (cleanCurrent.isEmpty()) {
            return@withContext when (languageCode) {
                "bn" -> listOf("আমি", "কেমন", "ভালো")
                "hi" -> listOf("नमस्ते", "आप", "क्या")
                "ar" -> listOf("مرحبا", "سلام", "شكرا")
                "ru" -> listOf("привет", "спасибо", "да")
                else -> listOf("I", "The", "Hello")
            }.take(limit)
        }

        // 2. Spell correction check for English (offline typo correction & edit distance)
        if (languageCode == "en") {
            val typoCorrection = EnglishSpellCorrector.getCorrection(currentWord)
            if (typoCorrection != null) {
                result.add(typoCorrection)
            } else if (cleanCurrent.length >= 3) {
                val editDistanceCandidates = SpellCheckEngine.findCandidates(cleanCurrent, limit = 2)
                for (cand in editDistanceCandidates) {
                    if (cand != cleanCurrent) {
                        result.add(cand)
                    }
                }
            }
        }

        // 3. Learned words from local Room database
        if (personalDictionaryEnabled) {
            try {
                val userWords = userWordDao.getWordsStartingWith(cleanCurrent, languageCode, limit)
                result.addAll(userWords)
            } catch (_: Exception) {}
        }

        // 4. Static dictionary completions
        val dict = when (languageCode) {
            "bn" -> bengaliWords
            "hi" -> hindiWords
            "ar" -> arabicWords
            "ru" -> russianWords
            else -> englishWords
        }
        for (w in dict) {
            if (w.lowercase().startsWith(cleanCurrent) && !result.contains(w)) {
                if (currentWord.isNotEmpty() && currentWord[0].isUpperCase()) {
                    result.add(w.replaceFirstChar { it.uppercase() })
                } else {
                    result.add(w)
                }
                if (result.size >= limit) break
            }
        }

        // 5. Fallback: current word if result is empty
        if (result.isEmpty()) {
            result.add(currentWord)
        }

        result.take(limit).toList()
    }

    override fun getAutoCorrection(word: String, languageCode: String): String? {
        if (languageCode != "en") return null
        return EnglishSpellCorrector.getCorrection(word)
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
