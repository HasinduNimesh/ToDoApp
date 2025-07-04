package com.example.moderntodo.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.moderntodo.data.model.AppSettings
import com.example.moderntodo.data.model.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val SETTINGS_KEY = stringPreferencesKey("app_settings")
    
    private val json = Json { 
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { preferences ->
        val settingsJson = preferences[SETTINGS_KEY]
        if (settingsJson != null) {
            try {
                json.decodeFromString<AppSettings>(settingsJson)
            } catch (e: Exception) {
                AppSettings()
            }
        } else {
            AppSettings()
        }
    }

    suspend fun updateSettings(settings: AppSettings) {
        context.dataStore.edit { preferences ->
            preferences[SETTINGS_KEY] = json.encodeToString(settings)
        }
    }

    suspend fun updateTheme(theme: ThemeMode) {
        context.dataStore.edit { preferences ->
            val currentSettings = preferences[SETTINGS_KEY]?.let { 
                try {
                    json.decodeFromString<AppSettings>(it)
                } catch (e: Exception) {
                    AppSettings()
                }
            } ?: AppSettings()
            
            val updatedSettings = currentSettings.copy(theme = theme)
            preferences[SETTINGS_KEY] = json.encodeToString(updatedSettings)
        }
    }

    suspend fun updateNotificationSettings(notificationSettings: com.example.moderntodo.data.model.NotificationSettings) {
        context.dataStore.edit { preferences ->
            val currentSettings = preferences[SETTINGS_KEY]?.let { 
                try {
                    json.decodeFromString<AppSettings>(it)
                } catch (e: Exception) {
                    AppSettings()
                }
            } ?: AppSettings()
            
            val updatedSettings = currentSettings.copy(notifications = notificationSettings)
            preferences[SETTINGS_KEY] = json.encodeToString(updatedSettings)
        }
    }
}
