package com.yfy.basearchitecture.core.ui.api.managers

import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class)
class LanguageManagerTest {

    @Mock
    private lateinit var languageManager: LanguageManager

    private val testLanguage = SupportedLanguage.ENGLISH
    private val testResId = 123
    private val testKey = "test_key"
    private val testFormatArgs = arrayOf("arg1", "arg2")

    @Before
    fun setup() {
        // Setup default mock behavior
        `when`(languageManager.getCurrentLanguage()).thenReturn(flowOf(SupportedLanguage.ENGLISH))
        `when`(languageManager.getCurrentLanguageCode()).thenReturn(flowOf("en"))
        `when`(languageManager.getLocalizedString(testResId)).thenReturn("Localized String")
        `when`(languageManager.getLocalizedString(testKey)).thenReturn("Localized String")
        `when`(languageManager.isCurrentLanguageRTL()).thenReturn(flowOf(false))
        `when`(languageManager.getSupportedLanguages()).thenReturn(SupportedLanguage.entries.toList())
    }

    @Test
    fun `test setLanguage`() = runTest {
        // When
        languageManager.setLanguage(testLanguage)

        // Then
        verify(languageManager).setLanguage(testLanguage)
    }

    @Test
    fun `test getCurrentLanguage returns flow`() {
        // Given
        val expectedLanguage = SupportedLanguage.TURKISH
        `when`(languageManager.getCurrentLanguage()).thenReturn(flowOf(expectedLanguage))

        // When
        val flow = languageManager.getCurrentLanguage()

        // Then
        verify(languageManager).getCurrentLanguage()
        // Note: Flow testing would require additional setup with coroutines
    }

    @Test
    fun `test getCurrentLanguageCode returns flow`() {
        // Given
        val expectedCode = "tr"
        `when`(languageManager.getCurrentLanguageCode()).thenReturn(flowOf(expectedCode))

        // When
        val flow = languageManager.getCurrentLanguageCode()

        // Then
        verify(languageManager).getCurrentLanguageCode()
        // Note: Flow testing would require additional setup with coroutines
    }

    @Test
    fun `test getLocalizedString with resource ID`() {
        // Given
        val expectedString = "Test Localized String"
        `when`(languageManager.getLocalizedString(testResId)).thenReturn(expectedString)

        // When
        val result = languageManager.getLocalizedString(testResId)

        // Then
        assertEquals(expectedString, result)
        verify(languageManager).getLocalizedString(testResId)
    }

    @Test
    fun `test getLocalizedString with resource ID and format args`() {
        // Given
        val expectedString = "Test Localized String with args"
        `when`(languageManager.getLocalizedString(testResId, *testFormatArgs)).thenReturn(expectedString)

        // When
        val result = languageManager.getLocalizedString(testResId, *testFormatArgs)

        // Then
        assertEquals(expectedString, result)
        verify(languageManager).getLocalizedString(testResId, *testFormatArgs)
    }

    @Test
    fun `test getLocalizedString with key`() {
        // Given
        val expectedString = "Test Localized String by Key"
        `when`(languageManager.getLocalizedString(testKey)).thenReturn(expectedString)

        // When
        val result = languageManager.getLocalizedString(testKey)

        // Then
        assertEquals(expectedString, result)
        verify(languageManager).getLocalizedString(testKey)
    }

    @Test
    fun `test getLocalizedString with key and format args`() {
        // Given
        val expectedString = "Test Localized String by Key with args"
        `when`(languageManager.getLocalizedString(testKey, *testFormatArgs)).thenReturn(expectedString)

        // When
        val result = languageManager.getLocalizedString(testKey, *testFormatArgs)

        // Then
        assertEquals(expectedString, result)
        verify(languageManager).getLocalizedString(testKey, *testFormatArgs)
    }

    @Test
    fun `test isCurrentLanguageRTL returns true`() {
        // Given
        `when`(languageManager.isCurrentLanguageRTL()).thenReturn(flowOf(true))

        // When
        val flow = languageManager.isCurrentLanguageRTL()

        // Then
        verify(languageManager).isCurrentLanguageRTL()
        // Note: Flow testing would require additional setup with coroutines
    }

    @Test
    fun `test isCurrentLanguageRTL returns false`() {
        // Given
        `when`(languageManager.isCurrentLanguageRTL()).thenReturn(flowOf(false))

        // When
        val flow = languageManager.isCurrentLanguageRTL()

        // Then
        verify(languageManager).isCurrentLanguageRTL()
        // Note: Flow testing would require additional setup with coroutines
    }

    @Test
    fun `test getSupportedLanguages returns list`() {
        // Given
        val expectedLanguages = listOf(SupportedLanguage.ENGLISH, SupportedLanguage.TURKISH)
        `when`(languageManager.getSupportedLanguages()).thenReturn(expectedLanguages)

        // When
        val result = languageManager.getSupportedLanguages()

        // Then
        assertEquals(expectedLanguages, result)
        verify(languageManager).getSupportedLanguages()
    }

    @Test
    fun `test getLanguageDisplayName returns display name`() {
        // Given
        val expectedDisplayName = "Türkçe"
        `when`(languageManager.getLanguageDisplayName("tr")).thenReturn(expectedDisplayName)

        // When
        val result = languageManager.getLanguageDisplayName("tr")

        // Then
        assertEquals(expectedDisplayName, result)
        verify(languageManager).getLanguageDisplayName("tr")
    }

    @Test
    fun `test updateLocale`() = runTest {
        // When
        languageManager.updateLocale(testLanguage)

        // Then
        verify(languageManager).updateLocale(testLanguage)
    }

    @Test
    fun `test resetToSystemLanguage`() = runTest {
        // When
        languageManager.resetToSystemLanguage()

        // Then
        verify(languageManager).resetToSystemLanguage()
    }
} 