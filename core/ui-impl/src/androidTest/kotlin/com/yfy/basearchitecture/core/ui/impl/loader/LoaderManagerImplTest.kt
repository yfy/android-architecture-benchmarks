package com.yfy.basearchitecture.core.ui.impl.loader

import android.content.Context
import com.yfy.basearchitecture.core.ui.api.managers.LoaderType
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class LoaderManagerImplTest {

    @Mock
    private lateinit var mockContext: Context

    private lateinit var loaderManager: LoaderManagerImpl

    @Before
    fun setup() {
        loaderManager = LoaderManagerImpl(mockContext)
    }

    @Test
    fun should_showLoader_when_messageProvided() {
        // Given
        val message = "Loading..."
        
        var callbackInvoked = false
        loaderManager.setLoaderStateCallback { config, isShowing -> 
            callbackInvoked = true
            assertTrue(isShowing)
            assertNotNull(config)
            assertEquals(LoaderType.CIRCULAR, config?.type)
            assertEquals(message, config?.message)
        }

        // When
        loaderManager.showLoader(message)

        // Then
        assertTrue(callbackInvoked)
        assertTrue(loaderManager.isLoaderShowing())
        assertEquals(message, loaderManager.getCurrentLoaderMessage())
        assertEquals(LoaderType.CIRCULAR, loaderManager.getCurrentLoaderType())
    }

    @Test
    fun should_showLoaderWithType_when_typeAndMessageProvided() {
        // Given
        val type = LoaderType.LINEAR
        val message = "Loading with type..."
        
        var callbackInvoked = false
        loaderManager.setLoaderStateCallback { config, isShowing -> 
            callbackInvoked = true
            assertTrue(isShowing)
            assertNotNull(config)
            assertEquals(type, config?.type)
            assertEquals(message, config?.message)
        }

        // When
        loaderManager.showLoader(type, message)

        // Then
        assertTrue(callbackInvoked)
        assertTrue(loaderManager.isLoaderShowing())
        assertEquals(message, loaderManager.getCurrentLoaderMessage())
        assertEquals(type, loaderManager.getCurrentLoaderType())
    }

    @Test
    fun should_hideLoader_when_called() {
        // Given
        loaderManager.showLoader("Loading...")
        
        var callbackInvoked = false
        loaderManager.setLoaderStateCallback { config, isShowing -> 
            callbackInvoked = true
            assertFalse(isShowing)
            assertNull(config)
        }

        // When
        loaderManager.hideLoader()

        // Then
        assertTrue(callbackInvoked)
        assertFalse(loaderManager.isLoaderShowing())
        assertNull(loaderManager.getCurrentLoaderMessage())
        assertNull(loaderManager.getCurrentLoaderType())
    }

    @Test
    fun should_returnTrue_when_loaderShowing() {
        // Given
        loaderManager.showLoader("Loading...")

        // When
        val result = loaderManager.isLoaderShowing()

        // Then
        assertTrue(result)
    }

    @Test
    fun should_returnFalse_when_loaderNotShowing() {
        // Given
        // No loader shown

        // When
        val result = loaderManager.isLoaderShowing()

        // Then
        assertFalse(result)
    }

    @Test
    fun should_returnCurrentMessage_when_loaderShowing() {
        // Given
        val message = "Current loader message"
        loaderManager.showLoader(message)

        // When
        val result = loaderManager.getCurrentLoaderMessage()

        // Then
        assertEquals(message, result)
    }

    @Test
    fun should_returnNullMessage_when_loaderNotShowing() {
        // Given
        // No loader shown

        // When
        val result = loaderManager.getCurrentLoaderMessage()

        // Then
        assertNull(result)
    }

    @Test
    fun should_returnCurrentType_when_loaderShowing() {
        // Given
        val type = LoaderType.LINEAR
        loaderManager.showLoader(type, "Loading...")

        // When
        val result = loaderManager.getCurrentLoaderType()

        // Then
        assertEquals(type, result)
    }

    @Test
    fun should_returnNullType_when_loaderNotShowing() {
        // Given
        // No loader shown

        // When
        val result = loaderManager.getCurrentLoaderType()

        // Then
        assertNull(result)
    }

    @Test
    fun should_getLoaderState_when_called() {
        // Given
        val type = LoaderType.CIRCULAR
        val message = "Loading state..."
        loaderManager.showLoader(type, message)

        // When
        val state = loaderManager.getLoaderState()

        // Then
        assertNotNull(state.first)
        assertEquals(type, state.first?.type)
        assertEquals(message, state.first?.message)
        assertTrue(state.second)
    }

    @Test
    fun should_getEmptyState_when_loaderNotShowing() {
        // Given
        // No loader shown

        // When
        val state = loaderManager.getLoaderState()

        // Then
        assertNull(state.first)
        assertFalse(state.second)
    }

    @Test
    fun should_handleNullCallbacks_when_provided() {
        // Given
        val message = "Loading..."

        // When & Then - Should not throw exception
        loaderManager.showLoader(message)
        loaderManager.hideLoader()
    }

    @Test
    fun should_createInstance_when_contextProvided() {
        // Given
        val context = mockk<Context>()

        // When
        val loaderManager = LoaderManagerImpl(context)

        // Then
        assertNotNull(loaderManager)
    }

    @Test
    fun should_handleMultipleShowHideCalls_when_provided() {
        // Given
        val message1 = "Loading 1..."
        val message2 = "Loading 2..."
        val type = LoaderType.LINEAR

        // When
        loaderManager.showLoader(message1)
        val message1Result = loaderManager.getCurrentLoaderMessage()
        val type1Result = loaderManager.getCurrentLoaderType()
        
        loaderManager.hideLoader()
        val hiddenResult = loaderManager.isLoaderShowing()
        
        loaderManager.showLoader(type, message2)
        val message2Result = loaderManager.getCurrentLoaderMessage()
        val type2Result = loaderManager.getCurrentLoaderType()

        // Then
        assertEquals(message1, message1Result)
        assertEquals(LoaderType.CIRCULAR, type1Result)
        assertFalse(hiddenResult)
        assertEquals(message2, message2Result)
        assertEquals(type, type2Result)
    }

    @Test
    fun should_handleAllLoaderTypes_when_provided() {
        // Given
        val types = listOf(LoaderType.CIRCULAR, LoaderType.LINEAR)
        val message = "Loading..."

        // When & Then
        types.forEach { type ->
            loaderManager.showLoader(type, message)
            assertEquals(type, loaderManager.getCurrentLoaderType())
            assertEquals(message, loaderManager.getCurrentLoaderMessage())
            assertTrue(loaderManager.isLoaderShowing())
            
            loaderManager.hideLoader()
            assertFalse(loaderManager.isLoaderShowing())
            assertNull(loaderManager.getCurrentLoaderMessage())
            assertNull(loaderManager.getCurrentLoaderType())
        }
    }

    @Test
    fun should_handleEmptyMessage_when_provided() {
        // Given
        val message = ""
        
        var callbackInvoked = false
        loaderManager.setLoaderStateCallback { config, isShowing -> 
            callbackInvoked = true
            assertTrue(isShowing)
            assertEquals("", config?.message)
        }

        // When
        loaderManager.showLoader(message)

        // Then
        assertTrue(callbackInvoked)
        assertEquals("", loaderManager.getCurrentLoaderMessage())
    }

    @Test
    fun should_handleLongMessage_when_provided() {
        // Given
        val message = "This is a very long loading message that should be handled properly by the loader manager implementation"
        
        var callbackInvoked = false
        loaderManager.setLoaderStateCallback { config, isShowing -> 
            callbackInvoked = true
            assertTrue(isShowing)
            assertEquals(message, config?.message)
        }

        // When
        loaderManager.showLoader(message)

        // Then
        assertTrue(callbackInvoked)
        assertEquals(message, loaderManager.getCurrentLoaderMessage())
    }
} 