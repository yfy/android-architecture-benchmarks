package com.yfy.basearchitecture.core.ui.api.handler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BaseErrorTest {

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

    @Test
    fun `should handle NetworkError with all properties`() {
        // Given
        val error = BaseError.NetworkError(
            message = "Network error",
            code = 404,
            retryable = true
        )
        
        // When & Then
        assertEquals("Network error", error.message)
        assertEquals(404, error.code)
        assertTrue(error.retryable)
        assertTrue(error.isRetryable())
        assertEquals("NETWORK_ERROR", error.getErrorCode())
    }

    @Test
    fun `should handle DatabaseError with table information`() {
        // Given
        val error = BaseError.DatabaseError(
            message = "Database error",
            table = "users"
        )
        
        // When & Then
        assertEquals("Database error", error.message)
        assertEquals("users", error.table)
        assertTrue(error.isRetryable())
        assertEquals("DATABASE_ERROR", error.getErrorCode())
    }

    @Test
    fun `should handle ValidationError with field and value`() {
        // Given
        val error = BaseError.ValidationError(
            field = "email",
            message = "Invalid email format",
            value = "invalid-email"
        )
        
        // When & Then
        assertEquals("email", error.field)
        assertEquals("Invalid email format", error.message)
        assertEquals("invalid-email", error.value)
        assertFalse(error.isRetryable())
        assertEquals("VALIDATION_ERROR", error.getErrorCode())
    }

    @Test
    fun `should handle ServerError with details`() {
        // Given
        val details = mapOf("error_code" to "E001", "timestamp" to "2023-01-01")
        val error = BaseError.ServerError(
            message = "Server error",
            code = 500,
            details = details
        )
        
        // When & Then
        assertEquals("Server error", error.message)
        assertEquals(500, error.code)
        assertEquals(details, error.details)
        assertTrue(error.isRetryable())
        assertEquals("SERVER_ERROR", error.getErrorCode())
    }

    @Test
    fun `should handle TimeoutError with custom timeout`() {
        // Given
        val error = BaseError.TimeoutError(
            message = "Request timed out",
            timeout = 30000L
        )
        
        // When & Then
        assertEquals("Request timed out", error.message)
        assertEquals(30000L, error.timeout)
        assertTrue(error.isRetryable())
        assertEquals("TIMEOUT_ERROR", error.getErrorCode())
    }

    @Test
    fun `should handle UnknownError with throwable`() {
        // Given
        val throwable = RuntimeException("Original exception")
        val error = BaseError.UnknownError(
            message = "Unknown error",
            throwable = throwable
        )
        
        // When & Then
        assertEquals("Unknown error", error.message)
        assertEquals(throwable, error.throwable)
        assertTrue(error.isRetryable())
        assertEquals("UNKNOWN_ERROR", error.getErrorCode())
    }
} 