package com.yfy.basearchitecture.core.ui.api.utils

import com.yfy.basearchitecture.core.ui.api.handler.BaseError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidationUtilsTest {

    @Test
    fun `should validate password strength correctly`() {
        // Strong passwords - en az 8 karakter, büyük harf, küçük harf ve rakam
        assertTrue(ValidationUtils.isValidPassword("Password123"))
        assertTrue(ValidationUtils.isValidPassword("MySecurePass1"))
        assertTrue(ValidationUtils.isValidPassword("Abc12345"))
        assertTrue(ValidationUtils.isValidPassword("TestPass99"))

        // Weak passwords
        assertFalse(ValidationUtils.isValidPassword(""))
        assertFalse(ValidationUtils.isValidPassword("password")) // büyük harf yok
        assertFalse(ValidationUtils.isValidPassword("12345678")) // harf yok
        assertFalse(ValidationUtils.isValidPassword("Password")) // rakam yok
        assertFalse(ValidationUtils.isValidPassword("password123")) // büyük harf yok
        assertFalse(ValidationUtils.isValidPassword("PASSWORD123")) // küçük harf yok
        assertFalse(ValidationUtils.isValidPassword("Pass1")) // çok kısa
    }

    @Test
    fun `should validate password and return error if invalid`() {
        // Valid password
        assertNull(ValidationUtils.validatePassword("Password123"))

        // Invalid password
        val error = ValidationUtils.validatePassword("weak")
        assertNotNull(error)
        assertTrue(error is BaseError.ValidationError)
        assertEquals("password", (error as BaseError.ValidationError).field)
        assertTrue((error as BaseError.ValidationError).message.contains("at least 8 characters"))
    }

    @Test
    fun `should validate required fields correctly`() {
        // Valid required fields
        assertTrue(ValidationUtils.isRequiredField("test"))
        assertTrue(ValidationUtils.isRequiredField("0"))
        assertTrue(ValidationUtils.isRequiredField("false"))
        assertTrue(ValidationUtils.isRequiredField("   content   ")) // trim edilecek

        // Invalid required fields
        assertFalse(ValidationUtils.isRequiredField(""))
        assertFalse(ValidationUtils.isRequiredField("   "))
        assertFalse(ValidationUtils.isRequiredField(null))
    }

    @Test
    fun `should validate required field and return error if invalid`() {
        // Valid required field
        assertNull(ValidationUtils.validateRequiredField("test", "testField"))

        // Invalid required field
        val error = ValidationUtils.validateRequiredField("", "testField")
        assertNotNull(error)
        assertTrue(error is BaseError.ValidationError)
        assertEquals("testField", (error as BaseError.ValidationError).field)
        assertEquals("testField is required", (error as BaseError.ValidationError).message)
    }

    @Test
    fun `should validate minimum length correctly`() {
        // Valid minimum length
        assertTrue(ValidationUtils.hasMinimumLength("test", 3))
        assertTrue(ValidationUtils.hasMinimumLength("test", 4))
        assertTrue(ValidationUtils.hasMinimumLength("testing", 5))
        assertTrue(ValidationUtils.hasMinimumLength("a", 1))

        // Invalid minimum length
        assertFalse(ValidationUtils.hasMinimumLength("test", 5))
        assertFalse(ValidationUtils.hasMinimumLength("", 1))
        assertFalse(ValidationUtils.hasMinimumLength("ab", 3))
    }

    @Test
    fun `should validate minimum length and return error if invalid`() {
        // Valid minimum length
        assertNull(ValidationUtils.validateMinimumLength("test", 3, "testField"))

        // Invalid minimum length
        val error = ValidationUtils.validateMinimumLength("test", 5, "testField")
        assertNotNull(error)
        assertTrue(error is BaseError.ValidationError)
        assertEquals("testField", (error as BaseError.ValidationError).field)
        assertEquals("testField must be at least 5 characters", (error as BaseError.ValidationError).message)
    }

    @Test
    fun `should validate maximum length correctly`() {
        // Valid maximum length
        assertTrue(ValidationUtils.hasMaximumLength("test", 5))
        assertTrue(ValidationUtils.hasMaximumLength("test", 4))
        assertTrue(ValidationUtils.hasMaximumLength("", 10))
        assertTrue(ValidationUtils.hasMaximumLength("a", 1))

        // Invalid maximum length
        assertFalse(ValidationUtils.hasMaximumLength("testing", 5))
        assertFalse(ValidationUtils.hasMaximumLength("test", 3))
        assertFalse(ValidationUtils.hasMaximumLength("toolong", 6))
    }

    @Test
    fun `should validate maximum length and return error if invalid`() {
        // Valid maximum length
        assertNull(ValidationUtils.validateMaximumLength("test", 5, "testField"))

        // Invalid maximum length
        val error = ValidationUtils.validateMaximumLength("testing", 5, "testField")
        assertNotNull(error)
        assertTrue(error is BaseError.ValidationError)
        assertEquals("testField", (error as BaseError.ValidationError).field)
        assertEquals("testField must be at most 5 characters", (error as BaseError.ValidationError).message)
    }

    @Test
    fun `should validate numeric input correctly`() {
        // Valid numeric input
        assertTrue(ValidationUtils.isNumeric("123"))
        assertTrue(ValidationUtils.isNumeric("0"))
        assertTrue(ValidationUtils.isNumeric("-123"))
        assertTrue(ValidationUtils.isNumeric("123.45"))
        assertTrue(ValidationUtils.isNumeric("0.5"))
        assertTrue(ValidationUtils.isNumeric("999999"))

        // Invalid numeric input
        assertFalse(ValidationUtils.isNumeric(""))
        assertFalse(ValidationUtils.isNumeric("abc"))
        assertFalse(ValidationUtils.isNumeric("123abc"))
        assertFalse(ValidationUtils.isNumeric("12.34.56"))
        assertFalse(ValidationUtils.isNumeric("12,34"))
    }

    @Test
    fun `should validate numeric value and return error if invalid`() {
        // Valid numeric value
        assertNull(ValidationUtils.validateNumeric("123", "testField"))

        // Invalid numeric value
        val error = ValidationUtils.validateNumeric("abc", "testField")
        assertNotNull(error)
        assertTrue(error is BaseError.ValidationError)
        assertEquals("testField", (error as BaseError.ValidationError).field)
        assertEquals("testField must be a valid number", (error as BaseError.ValidationError).message)
    }

    @Test
    fun `should validate integer input correctly`() {
        // Valid integer input
        assertTrue(ValidationUtils.isInteger("123"))
        assertTrue(ValidationUtils.isInteger("0"))
        assertTrue(ValidationUtils.isInteger("-123"))
        assertTrue(ValidationUtils.isInteger("999999"))

        // Invalid integer input
        assertFalse(ValidationUtils.isInteger(""))
        assertFalse(ValidationUtils.isInteger("123.45"))
        assertFalse(ValidationUtils.isInteger("abc"))
        assertFalse(ValidationUtils.isInteger("123abc"))
        assertFalse(ValidationUtils.isInteger("12.0"))
    }

    @Test
    fun `should validate integer value and return error if invalid`() {
        // Valid integer value
        assertNull(ValidationUtils.validateInteger("123", "testField"))

        // Invalid integer value
        val error = ValidationUtils.validateInteger("abc", "testField")
        assertNotNull(error)
        assertTrue(error is BaseError.ValidationError)
        assertEquals("testField", (error as BaseError.ValidationError).field)
        assertEquals("testField must be a valid integer", (error as BaseError.ValidationError).message)
    }

    @Test
    fun `should validate positive number correctly`() {
        // Valid positive numbers
        assertTrue(ValidationUtils.isPositiveNumber("123"))
        assertTrue(ValidationUtils.isPositiveNumber("123.45"))
        assertTrue(ValidationUtils.isPositiveNumber("0.1"))
        assertTrue(ValidationUtils.isPositiveNumber("1"))
        assertTrue(ValidationUtils.isPositiveNumber("999.99"))

        // Invalid positive numbers
        assertFalse(ValidationUtils.isPositiveNumber(""))
        assertFalse(ValidationUtils.isPositiveNumber("0"))
        assertFalse(ValidationUtils.isPositiveNumber("-123"))
        assertFalse(ValidationUtils.isPositiveNumber("abc"))
        assertFalse(ValidationUtils.isPositiveNumber("-0.1"))
    }

    @Test
    fun `should validate positive number and return error if invalid`() {
        // Valid positive number
        assertNull(ValidationUtils.validatePositiveNumber("123", "testField"))

        // Invalid positive number
        val error = ValidationUtils.validatePositiveNumber("-123", "testField")
        assertNotNull(error)
        assertTrue(error is BaseError.ValidationError)
        assertEquals("testField", (error as BaseError.ValidationError).field)
        assertEquals("testField must be a positive number", (error as BaseError.ValidationError).message)
    }
}