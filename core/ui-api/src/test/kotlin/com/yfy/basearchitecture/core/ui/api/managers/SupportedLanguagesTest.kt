package com.yfy.basearchitecture.core.ui.api.managers

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(JUnit4::class)
class SupportedLanguagesTest {

    @Test
    fun `test all SupportedLanguage enum values`() {
        // Test all enum values with their properties
        val english = SupportedLanguage.ENGLISH
        val turkish = SupportedLanguage.TURKISH
        val german = SupportedLanguage.GERMAN
        val french = SupportedLanguage.FRENCH
        val russian = SupportedLanguage.RUSSIAN
        val arabic = SupportedLanguage.ARABIC
        val spanish = SupportedLanguage.SPANISH

        // Test English
        assertEquals("en", english.code)
        assertEquals("English", english.displayName)
        assertFalse(english.isRTL)

        // Test Turkish
        assertEquals("tr", turkish.code)
        assertEquals("Türkçe", turkish.displayName)
        assertFalse(turkish.isRTL)

        // Test German
        assertEquals("de", german.code)
        assertEquals("Deutsch", german.displayName)
        assertFalse(german.isRTL)

        // Test French
        assertEquals("fr", french.code)
        assertEquals("Français", french.displayName)
        assertFalse(french.isRTL)

        // Test Russian
        assertEquals("ru", russian.code)
        assertEquals("Русский", russian.displayName)
        assertFalse(russian.isRTL)

        // Test Arabic (RTL language)
        assertEquals("ar", arabic.code)
        assertEquals("العربية", arabic.displayName)
        assertTrue(arabic.isRTL)

        // Test Spanish
        assertEquals("es", spanish.code)
        assertEquals("Español", spanish.displayName)
        assertFalse(spanish.isRTL)
    }

    @Test
    fun `test fromCode with valid codes`() {
        // Test all valid language codes
        assertEquals(SupportedLanguage.ENGLISH, SupportedLanguage.fromCode("en"))
        assertEquals(SupportedLanguage.TURKISH, SupportedLanguage.fromCode("tr"))
        assertEquals(SupportedLanguage.GERMAN, SupportedLanguage.fromCode("de"))
        assertEquals(SupportedLanguage.FRENCH, SupportedLanguage.fromCode("fr"))
        assertEquals(SupportedLanguage.RUSSIAN, SupportedLanguage.fromCode("ru"))
        assertEquals(SupportedLanguage.ARABIC, SupportedLanguage.fromCode("ar"))
        assertEquals(SupportedLanguage.SPANISH, SupportedLanguage.fromCode("es"))
    }

    @Test
    fun `test fromCode with invalid codes`() {
        // Test invalid language codes
        assertNull(SupportedLanguage.fromCode("invalid"))
        assertNull(SupportedLanguage.fromCode(""))
        assertNull(SupportedLanguage.fromCode("xx"))
        assertNull(SupportedLanguage.fromCode("EN")) // Case sensitive
        assertNull(SupportedLanguage.fromCode("en-US")) // Extended locale
    }

    @Test
    fun `test getDefault returns English`() {
        // When
        val defaultLanguage = SupportedLanguage.getDefault()

        // Then
        assertEquals(SupportedLanguage.ENGLISH, defaultLanguage)
    }

    @Test
    fun `test getRTLanguages returns only RTL languages`() {
        // When
        val rtlLanguages = SupportedLanguage.getRTLanguages()

        // Then
        assertEquals(1, rtlLanguages.size)
        assertTrue(rtlLanguages.contains(SupportedLanguage.ARABIC))
        assertFalse(rtlLanguages.contains(SupportedLanguage.ENGLISH))
        assertFalse(rtlLanguages.contains(SupportedLanguage.TURKISH))
        assertFalse(rtlLanguages.contains(SupportedLanguage.GERMAN))
        assertFalse(rtlLanguages.contains(SupportedLanguage.FRENCH))
        assertFalse(rtlLanguages.contains(SupportedLanguage.RUSSIAN))
        assertFalse(rtlLanguages.contains(SupportedLanguage.SPANISH))
    }

    @Test
    fun `test isRTL with RTL language code`() {
        // Test RTL language
        assertTrue(SupportedLanguage.isRTL("ar"))
    }

    @Test
    fun `test isRTL with non-RTL language codes`() {
        // Test non-RTL languages
        assertFalse(SupportedLanguage.isRTL("en"))
        assertFalse(SupportedLanguage.isRTL("tr"))
        assertFalse(SupportedLanguage.isRTL("de"))
        assertFalse(SupportedLanguage.isRTL("fr"))
        assertFalse(SupportedLanguage.isRTL("ru"))
        assertFalse(SupportedLanguage.isRTL("es"))
    }

    @Test
    fun `test isRTL with invalid language code`() {
        // Test invalid language codes
        assertFalse(SupportedLanguage.isRTL("invalid"))
        assertFalse(SupportedLanguage.isRTL(""))
        assertFalse(SupportedLanguage.isRTL("xx"))
    }

    @Test
    fun `test enum entries contain all languages`() {
        // When
        val entries = SupportedLanguage.entries

        // Then
        assertEquals(7, entries.size)
        assertTrue(entries.contains(SupportedLanguage.ENGLISH))
        assertTrue(entries.contains(SupportedLanguage.TURKISH))
        assertTrue(entries.contains(SupportedLanguage.GERMAN))
        assertTrue(entries.contains(SupportedLanguage.FRENCH))
        assertTrue(entries.contains(SupportedLanguage.RUSSIAN))
        assertTrue(entries.contains(SupportedLanguage.ARABIC))
        assertTrue(entries.contains(SupportedLanguage.SPANISH))
    }

    @Test
    fun `test enum values are unique`() {
        // When
        val entries = SupportedLanguage.entries
        val codes = entries.map { it.code }
        val displayNames = entries.map { it.displayName }

        // Then
        assertEquals(codes.size, codes.distinct().size) // All codes are unique
        assertEquals(displayNames.size, displayNames.distinct().size) // All display names are unique
    }

    @Test
    fun `test enum serialization compatibility`() {
        // Test that all enum values can be serialized/deserialized
        val allLanguages = SupportedLanguage.entries
        
        for (language in allLanguages) {
            // Test that the enum value can be converted to string and back
            val languageName = language.name
            val enumValue = SupportedLanguage.valueOf(languageName)
            assertEquals(language, enumValue)
        }
    }
} 