package com.example.moderntodo.data

import com.example.moderntodo.data.local.ToDoList
import com.example.moderntodo.data.local.ToDoListDao
import com.example.moderntodo.data.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoListRepository @Inject constructor(
    private val todoListDao: ToDoListDao,
    private val authRepository: AuthRepository
) {
    fun getAllLists(): Flow<List<ToDoList>> {
        return getAllListsForCurrentUser()
    }

    fun getAllListsForCurrentUser(): Flow<List<ToDoList>> {
        val userId = authRepository.getCurrentUserId()
        return if (userId != null) {
            todoListDao.getAllListsForUser(userId)
        } else {
            emptyFlow()
        }
    }

    suspend fun getListById(listId: Int): ToDoList? {
        val userId = authRepository.getCurrentUserId() ?: return null
        return todoListDao.getListById(listId, userId)
    }

    suspend fun insertList(title: String): Long? {
        val userId = authRepository.getCurrentUserId() ?: return null
        val list = ToDoList(userId = userId, title = title)
        return todoListDao.insertList(list)
    }

    suspend fun updateList(list: ToDoList) {
        val userId = authRepository.getCurrentUserId() ?: return
        if (list.userId == userId) {
            todoListDao.updateList(list)
        }
    }

    suspend fun deleteList(list: ToDoList) {
        val userId = authRepository.getCurrentUserId() ?: return
        if (list.userId == userId) {
            todoListDao.deleteList(list)
        }
    }

    suspend fun deleteListById(listId: Int) {
        val userId = authRepository.getCurrentUserId() ?: return
        todoListDao.deleteListById(listId, userId)
    }
      suspend fun getListCountForCurrentUser(): Int {
        val userId = authRepository.getCurrentUserId() ?: return 0
        return todoListDao.getListCountForUser(userId)
    }
    
    suspend fun deleteAllListsForUser() {
        val userId = authRepository.getCurrentUserId() ?: return
        todoListDao.deleteAllListsForUser(userId)
    }
}
