package com.example.moderntodo.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.moderntodo.data.local.ToDoItem
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.*
import java.util.*

@EntryPoint
@InstallIn(SingletonComponent::class)
interface TaskReminderReceiverEntryPoint {
    fun notificationHelper(): NotificationHelper
}

class TaskReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val todoId = intent.getIntExtra("todo_id", -1)
        val todoDescription = intent.getStringExtra("todo_description") ?: ""

        if (todoId != -1) {
            // Get dependencies using Hilt entry point
            val entryPoint = EntryPointAccessors.fromApplication(
                context.applicationContext,
                TaskReminderReceiverEntryPoint::class.java
            )
            
            val notificationHelper = entryPoint.notificationHelper()
            
            // Create a ToDoItem for the notification
            val todoItem = ToDoItem(
                id = todoId,
                listId = 1, // Default list
                userId = 1L, // Default user
                description = todoDescription,
                order = 0,
                isCompleted = false
            )

            notificationHelper.showReminderNotification(todoItem)
        }
    }
}
