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
        "jai" to "যাই",
        "jabona" to "যাবো না",
        "kothai" to "কোথায়",
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
        "ghor" to "ঘর",
        "pani" to "পানি",
        "jol" to "জল"
    )

    // Phonetic rule replacement chunks
    private val consonantRules = listOf(
        "kkh" to "ক্ষ", "kkhy" to "ক্ষ্য",
        "kh" to "খ", "gh" to "ঘ", "ng" to "ঙ",
        "ch" to "ছ", "c" to "চ", "jh" to "ঝ", "j" to "জ",
        "th" to "থ", "dh" to "ধ", "bh" to "ভ", "ph" to "ফ",
        "sh" to "শ", "ss" to "ষ", "s" to "স", "h" to "হ",
        "k" to "ক", "g" to "গ", "t" to "ত", "d" to "দ",
        "n" to "ন", "p" to "প", "b" to "ব", "m" to "ম",
        "r" to "র", "l" to "ল", "y" to "য", "w" to "ও"
    )

    private val vowelMapInitial = mapOf(
        "a" to "আ", "aa" to "আ", "i" to "ই", "ee" to "ঈ",
        "u" to "উ", "oo" to "ঊ", "e" to "এ", "o" to "ও", "oi" to "ঐ", "ou" to "ঔ"
    )

    private val vowelMapKar = mapOf(
        "a" to "া", "aa" to "া", "i" to "ি", "ee" to "ী",
        "u" to "ু", "oo" to "ূ", "e" to "ে", "o" to "ো", "oi" to "ৈ", "ou" to "ৌ"
    )

    override fun transliterate(englishInput: String): String {
        if (englishInput.isBlank()) return ""
        val lower = englishInput.lowercase().trim()
        dictionary[lower]?.let { return it }

        // Rule based fallback phonetic transliteration
        return ruleBasedTransliterate(lower)
    }

    override fun getCandidates(englishInput: String): List<String> {
        if (englishInput.isBlank()) return emptyList()
        val lower = englishInput.lowercase().trim()
        val result = mutableListOf<String>()

        // 1. Exact or prefix match from dictionary
        dictionary[lower]?.let { result.add(it) }

        // 2. Rule based conversion
        val ruleBased = ruleBasedTransliterate(lower)
        if (!result.contains(ruleBased)) {
            result.add(ruleBased)
        }

        // 3. Dictionary completions
        for ((k, v) in dictionary) {
            if (k.startsWith(lower) && k != lower && !result.contains(v)) {
                result.add(v)
                if (result.size >= 4) break
            }
        }
        return result
    }

    private fun ruleBasedTransliterate(input: String): String {
        val sb = StringBuilder()
        var i = 0
        var prevWasConsonant = false

        while (i < input.length) {
            // Check dictionary prefixes or digraphs
            var matched = false
            for ((pat, replacement) in consonantRules) {
                if (input.startsWith(pat, i)) {
                    sb.append(replacement)
                    i += pat.length
                    prevWasConsonant = true
                    matched = true
                    break
                }
            }
            if (matched) continue

            // Check vowels
            for (len in 2 downTo 1) {
                if (i + len <= input.length) {
                    val sub = input.substring(i, i + len)
                    if (prevWasConsonant) {
                        vowelMapKar[sub]?.let { kar ->
                            sb.append(kar)
                            i += len
                            prevWasConsonant = false
                            matched = true
                        }
                    } else {
                        vowelMapInitial[sub]?.let { initial ->
                            sb.append(initial)
                            i += len
                            prevWasConsonant = false
                            matched = true
                        }
                    }
                    if (matched) break
                }
            }
            if (matched) continue

            // Fallback: character verbatim
            sb.append(input[i])
            prevWasConsonant = false
            i++
        }
        return sb.toString()
    }
}
