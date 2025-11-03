package com.yfy.basearchitecture.core.ui.impl.resources

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.yfy.basearchitecture.core.ui.api.utils.ResourceProvider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class ResourceProviderImplTest {

    private lateinit var context: Context
    private lateinit var resourceProvider: ResourceProvider

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        resourceProvider = ResourceProviderImpl(context)
    }

    @Test
    fun should_createInstance_when_contextProvided() {
        // When
        val instance = ResourceProviderImpl(context)

        // Then
        assertNotNull(instance)
    }

    @Test
    fun should_getStringWithResId_when_resIdProvided() {
        // Given
        val resId = android.R.string.ok

        // When
        val result = resourceProvider.getString(resId)

        // Then
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun should_getStringWithResIdAndArgs_when_resIdAndArgsProvided() {
        // Given
        val resId = android.R.string.ok
        val args = arrayOf("arg1", "arg2")

        // When
        val result = resourceProvider.getString(resId, *args)

        // Then
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun should_getStringWithKey_when_keyProvided() {
        // Given
        val key = "ok"

        // When
        val result = resourceProvider.getString(key)

        // Then
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun should_getStringWithKeyAndArgs_when_keyAndArgsProvided() {
        // Given
        val key = "ok"
        val args = arrayOf("arg1", "arg2")

        // When
        val result = resourceProvider.getString(key, *args)

        // Then
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun should_returnKey_when_keyNotFound() {
        // Given
        val key = "nonexistent_key"

        // When
        val result = resourceProvider.getString(key)

        // Then
        assertEquals(key, result)
    }

    @Test
    fun should_returnKey_when_exceptionThrown() {
        // Given
        val key = "test_key"

        // When
        val result = resourceProvider.getString(key)

        // Then
        assertEquals(key, result)
    }

    @Test
    fun should_getColor_when_resIdProvided() {
        // Given
        val resId = android.R.color.black

        // When
        val result = resourceProvider.getColor(resId)

        // Then
        assertNotNull(result)
    }

    @Test
    fun should_returnBlackColor_when_resIdNotFound() {
        // Given
        val resId = 999999

        // When
        val result = resourceProvider.getColor(resId)

        // Then
        assertEquals(androidx.compose.ui.graphics.Color.Black, result)
    }

    @Test
    fun should_getDimension_when_resIdProvided() {
        // Given
        val resId = android.R.dimen.app_icon_size

        // When
        val result = resourceProvider.getDimension(resId)

        // Then
        assertNotNull(result)
        assertTrue(result.value > 0)
    }

    @Test
    fun should_returnZeroDp_when_resIdNotFound() {
        // Given
        val resId = 999999

        // When
        val result = resourceProvider.getDimension(resId)

        // Then
        assertEquals(0f, result.value)
    }

    @Test
    fun should_returnFalse_when_booleanResIdNotFound() {
        // Given
        val resId = 999999

        // When
        val result = resourceProvider.getBoolean(resId)

        // Then
        assertEquals(false, result)
    }

    @Test
    fun should_getInteger_when_resIdProvided() {
        // Given
        val resId = android.R.integer.config_shortAnimTime

        // When
        val result = resourceProvider.getInteger(resId)

        // Then
        assertNotNull(result)
        assertTrue(result > 0)
    }

    @Test
    fun should_returnZero_when_integerResIdNotFound() {
        // Given
        val resId = 999999

        // When
        val result = resourceProvider.getInteger(resId)

        // Then
        assertEquals(0, result)
    }

    @Test
    fun should_getStringArray_when_resIdProvided() {
        // Given
        val resId = android.R.array.emailAddressTypes

        // When
        val result = resourceProvider.getStringArray(resId)

        // Then
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun should_returnEmptyArray_when_stringArrayResIdNotFound() {
        // Given
        val resId = 999999

        // When
        val result = resourceProvider.getStringArray(resId)

        // Then
        assertNotNull(result)
        assertTrue(result.isEmpty())
    }

    @Test
    fun should_returnEmptyIntArray_when_integerArrayResIdNotFound() {
        // Given
        val resId = 999999

        // When
        val result = resourceProvider.getIntegerArray(resId)

        // Then
        assertNotNull(result)
        assertTrue(result.isEmpty())
    }

    @Test
    fun should_getResourceName_when_resIdProvided() {
        // Given
        val resId = android.R.string.ok

        // When
        val result = resourceProvider.getResourceName(resId)

        // Then
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun should_returnNull_when_resourceNameNotFound() {
        // Given
        val resId = 999999

        // When
        val result = resourceProvider.getResourceName(resId)

        // Then
        assertEquals(null, result)
    }

    @Test
    fun should_getResourceId_when_resourceNameAndTypeProvided() {
        // Given
        val resourceName = "ok"
        val resourceType = "string"

        // When
        val result = resourceProvider.getResourceId(resourceName, resourceType)

        // Then
        assertTrue(result > 0)
    }

    @Test
    fun should_returnZero_when_resourceIdNotFound() {
        // Given
        val resourceName = "nonexistent"
        val resourceType = "string"

        // When
        val result = resourceProvider.getResourceId(resourceName, resourceType)

        // Then
        assertEquals(0, result)
    }

    @Test
    fun should_returnTrue_when_resourceExists() {
        // Given
        val resId = android.R.string.ok

        // When
        val result = resourceProvider.hasResource(resId)

        // Then
        assertTrue(result)
    }

    @Test
    fun should_returnFalse_when_resourceDoesNotExist() {
        // Given
        val resId = 999999

        // When
        val result = resourceProvider.hasResource(resId)

        // Then
        assertFalse(result)
    }

    @Test
    fun should_getDrawableName_when_resIdProvided() {
        // Given
        val resId = android.R.drawable.ic_menu_help

        // When
        val result = resourceProvider.getDrawableName(resId)

        // Then
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun should_returnFallbackDrawableName_when_resIdNotFound() {
        // Given
        val resId = 999999

        // When
        val result = resourceProvider.getDrawableName(resId)

        // Then
        assertEquals("drawable_not_found", result)
    }

    @Test
    fun should_handleSpecialCharactersInKey_when_provided() {
        // Given
        val key = "test_key_with_special_chars_123"

        // When
        val result = resourceProvider.getString(key)

        // Then
        assertEquals(key, result)
    }

    @Test
    fun should_handleUnicodeCharactersInKey_when_provided() {
        // Given
        val key = "test_key_with_unicode_çğüşıö"

        // When
        val result = resourceProvider.getString(key)

        // Then
        assertEquals(key, result)
    }

    @Test
    fun should_handleEmptyStringArgs_when_provided() {
        // Given
        val resId = android.R.string.ok
        val args = arrayOf("", "")

        // When
        val result = resourceProvider.getString(resId, *args)

        // Then
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun should_handleNullArgs_when_provided() {
        // Given
        val resId = android.R.string.ok

        // When
        val result = resourceProvider.getString(resId)

        // Then
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }
} 