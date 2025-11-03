package com.yfy.basearchitecture.core.ui.api.extensions

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.yfy.basearchitecture.core.ui.api.utils.ResourceProvider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class ResourceExtensionsTest {

    @Mock
    private lateinit var resourceProvider: ResourceProvider

    private val testResId = 123
    private val testKey = "test_key"
    private val testFormatArgs = arrayOf("arg1", "arg2")

    @Before
    fun setup() {
        // Setup default mock behavior
        `when`(resourceProvider.getString(testResId)).thenReturn("Test String")
        `when`(resourceProvider.getString(testKey)).thenReturn("Test String by Key")
        `when`(resourceProvider.getColor(testResId)).thenReturn(Color.Red)
        `when`(resourceProvider.getDimension(testResId)).thenReturn(Dp(16f))
        `when`(resourceProvider.getBoolean(testResId)).thenReturn(true)
        `when`(resourceProvider.getInteger(testResId)).thenReturn(42)
        `when`(resourceProvider.getStringArray(testResId)).thenReturn(arrayOf("item1", "item2"))
        `when`(resourceProvider.getIntegerArray(testResId)).thenReturn(intArrayOf(1, 2, 3))
        `when`(resourceProvider.hasResource(testResId)).thenReturn(true)
        `when`(resourceProvider.getResourceName(testResId)).thenReturn("test_resource")
        `when`(resourceProvider.getDrawableName(testResId)).thenReturn("test_drawable")
    }

    @Test
    fun `test getString extension with resource ID`() {
        // Given
        val expectedString = "Test String Resource"
        `when`(resourceProvider.getString(testResId)).thenReturn(expectedString)

        // When
        val result = testResId.getString(resourceProvider)

        // Then
        assertEquals(expectedString, result)
        verify(resourceProvider).getString(testResId)
    }

    @Test
    fun `test getString extension with resource ID and format args`() {
        // Given
        val expectedString = "Test String with args: arg1, arg2"
        `when`(resourceProvider.getString(testResId, *testFormatArgs)).thenReturn(expectedString)

        // When
        val result = testResId.getString(resourceProvider, *testFormatArgs)

        // Then
        assertEquals(expectedString, result)
        verify(resourceProvider).getString(testResId, *testFormatArgs)
    }

    @Test
    fun `test getString extension with key`() {
        // Given
        val expectedString = "Test String by Key"
        `when`(resourceProvider.getString(testKey)).thenReturn(expectedString)

        // When
        val result = testKey.getString(resourceProvider)

        // Then
        assertEquals(expectedString, result)
        verify(resourceProvider).getString(testKey)
    }

    @Test
    fun `test getString extension with key and format args`() {
        // Given
        val expectedString = "Test String by Key with args: arg1, arg2"
        `when`(resourceProvider.getString(testKey, *testFormatArgs)).thenReturn(expectedString)

        // When
        val result = testKey.getString(resourceProvider, *testFormatArgs)

        // Then
        assertEquals(expectedString, result)
        verify(resourceProvider).getString(testKey, *testFormatArgs)
    }

    @Test
    fun `test getColor extension`() {
        // Given
        val expectedColor = Color.Blue
        `when`(resourceProvider.getColor(testResId)).thenReturn(expectedColor)

        // When
        val result = testResId.getColor(resourceProvider)

        // Then
        assertEquals(expectedColor, result)
        verify(resourceProvider).getColor(testResId)
    }

    @Test
    fun `test getDimension extension`() {
        // Given
        val expectedDimension = Dp(24f)
        `when`(resourceProvider.getDimension(testResId)).thenReturn(expectedDimension)

        // When
        val result = testResId.getDimension(resourceProvider)

        // Then
        assertEquals(expectedDimension, result)
        verify(resourceProvider).getDimension(testResId)
    }

    @Test
    fun `test getBoolean extension returns true`() {
        // Given
        `when`(resourceProvider.getBoolean(testResId)).thenReturn(true)

        // When
        val result = testResId.getBoolean(resourceProvider)

        // Then
        assertTrue(result)
        verify(resourceProvider).getBoolean(testResId)
    }

    @Test
    fun `test getBoolean extension returns false`() {
        // Given
        `when`(resourceProvider.getBoolean(testResId)).thenReturn(false)

        // When
        val result = testResId.getBoolean(resourceProvider)

        // Then
        assertFalse(result)
        verify(resourceProvider).getBoolean(testResId)
    }

    @Test
    fun `test getInteger extension`() {
        // Given
        val expectedInteger = 100
        `when`(resourceProvider.getInteger(testResId)).thenReturn(expectedInteger)

        // When
        val result = testResId.getInteger(resourceProvider)

        // Then
        assertEquals(expectedInteger, result)
        verify(resourceProvider).getInteger(testResId)
    }

    @Test
    fun `test getStringArray extension`() {
        // Given
        val expectedArray = arrayOf("item1", "item2", "item3")
        `when`(resourceProvider.getStringArray(testResId)).thenReturn(expectedArray)

        // When
        val result = testResId.getStringArray(resourceProvider)

        // Then
        assertEquals(expectedArray, result)
        verify(resourceProvider).getStringArray(testResId)
    }

    @Test
    fun `test getIntegerArray extension`() {
        // Given
        val expectedArray = intArrayOf(10, 20, 30)
        `when`(resourceProvider.getIntegerArray(testResId)).thenReturn(expectedArray)

        // When
        val result = testResId.getIntegerArray(resourceProvider)

        // Then
        assertEquals(expectedArray, result)
        verify(resourceProvider).getIntegerArray(testResId)
    }

    @Test
    fun `test hasResource extension returns true`() {
        // Given
        `when`(resourceProvider.hasResource(testResId)).thenReturn(true)

        // When
        val result = testResId.hasResource(resourceProvider)

        // Then
        assertTrue(result)
        verify(resourceProvider).hasResource(testResId)
    }

    @Test
    fun `test hasResource extension returns false`() {
        // Given
        `when`(resourceProvider.hasResource(testResId)).thenReturn(false)

        // When
        val result = testResId.hasResource(resourceProvider)

        // Then
        assertFalse(result)
        verify(resourceProvider).hasResource(testResId)
    }

    @Test
    fun `test getResourceName extension returns name`() {
        // Given
        val expectedResourceName = "test_resource_name"
        `when`(resourceProvider.getResourceName(testResId)).thenReturn(expectedResourceName)

        // When
        val result = testResId.getResourceName(resourceProvider)

        // Then
        assertEquals(expectedResourceName, result)
        verify(resourceProvider).getResourceName(testResId)
    }

    @Test
    fun `test getResourceName extension returns null`() {
        // Given
        `when`(resourceProvider.getResourceName(testResId)).thenReturn(null)

        // When
        val result = testResId.getResourceName(resourceProvider)

        // Then
        assertNull(result)
        verify(resourceProvider).getResourceName(testResId)
    }

    @Test
    fun `test getDrawableName extension`() {
        // Given
        val expectedDrawableName = "test_icon"
        `when`(resourceProvider.getDrawableName(testResId)).thenReturn(expectedDrawableName)

        // When
        val result = testResId.getDrawableName(resourceProvider)

        // Then
        assertEquals(expectedDrawableName, result)
        verify(resourceProvider).getDrawableName(testResId)
    }

    @Test
    fun `test multiple extension calls`() {
        // Given
        val resId1 = 100
        val resId2 = 200
        `when`(resourceProvider.getString(resId1)).thenReturn("String 1")
        `when`(resourceProvider.getString(resId2)).thenReturn("String 2")
        `when`(resourceProvider.getColor(resId1)).thenReturn(Color.Red)
        `when`(resourceProvider.getColor(resId2)).thenReturn(Color.Blue)

        // When & Then
        assertEquals("String 1", resId1.getString(resourceProvider))
        assertEquals("String 2", resId2.getString(resourceProvider))
        assertEquals(Color.Red, resId1.getColor(resourceProvider))
        assertEquals(Color.Blue, resId2.getColor(resourceProvider))

        verify(resourceProvider).getString(resId1)
        verify(resourceProvider).getString(resId2)
        verify(resourceProvider).getColor(resId1)
        verify(resourceProvider).getColor(resId2)
    }

    @Test
    fun `test extension with different resource types`() {
        // Given
        val stringResId = 100
        val colorResId = 200
        val dimensionResId = 300
        val booleanResId = 400
        val integerResId = 500

        `when`(resourceProvider.getString(stringResId)).thenReturn("Test String")
        `when`(resourceProvider.getColor(colorResId)).thenReturn(Color.Green)
        `when`(resourceProvider.getDimension(dimensionResId)).thenReturn(Dp(32f))
        `when`(resourceProvider.getBoolean(booleanResId)).thenReturn(true)
        `when`(resourceProvider.getInteger(integerResId)).thenReturn(999)

        // When & Then
        assertEquals("Test String", stringResId.getString(resourceProvider))
        assertEquals(Color.Green, colorResId.getColor(resourceProvider))
        assertEquals(Dp(32f), dimensionResId.getDimension(resourceProvider))
        assertTrue(booleanResId.getBoolean(resourceProvider))
        assertEquals(999, integerResId.getInteger(resourceProvider))

        verify(resourceProvider).getString(stringResId)
        verify(resourceProvider).getColor(colorResId)
        verify(resourceProvider).getDimension(dimensionResId)
        verify(resourceProvider).getBoolean(booleanResId)
        verify(resourceProvider).getInteger(integerResId)
    }
} 