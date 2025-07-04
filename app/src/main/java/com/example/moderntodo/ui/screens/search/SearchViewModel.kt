package com.example.moderntodo.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moderntodo.data.TodoItemRepository
import com.example.moderntodo.data.local.ToDoItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val todoItemRepository: TodoItemRepository
) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<ToDoItem>>(emptyList())
    val searchResults: StateFlow<List<ToDoItem>> = _searchResults

    fun searchItems(query: String) {
        viewModelScope.launch {
            todoItemRepository.searchItems(query)
                .collect { items ->
                    _searchResults.value = items
                }
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
}
