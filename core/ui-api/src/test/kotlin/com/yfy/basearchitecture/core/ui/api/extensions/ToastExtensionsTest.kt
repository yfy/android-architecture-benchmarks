package com.yfy.basearchitecture.core.ui.api.extensions

import com.yfy.basearchitecture.core.ui.api.managers.ToastDuration
import com.yfy.basearchitecture.core.ui.api.managers.ToastManager
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ToastExtensionsTest {
    @Mock
    private lateinit var toastManager: ToastManager

    private val testMessage = "Test message"
    private val emptyMessage = ""
    private val nullMessage: String? = null

    @Before
    fun setup() {
        // No stubbing needed, just verify calls
    }

    @Test
    fun `test showShort extension`() {
        testMessage.showShort(toastManager)
        verify(toastManager).showShort(testMessage)
    }

    @Test
    fun `test showLong extension`() {
        testMessage.showLong(toastManager)
        verify(toastManager).showLong(testMessage)
    }

    @Test
    fun `test showToast with default duration`() {
        testMessage.showToast(toastManager)
        verify(toastManager).show(testMessage, ToastDuration.SHORT)
    }

    @Test
    fun `test showToast with custom duration`() {
        testMessage.showToast(toastManager, ToastDuration.LONG)
        verify(toastManager).show(testMessage, ToastDuration.LONG)
    }

    @Test
    fun `test showSuccess with default duration`() {
        testMessage.showSuccess(toastManager)
        verify(toastManager).showSuccess(testMessage, ToastDuration.SHORT)
    }

    @Test
    fun `test showSuccess with custom duration`() {
        testMessage.showSuccess(toastManager, ToastDuration.LONG)
        verify(toastManager).showSuccess(testMessage, ToastDuration.LONG)
    }

    @Test
    fun `test showError with default duration`() {
        testMessage.showError(toastManager)
        verify(toastManager).showError(testMessage, ToastDuration.LONG)
    }

    @Test
    fun `test showError with custom duration`() {
        testMessage.showError(toastManager, ToastDuration.SHORT)
        verify(toastManager).showError(testMessage, ToastDuration.SHORT)
    }

    @Test
    fun `test showWarning with default duration`() {
        testMessage.showWarning(toastManager)
        verify(toastManager).showWarning(testMessage, ToastDuration.SHORT)
    }

    @Test
    fun `test showWarning with custom duration`() {
        testMessage.showWarning(toastManager, ToastDuration.LONG)
        verify(toastManager).showWarning(testMessage, ToastDuration.LONG)
    }

    @Test
    fun `test showInfo with default duration`() {
        testMessage.showInfo(toastManager)
        verify(toastManager).showInfo(testMessage, ToastDuration.SHORT)
    }

    @Test
    fun `test showInfo with custom duration`() {
        testMessage.showInfo(toastManager, ToastDuration.LONG)
        verify(toastManager).showInfo(testMessage, ToastDuration.LONG)
    }

    @Test
    fun `test showShortOrEmpty with non-null string`() {
        testMessage.showShortOrEmpty(toastManager)
        verify(toastManager).showShort(testMessage)
    }

    @Test
    fun `test showShortOrEmpty with null string`() {
        nullMessage.showShortOrEmpty(toastManager)
        verify(toastManager).showShort("")
    }

    @Test
    fun `test showShortOrEmpty with null string and custom default`() {
        nullMessage.showShortOrEmpty(toastManager, "Default message")
        verify(toastManager).showShort("Default message")
    }

    @Test
    fun `test showLongOrEmpty with non-null string`() {
        testMessage.showLongOrEmpty(toastManager)
        verify(toastManager).showLong(testMessage)
    }

    @Test
    fun `test showLongOrEmpty with null string`() {
        nullMessage.showLongOrEmpty(toastManager)
        verify(toastManager).showLong("")
    }

    @Test
    fun `test showLongOrEmpty with null string and custom default`() {
        nullMessage.showLongOrEmpty(toastManager, "Default message")
        verify(toastManager).showLong("Default message")
    }

    @Test
    fun `test showSuccessOrEmpty with non-null string`() {
        testMessage.showSuccessOrEmpty(toastManager)
        verify(toastManager).showSuccess(testMessage)
    }

    @Test
    fun `test showSuccessOrEmpty with null string`() {
        nullMessage.showSuccessOrEmpty(toastManager)
        verify(toastManager).showSuccess("")
    }

    @Test
    fun `test showSuccessOrEmpty with null string and custom default`() {
        nullMessage.showSuccessOrEmpty(toastManager, "Default message")
        verify(toastManager).showSuccess("Default message")
    }

    @Test
    fun `test showErrorOrEmpty with non-null string`() {
        testMessage.showErrorOrEmpty(toastManager)
        verify(toastManager).showError(testMessage)
    }

    @Test
    fun `test showErrorOrEmpty with null string`() {
        nullMessage.showErrorOrEmpty(toastManager)
        verify(toastManager).showError("")
    }

    @Test
    fun `test showErrorOrEmpty with null string and custom default`() {
        nullMessage.showErrorOrEmpty(toastManager, "Default message")
        verify(toastManager).showError("Default message")
    }

    @Test
    fun `test multiple extension calls`() {
        testMessage.showShort(toastManager)
        testMessage.showLong(toastManager)
        testMessage.showSuccess(toastManager)
        testMessage.showError(toastManager)
        verify(toastManager).showShort(testMessage)
        verify(toastManager).showLong(testMessage)
        verify(toastManager).showSuccess(testMessage, ToastDuration.SHORT)
        verify(toastManager).showError(testMessage, ToastDuration.LONG)
    }

    @Test
    fun `test empty string handling`() {
        emptyMessage.showShort(toastManager)
        emptyMessage.showLong(toastManager)
        emptyMessage.showSuccess(toastManager)
        emptyMessage.showError(toastManager)
        verify(toastManager).showShort(emptyMessage)
        verify(toastManager).showLong(emptyMessage)
        verify(toastManager).showSuccess(emptyMessage, ToastDuration.SHORT)
        verify(toastManager).showError(emptyMessage, ToastDuration.LONG)
    }
} 