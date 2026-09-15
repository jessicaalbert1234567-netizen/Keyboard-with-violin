package com.example

import com.example.keyboard.KeyboardLayoutProvider
import com.example.language.BengaliTransliterator
import com.example.suggestions.EnglishSpellCorrector
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    private val transliterator = BengaliTransliterator()

    @Test
    fun testBengaliKeysQtoM_produceValidBengaliCharacters() {
        val qwertyLetters = ('a'..'z') + ('A'..'Z')
        for (char in qwertyLetters) {
            val output = transliterator.transliterate(char.toString())
            assertNotNull("Output should not be null for $char", output)
            assertTrue("Output should not be empty for $char", output.isNotEmpty())
            // Ensure none of the English keys output raw English letters
            for (outChar in output) {
                assertFalse(
                    "Output for key '$char' produced English character '$outChar'",
                    outChar in 'a'..'z' || outChar in 'A'..'Z'
                )
            }
        }
    }

    @Test
    fun testCommonBengaliPhoneticWords() {
        assertEquals("আমার", transliterator.transliterate("amar"))
        assertEquals("আমি", transliterator.transliterate("ami"))
        assertEquals("তুমি", transliterator.transliterate("tumi"))
        assertEquals("বাংলা", transliterator.transliterate("bangla"))
        assertEquals("ভাষা", transliterator.transliterate("bhasha"))
        assertEquals("ধন্যবাদ", transliterator.transliterate("dhonnobad"))
        assertEquals("ভালো", transliterator.transliterate("bhalo"))
        assertEquals("কেমন", transliterator.transliterate("kemon"))
        assertEquals("কোথায়", transliterator.transliterate("kothay"))
        assertEquals("থাক", transliterator.transliterate("thak"))
    }

    @Test
    fun testBengaliCandidatesDoNotDuplicate() {
        val candidates = transliterator.getCandidates("kothay")
        assertTrue("Candidates should contain কোথায়", candidates.contains("কোথায়"))
        val distinct = candidates.distinct()
        assertEquals("Candidates must not have duplicates", distinct.size, candidates.size)
    }

    @Test
    fun testHindiTransliterator() {
        val hindi = com.example.language.HindiTransliterator()
        assertEquals("नमस्ते", hindi.transliterate("namaste"))
        assertEquals("भारत", hindi.transliterate("bharat"))
    }

    @Test
    fun testDownloadedLanguageSpaceLabel() {
        val hindiPack = com.example.language.LanguagePack(
            id = "hi",
            name = "Hindi",
            nativeName = "हिन्दी",
            version = 1,
            fileSizeFormatted = "1.2 MB",
            status = com.example.language.PackDownloadStatus.INSTALLED,
            hasNativeLayout = true,
            hasPhoneticMode = true
        )
        val nativeSpace = KeyboardLayoutProvider.getSpaceLabel("hi", com.example.data.settings.KeyboardInputMode.NATIVE, hindiPack)
        assertEquals("हिन्दी", nativeSpace)

        val phoneticSpace = KeyboardLayoutProvider.getSpaceLabel("hi", com.example.data.settings.KeyboardInputMode.PHONETIC, hindiPack)
        assertEquals("हिन्दी (Phonetic)", phoneticSpace)
    }

    @Test
    fun testEnglishSpellCorrection() {
        assertEquals("the", EnglishSpellCorrector.getCorrection("teh"))
        assertEquals("receive", EnglishSpellCorrector.getCorrection("recieve"))
        assertEquals("separate", EnglishSpellCorrector.getCorrection("seperate"))
        assertEquals("definitely", EnglishSpellCorrector.getCorrection("definately"))
        assertEquals("grammar", EnglishSpellCorrector.getCorrection("grammer"))
        assertEquals("until", EnglishSpellCorrector.getCorrection("untill"))
        assertEquals("tomorrow", EnglishSpellCorrector.getCorrection("tommorow"))
        assertEquals("weather", EnglishSpellCorrector.getCorrection("wether"))
        // Correct word should not be modified
        assertNull(EnglishSpellCorrector.getCorrection("hello"))
    }

    @Test
    fun testSpellCheckEngineDamerauLevenshtein() {
        val distance = com.example.suggestions.SpellCheckEngine.damerauLevenshteinDistance("teh", "the")
        assertEquals(1, distance)

        val candidates = com.example.suggestions.SpellCheckEngine.findCandidates("helo", limit = 2)
        assertTrue(candidates.contains("hello") || candidates.contains("help"))
    }

    @Test
    fun testBengaliCandidatesAmiAndTumi() {
        val amiCandidates = transliterator.getCandidates("ami")
        assertTrue("ami candidates should contain আমি", amiCandidates.contains("আমি"))
        assertTrue("ami candidates should have up to 3 options", amiCandidates.size in 1..3)

        val tumiCandidates = transliterator.getCandidates("tumi")
        assertTrue("tumi candidates should contain তুমি", tumiCandidates.contains("তুমি"))
    }

    @Test
    fun testMultiLanguageTransliterators() {
        val arabic = com.example.language.ArabicTransliterator()
        val arabicRes = arabic.transliterate("salam")
        assertTrue("Arabic transliteration should not be empty", arabicRes.isNotEmpty())

        val russian = com.example.language.RussianTransliterator()
        val russianRes = russian.transliterate("privet")
        assertTrue("Russian transliteration should not be empty", russianRes.isNotEmpty())
    }

    @Test
    fun testKeyboardLayoutRows() {
        val rows = KeyboardLayoutProvider.getEnglishAlphaRows(isShifted = false, isCapsLock = false, showNumberRow = true)
        assertTrue(rows.isNotEmpty())
        assertEquals(5, rows.size) // 1 number row + 3 letter rows + 1 bottom row = 5 rows
        val spaceLabel = KeyboardLayoutProvider.getSpaceLabel("bn", com.example.data.settings.KeyboardInputMode.NATIVE)
        assertEquals("বাংলা", spaceLabel)
    }
}
