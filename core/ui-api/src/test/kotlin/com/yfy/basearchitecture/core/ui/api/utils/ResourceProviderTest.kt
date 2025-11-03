package com.yfy.basearchitecture.core.ui.api.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
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
class ResourceProviderTest {

    @Mock
    private lateinit var resourceProvider: ResourceProvider

    private val testResId = 123
    private val testKey = "test_key"
    private val testFormatArgs = arrayOf("arg1", "arg2")
    private val testResourceName = "test_resource"
    private val testResourceType = "string"

    @Before
    fun setup() {
        // Setup default mock behavior
        `when`(resourceProvider.getString(testResId)).thenReturn("Test String")
        `when`(resourceProvider.getString(testKey)).thenReturn("Test String by Key")
        `when`(resourceProvider.getColor(testResId)).thenReturn(Color.Red)
        `when`(resourceProvider.getDimension(testResId)).thenReturn(Dp(16f))
        `when`(resourceProvider.getDrawableName(testResId)).thenReturn("test_drawable")
        `when`(resourceProvider.getBoolean(testResId)).thenReturn(true)
        `when`(resourceProvider.getInteger(testResId)).thenReturn(42)
        `when`(resourceProvider.getStringArray(testResId)).thenReturn(arrayOf("item1", "item2"))
        `when`(resourceProvider.getIntegerArray(testResId)).thenReturn(intArrayOf(1, 2, 3))
        `when`(resourceProvider.hasResource(testResId)).thenReturn(true)
        `when`(resourceProvider.getResourceId(testResourceName, testResourceType)).thenReturn(testResId)
        `when`(resourceProvider.getResourceName(testResId)).thenReturn(testResourceName)
    }

    @Test
    fun `test getString with resource ID`() {
        // Given
        val expectedString = "Test String Resource"
        `when`(resourceProvider.getString(testResId)).thenReturn(expectedString)

        // When
        val result = resourceProvider.getString(testResId)

        // Then
        assertEquals(expectedString, result)
        verify(resourceProvider).getString(testResId)
    }

    @Test
    fun `test getString with resource ID and format args`() {
        // Given
        val expectedString = "Test String with args: arg1, arg2"
        `when`(resourceProvider.getString(testResId, *testFormatArgs)).thenReturn(expectedString)

        // When
        val result = resourceProvider.getString(testResId, *testFormatArgs)

        // Then
        assertEquals(expectedString, result)
        verify(resourceProvider).getString(testResId, *testFormatArgs)
    }

    @Test
    fun `test getString with key`() {
        // Given
        val expectedString = "Test String by Key"
        `when`(resourceProvider.getString(testKey)).thenReturn(expectedString)

        // When
        val result = resourceProvider.getString(testKey)

        // Then
        assertEquals(expectedString, result)
        verify(resourceProvider).getString(testKey)
    }

    @Test
    fun `test getString with key and format args`() {
        // Given
        val expectedString = "Test String by Key with args: arg1, arg2"
        `when`(resourceProvider.getString(testKey, *testFormatArgs)).thenReturn(expectedString)

        // When
        val result = resourceProvider.getString(testKey, *testFormatArgs)

        // Then
        assertEquals(expectedString, result)
        verify(resourceProvider).getString(testKey, *testFormatArgs)
    }

    @Test
    fun `test getColor`() {
        // Given
        val expectedColor = Color.Blue
        `when`(resourceProvider.getColor(testResId)).thenReturn(expectedColor)

        // When
        val result = resourceProvider.getColor(testResId)

        // Then
        assertEquals(expectedColor, result)
        verify(resourceProvider).getColor(testResId)
    }

    @Test
    fun `test getDimension`() {
        // Given
        val expectedDimension = Dp(24f)
        `when`(resourceProvider.getDimension(testResId)).thenReturn(expectedDimension)

        // When
        val result = resourceProvider.getDimension(testResId)

        // Then
        assertEquals(expectedDimension, result)
        verify(resourceProvider).getDimension(testResId)
    }

    @Test
    fun `test getDrawableName`() {
        // Given
        val expectedDrawableName = "test_icon"
        `when`(resourceProvider.getDrawableName(testResId)).thenReturn(expectedDrawableName)

        // When
        val result = resourceProvider.getDrawableName(testResId)

        // Then
        assertEquals(expectedDrawableName, result)
        verify(resourceProvider).getDrawableName(testResId)
    }

