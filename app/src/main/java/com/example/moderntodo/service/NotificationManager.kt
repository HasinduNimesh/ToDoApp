package com.example.moderntodo.service

import com.example.moderntodo.data.TodoItemRepository
import com.example.moderntodo.data.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationManager @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val notificationService: NotificationService,
    private val todoItemRepository: TodoItemRepository
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        // Monitor notification settings changes
        scope.launch {
            settingsRepository.settingsFlow
                .distinctUntilChangedBy { it.notifications }
                .collect { settings ->
                    handleNotificationSettingsChange(settings.notifications)
                }
        }
    }

    private suspend fun handleNotificationSettingsChange(notificationSettings: com.example.moderntodo.data.model.NotificationSettings) {
        try {
            val allTodos = todoItemRepository.getAllTodos()
            
            if (!notificationSettings.enabled || !notificationSettings.reminderEnabled) {
                // Cancel all notifications if notifications are disabled
                allTodos.forEach { todo ->
                    if (todo.reminderDateTimeTimestamp != null && !todo.isCompleted) {
                        notificationService.cancelNotification(todo.id)
                    }
                }
            } else {
                // Reschedule all notifications if notifications are re-enabled
                allTodos.forEach { todo ->
                    if (todo.reminderDateTimeTimestamp != null && !todo.isCompleted) {
                        notificationService.scheduleNotification(todo)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
