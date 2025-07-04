package com.example.moderntodo.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moderntodo.data.model.AppSettings
import com.example.moderntodo.data.model.NotificationSettings
import com.example.moderntodo.data.repository.SettingsRepository
import com.example.moderntodo.service.NotificationService
import com.example.moderntodo.data.TodoItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val notificationService: NotificationService,
    private val todoItemRepository: TodoItemRepository
) : ViewModel() {

    val settings: StateFlow<AppSettings> = settingsRepository.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings()
        )

    fun updateNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val currentSettings = settings.value
            val updatedNotificationSettings = currentSettings.notifications.copy(enabled = enabled)
            settingsRepository.updateNotificationSettings(updatedNotificationSettings)
            
            // If notifications are disabled, cancel all pending notifications
            if (!enabled) {
                cancelAllNotifications()
            } else {
                // If notifications are re-enabled, reschedule all pending notifications
                rescheduleAllNotifications()
            }
        }
    }

    fun updateReminderEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val currentSettings = settings.value
            val updatedNotificationSettings = currentSettings.notifications.copy(reminderEnabled = enabled)
            settingsRepository.updateNotificationSettings(updatedNotificationSettings)
            
            if (!enabled) {
                cancelAllNotifications()
            } else if (currentSettings.notifications.enabled) {
                rescheduleAllNotifications()
            }
        }
    }

    fun updateSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val currentSettings = settings.value
            val updatedNotificationSettings = currentSettings.notifications.copy(soundEnabled = enabled)
            settingsRepository.updateNotificationSettings(updatedNotificationSettings)
        }
    }

    fun updateVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val currentSettings = settings.value
            val updatedNotificationSettings = currentSettings.notifications.copy(vibrationEnabled = enabled)
            settingsRepository.updateNotificationSettings(updatedNotificationSettings)
        }
    }

    fun updateDailySummary(enabled: Boolean) {
        viewModelScope.launch {
            val currentSettings = settings.value
            val updatedNotificationSettings = currentSettings.notifications.copy(dailySummary = enabled)
            settingsRepository.updateNotificationSettings(updatedNotificationSettings)
        }
    }

    fun updateReminderMinutes(minutes: Int) {
        viewModelScope.launch {
            val currentSettings = settings.value
            val updatedNotificationSettings = currentSettings.notifications.copy(reminderMinutes = minutes)
            settingsRepository.updateNotificationSettings(updatedNotificationSettings)
        }
    }

    private suspend fun cancelAllNotifications() {
        try {
            val allTodos = todoItemRepository.getAllTodos()
            allTodos.forEach { todo ->
                if (todo.reminderDateTimeTimestamp != null && !todo.isCompleted) {
                    notificationService.cancelNotification(todo.id)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun rescheduleAllNotifications() {
        try {
            val allTodos = todoItemRepository.getAllTodos()
            allTodos.forEach { todo ->
                if (todo.reminderDateTimeTimestamp != null && !todo.isCompleted) {
                    notificationService.scheduleNotification(todo)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
