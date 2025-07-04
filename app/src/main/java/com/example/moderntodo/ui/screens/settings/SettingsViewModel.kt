package com.example.moderntodo.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moderntodo.data.TodoItemRepository
import com.example.moderntodo.data.TodoListRepository
import com.example.moderntodo.data.backup.LocalBackupService
import com.example.moderntodo.data.local.UserDao
import com.example.moderntodo.data.model.ThemeMode
import com.example.moderntodo.data.repository.AuthRepository
import com.example.moderntodo.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    val settingsRepository: SettingsRepository,
    private val localBackupService: LocalBackupService,
    private val authRepository: AuthRepository,
    private val todoListRepository: TodoListRepository,
    private val todoItemRepository: TodoItemRepository,
    private val userDao: UserDao
) : ViewModel() {

    val settings = settingsRepository.settingsFlow

    private val _backupState = MutableStateFlow<BackupState>(BackupState.Initial)
    val backupState: StateFlow<BackupState> = _backupState.asStateFlow()

    private val _restoreState = MutableStateFlow<RestoreState>(RestoreState.Initial)
    val restoreState: StateFlow<RestoreState> = _restoreState.asStateFlow()

    fun createLocalBackup() {
        viewModelScope.launch {
            _backupState.value = BackupState.Loading
            
            try {
                // Get current user
                val currentUser = authRepository.getCurrentUser()
                
                // Get all user's data
                val todoLists = todoListRepository.getAllLists().first()
                val todoItems = todoItemRepository.getAllItemsForCurrentUser().first()
                
                // Create backup using LocalBackupService
                val result = localBackupService.createBackup(currentUser, todoLists, todoItems)
                
                if (result.isSuccess) {
                    _backupState.value = BackupState.Success
                } else {
                    _backupState.value = BackupState.Error(
                        result.exceptionOrNull()?.message ?: "Unknown error during backup"
                    )
                }
            } catch (e: Exception) {
                _backupState.value = BackupState.Error(
                    e.message ?: "Unknown error during backup"
                )
            }
        }
    }

    fun restoreFromLocalBackup() {
        viewModelScope.launch {
            _restoreState.value = RestoreState.Loading
            
            try {
                // Get available backups
                val backupsResult = localBackupService.getAvailableBackups()
                if (backupsResult.isFailure) {
                    _restoreState.value = RestoreState.Error(
                        backupsResult.exceptionOrNull()?.message ?: "Failed to find backups"
                    )
                    return@launch
                }
                
                val backups = backupsResult.getOrNull() ?: emptyList()
                if (backups.isEmpty()) {
                    _restoreState.value = RestoreState.Error("No backup files found")
                    return@launch
                }
                
                // Use the most recent backup
                val latestBackup = backups.first()
                restoreFromSpecificBackup(latestBackup.filePath)
            } catch (e: Exception) {
                _restoreState.value = RestoreState.Error(
                    e.message ?: "Unknown error during restore"
                )
            }
        }
    }
    
    fun restoreFromSpecificBackup(backupFilePath: String) {
        viewModelScope.launch {
            _restoreState.value = RestoreState.Loading
            
            try {
                val restoreResult = localBackupService.restoreFromBackup(backupFilePath)
                
                if (restoreResult.isFailure) {
                    _restoreState.value = RestoreState.Error(
                        restoreResult.exceptionOrNull()?.message ?: "Failed to restore backup"
                    )
                    return@launch
                }
                
                val backupData = restoreResult.getOrNull()!!
                
                // Get current user for restoration
                val currentUser = authRepository.getCurrentUser()
                if (currentUser == null) {
                    _restoreState.value = RestoreState.Error("No user logged in")
                    return@launch
                }
                
                // Clear existing data for current user
                todoListRepository.deleteAllListsForUser()
                
                // Restore user data if available
                backupData.user?.let { backupUser ->
                    val updatedUser = currentUser.copy(
                        displayName = backupUser.displayName,
                        lastLoginAt = System.currentTimeMillis()
                    )
                    userDao.updateUser(updatedUser)
                }
                
                // Restore todo lists
                val listIdMapping = mutableMapOf<Int, Int>()
                backupData.todoLists.forEach { list ->
                    val newListId = todoListRepository.insertList(list.title)
                    newListId?.let {
                        listIdMapping[list.id] = it.toInt()
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
                
                _restoreState.value = RestoreState.Success
            } catch (e: Exception) {
                _restoreState.value = RestoreState.Error(
                    e.message ?: "Unknown error during restore"
                )
            }
        }
    }

    fun updateTheme(theme: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.updateTheme(theme)
        }
    }

    // Reset states
    fun resetBackupState() {
        _backupState.value = BackupState.Initial
    }

    fun resetRestoreState() {
        _restoreState.value = RestoreState.Initial
    }
    
    suspend fun getAvailableBackups() = localBackupService.getAvailableBackups()
    
    fun getBackupDirectorySize() = localBackupService.getBackupDirectorySize()
}

sealed class BackupState {
    object Initial : BackupState()
    object Loading : BackupState()
    object Success : BackupState()
    data class Error(val message: String) : BackupState()
}

sealed class RestoreState {
    object Initial : RestoreState()
    object Loading : RestoreState()
    object Success : RestoreState()
    data class Error(val message: String) : RestoreState()
}
