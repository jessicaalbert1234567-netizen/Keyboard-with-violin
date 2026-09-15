package com.example.language

class BengaliTransliterator : LanguageTransliterator {
    override val languageCode: String = "bn"
    override val languageName: String = "Bengali"

    // High frequency phonetic dictionary mapping for instant natural transliteration
    private val dictionary = mapOf(
        "ami" to "আমি",
        "amra" to "আমরা",
        "amar" to "আমার",
        "amader" to "আমাদের",
        "tumi" to "তুমি",
        "tomra" to "তোমরা",
        "tomar" to "তোমার",
        "tumar" to "তোমার",
        "tomader" to "তোমাদের",
        "apni" to "আপনি",
        "apnar" to "আপনার",
        "apnader" to "আপনাদের",
        "se" to "সে",
        "tini" to "তিনি",
        "tar" to "তার",
        "tader" to "তাদের",
        "bhalo" to "ভালো",
        "valo" to "ভালো",
        "bhaloachi" to "ভালো আছি",
        "valoachi" to "ভালো আছি",
        "kemon" to "কেমন",
        "acho" to "আছো",
        "achho" to "আছো",
        "achen" to "আছেন",
        "bangladesh" to "বাংলাদেশ",
        "bangla" to "বাংলা",
        "sonar" to "সোনার",
        "dhaka" to "ঢাকা",
        "shob" to "সব",
        "sob" to "সব",
        "khobor" to "খবর",
        "bondhu" to "বন্ধু",
        "dhonnobad" to "ধন্যবাদ",
        "shuvo" to "শুভ",
        "sokal" to "সকাল",
        "shokal" to "সকাল",
        "rat" to "রাত",
        "bhat" to "ভাত",
        "khaba" to "খাবা",
        "khabo" to "খাবো",
        "khabar" to "খাবার",
        "jai" to "যাই",
        "jabo" to "যাবো",
        "jabona" to "যাবো না",
        "kothai" to "কোথায়",
        "kothay" to "কোথায়",
        "ki" to "কি",
        "keno" to "কেন",
        "kokhon" to "কখন",
        "onek" to "অনেক",
        "sundor" to "সুন্দর",
        "shundor" to "সুন্দর",
        "manush" to "মানুষ",
        "desh" to "দেশ",
        "valobashi" to "ভালোবাসি",
        "bhalobashi" to "ভালোবাসি",
        "somoy" to "সময়",
        "kaj" to "কাজ",
        "bari" to "বাড়ি",
        "basha" to "বাসা",
        "bhasha" to "ভাষা",
        "ghor" to "ঘর",
        "pani" to "পানি",
        "jol" to "জল",
        "dada" to "দাদা",
        "bhai" to "ভাই",
        "bon" to "বোন",
        "baba" to "বাবা",
        "ma" to "মা",
        "kichu" to "কিছু",
        "ekhon" to "এখন",
        "tokhon" to "তখন",
        "jekhane" to "যেখানে",
        "shekhane" to "সেখানে",
        "shathe" to "সাথে",
        "sathe" to "সাথে",
        "boi" to "বই",
        "gaan" to "গান"
    )

    // Multi-letter and consonant rules (checked in greedy longest-prefix order)
    private val consonantRules = listOf(
        "kkhy" to "ক্ষ্য", "kkh" to "ক্ষ",
        "ggy" to "জ্ঞ", "jnh" to "জ্ঞ",
        "khr" to "খ্র", "ghr" to "ঘ্র", "phr" to "ফ্র",
        "k" to "ক", "kh" to "খ", "g" to "গ", "gh" to "ঘ", "ng" to "ং",
        "ch" to "ছ", "c" to "চ", "jh" to "ঝ", "j" to "জ",
        "th" to "থ", "dh" to "ধ", "bh" to "ভ", "ph" to "ফ",
        "sh" to "শ", "ss" to "ষ", "s" to "স", "h" to "হ",
        "t" to "ত", "d" to "দ",
        "T" to "ট", "Th" to "ঠ", "D" to "ড", "Dh" to "ঢ",
        "n" to "ন", "N" to "ণ",
        "p" to "প", "f" to "ফ", "b" to "ব", "v" to "ভ", "m" to "ম",
        "r" to "র", "R" to "ড়", "Rh" to "ঢ়",
        "l" to "ল",
        "w" to "ও",
        "z" to "জ", "Z" to "্য",
        "x" to "ক্স", "X" to "ক্ষ",
        "q" to "ক", "Q" to "ৎ"
    )

    // Single character QWERTY fallback map for keys Q through M
    // Guarantees every single English letter produces a valid Bengali character
    private val singleKeyBengaliMap = mapOf(
        'q' to "ক", 'w' to "ও", 'e' to "এ", 'r' to "র", 't' to "ত",
        'y' to "য", 'u' to "উ", 'i' to "ই", 'o' to "ও", 'p' to "প",
        'a' to "আ", 's' to "স", 'd' to "দ", 'f' to "ফ", 'g' to "গ",
        'h' to "হ", 'j' to "জ", 'k' to "ক", 'l' to "ল",
        'z' to "জ", 'x' to "ক্স", 'c' to "চ", 'v' to "ভ", 'b' to "ব",
        'n' to "ন", 'm' to "ম",
        // Capital (Shifted) QWERTY letters
        'Q' to "ৎ", 'W' to "ও", 'E' to "ঈ", 'R' to "ড়", 'T' to "ট",
        'Y' to "য়", 'U' to "ঊ", 'I' to "ঈ", 'O' to "ঔ", 'P' to "ফ",
        'A' to "অ", 'S' to "শ", 'D' to "ড", 'F' to "ফ", 'G' to "ঘ",
        'H' to "ঃ", 'J' to "ঝ", 'K' to "খ", 'L' to "ল",
        'Z' to "্য", 'X' to "ক্ষ", 'C' to "ছ", 'V' to "ভ", 'B' to "ব",
        'N' to "ণ", 'M' to "ং"
    )

