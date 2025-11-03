package com.yfy.basearchitecture.core.datastore.impl.managers

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import app.cash.turbine.test
import com.yfy.basearchitecture.core.datastore.api.models.UserSession
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.serializer
import java.io.File
import java.nio.file.Files
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PreferenceManagerImplTest {

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var preferenceManager: PreferenceManagerImpl

    private lateinit var tempDir: File

    @BeforeTest
    fun setup() {
        tempDir = Files.createTempDirectory("test_preferences").toFile()
        dataStore = PreferenceDataStoreFactory.create {
            File(tempDir, "test_preferences.preferences_pb")
        }
        preferenceManager = PreferenceManagerImpl(dataStore)
    }

    @AfterTest
    fun tearDown() {
        tempDir.delete()
    }

    @Test
    fun `should save and retrieve string preference`() = runTest {
        // Given
        val key = "test_key"
        val value = "test_value"

        // When
        preferenceManager.setString(key, value)

        // Then
        preferenceManager.getString(key).test {
            assertEquals(value, awaitItem())
        }
    }

    @Test
    fun `should save and retrieve boolean preference`() = runTest {
        // Given
        val key = "test_boolean"
        val value = true

        // When
        preferenceManager.setBoolean(key, value)

        // Then
        preferenceManager.getBoolean(key, false).test {
            assertTrue(awaitItem())
        }
    }

    @Test
    fun `should save and retrieve complex object`() = runTest {
        // Given
        val key = "test_session"
        val userSession = UserSession(
            userId = "123",
            username = "testuser",
            email = "test@example.com",
            accessToken = "access_token",
            refreshToken = "refresh_token",
            expiresAt = System.currentTimeMillis() + 3600000L
        )

        // When
        preferenceManager.setObject(key, userSession, serializer())

        // Then
        preferenceManager.getObject(key, serializer<UserSession>(), userSession).test {
            val retrieved = awaitItem()
            assertEquals(userSession.userId, retrieved.userId)
            assertEquals(userSession.username, retrieved.username)
            assertEquals(userSession.email, retrieved.email)
        }
    }

    @Test
    fun `should remove preference`() = runTest {
        // Given
        val key = "test_remove"
        val value = "to_be_removed"

        // When
        preferenceManager.setString(key, value)
        preferenceManager.remove(key)

        // Then
        preferenceManager.getString(key, "default").test {
            assertEquals("default", awaitItem())
        }
    }

    @Test
    fun `should clear all preferences`() = runTest {
        // Given
        preferenceManager.setString("key1", "value1")
        preferenceManager.setString("key2", "value2")

        // When
        preferenceManager.clearAll()

        // Then
        preferenceManager.getString("key1", "default").test {
            assertEquals("default", awaitItem())
        }
        preferenceManager.getString("key2", "default").test {
            assertEquals("default", awaitItem())
        }
    }
}