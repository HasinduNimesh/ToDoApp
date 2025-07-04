package com.example.moderntodo.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.moderntodo.data.local.ToDoItem
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleNotification(todoItem: ToDoItem) {
        val reminderTimestamp = todoItem.reminderDateTimeTimestamp ?: return
        
        // Don't schedule if the notification time is in the past
        if (reminderTimestamp <= System.currentTimeMillis()) {
            return
        }

        val intent = Intent(context, TaskReminderReceiver::class.java).apply {
            putExtra("todo_id", todoItem.id)
            putExtra("todo_description", todoItem.description)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            todoItem.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    reminderTimestamp,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    reminderTimestamp,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            // Handle the case where exact alarms are not allowed
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                reminderTimestamp,
                pendingIntent
            )
        }
    }

    fun cancelNotification(todoId: Int) {
        val intent = Intent(context, TaskReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            todoId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    fun rescheduleNotification(todoItem: ToDoItem) {
        cancelNotification(todoItem.id)
        scheduleNotification(todoItem)
    }
}
