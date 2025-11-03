package com.yfy.basearchitecture.core.ui.api.utils

import com.yfy.basearchitecture.core.ui.api.handler.BaseError

/**
 * Validation utility functions
 */
object ValidationUtils {
    
    /**
     * Validate email format
     */
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    /**
     * Validate email and return error if invalid
     */
    fun validateEmail(email: String): BaseError? {
        return if (isValidEmail(email)) null else {
            BaseError.ValidationError("email", "Invalid email format")
        }
    }
    
    /**
     * Validate password strength
     */
    fun isValidPassword(password: String, minLength: Int = 8): Boolean {
        return password.length >= minLength &&
                password.any { it.isUpperCase() } &&
                password.any { it.isLowerCase() } &&
                password.any { it.isDigit() }
    }
    
    /**
     * Validate password and return error if invalid
     */
    fun validatePassword(password: String, minLength: Int = 8): BaseError? {
        return if (isValidPassword(password, minLength)) null else {
            BaseError.ValidationError("password", "Password must be at least $minLength characters with uppercase, lowercase, and number")
        }
    }
    
    /**
     * Validate phone number format
     */
    fun isValidPhoneNumber(phone: String): Boolean {
        return android.util.Patterns.PHONE.matcher(phone).matches()
    }
    
    /**
     * Validate phone number and return error if invalid
     */
    fun validatePhoneNumber(phone: String): BaseError? {
        return if (isValidPhoneNumber(phone)) null else {
            BaseError.ValidationError("phone", "Invalid phone number format")
        }
    }
    
    /**
     * Validate URL format
     */
    fun isValidUrl(url: String): Boolean {
        return android.util.Patterns.WEB_URL.matcher(url).matches()
    }
    
    /**
     * Validate URL and return error if invalid
     */
    fun validateUrl(url: String): BaseError? {
        return if (isValidUrl(url)) null else {
            BaseError.ValidationError("url", "Invalid URL format")
        }
    }
    
    /**
     * Validate required field
     */
    fun isRequiredField(value: String?): Boolean {
        return !value.isNullOrBlank()
    }
    
    /**
     * Validate required field and return error if invalid
     */
    fun validateRequiredField(value: String?, fieldName: String): BaseError? {
        return if (isRequiredField(value)) null else {
            BaseError.ValidationError(fieldName, "$fieldName is required")
        }
    }
    
    /**
     * Validate minimum length
     */
    fun hasMinimumLength(value: String, minLength: Int): Boolean {
        return value.length >= minLength
    }
    
    /**
     * Validate minimum length and return error if invalid
     */
    fun validateMinimumLength(value: String, minLength: Int, fieldName: String): BaseError? {
        return if (hasMinimumLength(value, minLength)) null else {
            BaseError.ValidationError(fieldName, "$fieldName must be at least $minLength characters")
        }
    }
    
    /**
     * Validate maximum length
     */
    fun hasMaximumLength(value: String, maxLength: Int): Boolean {
        return value.length <= maxLength
    }
    
    /**
     * Validate maximum length and return error if invalid
     */
    fun validateMaximumLength(value: String, maxLength: Int, fieldName: String): BaseError? {
        return if (hasMaximumLength(value, maxLength)) null else {
            BaseError.ValidationError(fieldName, "$fieldName must be at most $maxLength characters")
        }
    }
    
    /**
     * Validate numeric value
     */
    fun isNumeric(value: String): Boolean {
        return value.toDoubleOrNull() != null
    }
    
    /**
     * Validate numeric value and return error if invalid
     */
    fun validateNumeric(value: String, fieldName: String): BaseError? {
        return if (isNumeric(value)) null else {
            BaseError.ValidationError(fieldName, "$fieldName must be a valid number")
        }
    }
    
    /**
     * Validate integer value
     */
    fun isInteger(value: String): Boolean {
        return value.toIntOrNull() != null
    }
    
    /**
     * Validate integer value and return error if invalid
     */
    fun validateInteger(value: String, fieldName: String): BaseError? {
        return if (isInteger(value)) null else {
            BaseError.ValidationError(fieldName, "$fieldName must be a valid integer")
        }
    }
    
    /**
     * Validate positive number
     */
    fun isPositiveNumber(value: String): Boolean {
        val number = value.toDoubleOrNull()
        return number != null && number > 0
    }
    
    /**
     * Validate positive number and return error if invalid
     */
    fun validatePositiveNumber(value: String, fieldName: String): BaseError? {
        return if (isPositiveNumber(value)) null else {
            BaseError.ValidationError(fieldName, "$fieldName must be a positive number")
        }
    }
} 