    @Test
    fun `test getBoolean returns true`() {
        // Given
        `when`(resourceProvider.getBoolean(testResId)).thenReturn(true)

        // When
        val result = resourceProvider.getBoolean(testResId)

        // Then
        assertTrue(result)
        verify(resourceProvider).getBoolean(testResId)
    }

    @Test
    fun `test getBoolean returns false`() {
        // Given
        `when`(resourceProvider.getBoolean(testResId)).thenReturn(false)

        // When
        val result = resourceProvider.getBoolean(testResId)

        // Then
        assertFalse(result)
        verify(resourceProvider).getBoolean(testResId)
    }

    @Test
    fun `test getInteger`() {
        // Given
        val expectedInteger = 100
        `when`(resourceProvider.getInteger(testResId)).thenReturn(expectedInteger)

        // When
        val result = resourceProvider.getInteger(testResId)

        // Then
        assertEquals(expectedInteger, result)
        verify(resourceProvider).getInteger(testResId)
    }

    @Test
    fun `test getStringArray`() {
        // Given
        val expectedArray = arrayOf("item1", "item2", "item3")
        `when`(resourceProvider.getStringArray(testResId)).thenReturn(expectedArray)

        // When
        val result = resourceProvider.getStringArray(testResId)

        // Then
        assertEquals(expectedArray, result)
        verify(resourceProvider).getStringArray(testResId)
    }

    @Test
    fun `test getIntegerArray`() {
        // Given
        val expectedArray = intArrayOf(10, 20, 30)
        `when`(resourceProvider.getIntegerArray(testResId)).thenReturn(expectedArray)

        // When
        val result = resourceProvider.getIntegerArray(testResId)

        // Then
        assertEquals(expectedArray, result)
        verify(resourceProvider).getIntegerArray(testResId)
    }

    @Test
    fun `test hasResource returns true`() {
        // Given
        `when`(resourceProvider.hasResource(testResId)).thenReturn(true)

        // When
        val result = resourceProvider.hasResource(testResId)

        // Then
        assertTrue(result)
        verify(resourceProvider).hasResource(testResId)
    }

    @Test
    fun `test hasResource returns false`() {
        // Given
        `when`(resourceProvider.hasResource(testResId)).thenReturn(false)

        // When
        val result = resourceProvider.hasResource(testResId)

        // Then
        assertFalse(result)
        verify(resourceProvider).hasResource(testResId)
    }

    @Test
    fun `test getResourceId`() {
        // Given
        val expectedResourceId = 456
        `when`(resourceProvider.getResourceId(testResourceName, testResourceType)).thenReturn(expectedResourceId)

        // When
        val result = resourceProvider.getResourceId(testResourceName, testResourceType)

        // Then
        assertEquals(expectedResourceId, result)
        verify(resourceProvider).getResourceId(testResourceName, testResourceType)
    }

    @Test
    fun `test getResourceName returns name`() {
        // Given
        val expectedResourceName = "test_resource_name"
        `when`(resourceProvider.getResourceName(testResId)).thenReturn(expectedResourceName)

        // When
        val result = resourceProvider.getResourceName(testResId)

        // Then
        assertEquals(expectedResourceName, result)
        verify(resourceProvider).getResourceName(testResId)
    }

    @Test
    fun `test getResourceName returns null`() {
        // Given
        `when`(resourceProvider.getResourceName(testResId)).thenReturn(null)

        // When
        val result = resourceProvider.getResourceName(testResId)

        // Then
        assertNull(result)
        verify(resourceProvider).getResourceName(testResId)
    }

    @Test
    fun `test multiple resource calls`() {
        // Given
        val resId1 = 100
        val resId2 = 200
        `when`(resourceProvider.getString(resId1)).thenReturn("String 1")
        `when`(resourceProvider.getString(resId2)).thenReturn("String 2")
        `when`(resourceProvider.getColor(resId1)).thenReturn(Color.Red)
        `when`(resourceProvider.getColor(resId2)).thenReturn(Color.Blue)

        // When & Then
        assertEquals("String 1", resourceProvider.getString(resId1))
        assertEquals("String 2", resourceProvider.getString(resId2))
        assertEquals(Color.Red, resourceProvider.getColor(resId1))
        assertEquals(Color.Blue, resourceProvider.getColor(resId2))

        verify(resourceProvider).getString(resId1)
        verify(resourceProvider).getString(resId2)
        verify(resourceProvider).getColor(resId1)
        verify(resourceProvider).getColor(resId2)
    }
} 