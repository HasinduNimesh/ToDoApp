// GetTasksUseCase.kt
package com.example.moderntodo.domain.usecase

import com.example.moderntodo.data.TaskRepository
import com.example.moderntodo.domain.model.Task
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    fun getAllTasks(): Flow<List<Task>> = repository.getAllTasks()

    fun getActiveTasks(): Flow<List<Task>> = repository.getActiveTasks()

    fun getCompletedTasks(): Flow<List<Task>> = repository.getCompletedTasks()
}