package com.yfy.basearchitecture.core.ui.api.managers

import kotlinx.serialization.Serializable

/**
 * Supported languages enum with language codes and display names
 */
@Serializable
enum class SupportedLanguage(val code: String, val displayName: String, val isRTL: Boolean = false) {
    ENGLISH("en", "English"),
    TURKISH("tr", "Türkçe"),
    GERMAN("de", "Deutsch"),
    FRENCH("fr", "Français"),
    RUSSIAN("ru", "Русский"),
    ARABIC("ar", "العربية", isRTL = true),
    SPANISH("es", "Español");

    companion object {
        /**
         * Get language by code
         */
        fun fromCode(code: String): SupportedLanguage? {
            return entries.find { it.code == code }
        }

        /**
         * Get default language
         */
        fun getDefault(): SupportedLanguage = ENGLISH

        /**
         * Get all RTL languages
         */
        fun getRTLanguages(): List<SupportedLanguage> {
            return entries.filter { it.isRTL }
        }

        /**
         * Check if language code is RTL
         */
        fun isRTL(code: String): Boolean {
            return fromCode(code)?.isRTL ?: false
        }
    }
} 