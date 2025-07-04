// AddTaskUseCase.kt
package com.example.moderntodo.domain.usecase

import com.example.moderntodo.data.TaskRepository
import com.example.moderntodo.domain.model.Task
import javax.inject.Inject

class AddTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task) {
        repository.addTask(task)
    }
}