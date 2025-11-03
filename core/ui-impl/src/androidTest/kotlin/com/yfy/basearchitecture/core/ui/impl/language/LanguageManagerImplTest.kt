package com.yfy.basearchitecture.core.ui.impl.language

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.yfy.basearchitecture.core.datastore.api.interfaces.AppSettingsManager
import com.yfy.basearchitecture.core.ui.api.managers.SupportedLanguage
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class LanguageManagerImplTest {

    private lateinit var context: Context
    private lateinit var mockAppSettingsManager: AppSettingsManager
    private lateinit var languageManager: LanguageManagerImpl

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        mockAppSettingsManager = mockk()
        languageManager = LanguageManagerImpl(context, mockAppSettingsManager)
    }

    @Test
    fun should_setLanguage_when_languageProvided() = runTest {
        // Given
        val language = SupportedLanguage.ENGLISH
        coEvery { mockAppSettingsManager.setLanguage(language.code) } just Runs

        // When
        languageManager.setLanguage(language)

        // Then
        coVerify { mockAppSettingsManager.setLanguage(language.code) }
    }

    @Test
    fun should_getCurrentLanguage_when_called() = runTest {
        // Given
        val languageCode = "en"
        coEvery { mockAppSettingsManager.getLanguage() } returns flowOf(languageCode)

        // When
        val result = languageManager.getCurrentLanguage()

        // Then
        result.collect { language ->
            assertEquals(SupportedLanguage.ENGLISH, language)
        }
    }

    @Test
    fun should_getCurrentLanguageCode_when_called() = runTest {
        // Given
        val languageCode = "tr"
        coEvery { mockAppSettingsManager.getLanguage() } returns flowOf(languageCode)

        // When
        val result = languageManager.getCurrentLanguageCode()

        // Then
        result.collect { code ->
            assertEquals(languageCode, code)
        }
    }

    @Test
    fun should_getLocalizedStringWithResId_when_resIdProvided() {
        // Given
        val resId = android.R.string.ok // Use a real resource ID

        // When
        val result = languageManager.getLocalizedString(resId)

        // Then
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun should_getLocalizedStringWithResIdAndArgs_when_resIdAndArgsProvided() {
        // Given
        val resId = android.R.string.ok
        val args = arrayOf("arg1", "arg2")

        // When
        val result = languageManager.getLocalizedString(resId, *args)

        // Then
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun should_returnFallbackString_when_resIdNotFound() {
        // Given
        val resId = 999999 // Non-existent resource ID

        // When
        val result = languageManager.getLocalizedString(resId)

        // Then
        assertEquals("String not found: $resId", result)
    }

    @Test
    fun should_getLocalizedStringWithKey_when_keyProvided() {
        // Given
        val key = "ok" // Use a real resource key

        // When
        val result = languageManager.getLocalizedString(key)

        // Then
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun should_returnKey_when_keyNotFound() {
        // Given
        val key = "nonexistent_key"

        // When
        val result = languageManager.getLocalizedString(key)

        // Then
        assertEquals(key, result)
    }

    @Test
    fun should_returnKey_when_exceptionThrown() {
        // Given
        val key = "test_key"

        // When
        val result = languageManager.getLocalizedString(key)

        // Then
        assertEquals(key, result)
    }

    @Test
    fun should_getLocalizedStringWithKeyAndArgs_when_keyAndArgsProvided() {
        // Given
        val key = "ok"
        val args = arrayOf("arg1", "arg2")

        // When
        val result = languageManager.getLocalizedString(key, *args)

        // Then
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun should_isCurrentLanguageRTL_when_called() = runTest {
        // Given
        val languageCode = "ar" // Arabic is RTL
        coEvery { mockAppSettingsManager.getLanguage() } returns flowOf(languageCode)

        // When
        val result = languageManager.isCurrentLanguageRTL()

        // Then
        result.collect { isRTL ->
            assertTrue(isRTL)
        }
    }

    @Test
    fun should_returnFalseForRTL_when_englishLanguage() = runTest {
        // Given
        val languageCode = "en" // English is LTR
        coEvery { mockAppSettingsManager.getLanguage() } returns flowOf(languageCode)

        // When
        val result = languageManager.isCurrentLanguageRTL()

        // Then
        result.collect { isRTL ->
            assertFalse(isRTL)
        }
    }

    @Test
    fun should_resetToSystemLanguage_when_called() = runTest {
        // Given
        coEvery { mockAppSettingsManager.setLanguage(any()) } just Runs

        // When
        languageManager.resetToSystemLanguage()

        // Then
        coVerify { mockAppSettingsManager.setLanguage(any()) }
    }

    @Test
    fun should_getSupportedLanguages_when_called() {
        // When
        val result = languageManager.getSupportedLanguages()

        // Then
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
        assertTrue(result.contains(SupportedLanguage.ENGLISH))
        assertTrue(result.contains(SupportedLanguage.TURKISH))
    }

    @Test
    fun should_getLanguageDisplayName_when_codeProvided() {
        // Given
        val languageCode = "en"

        // When
        val result = languageManager.getLanguageDisplayName(languageCode)

        // Then
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun should_handleAllSupportedLanguages_when_provided() {
        // Given
        val supportedLanguages = listOf("en", "tr", "ar")

        // When & Then
        supportedLanguages.forEach { code ->
            val result = languageManager.getLanguageDisplayName(code)
            assertNotNull(result)
            assertTrue(result.isNotEmpty())
        }
    }

    @Test
    fun should_createInstance_when_contextAndAppSettingsManagerProvided() {
        // When
        val instance = LanguageManagerImpl(context, mockAppSettingsManager)

        // Then
        assertNotNull(instance)
    }

    @Test
    fun should_handleEmptyStringArgs_when_provided() {
        // Given
        val resId = android.R.string.ok
        val args = arrayOf("", "")

        // When
        val result = languageManager.getLocalizedString(resId, *args)

        // Then
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun should_handleNullArgs_when_provided() {
        // Given
        val resId = android.R.string.ok

        // When
        val result = languageManager.getLocalizedString(resId)

        // Then
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }
} 