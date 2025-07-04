package com.example.moderntodo.data

import com.example.moderntodo.data.local.TaskDao
import com.example.moderntodo.data.local.TaskEntity
import com.example.moderntodo.domain.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {
    // Get all tasks as a Flow
    fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { entities ->
            entities.map { it.toTask() }
        }
    }

    // Get active (not completed) tasks
    fun getActiveTasks(): Flow<List<Task>> {
        return taskDao.getActiveTasks().map { entities ->
            entities.map { it.toTask() }
        }
    }

    // Get completed tasks
    fun getCompletedTasks(): Flow<List<Task>> {
        return taskDao.getCompletedTasks().map { entities ->
            entities.map { it.toTask() }
        }
    }

    // Add a new task
    suspend fun addTask(task: Task) {
        taskDao.insertTask(TaskEntity.fromTask(task))
    }

    // Update existing task
    suspend fun updateTask(task: Task) {
        taskDao.updateTask(TaskEntity.fromTask(task))
    }

    // Delete a task
    suspend fun deleteTask(task: Task) {
        taskDao.deleteTaskById(task.id)
    }

    // Toggle task completion status
    suspend fun toggleTaskCompletion(task: Task) {
        updateTask(task.copy(isCompleted = !task.isCompleted))
    }
}