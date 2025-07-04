package com.example.moderntodo.data.repository

import com.example.moderntodo.data.TodoItemRepository
import com.example.moderntodo.data.TodoListRepository
import com.example.moderntodo.data.backup.BackupData
import com.example.moderntodo.data.backup.FirebaseBackupService
import com.example.moderntodo.data.local.ToDoItem
import com.example.moderntodo.data.local.ToDoList
import com.example.moderntodo.data.local.UserDao
import com.example.moderntodo.data.model.User
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupRepository @Inject constructor(
    private val firebaseBackupService: FirebaseBackupService,
    private val authRepository: AuthRepository,
    private val todoListRepository: TodoListRepository,
    private val todoItemRepository: TodoItemRepository,
    private val userDao: UserDao
) {
    
    suspend fun signInToFirebase(): Result<String> {
        firebaseBackupService.initialize()
        return firebaseBackupService.signInAnonymously()
    }
    
    suspend fun createBackup(): Result<String> {
        return try {
            firebaseBackupService.initialize()
            
            val currentUser = authRepository.getCurrentUser() 
                ?: throw Exception("No user logged in")
            
            // Get all user's data
            val todoLists = todoListRepository.getAllLists().first()
            val todoItems = todoItemRepository.getAllItemsForCurrentUser().first()
            
            firebaseBackupService.backupData(currentUser, todoLists, todoItems)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAvailableBackups(): Result<List<BackupData>> {
        firebaseBackupService.initialize()
        return firebaseBackupService.getBackups()
    }
    
    suspend fun restoreFromBackup(backupData: BackupData): Result<String> {
        return try {
            val currentUser = authRepository.getCurrentUser()
                ?: throw Exception("No user logged in")
            
            val backupUser = backupData.user ?: throw Exception("Invalid backup data: no user information")
            
            // Clear existing data for current user
            todoListRepository.deleteAllListsForUser()
            
            // Restore user data (update current user with backup info)
            val updatedUser = currentUser.copy(
                displayName = backupUser.displayName,
                lastLoginAt = System.currentTimeMillis()
            )
            userDao.updateUser(updatedUser)
            
            // Restore todo lists
            backupData.todoLists.forEach { list ->
                val newList = list.copy(userId = currentUser.id)
                todoListRepository.insertList(newList.title)
            }
            
            // Get the newly created lists to map old IDs to new IDs
            val newLists = todoListRepository.getAllLists().first()
            val listIdMapping = mutableMapOf<Int, Int>()
            
            backupData.todoLists.forEachIndexed { index, oldList ->
                if (index < newLists.size) {
                    listIdMapping[oldList.id] = newLists[index].id
                }
            }
            
            // Restore todo items with correct list IDs
            backupData.todoItems.forEach { item ->
                val newListId = listIdMapping[item.listId] ?: return@forEach
                val newItem = item.copy(
                    id = 0, // Reset ID for auto-generation
                    listId = newListId,
                    userId = currentUser.id
                )
                todoItemRepository.insertItem(newItem)
            }
            
            Result.success("Data restored successfully")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun checkFirebaseSignInStatus(): Boolean {
        firebaseBackupService.initialize()
        return firebaseBackupService.isSignedIn()
    }
    
    fun isSignedInToFirebase(): Boolean {
        return firebaseBackupService.isSignedIn()
    }
    
    fun signOutFromFirebase() {
        firebaseBackupService.signOut()
    }
    
    fun getFirebaseUserId(): String? {
        return firebaseBackupService.getCurrentUserId()
    }
}
