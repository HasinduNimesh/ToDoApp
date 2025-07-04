package com.example.moderntodo.data

import com.example.moderntodo.data.local.ToDoItem
import com.example.moderntodo.data.local.ToDoItemDao
import com.example.moderntodo.data.repository.AuthRepository
import com.example.moderntodo.data.repository.SettingsRepository
import com.example.moderntodo.service.NotificationService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoItemRepository @Inject constructor(
    private val todoItemDao: ToDoItemDao,
    private val authRepository: AuthRepository,
    private val notificationService: NotificationService,
    private val settingsRepository: SettingsRepository
) {
    fun getItemsByListId(listId: Int): Flow<List<ToDoItem>> {
        return authRepository.currentUserFlow.flatMapLatest { currentUser ->
            if (currentUser != null) {
                todoItemDao.getItemsByListId(listId, currentUser.id)
            } else {
                emptyFlow()
            }
        }
    }

    fun searchItems(query: String): Flow<List<ToDoItem>> {
        return authRepository.currentUserFlow.flatMapLatest { currentUser ->
            if (currentUser != null) {
                todoItemDao.searchItems(query, currentUser.id)
            } else {
                emptyFlow()
            }
        }
    }

    fun getAllItemsForCurrentUser(): Flow<List<ToDoItem>> {
        return authRepository.currentUserFlow.flatMapLatest { currentUser ->
            if (currentUser != null) {
                todoItemDao.getAllItemsForUser(currentUser.id)
            } else {
                emptyFlow()
            }
        }
    }

    suspend fun insertItem(item: ToDoItem): Long {
        val currentUser = authRepository.getCurrentUser()
        return if (currentUser != null) {
            val itemWithUser = item.copy(userId = currentUser.id)
            val result = todoItemDao.insertItem(itemWithUser)
            
            // Schedule notification if reminder is set and notifications are enabled
            if (itemWithUser.reminderDateTimeTimestamp != null && !itemWithUser.isCompleted) {
                val settings = settingsRepository.settingsFlow.first()
                if (settings.notifications.enabled && settings.notifications.reminderEnabled) {
                    notificationService.scheduleNotification(itemWithUser)
                }
            }
            
            result
        } else {
            throw IllegalStateException("No authenticated user")
        }
    }

    suspend fun updateItem(item: ToDoItem) {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null && item.userId == currentUser.id) {
            todoItemDao.updateItem(item)
            
            // Handle notification updates
            val settings = settingsRepository.settingsFlow.first()
            if (item.isCompleted || item.reminderDateTimeTimestamp == null || 
                !settings.notifications.enabled || !settings.notifications.reminderEnabled) {
                // Cancel notification if task is completed, reminder is removed, or notifications are disabled
                notificationService.cancelNotification(item.id)
            } else {
                // Reschedule notification with new reminder time
                notificationService.rescheduleNotification(item)
            }
        } else {
            throw IllegalStateException("Unauthorized or no authenticated user")
        }
    }

    suspend fun deleteItem(item: ToDoItem) {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null && item.userId == currentUser.id) {
            todoItemDao.deleteItem(item)
            // Cancel any pending notification for this item
            notificationService.cancelNotification(item.id)
        } else {
            throw IllegalStateException("Unauthorized or no authenticated user")
        }
    }

    suspend fun deleteItemById(itemId: Int) {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
            todoItemDao.deleteItemById(itemId, currentUser.id)
            // Cancel any pending notification for this item
            notificationService.cancelNotification(itemId)
        } else {
            throw IllegalStateException("No authenticated user")
        }
    }

    suspend fun getMaxOrderByListId(listId: Int): Int {
        val currentUser = authRepository.getCurrentUser()
        return if (currentUser != null) {
            todoItemDao.getMaxOrderByListId(listId, currentUser.id) ?: 0
        } else {
            0
        }
    }

    suspend fun reorderItems(listId: Int, fromPosition: Int, toPosition: Int) {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
            todoItemDao.reorderItems(listId, currentUser.id, fromPosition, toPosition)
        } else {
            throw IllegalStateException("No authenticated user")
        }
    }

    // Additional methods for notification system
    suspend fun getTodoById(todoId: Int): ToDoItem? {
        return todoItemDao.getTodoById(todoId)
    }

    suspend fun getAllTodos(): List<ToDoItem> {
        return todoItemDao.getAllTodos()
    }

    suspend fun getAllPendingTodos(): List<ToDoItem> {
        val currentTime = System.currentTimeMillis()
        return todoItemDao.getAllPendingTodos(currentTime)
    }

    suspend fun updateTodo(todo: ToDoItem) {
        todoItemDao.updateItem(todo)
        
        // Handle notification updates for direct updates
        val settings = settingsRepository.settingsFlow.first()
        if (todo.isCompleted || todo.reminderDateTimeTimestamp == null || 
            !settings.notifications.enabled || !settings.notifications.reminderEnabled) {
            notificationService.cancelNotification(todo.id)
        } else {
            notificationService.rescheduleNotification(todo)
        }
    }
}
