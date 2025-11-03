package com.yfy.basearchitecture.core.ui.api.extensions

import com.yfy.basearchitecture.core.ui.api.handler.BaseError
import com.yfy.basearchitecture.core.ui.api.handler.ErrorHandler
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ErrorExtensionsTest {

    private lateinit var mockErrorHandler: ErrorHandler

    @Before
    fun setup() {
        mockErrorHandler = mockk(relaxed = true)
    }

    @Test
    fun `should show error using ErrorHandler`() {
        // Given
        val error = BaseError.NetworkError("Network error")
        
        // When
        error.show(mockErrorHandler)
        
        // Then
        verify { mockErrorHandler.showError(error) }
    }

    @Test
    fun `should handle error using ErrorHandler`() {
        // Given
        val error = BaseError.DatabaseError("Database error")
        
        // When
        error.handle(mockErrorHandler)
        
        // Then
        verify { mockErrorHandler.handleError(error) }
    }

    @Test
    fun `should check if error should be shown`() {
        // Given
        val error = BaseError.ValidationError("field", "Validation error")
        
        // When
        error.shouldShow(mockErrorHandler)
        
        // Then
        verify { mockErrorHandler.shouldShowError(error) }
    }

    @Test
    fun `should return correct user message for NetworkError`() {
        // Given
        val error = BaseError.NetworkError("Network connection failed")
        
        // When
        val message = error.getUserMessage()
        
        // Then
        assertEquals("Network connection failed", message)
    }

    @Test
    fun `should return default message for NetworkError with empty message`() {
        // Given
        val error = BaseError.NetworkError("")
        
        // When
        val message = error.getUserMessage()
        
        // Then
        assertEquals("Network error occurred", message)
    }

    @Test
    fun `should return correct user message for DatabaseError`() {
        // Given
        val error = BaseError.DatabaseError("Database connection failed")
        
        // When
        val message = error.getUserMessage()
        
        // Then
        assertEquals("Database connection failed", message)
    }

    @Test
    fun `should return default message for DatabaseError with empty message`() {
        // Given
        val error = BaseError.DatabaseError("")
        
        // When
        val message = error.getUserMessage()
        
        // Then
        assertEquals("Database error occurred", message)
    }

    @Test
    fun `should return correct user message for ValidationError`() {
        // Given
        val error = BaseError.ValidationError("email", "Invalid email format")
        
        // When
        val message = error.getUserMessage()
        
        // Then
        assertEquals("Invalid email format", message)
    }

    @Test
    fun `should return default message for ValidationError with empty message`() {
        // Given
        val error = BaseError.ValidationError("email", "")
        
        // When
        val message = error.getUserMessage()
        
        // Then
        assertEquals("Validation error occurred", message)
    }

    @Test
    fun `should return correct user message for AuthenticationError`() {
        // Given
        val error = BaseError.AuthenticationError("Invalid credentials")
        
        // When
        val message = error.getUserMessage()
        
        // Then
        assertEquals("Invalid credentials", message)
    }

    @Test
    fun `should return default message for AuthenticationError with empty message`() {
        // Given
        val error = BaseError.AuthenticationError("")
        
        // When
        val message = error.getUserMessage()
        
        // Then
        assertEquals("Authentication error occurred", message)
    }

    @Test
    fun `should return correct user message for PermissionError`() {
        // Given
        val error = BaseError.PermissionError("Camera permission required")
        
        // When
        val message = error.getUserMessage()
        
        // Then
        assertEquals("Camera permission required", message)
    }

    @Test
    fun `should return default message for PermissionError with empty message`() {
        // Given
        val error = BaseError.PermissionError("")
        
        // When
        val message = error.getUserMessage()
        
        // Then
        assertEquals("Permission denied", message)
    }

    @Test
    fun `should return correct user message for UnknownError`() {
        // Given
        val error = BaseError.UnknownError("Unexpected error occurred")
        
        // When
        val message = error.getUserMessage()
        
        // Then
        assertEquals("Unexpected error occurred", message)
    }

    @Test
    fun `should return default message for UnknownError with empty message`() {
        // Given
        val error = BaseError.UnknownError("")
        
        // When
        val message = error.getUserMessage()
        
        // Then
        assertEquals("An unknown error occurred", message)
    }

    @Test
    fun `should return correct user message for ServerError`() {
        // Given
        val error = BaseError.ServerError("Internal server error", 500)
        
        // When
        val message = error.getUserMessage()
        
        // Then
        assertEquals("Internal server error", message)
    }

    @Test
    fun `should return default message for ServerError with empty message`() {
        // Given
        val error = BaseError.ServerError("", 500)
        
        // When
        val message = error.getUserMessage()
        
        // Then
        assertEquals("Server error occurred", message)
    }

    @Test
    fun `should return correct user message for TimeoutError`() {
        // Given
        val error = BaseError.TimeoutError("Request timed out after 30 seconds")
        
        // When
        val message = error.getUserMessage()
        
        // Then
        assertEquals("Request timed out after 30 seconds", message)
    }

    @Test
    fun `should return default message for TimeoutError`() {
        // Given
        val error = BaseError.TimeoutError()
        
        // When
        val message = error.getUserMessage()
        
        // Then
        assertEquals("Request timed out", message)
    }

    @Test
    fun `should return correct user message for NoInternetError`() {
        // Given
        val error = BaseError.NoInternetError("No internet connection available")
        
        // When
        val message = error.getUserMessage()
        
        // Then
        assertEquals("No internet connection available", message)
    }

    @Test
    fun `should return default message for NoInternetError`() {
        // Given
        val error = BaseError.NoInternetError()
        
        // When
        val message = error.getUserMessage()
        
        // Then
        assertEquals("No internet connection", message)
    }

    @Test
    fun `should return correct error code for NetworkError`() {
        // Given
        val error = BaseError.NetworkError("Network error")
        
        // When
        val code = error.getErrorCode()
        
        // Then
        assertEquals("NETWORK_ERROR", code)
    }

    @Test
    fun `should return correct error code for DatabaseError`() {
        // Given
        val error = BaseError.DatabaseError("Database error")
        
        // When
        val code = error.getErrorCode()
        
        // Then
        assertEquals("DATABASE_ERROR", code)
    }

    @Test
    fun `should return correct error code for ValidationError`() {
        // Given
        val error = BaseError.ValidationError("field", "Validation error")
        
        // When
        val code = error.getErrorCode()
        
        // Then
        assertEquals("VALIDATION_ERROR", code)
    }

    @Test
    fun `should return correct error code for AuthenticationError`() {
        // Given
        val error = BaseError.AuthenticationError("Auth error")
        
        // When
        val code = error.getErrorCode()
        
        // Then
        assertEquals("AUTH_ERROR", code)
    }

    @Test
    fun `should return correct error code for PermissionError`() {
        // Given
        val error = BaseError.PermissionError("Permission error")
        
        // When
        val code = error.getErrorCode()
        
        // Then
        assertEquals("PERMISSION_ERROR", code)
    }

    @Test
    fun `should return correct error code for UnknownError`() {
        // Given
        val error = BaseError.UnknownError("Unknown error")
        
        // When
        val code = error.getErrorCode()
        
        // Then
        assertEquals("UNKNOWN_ERROR", code)
    }

    @Test
    fun `should return correct error code for ServerError`() {
        // Given
        val error = BaseError.ServerError("Server error", 500)
        
        // When
        val code = error.getErrorCode()
        
        // Then
        assertEquals("SERVER_ERROR", code)
    }

    @Test
    fun `should return correct error code for TimeoutError`() {
        // Given
        val error = BaseError.TimeoutError()
        
        // When
        val code = error.getErrorCode()
        
        // Then
        assertEquals("TIMEOUT_ERROR", code)
    }

    @Test
    fun `should return correct error code for NoInternetError`() {
        // Given
        val error = BaseError.NoInternetError()
        
        // When
        val code = error.getErrorCode()
        
        // Then
        assertEquals("NO_INTERNET_ERROR", code)
    }

    @Test
    fun `should be retryable for NetworkError with retryable true`() {
        // Given
        val error = BaseError.NetworkError("Network error", retryable = true)
        
        // When
        val isRetryable = error.isRetryable()
        
        // Then
        assertTrue(isRetryable)
    }

    @Test
    fun `should not be retryable for NetworkError with retryable false`() {
        // Given
        val error = BaseError.NetworkError("Network error", retryable = false)
        
        // When
        val isRetryable = error.isRetryable()
        
        // Then
        assertFalse(isRetryable)
    }

    @Test
    fun `should be retryable for DatabaseError`() {
        // Given
        val error = BaseError.DatabaseError("Database error")
        
        // When
        val isRetryable = error.isRetryable()
        
        // Then
        assertTrue(isRetryable)
    }

    @Test
    fun `should not be retryable for ValidationError`() {
        // Given
        val error = BaseError.ValidationError("field", "Validation error")
        
        // When
        val isRetryable = error.isRetryable()
        
        // Then
        assertFalse(isRetryable)
    }

    @Test
    fun `should be retryable for AuthenticationError when shouldLogout is false`() {
        // Given
        val error = BaseError.AuthenticationError("Auth error", shouldLogout = false)
        
        // When
        val isRetryable = error.isRetryable()
        
        // Then
        assertTrue(isRetryable)
    }

    @Test
    fun `should not be retryable for AuthenticationError when shouldLogout is true`() {
        // Given
        val error = BaseError.AuthenticationError("Auth error", shouldLogout = true)
        
        // When
        val isRetryable = error.isRetryable()
        
        // Then
        assertFalse(isRetryable)
    }

    @Test
    fun `should not be retryable for PermissionError`() {
        // Given
        val error = BaseError.PermissionError("Permission error")
        
        // When
        val isRetryable = error.isRetryable()
        
        // Then
        assertFalse(isRetryable)
    }

    @Test
    fun `should be retryable for UnknownError`() {
        // Given
        val error = BaseError.UnknownError("Unknown error")
        
        // When
        val isRetryable = error.isRetryable()
        
        // Then
        assertTrue(isRetryable)
    }

    @Test
    fun `should be retryable for ServerError with 5xx code`() {
        // Given
        val error = BaseError.ServerError("Server error", 500)
        
        // When
        val isRetryable = error.isRetryable()
        
        // Then
        assertTrue(isRetryable)
    }

    @Test
    fun `should not be retryable for ServerError with 4xx code`() {
        // Given
        val error = BaseError.ServerError("Client error", 400)
        
        // When
        val isRetryable = error.isRetryable()
        
        // Then
        assertFalse(isRetryable)
    }

    @Test
    fun `should be retryable for TimeoutError`() {
        // Given
        val error = BaseError.TimeoutError()
        
        // When
        val isRetryable = error.isRetryable()
        
        // Then
        assertTrue(isRetryable)
    }

    @Test
    fun `should be retryable for NoInternetError`() {
        // Given
        val error = BaseError.NoInternetError()
        
        // When
        val isRetryable = error.isRetryable()
        
        // Then
        assertTrue(isRetryable)
    }
} 