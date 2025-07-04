package com.example.moderntodo.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.moderntodo.data.TodoItemRepository
import com.example.moderntodo.service.NotificationHelper.Companion.ACTION_COMPLETE
import com.example.moderntodo.service.NotificationHelper.Companion.ACTION_SNOOZE
import com.example.moderntodo.service.NotificationHelper.Companion.EXTRA_TODO_ID
import com.example.moderntodo.service.NotificationHelper.Companion.SNOOZE_DURATION_MINUTES
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.*
import java.util.*

@EntryPoint
@InstallIn(SingletonComponent::class)
interface TaskActionReceiverEntryPoint {
    fun todoRepository(): TodoItemRepository
    fun notificationService(): NotificationService
    fun notificationHelper(): NotificationHelper
}

class TaskActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val todoId = intent.getIntExtra(EXTRA_TODO_ID, -1)
        if (todoId == -1) return

        // Get dependencies using Hilt entry point
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            TaskActionReceiverEntryPoint::class.java
        )
        
        val todoRepository = entryPoint.todoRepository()
        val notificationService = entryPoint.notificationService()
        val notificationHelper = entryPoint.notificationHelper()

        when (intent.action) {
            ACTION_COMPLETE -> {
                // Mark task as completed
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val todoItem = todoRepository.getTodoById(todoId)
                        if (todoItem != null) {
                            todoRepository.updateTodo(todoItem.copy(isCompleted = true))
                            // Cancel any future notifications for this task
                            notificationService.cancelNotification(todoId)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                // Cancel the notification
                notificationHelper.cancelNotification(todoId)
            }
            
            ACTION_SNOOZE -> {
                // Reschedule notification for later
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val todoItem = todoRepository.getTodoById(todoId)
                        if (todoItem != null) {
                            // Create a new reminder date that's SNOOZE_DURATION_MINUTES from now
                            val newReminderDate = java.time.LocalDateTime.now().plusMinutes(SNOOZE_DURATION_MINUTES.toLong())
                            val newReminderTimestamp = newReminderDate.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                            
                            val updatedTodo = todoItem.copy(reminderDateTimeTimestamp = newReminderTimestamp)
                            todoRepository.updateTodo(updatedTodo)
                            notificationService.scheduleNotification(updatedTodo)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                // Cancel the current notification
                notificationHelper.cancelNotification(todoId)
            }
        }
    }
}
