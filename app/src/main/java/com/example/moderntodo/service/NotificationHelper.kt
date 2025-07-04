package com.example.moderntodo.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.moderntodo.R
import com.example.moderntodo.data.local.ToDoItem
import com.example.moderntodo.ui.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_ID = "todo_reminders"
        const val CHANNEL_NAME = "Todo Reminders"
        const val CHANNEL_DESCRIPTION = "Notifications for todo item reminders"
        
        const val ACTION_COMPLETE = "ACTION_COMPLETE"
        const val ACTION_SNOOZE = "ACTION_SNOOZE"
        const val EXTRA_TODO_ID = "EXTRA_TODO_ID"
        
        const val SNOOZE_DURATION_MINUTES = 10
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                setShowBadge(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showReminderNotification(todoItem: ToDoItem) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("todo_id", todoItem.id)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            todoItem.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Complete action
        val completeIntent = Intent(context, TaskActionReceiver::class.java).apply {
            action = ACTION_COMPLETE
            putExtra(EXTRA_TODO_ID, todoItem.id)
        }
        val completePendingIntent = PendingIntent.getBroadcast(
            context,
            todoItem.id * 10 + 1,
            completeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Snooze action
        val snoozeIntent = Intent(context, TaskActionReceiver::class.java).apply {
            action = ACTION_SNOOZE
            putExtra(EXTRA_TODO_ID, todoItem.id)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            todoItem.id * 10 + 2,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Todo Reminder")
            .setContentText(todoItem.description)
            .setStyle(NotificationCompat.BigTextStyle().bigText(todoItem.description))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(
                R.drawable.ic_check,
                "Complete",
                completePendingIntent
            )
            .addAction(
                R.drawable.ic_snooze,
                "Snooze",
                snoozePendingIntent
            )
            .build()

        NotificationManagerCompat.from(context).notify(todoItem.id, notification)
    }

    fun cancelNotification(todoId: Int) {
        NotificationManagerCompat.from(context).cancel(todoId)
    }
}
