// TaskDetailViewModel.kt
package com.example.moderntodo.ui.screens.taskdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moderntodo.data.TaskRepository
import com.example.moderntodo.domain.model.Task
import com.example.moderntodo.domain.usecase.DeleteTaskUseCase
import com.example.moderntodo.domain.usecase.UpdateTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // UI state for task details
    data class TaskDetailUiState(
        val task: Task? = null,
        val isLoading: Boolean = true,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(TaskDetailUiState())
    val uiState: StateFlow<TaskDetailUiState> = _uiState.asStateFlow()

    // Get task ID from navigation arguments
    private val taskId: String = checkNotNull(savedStateHandle["taskId"])

    init {
        loadTask()
    }

    private fun loadTask() {
        viewModelScope.launch {
            repository.getAllTasks().collectLatest { tasks ->
                val task = tasks.find { it.id == taskId }
                if (task != null) {
                    _uiState.value = TaskDetailUiState(task = task, isLoading = false)
                } else {
                    _uiState.value = TaskDetailUiState(
                        isLoading = false,
                        error = "Task not found"
                    )
                }
            }
        }
    }

    fun updateTask(title: String, dueDate: LocalDate?) {
        val currentTask = _uiState.value.task ?: return

        viewModelScope.launch {
            val updatedTask = currentTask.copy(
                title = title,
                dueDate = dueDate
            )
            updateTaskUseCase(updatedTask)
        }
    }

    fun toggleTaskCompletion() {
        val currentTask = _uiState.value.task ?: return

        viewModelScope.launch {
            updateTaskUseCase.toggleTaskCompletion(currentTask)
        }
    }

    fun deleteTask() {
        val currentTask = _uiState.value.task ?: return

        viewModelScope.launch {
            deleteTaskUseCase(currentTask)
        }
    }
}
