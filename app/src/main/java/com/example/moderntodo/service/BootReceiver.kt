package com.example.moderntodo.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.moderntodo.data.TodoItemRepository
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.*

@EntryPoint
@InstallIn(SingletonComponent::class)
interface BootReceiverEntryPoint {
    fun todoRepository(): TodoItemRepository
    fun notificationService(): NotificationService
}

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            intent.action == Intent.ACTION_PACKAGE_REPLACED
        ) {
            // Get dependencies using Hilt entry point
            val entryPoint = EntryPointAccessors.fromApplication(
                context.applicationContext,
                BootReceiverEntryPoint::class.java
            )
            
            val todoRepository = entryPoint.todoRepository()
            val notificationService = entryPoint.notificationService()
            
            // Reschedule all pending notifications after device reboot
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val allTodos = todoRepository.getAllTodos()
                    allTodos.forEach { todo ->
                        if (!todo.isCompleted && todo.reminderDateTimeTimestamp != null) {
                            notificationService.scheduleNotification(todo)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
