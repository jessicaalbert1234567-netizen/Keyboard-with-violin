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
    fun testKeyboardLayoutRows() {
        val rows = KeyboardLayoutProvider.getEnglishAlphaRows(isShifted = false, isCapsLock = false, showNumberRow = true)
        assertTrue(rows.isNotEmpty())
        assertEquals(5, rows.size) // 1 number row + 3 letter rows + 1 bottom row = 5 rows
        val spaceLabel = KeyboardLayoutProvider.getSpaceLabel("bn", com.example.data.settings.KeyboardInputMode.NATIVE)
        assertEquals("বাংলা", spaceLabel)
    }
}
