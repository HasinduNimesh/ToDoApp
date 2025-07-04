package com.example.moderntodo.ui.backup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moderntodo.data.backup.BackupData
import com.example.moderntodo.data.repository.BackupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BackupUiState(
    val isLoading: Boolean = false,
    val isSignedIn: Boolean = false,
    val availableBackups: List<BackupData> = emptyList(),
    val message: String? = null,
    val error: String? = null
)

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val backupRepository: BackupRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(BackupUiState())
    val uiState: StateFlow<BackupUiState> = _uiState.asStateFlow()
    
    init {
        initializeAndCheckSignInStatus()
    }
    
    private fun initializeAndCheckSignInStatus() {
        viewModelScope.launch {
            val isSignedIn = backupRepository.checkFirebaseSignInStatus()
            _uiState.value = _uiState.value.copy(isSignedIn = isSignedIn)
            
            if (isSignedIn) {
                loadAvailableBackups()
            }
        }
    }
    
    fun signInToFirebase() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            backupRepository.signInToFirebase()
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSignedIn = true,
                        message = "Signed in successfully"
                    )
                    loadAvailableBackups()
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to sign in: ${error.message}"
                    )
                }
        }
    }
    
    fun createBackup() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            backupRepository.createBackup()
                .onSuccess { message ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        message = message
                    )
                    loadAvailableBackups() // Refresh the list
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Backup failed: ${error.message}"
                    )
                }
        }
    }
    
    fun loadAvailableBackups() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            backupRepository.getAvailableBackups()
                .onSuccess { backups ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        availableBackups = backups
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to load backups: ${error.message}"
                    )
                }
        }
    }
    
    fun restoreFromBackup(backupData: BackupData) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            backupRepository.restoreFromBackup(backupData)
                .onSuccess { message ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        message = message
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Restore failed: ${error.message}"
                    )
                }
        }
    }
    
    fun signOut() {
        backupRepository.signOutFromFirebase()
        _uiState.value = _uiState.value.copy(
            isSignedIn = false,
            availableBackups = emptyList(),
            message = "Signed out successfully"
        )
    }
    
    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
