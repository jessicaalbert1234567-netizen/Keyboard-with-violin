package com.example.suggestions

object EnglishSpellCorrector {

    // Common English misspellings and contractions
    private val typoMap = mapOf(
        "teh" to "the",
        "recieve" to "receive",
        "recieved" to "received",
        "recieving" to "receiving",
        "becuase" to "because",
        "dont" to "don't",
        "wont" to "won't",
        "im" to "I'm",
        "cant" to "can't",
        "didnt" to "didn't",
        "doesnt" to "doesn't",
        "couldnt" to "couldn't",
        "shouldnt" to "shouldn't",
        "wouldnt" to "wouldn't",
        "isnt" to "isn't",
        "arent" to "aren't",
        "wasnt" to "wasn't",
        "werent" to "weren't",
        "hasnt" to "hasn't",
        "havent" to "haven't",
        "hadnt" to "hadn't",
        "youre" to "you're",
        "theyre" to "they're",
        "weve" to "we've",
        "youve" to "you've",
        "ive" to "I've",
        "ill" to "I'll",
        "youll" to "you'll",
        "theyll" to "they'll",
        "id" to "I'd",
        "adn" to "and",
        "taht" to "that",
        "thier" to "their",
        "hte" to "the",
        "waht" to "what",
        "wierd" to "weird",
        "seperate" to "separate",
        "occured" to "occurred",
        "untill" to "until",
        "definately" to "definitely",
        "beleive" to "believe",
        "tommorrow" to "tomorrow",
        "tommorow" to "tomorrow",
        "grammer" to "grammar",
        "wether" to "weather",
        "alot" to "a lot",
        "goverment" to "government",
        "accomodate" to "accommodate",
        "truely" to "truly",
        "peopel" to "people",
        "freind" to "friend",
        "wich" to "which",
        "thna" to "than",
        "jsut" to "just"
    )

    fun getCorrection(rawWord: String): String? {
        if (rawWord.length < 2) return null
        val lower = rawWord.lowercase()
        val corrected = typoMap[lower] ?: return null

        // Preserve case
        return when {
            rawWord.all { it.isUpperCase() } -> corrected.uppercase()
            rawWord[0].isUpperCase() -> corrected.replaceFirstChar { it.uppercase() }
            else -> corrected
        }
    }

    fun isKnownTypo(rawWord: String): Boolean {
        return typoMap.containsKey(rawWord.lowercase())
    }
}
