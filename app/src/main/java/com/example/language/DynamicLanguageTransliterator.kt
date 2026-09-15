package com.example.language

import org.json.JSONObject
import java.io.File

class DynamicLanguageTransliterator(
    override val languageCode: String,
    override val languageName: String,
    private val phoneticMap: Map<String, String>,
    private val dictionary: List<String> = emptyList(),
    private val phrases: Map<String, List<String>> = emptyMap()
) : LanguageTransliterator {

    override fun transliterate(englishInput: String): String {
        if (englishInput.isBlank()) return ""
        val lower = englishInput.trim().lowercase()

        // 1. Check exact map
        phoneticMap[lower]?.let { return it }

        // 2. Character-by-character replacement
        val sb = StringBuilder()
        var i = 0
        while (i < lower.length) {
            // Check 3-letter, 2-letter, 1-letter
            var matched = false
            for (len in 3 downTo 1) {
                if (i + len <= lower.length) {
                    val sub = lower.substring(i, i + len)
                    val mapped = phoneticMap[sub]
                    if (mapped != null) {
                        sb.append(mapped)
                        i += len
                        matched = true
                        break
                    }
                }
            }
            if (!matched) {
                sb.append(lower[i])
                i++
            }
        }
        return sb.toString()
    }

    override fun getCandidates(englishInput: String): List<String> {
        if (englishInput.isBlank()) return emptyList()
        val lower = englishInput.trim().lowercase()
        val result = LinkedHashSet<String>()

        phrases[lower]?.let { result.addAll(it) }

        phoneticMap[lower]?.let { result.add(it) }

        val converted = transliterate(englishInput)
        if (converted.isNotBlank()) result.add(converted)

        for ((k, v) in phoneticMap) {
            if (k.startsWith(lower) && k != lower && !result.contains(v)) {
                result.add(v)
                if (result.size >= 3) break
            }
        }

        for (w in dictionary) {
            if (w.lowercase().startsWith(lower) && !result.contains(w)) {
                result.add(w)
                if (result.size >= 3) break
            }
        }

        return result.take(3).toList()
    }

    companion object {
        fun fromDirectory(dir: File, code: String, name: String): DynamicLanguageTransliterator? {
            if (!dir.exists() || !dir.isDirectory) return null
            val mapFile = File(dir, "phonetic_map.json")
            val dictFile = File(dir, "dictionary.json")

            val map = mutableMapOf<String, String>()
            if (mapFile.exists()) {
                try {
                    val json = JSONObject(mapFile.readText())
                    val keys = json.keys()
                    while (keys.hasNext()) {
                        val k = keys.next()
                        map[k] = json.getString(k)
                    }
                } catch (_: Exception) {}
            }

            val dict = mutableListOf<String>()
            if (dictFile.exists()) {
                try {
                    val json = JSONObject(dictFile.readText())
                    val wordsArr = json.optJSONArray("words")
                    if (wordsArr != null) {
                        for (i in 0 until wordsArr.length()) {
                            dict.add(wordsArr.getString(i))
                        }
                    }
                } catch (_: Exception) {}
            }

            return DynamicLanguageTransliterator(code, name, map, dict)
        }
    }
}