    private val vowelMapInitial = mapOf(
        "aa" to "আ", "a" to "আ", "ee" to "ঈ", "i" to "ই",
        "oo" to "ঊ", "u" to "উ", "e" to "এ", "o" to "ও",
        "oi" to "ঐ", "ou" to "ঔ", "ri" to "ঋ"
    )

    private val vowelMapKar = mapOf(
        "aa" to "া", "a" to "া", "ee" to "ী", "i" to "ি",
        "oo" to "ূ", "u" to "ু", "e" to "ে", "o" to "ো",
        "oi" to "ৈ", "ou" to "ৌ", "ri" to "ৃ"
    )

    private val contextualCandidates = mapOf(
        "ami" to listOf("আমি", "আমি তো", "আমার"),
        "tumi" to listOf("তুমি", "তোমার", "তোমাকে"),
        "apni" to listOf("আপনি", "আপনার", "আপনাকে"),
        "amra" to listOf("আমরা", "আমাদের", "আমাদের সাথে"),
        "valo" to listOf("ভালো", "ভালো আছি", "ভালো আছো"),
        "bhalo" to listOf("ভালো", "ভালো আছি", "ভালো আছো"),
        "kemon" to listOf("কেমন", "কেমন আছো", "কেমন আছেন"),
        "kothay" to listOf("কোথায়", "কোথায় আছো", "কোথায় যাবেন"),
        "kothai" to listOf("কোথায়", "কোথায় আছো", "কোথায় যাবেন"),
        "dhonnobad" to listOf("ধন্যবাদ", "অনেক ধন্যবাদ", "ধন্যবাদ আপনাকে"),
        "shuvo" to listOf("শুভ", "শুভ সকাল", "শুভ রাত্রি")
    )

    override fun transliterate(englishInput: String): String {
        if (englishInput.isBlank()) return ""
        val trimmed = englishInput.trim()
        val lower = trimmed.lowercase()

        // 1. Direct dictionary match
        dictionary[lower]?.let { return it }

        // 2. Single key direct check (if exact single letter typed)
        if (trimmed.length == 1) {
            singleKeyBengaliMap[trimmed[0]]?.let { return it }
        }

        // 3. Rule based phonetic transliteration
        return ruleBasedTransliterate(trimmed)
    }

    override fun getCandidates(englishInput: String): List<String> {
        if (englishInput.isBlank()) return emptyList()
        val trimmed = englishInput.trim()
        val lower = trimmed.lowercase()
        val result = LinkedHashSet<String>()

        // 1. Contextual candidates (prioritized top suggestions like "আমি", "আমি তো", "আমার")
        contextualCandidates[lower]?.let { result.addAll(it) }

        // 2. Exact or prefix match from dictionary
        dictionary[lower]?.let { result.add(it) }

        // 3. Rule based conversion
        val ruleBased = ruleBasedTransliterate(trimmed)
        if (ruleBased.isNotBlank()) {
            result.add(ruleBased)
        }

        // 4. Dictionary completions
        for ((k, v) in dictionary) {
            if (k.startsWith(lower) && k != lower && !result.contains(v)) {
                result.add(v)
                if (result.size >= 4) break
            }
        }
        return result.take(3).toList()
    }

    private fun ruleBasedTransliterate(input: String): String {
        val sb = StringBuilder()
        var i = 0
        var prevWasConsonant = false

        while (i < input.length) {
            // Check special diphthong sequences like "ay" -> "ায়", "oy" -> "য়"
            if (prevWasConsonant && input.startsWith("ay", i, ignoreCase = true)) {
                sb.append("ায়")
                i += 2
                prevWasConsonant = false
                continue
            }
            if (prevWasConsonant && input.startsWith("oy", i, ignoreCase = true)) {
                sb.append("য়")
                i += 2
                prevWasConsonant = false
                continue
            }

            // Check multi-letter consonants in order
            var matchedConsonant = false
            for ((pat, replacement) in consonantRules) {
                if (input.startsWith(pat, i, ignoreCase = pat.all { it.isLowerCase() })) {
                    sb.append(replacement)
                    i += pat.length
                    prevWasConsonant = true
                    matchedConsonant = true
                    break
                }
            }
            if (matchedConsonant) continue

            // Check vowels (length 2 down to 1)
            var matchedVowel = false
            for (len in 2 downTo 1) {
                if (i + len <= input.length) {
                    val sub = input.substring(i, i + len).lowercase()
                    if (prevWasConsonant) {
                        vowelMapKar[sub]?.let { kar ->
                            sb.append(kar)
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

            // Check single key mapping fallback (never leave English letter Q-M unmapped)
            val char = input[i]
            val mapped = singleKeyBengaliMap[char] ?: singleKeyBengaliMap[char.lowercaseChar()]
            if (mapped != null) {
                sb.append(mapped)
                prevWasConsonant = true
            } else {
                sb.append(char)
                prevWasConsonant = false
            }
            i++
        }
        return sb.toString()
    }
}
