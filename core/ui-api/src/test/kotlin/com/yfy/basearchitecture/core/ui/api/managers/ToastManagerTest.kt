package com.yfy.basearchitecture.core.ui.api.managers

import com.yfy.basearchitecture.core.ui.api.handler.SnackbarDuration
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue

class ToastManagerTest {

    @Test
    fun `should have correct toast duration values`() {
        // Then
        assertEquals(0, ToastDuration.SHORT.ordinal)
        assertEquals(1, ToastDuration.LONG.ordinal)
    }

    @Test
    fun `should have correct snackbar duration values`() {
        // Then
        assertEquals(0, SnackbarDuration.SHORT.ordinal)
        assertEquals(1, SnackbarDuration.LONG.ordinal)
        assertEquals(2, SnackbarDuration.INDEFINITE.ordinal)
    }

    @Test
    fun `should have correct loader type values`() {
        // Then
        assertEquals(0, LoaderType.CIRCULAR.ordinal)
        assertEquals(1, LoaderType.LINEAR.ordinal)
        assertEquals(2, LoaderType.DOTS.ordinal)
        assertEquals(3, LoaderType.PULSE.ordinal)
        assertEquals(4, LoaderType.CUSTOM.ordinal)
    }

    @Test
    fun `should have correct dialog type values`() {
        // Then
        assertEquals(0, DialogType.ALERT.ordinal)
        assertEquals(1, DialogType.CONFIRMATION.ordinal)
        assertEquals(2, DialogType.INPUT.ordinal)
        assertEquals(3, DialogType.LOADING.ordinal)
        assertEquals(4, DialogType.CUSTOM.ordinal)
    }

    @Test
    fun `should have correct supported languages`() {
        // Then
        assertEquals("en", SupportedLanguage.ENGLISH.code)
        assertEquals("tr", SupportedLanguage.TURKISH.code)
        assertEquals("English", SupportedLanguage.ENGLISH.displayName)
        assertEquals("Türkçe", SupportedLanguage.TURKISH.displayName)
    }

    @Test
    fun `should find language by code`() {
        // When & Then
        assertEquals(SupportedLanguage.ENGLISH, SupportedLanguage.fromCode("en"))
        assertEquals(SupportedLanguage.TURKISH, SupportedLanguage.fromCode("tr"))
        assertNull(SupportedLanguage.fromCode("invalid"))
    }

    @Test
    fun `should get all language codes`() {
        // When
        val codes = SupportedLanguage.entries.map { it.code }
        
        // Then
        assertEquals(7, codes.size)
        assertTrue(codes.contains("en"))
        assertTrue(codes.contains("tr"))
    }

    @Test
    fun `should get all display names`() {
        // When
        val displayNames = SupportedLanguage.entries.map { it.displayName }
        
        // Then
        assertEquals(7, displayNames.size)
        assertTrue(displayNames.contains("English"))
        assertTrue(displayNames.contains("Türkçe"))
    }
} 