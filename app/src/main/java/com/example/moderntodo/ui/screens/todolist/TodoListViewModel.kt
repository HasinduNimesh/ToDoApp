package com.example.moderntodo.ui.screens.todolist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moderntodo.data.TodoItemRepository
import com.example.moderntodo.data.TodoListRepository
import com.example.moderntodo.data.local.Priority
import com.example.moderntodo.data.local.ToDoItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val todoListRepository: TodoListRepository,
    private val todoItemRepository: TodoItemRepository
) : ViewModel() {

    private val _currentListId = MutableStateFlow<Int?>(null)

    val listTitle: StateFlow<String> = _currentListId
        .filterNotNull()
        .flatMapLatest { listId ->
            flow {
                val list = todoListRepository.getListById(listId)
                emit(list?.title ?: "My Tasks")
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    val todoItems: Flow<List<ToDoItem>> = _currentListId
        .filterNotNull()
        .flatMapLatest { listId ->
            todoItemRepository.getItemsByListId(listId)
        }

    fun setCurrentListId(listId: Int) {
        _currentListId.value = listId
    }    fun addItem(
        description: String,
        reminderDateTime: LocalDateTime? = null,
        priority: Priority = Priority.NORMAL,
        category: String? = null
    ) {
        val listId = _currentListId.value ?: return
        
        viewModelScope.launch {
            val maxOrder = todoItemRepository.getMaxOrderByListId(listId)
            val newItem = ToDoItem(
                listId = listId,
                userId = 0L, // Will be set by repository with current user
                description = description,
                order = maxOrder + 1,
                reminderDateTimeTimestamp = reminderDateTime?.atZone(java.time.ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
                priority = priority,
                category = category
            )
            todoItemRepository.insertItem(newItem)
        }
    }

    fun updateItem(item: ToDoItem) {
        viewModelScope.launch {
            todoItemRepository.updateItem(item)
        }
    }

    fun toggleItemCompletion(item: ToDoItem) {
        viewModelScope.launch {
            todoItemRepository.updateItem(item.copy(isCompleted = !item.isCompleted))
        }
    }

    fun deleteItem(item: ToDoItem) {
        viewModelScope.launch {
            todoItemRepository.deleteItem(item)
        }
    }

    fun reorderItems(fromPosition: Int, toPosition: Int) {
        val listId = _currentListId.value ?: return
        viewModelScope.launch {
            todoItemRepository.reorderItems(listId, fromPosition, toPosition)
        }
    }
}
