package com.example.moderntodo.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val theme: ThemeMode = ThemeMode.SYSTEM,
    val notifications: NotificationSettings = NotificationSettings(),
    val general: GeneralSettings = GeneralSettings()
)

@Serializable
enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

@Serializable
data class NotificationSettings(
    val enabled: Boolean = true,
    val reminderEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val dailySummary: Boolean = false,
    val reminderMinutes: Int = 15 // minutes before due time
)

@Serializable
data class GeneralSettings(
    val autoBackup: Boolean = false,
    val showCompletedTasks: Boolean = true,
    val defaultDueDateEnabled: Boolean = false
)
