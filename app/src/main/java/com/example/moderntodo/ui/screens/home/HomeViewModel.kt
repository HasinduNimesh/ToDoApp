package com.example.moderntodo.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moderntodo.data.TaskRepository
import com.example.moderntodo.domain.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    // Filter state
    enum class TaskFilter { ALL, ACTIVE, COMPLETED }

    private val _currentFilter = MutableStateFlow(TaskFilter.ALL)

    // Task states from repository
    private val _allTasks = taskRepository.getAllTasks()
    private val _activeTasks = taskRepository.getActiveTasks()
    private val _completedTasks = taskRepository.getCompletedTasks()

    // UI state for tasks
    data class HomeUiState(
        val tasks: List<Task> = emptyList(),
        val isLoading: Boolean = false,
        val currentFilter: TaskFilter = TaskFilter.ALL
    )

    // Combined state flow for UI
    val uiState: StateFlow<HomeUiState> = combine(
        _currentFilter,
        _allTasks,
        _activeTasks,
        _completedTasks
    ) { filter, allTasks, activeTasks, completedTasks ->
        val tasks = when (filter) {
            TaskFilter.ALL -> allTasks
            TaskFilter.ACTIVE -> activeTasks
            TaskFilter.COMPLETED -> completedTasks
        }
        HomeUiState(
            tasks = tasks,
            isLoading = false,
            currentFilter = filter
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    // Change the current filter
    fun setFilter(filter: TaskFilter) {
        _currentFilter.value = filter
    }

    // Add a new task
    fun addTask(title: String, dueDate: LocalDate? = null) {
        if (title.isBlank()) return

        viewModelScope.launch {
            val newTask = Task(
                title = title,
                dueDate = dueDate
            )
            taskRepository.addTask(newTask)
        }
    }

    // Toggle task completion status
    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            taskRepository.toggleTaskCompletion(task)
        }
    }

    // Delete a task
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }

    // Edit a task
    fun updateTask(task: Task, newTitle: String, newDueDate: LocalDate?) {
        if (newTitle.isBlank()) return

        viewModelScope.launch {
            val updatedTask = task.copy(
                title = newTitle,
                dueDate = newDueDate
            )
            taskRepository.updateTask(updatedTask)
        }
    }
}