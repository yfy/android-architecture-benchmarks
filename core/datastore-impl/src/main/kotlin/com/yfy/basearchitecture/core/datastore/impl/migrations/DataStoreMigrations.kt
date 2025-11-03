package com.yfy.basearchitecture.core.datastore.impl.migrations

import androidx.datastore.core.DataMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import timber.log.Timber

object DataStoreMigrations {

    fun getMigrations(): List<DataMigration<Preferences>> {
        return listOf(
            Migration1to2(),
            Migration2to3()
        )
    }

    private class Migration1to2 : DataMigration<Preferences> {
        override suspend fun shouldMigrate(currentData: Preferences): Boolean {
            // Check if migration is needed
            return !currentData.contains(stringPreferencesKey("migration_version")) ||
                    currentData[stringPreferencesKey("migration_version")] == "1"
        }

        override suspend fun migrate(currentData: Preferences): Preferences {
            return try {
                val mutablePreferences = currentData.toMutablePreferences()

                // Example: Migrate old theme setting to new format
                val oldTheme = currentData[booleanPreferencesKey("dark_theme")]
                if (oldTheme != null) {
                    val newThemeValue = if (oldTheme) "DARK" else "LIGHT"
                    mutablePreferences[stringPreferencesKey("theme_mode")] = newThemeValue
                    mutablePreferences.remove(booleanPreferencesKey("dark_theme"))
                }

                // Set migration version
                mutablePreferences[stringPreferencesKey("migration_version")] = "2"

                Timber.i("DataStore migration 1->2 completed successfully")
                mutablePreferences.toPreferences()
            } catch (e: Exception) {
                Timber.e(e, "Failed to migrate DataStore from version 1 to 2")
                throw e
            }
        }

        override suspend fun cleanUp() {
            Timber.i("Migration 1->2 cleanup completed")
        }
    }

    private class Migration2to3 : DataMigration<Preferences> {
        override suspend fun shouldMigrate(currentData: Preferences): Boolean {
            return currentData[stringPreferencesKey("migration_version")] == "2"
        }

        override suspend fun migrate(currentData: Preferences): Preferences {
            return try {
                val mutablePreferences = currentData.toMutablePreferences()

                // Example: Add default notification settings
                if (!currentData.contains(stringPreferencesKey("notification_settings"))) {
                    // Set default notification settings as JSON
                    val defaultSettings = """
                        {
                            "pushNotificationsEnabled": true,
                            "soundEnabled": true,
                            "vibrationEnabled": true,
                            "showOnLockscreen": true,
                            "categorySettings": {}
                        }
                    """.trimIndent()
                    mutablePreferences[stringPreferencesKey("notification_settings")] = defaultSettings
                }

                // Set migration version
                mutablePreferences[stringPreferencesKey("migration_version")] = "3"

                Timber.i("DataStore migration 2->3 completed successfully")
                mutablePreferences.toPreferences()
            } catch (e: Exception) {
                Timber.e(e, "Failed to migrate DataStore from version 2 to 3")
                throw e
            }
        }

        override suspend fun cleanUp() {
            Timber.i("Migration 2->3 cleanup completed")
        }
    }
}