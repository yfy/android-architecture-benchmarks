package com.yfy.basearchitecture.core.ui.api.managers

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class BackPressManagerTest {

    @Mock
    private lateinit var backPressManager: BackPressManager

    @Before
    fun setup() {
        // Setup default mock behavior
        `when`(backPressManager.onBackPressed()).thenReturn(false)
        `when`(backPressManager.isBackPressEnabled()).thenReturn(true)
    }

    @Test
    fun `test onBackPressed returns true when handled`() {
        // Given
        `when`(backPressManager.onBackPressed()).thenReturn(true)

        // When
        val result = backPressManager.onBackPressed()

        // Then
        assertTrue(result)
        verify(backPressManager).onBackPressed()
    }

    @Test
    fun `test onBackPressed returns false when not handled`() {
        // Given
        `when`(backPressManager.onBackPressed()).thenReturn(false)

        // When
        val result = backPressManager.onBackPressed()

        // Then
        assertFalse(result)
        verify(backPressManager).onBackPressed()
    }

    @Test
    fun `test setBackPressEnabled with true`() {
        // When
        backPressManager.setBackPressEnabled(true)

        // Then
        verify(backPressManager).setBackPressEnabled(true)
    }

    @Test
    fun `test setBackPressEnabled with false`() {
        // When
        backPressManager.setBackPressEnabled(false)

        // Then
        verify(backPressManager).setBackPressEnabled(false)
    }

    @Test
    fun `test isBackPressEnabled returns true`() {
        // Given
        `when`(backPressManager.isBackPressEnabled()).thenReturn(true)

        // When
        val result = backPressManager.isBackPressEnabled()

        // Then
        assertTrue(result)
        verify(backPressManager).isBackPressEnabled()
    }

    @Test
    fun `test isBackPressEnabled returns false`() {
        // Given
        `when`(backPressManager.isBackPressEnabled()).thenReturn(false)

        // When
        val result = backPressManager.isBackPressEnabled()

        // Then
        assertFalse(result)
        verify(backPressManager).isBackPressEnabled()
    }

    @Test
    fun `test multiple back press calls`() {
        // Given
        `when`(backPressManager.onBackPressed()).thenReturn(true, false, true)

        // When & Then
        assertTrue(backPressManager.onBackPressed())
        assertFalse(backPressManager.onBackPressed())
        assertTrue(backPressManager.onBackPressed())

        verify(backPressManager, times(3)).onBackPressed()
    }

    @Test
    fun `test back press enabled state changes`() {
        // Given
        `when`(backPressManager.isBackPressEnabled()).thenReturn(true, false, true)

        // When & Then
        assertTrue(backPressManager.isBackPressEnabled())
        assertFalse(backPressManager.isBackPressEnabled())
        assertTrue(backPressManager.isBackPressEnabled())

        verify(backPressManager, times(3)).isBackPressEnabled()
    }
}