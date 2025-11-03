package com.yfy.basearchitecture.core.ui.impl.backpress

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class BackPressManagerImplTest {

    private lateinit var backPressManager: BackPressManagerImpl

    @Before
    fun setup() {
        backPressManager = BackPressManagerImpl()
    }

    @Test
    fun should_returnFalse_when_noCallbacksRegistered() {
        // Given
        backPressManager.setBackPressEnabled(true)

        // When
        val result = backPressManager.onBackPressed()

        // Then
        assertFalse(result)
    }

    @Test
    fun should_returnTrue_when_callbackHandlesBackPress() {
        // Given
        backPressManager.setBackPressEnabled(true)
        var callbackInvoked = false
        backPressManager.addBackPressCallback { 
            callbackInvoked = true
            true // Handle back press
        }

        // When
        val result = backPressManager.onBackPressed()

        // Then
        assertTrue(result)
        assertTrue(callbackInvoked)
    }

    @Test
    fun should_returnFalse_when_callbackDoesNotHandleBackPress() {
        // Given
        backPressManager.setBackPressEnabled(true)
        var callbackInvoked = false
        backPressManager.addBackPressCallback { 
            callbackInvoked = true
            false // Don't handle back press
        }

        // When
        val result = backPressManager.onBackPressed()

        // Then
        assertFalse(result)
        assertTrue(callbackInvoked)
    }

    @Test
    fun should_stopExecution_when_callbackHandlesBackPress() {
        // Given
        backPressManager.setBackPressEnabled(true)
        var firstCallbackInvoked = false
        var secondCallbackInvoked = false
        
        backPressManager.addBackPressCallback { 
            firstCallbackInvoked = true
            false
        }
        backPressManager.addBackPressCallback { 
            secondCallbackInvoked = true
            true // Handle back press
        }

        // When
        backPressManager.onBackPressed()

        // Then
        assertTrue(secondCallbackInvoked)
        assertFalse(firstCallbackInvoked) // Should not be executed
    }

    @Test
    fun should_setBackPressEnabled_when_enabledProvided() {
        // Given
        val enabled = false

        // When
        backPressManager.setBackPressEnabled(enabled)

        // Then
        assertEquals(enabled, backPressManager.isBackPressEnabled())
    }

    @Test
    fun should_returnTrue_when_backPressEnabled() {
        // Given
        backPressManager.setBackPressEnabled(true)

        // When
        val result = backPressManager.isBackPressEnabled()

        // Then
        assertTrue(result)
    }

    @Test
    fun should_returnFalse_when_backPressDisabled() {
        // Given
        backPressManager.setBackPressEnabled(false)

        // When
        val result = backPressManager.isBackPressEnabled()

        // Then
        assertFalse(result)
    }

    @Test
    fun should_addBackPressCallback_when_callbackProvided() {
        // Given
        var callbackInvoked = false
        val callback: () -> Boolean = { 
            callbackInvoked = true
            true
        }

        // When
        val callbackId = backPressManager.addBackPressCallback(callback)

        // Then
        assertNotNull(callbackId)
        assertEquals(1, backPressManager.getCallbackCount())
    }

    @Test
    fun should_removeBackPressCallback_when_callbackIdProvided() {
        // Given
        backPressManager.addBackPressCallback { true }
        assertEquals(1, backPressManager.getCallbackCount())

        // When
        backPressManager.removeBackPressCallback("test_id")

        // Then
        assertEquals(0, backPressManager.getCallbackCount())
    }

    @Test
    fun should_clearAllCallbacks_when_called() {
        // Given
        backPressManager.addBackPressCallback { true }
        backPressManager.addBackPressCallback { true }
        assertEquals(2, backPressManager.getCallbackCount())

        // When
        backPressManager.clearBackPressCallbacks()

        // Then
        assertEquals(0, backPressManager.getCallbackCount())
    }

    @Test
    fun should_returnCorrectCallbackCount_when_callbacksAdded() {
        // Given
        assertEquals(0, backPressManager.getCallbackCount())

        // When
        backPressManager.addBackPressCallback { true }
        backPressManager.addBackPressCallback { true }
        backPressManager.addBackPressCallback { true }

        // Then
        assertEquals(3, backPressManager.getCallbackCount())
    }

    @Test
    fun should_handleExceptionInCallback_when_callbackThrowsException() {
        // Given
        backPressManager.setBackPressEnabled(true)
        var secondCallbackInvoked = false
        
        backPressManager.addBackPressCallback { 
            throw RuntimeException("Test exception")
        }
        backPressManager.addBackPressCallback { 
            secondCallbackInvoked = true
            true
        }

        // When
        val result = backPressManager.onBackPressed()

        // Then
        assertTrue(result)
        assertTrue(secondCallbackInvoked) // Should continue execution
    }

    @Test
    fun should_createInstance_when_constructorCalled() {
        // Given & When
        val backPressManager = BackPressManagerImpl()

        // Then
        assertNotNull(backPressManager)
        assertTrue(backPressManager.isBackPressEnabled())
        assertEquals(0, backPressManager.getCallbackCount())
    }

    @Test
    fun should_handleMultipleBackPressCalls_when_provided() {
        // Given
        backPressManager.setBackPressEnabled(true)
        var callbackInvokedCount = 0
        backPressManager.addBackPressCallback { 
            callbackInvokedCount++
            true
        }

        // When
        backPressManager.onBackPressed()
        backPressManager.onBackPressed()
        backPressManager.onBackPressed()

        // Then
        assertEquals(3, callbackInvokedCount)
    }

    @Test
    fun should_handleEmptyCallback_when_provided() {
        // Given
        backPressManager.setBackPressEnabled(true)
        var callbackInvoked = false
        backPressManager.addBackPressCallback { 
            callbackInvoked = true
            false
        }

        // When
        val result = backPressManager.onBackPressed()

        // Then
        assertFalse(result)
        assertTrue(callbackInvoked)
    }

    @Test
    fun should_handleNullCallback_when_provided() {
        // Given
        backPressManager.setBackPressEnabled(true)
        backPressManager.addBackPressCallback { 
            // Empty callback
            false
        }

        // When
        val result = backPressManager.onBackPressed()

        // Then
        assertFalse(result)
    }

    @Test
    fun should_generateUniqueCallbackIds_when_multipleCallbacksAdded() {
        // Given
        val callback1: () -> Boolean = { true }
        val callback2: () -> Boolean = { true }

        // When
        val id1 = backPressManager.addBackPressCallback(callback1)
        val id2 = backPressManager.addBackPressCallback(callback2)

        // Then
        assertNotNull(id1)
        assertNotNull(id2)
        assertTrue(id1 != id2)
    }

    @Test
    fun should_handleBackPressAfterClear_when_callbacksCleared() {
        // Given
        backPressManager.setBackPressEnabled(true)
        backPressManager.addBackPressCallback { true }
        backPressManager.clearBackPressCallbacks()

        // When
        val result = backPressManager.onBackPressed()

        // Then
        assertFalse(result)
        assertEquals(0, backPressManager.getCallbackCount())
    }
} 