package com.example.moderntodo.ui.screens.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moderntodo.data.TodoListRepository
import com.example.moderntodo.data.local.ToDoList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListsViewModel @Inject constructor(
    private val todoListRepository: TodoListRepository
) : ViewModel() {

    val allLists: Flow<List<ToDoList>> = todoListRepository.getAllLists()
    
    fun addList(title: String) {
        viewModelScope.launch {
            todoListRepository.insertList(title)
        }
    }

    fun addList(list: ToDoList) {
        viewModelScope.launch {
            todoListRepository.insertList(list.title)
        }
    }

    fun updateList(list: ToDoList) {
        viewModelScope.launch {
            todoListRepository.updateList(list)
        }
    }

    fun deleteList(list: ToDoList) {
        viewModelScope.launch {
            todoListRepository.deleteList(list)
        }
    }
}
