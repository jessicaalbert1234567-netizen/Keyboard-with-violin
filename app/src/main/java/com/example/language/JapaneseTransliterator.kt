package com.example.language

class JapaneseTransliterator : LanguageTransliterator {
    override val languageCode: String = "ja"
    override val languageName: String = "Japanese"

    private val dictionary = mapOf(
        "ohayou" to "おはよう",
        "konnichiwa" to "こんにちは",
        "arigatou" to "ありがとう",
        "sayounara" to "さようなら",
        "hai" to "はい",
        "iie" to "いいえ",
        "sumimasen" to "すみません",
        "gomen" to "ごめん",
        "oyasumi" to "おやすみ",
        "genki" to "元気",
        "nihon" to "日本"
    )

    private val candidateSuggestions = mapOf(
        "ohayou" to listOf("おはよう", "おはようございます", "おやすみ"),
        "konnichiwa" to listOf("こんにちは", "こんばんは", "こんばんわ"),
        "arigatou" to listOf("ありがとう", "ありがとうございます", "どうも")
    )

    private val romajiToHiragana = listOf(
        "kya" to "きゃ", "kyu" to "きゅ", "kyo" to "きょ",
        "sha" to "しゃ", "shu" to "しゅ", "sho" to "しょ",
        "cha" to "ちゃ", "chu" to "ちゅ", "cho" to "ちょ",
        "nya" to "にゃ", "nyu" to "にゅ", "nyo" to "にょ",
        "hya" to "ひゃ", "hyu" to "ひゅ", "hyo" to "ひょ",
        "mya" to "みゃ", "myu" to "みゅ", "myo" to "みょ",
        "rya" to "りゃ", "ryu" to "りゅ", "ryo" to "りょ",
        "gya" to "ぎゃ", "gyu" to "ぎゅ", "gyo" to "ぎょ",
        "bya" to "びゃ", "byu" to "びゅ", "byo" to "びょ",
        "pya" to "ぴゃ", "pyu" to "ぴゅ", "pyo" to "ぴょ",
        "shi" to "し", "chi" to "ち", "tsu" to "つ", "fu" to "ふ",
        "ka" to "か", "ki" to "き", "ku" to "く", "ke" to "け", "ko" to "こ",
        "sa" to "さ", "su" to "す", "se" to "せ", "so" to "そ",
        "ta" to "た", "te" to "て", "to" to "と",
        "na" to "な", "ni" to "に", "nu" to "ぬ", "ne" to "ね", "no" to "の",
        "ha" to "は", "hi" to "ひ", "he" to "へ", "ho" to "ほ",
        "ma" to "ま", "mi" to "み", "mu" to "む", "me" to "め", "mo" to "も",
        "ya" to "や", "yu" to "ゆ", "yo" to "よ",
        "ra" to "ら", "ri" to "り", "ru" to "る", "re" to "れ", "ro" to "ろ",
        "wa" to "わ", "wo" to "を", "nn" to "ん",
        "ga" to "が", "gi" to "ぎ", "gu" to "ぐ", "ge" to "げ", "go" to "ご",
        "za" to "ざ", "ji" to "じ", "zu" to "ず", "ze" to "ぜ", "zo" to "ぞ",
        "da" to "だ", "de" to "で", "do" to "ど",
        "ba" to "ば", "bi" to "び", "bu" to "ぶ", "be" to "べ", "bo" to "ぼ",
        "pa" to "ぱ", "pi" to "ぴ", "pu" to "ぷ", "pe" to "ぺ", "po" to "ぽ",
        "a" to "あ", "i" to "い", "u" to "う", "e" to "え", "o" to "お", "n" to "ん"
    )

    override fun transliterate(englishInput: String): String {
        if (englishInput.isBlank()) return ""
        val lower = englishInput.trim().lowercase()
        dictionary[lower]?.let { return it }

        val sb = StringBuilder()
        var i = 0
        while (i < lower.length) {
            // Check double consonants for small tsu (っ)
            if (i + 1 < lower.length && lower[i] == lower[i + 1] && lower[i] !in "aeiouyn") {
                sb.append("っ")
                i++
                continue
            }
            var matched = false
            for ((pat, rep) in romajiToHiragana) {
                if (lower.startsWith(pat, i)) {
                    sb.append(rep)
                    i += pat.length
                    matched = true
                    break
                }
            }
            if (matched) continue
            sb.append(lower[i])
            i++
        }
        return sb.toString()
    }

    override fun getCandidates(englishInput: String): List<String> {
        if (englishInput.isBlank()) return emptyList()
        val lower = englishInput.trim().lowercase()
        val result = LinkedHashSet<String>()

        candidateSuggestions[lower]?.let { result.addAll(it) }
        dictionary[lower]?.let { result.add(it) }

        val ruleBased = transliterate(englishInput)
        if (ruleBased.isNotBlank()) result.add(ruleBased)

        for ((k, v) in dictionary) {
            if (k.startsWith(lower) && k != lower) {
                result.add(v)
                if (result.size >= 3) break
            }
        }
        return result.take(3).toList()
    }
}
