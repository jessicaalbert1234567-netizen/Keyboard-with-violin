package com.example.suggestions

import kotlin.math.min

object SpellCheckEngine {

    // Common English dictionary words with frequency weight
    private val englishDictionaryWithWeights: Map<String, Int> = mapOf(
        "the" to 100, "be" to 95, "to" to 94, "of" to 93, "and" to 92, "a" to 91,
        "in" to 90, "that" to 89, "have" to 88, "it" to 87, "for" to 86, "not" to 85,
        "on" to 84, "with" to 83, "he" to 82, "as" to 81, "you" to 80, "do" to 79,
        "at" to 78, "this" to 77, "but" to 76, "his" to 75, "by" to 74, "from" to 73,
        "they" to 72, "we" to 71, "say" to 70, "her" to 69, "she" to 68, "or" to 67,
        "an" to 66, "will" to 65, "my" to 64, "one" to 63, "all" to 62, "would" to 61,
        "there" to 60, "their" to 59, "what" to 58, "so" to 57, "up" to 56, "out" to 55,
        "if" to 54, "about" to 53, "who" to 52, "get" to 51, "which" to 50, "go" to 49,
        "me" to 48, "when" to 47, "make" to 46, "can" to 45, "like" to 44, "time" to 43,
        "no" to 42, "just" to 41, "him" to 40, "know" to 39, "take" to 38, "people" to 37,
        "into" to 36, "year" to 35, "your" to 34, "good" to 33, "some" to 32, "could" to 31,
        "them" to 30, "see" to 29, "other" to 28, "than" to 27, "then" to 26, "now" to 25,
        "look" to 24, "only" to 23, "come" to 22, "its" to 21, "over" to 20, "think" to 19,
        "also" to 18, "back" to 17, "after" to 16, "use" to 15, "two" to 14, "how" to 13,
        "our" to 12, "work" to 11, "first" to 10, "well" to 9, "way" to 8, "even" to 7,
        "new" to 6, "want" to 5, "because" to 40, "any" to 20, "these" to 20, "give" to 20,
        "day" to 20, "most" to 20, "us" to 20, "hello" to 35, "help" to 30, "held" to 15,
        "here" to 30, "happy" to 25, "hope" to 20, "keyboard" to 30, "android" to 30,
        "awesome" to 25, "beautiful" to 25, "please" to 30, "thanks" to 30, "thank" to 35,
        "welcome" to 25, "morning" to 20, "friend" to 25, "receive" to 30, "separate" to 25,
        "definitely" to 25, "grammar" to 20, "tomorrow" to 25, "weather" to 20, "until" to 25,
        "government" to 20, "truly" to 20, "believe" to 25, "coming" to 25, "going" to 30,
        "something" to 30, "nothing" to 25, "anything" to 25, "everything" to 25,
        "important" to 25, "different" to 20, "together" to 20, "always" to 25, "never" to 25
    )

    /**
     * Calculates Damerau-Levenshtein distance (handles insertions, deletions, substitutions, and adjacent transpositions)
     */
    fun damerauLevenshteinDistance(source: String, target: String): Int {
        val sLen = source.length
        val tLen = target.length
        if (sLen == 0) return tLen
        if (tLen == 0) return sLen

        val dp = Array(sLen + 1) { IntArray(tLen + 1) }

        for (i in 0..sLen) dp[i][0] = i
        for (j in 0..tLen) dp[0][j] = j

        for (i in 1..sLen) {
            for (j in 1..tLen) {
                val cost = if (source[i - 1] == target[j - 1]) 0 else 1
                dp[i][j] = min(
                    min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                )
                // Adjacent character transposition
                if (i > 1 && j > 1 &&
                    source[i - 1] == target[j - 2] &&
                    source[i - 2] == target[j - 1]
                ) {
                    dp[i][j] = min(dp[i][j], dp[i - 2][j - 2] + 1)
                }
            }
        }
        return dp[sLen][tLen]
    }

    /**
     * Finds candidates within edit distance <= maxDistance, sorted by edit distance then frequency
     */
    fun findCandidates(
        input: String,
        dictionary: Map<String, Int> = englishDictionaryWithWeights,
        maxDistance: Int = 2,
        limit: Int = 3
    ): List<String> {
        val clean = input.lowercase().trim()
        if (clean.length < 2) return emptyList()

        // 1. Direct typo map check
        EnglishSpellCorrector.getCorrection(clean)?.let { return listOf(it) }

        // 2. If exact word exists in dictionary, return it as top candidate
        if (dictionary.containsKey(clean)) {
            return listOf(clean)
        }

        // 3. Edit distance candidates
        data class Candidate(val word: String, val distance: Int, val weight: Int)

        val candidates = mutableListOf<Candidate>()
        for ((word, weight) in dictionary) {
            // Optimization: skip words with length difference > maxDistance
            if (kotlin.math.abs(word.length - clean.length) > maxDistance) continue
            // Skip words with completely different first letter unless length is short
            if (clean.length > 3 && word[0] != clean[0]) continue

            val dist = damerauLevenshteinDistance(clean, word)
            if (dist <= maxDistance) {
                candidates.add(Candidate(word, dist, weight))
            }
        }

        return candidates
            .sortedWith(compareBy<Candidate> { it.distance }.thenByDescending { it.weight })
            .map { it.word }
            .take(limit)
    }
}
