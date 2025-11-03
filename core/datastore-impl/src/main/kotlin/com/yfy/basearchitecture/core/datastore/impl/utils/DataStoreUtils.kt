package com.yfy.basearchitecture.core.datastore.impl.utils

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber

object DataStoreUtils {

    private fun <T> Flow<Preferences>.getValueFlow(
        key: Preferences.Key<T>,
        defaultValue: T
    ): Flow<T> {
        return this.map { preferences ->
            preferences[key] ?: defaultValue
        }.catch { exception ->
            Timber.e(exception, "Error reading preference: ${key.name}")
            emit(defaultValue)
        }
    }

    fun Flow<Preferences>.getStringFlow(
        key: String,
        defaultValue: String = ""
    ): Flow<String> {
        val prefKey = stringPreferencesKey(key)
        return getValueFlow(prefKey, defaultValue)
    }

    fun Flow<Preferences>.getIntFlow(
        key: String,
        defaultValue: Int = 0
    ): Flow<Int> {
        val prefKey = intPreferencesKey(key)
        return getValueFlow(prefKey, defaultValue)
    }

    fun Flow<Preferences>.getBooleanFlow(
        key: String,
        defaultValue: Boolean = false
    ): Flow<Boolean> {
        val prefKey = booleanPreferencesKey(key)
        return getValueFlow(prefKey, defaultValue)
    }

    fun Flow<Preferences>.getLongFlow(
        key: String,
        defaultValue: Long = 0L
    ): Flow<Long> {
        val prefKey = longPreferencesKey(key)
        return getValueFlow(prefKey, defaultValue)
    }

    fun Flow<Preferences>.getFloatFlow(
        key: String,
        defaultValue: Float = 0f
    ): Flow<Float> {
        val prefKey = floatPreferencesKey(key)
        return getValueFlow(prefKey, defaultValue)
    }

    fun Flow<Preferences>.getStringSetFlow(
        key: String,
        defaultValue: Set<String> = emptySet()
    ): Flow<Set<String>> {
        val prefKey = stringSetPreferencesKey(key)
        return getValueFlow(prefKey, defaultValue)
    }

    fun Flow<Preferences>.hasKeyFlow(key: String): Flow<Boolean> {
        val prefKey = stringPreferencesKey(key)
        return this.map { preferences ->
            preferences.contains(prefKey)
        }.catch { exception ->
            Timber.e(exception, "Error checking key existence: $key")
            emit(false)
        }
    }

    fun Flow<Preferences>.getAllKeysFlow(): Flow<Set<String>> {
        return this.map { preferences ->
            preferences.asMap().keys.map { it.name }.toSet()
        }.catch { exception ->
            Timber.e(exception, "Error getting all keys")
            emit(emptySet())
        }
    }
}