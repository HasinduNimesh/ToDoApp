// UpdateTaskUseCase.kt
package com.example.moderntodo.domain.usecase

import com.example.moderntodo.data.TaskRepository
import com.example.moderntodo.domain.model.Task
import javax.inject.Inject

class UpdateTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task) {
        repository.updateTask(task)
    }

    suspend fun toggleTaskCompletion(task: Task) {
        repository.toggleTaskCompletion(task)
    }
}