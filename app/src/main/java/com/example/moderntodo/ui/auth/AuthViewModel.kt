package com.example.moderntodo.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moderntodo.data.model.User
import com.example.moderntodo.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Initial : AuthState()
    object FirstTimeSetup : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _allUsers = MutableStateFlow<List<User>>(emptyList())
    val allUsers: StateFlow<List<User>> = _allUsers.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentUser = authRepository.getCurrentUser()
                val users = authRepository.getAllUsers()
                _allUsers.value = users

                _authState.value = when {
                    currentUser != null -> AuthState.Authenticated(currentUser)
                    users.isEmpty() -> AuthState.FirstTimeSetup
                    else -> AuthState.Unauthenticated
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Failed to check authentication state: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(email: String, password: String, displayName: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _authState.value = AuthState.Error("Please enter a valid email address")
            return
        }

        if (password.length < 6) {
            _authState.value = AuthState.Error("Password must be at least 6 characters long")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = authRepository.register(email, password, displayName.ifBlank { email.substringBefore("@") })
                if (result.isSuccess) {
                    val user = result.getOrNull()
                    if (user != null) {
                        _authState.value = AuthState.Authenticated(user)
                    } else {
                        _authState.value = AuthState.Error("Registration failed")
                    }
                } else {
                    val errorMessage = result.exceptionOrNull()?.message ?: "Registration failed"
                    _authState.value = AuthState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Registration failed: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _authState.value = AuthState.Error("Please enter a valid email address")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = authRepository.login(email, password)
                if (result.isSuccess) {
                    val user = result.getOrNull()
                    if (user != null) {
                        _authState.value = AuthState.Authenticated(user)
                    } else {
                        _authState.value = AuthState.Error("Login failed")
                    }
                } else {
                    val errorMessage = result.exceptionOrNull()?.message ?: "Login failed"
                    _authState.value = AuthState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Login failed: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun switchUser(user: User) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                authRepository.switchUser(user)
                _authState.value = AuthState.Authenticated(user)
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Failed to switch user: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                authRepository.logout()
                val users = authRepository.getAllUsers()
                _allUsers.value = users
                _authState.value = if (users.isEmpty()) {
                    AuthState.FirstTimeSetup
                } else {
                    AuthState.Unauthenticated
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Logout failed: ${e.message}")
            }
        }
    }

    fun clearError() {
        if (_authState.value is AuthState.Error) {
            val users = _allUsers.value
            _authState.value = if (users.isEmpty()) {
                AuthState.FirstTimeSetup
            } else {
                AuthState.Unauthenticated
            }
        }
    }
}
