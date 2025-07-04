package com.example.moderntodo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.moderntodo.ui.auth.AuthScreen
import com.example.moderntodo.ui.auth.AuthState
import com.example.moderntodo.ui.auth.AuthViewModel
import com.example.moderntodo.ui.screens.home.HomeScreen
import com.example.moderntodo.ui.screens.lists.ListsScreen
import com.example.moderntodo.ui.screens.search.SearchScreen
import com.example.moderntodo.ui.screens.settings.SettingsScreen
import com.example.moderntodo.ui.screens.settings.NotificationSettingsScreen
import com.example.moderntodo.ui.screens.settings.ThemeSettingsScreen
import com.example.moderntodo.ui.screens.settings.PrivacyPolicyScreen
import com.example.moderntodo.ui.screens.settings.TermsOfServiceScreen
import com.example.moderntodo.ui.screens.settings.SettingsViewModel
import com.example.moderntodo.data.repository.SettingsRepository
import com.example.moderntodo.ui.screens.todolist.TodoListScreen

@Composable
fun TodoNavHost(navController: NavHostController) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsState()

    // Handle navigation based on auth state changes
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                // Navigate to home if currently on auth screen
                if (navController.currentDestination?.route == "auth") {
                    navController.navigate("home") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            }
            is AuthState.Unauthenticated, 
            is AuthState.FirstTimeSetup, 
            is AuthState.Error -> {
                // Navigate to auth screen if not already there
                if (navController.currentDestination?.route != "auth") {
                    navController.navigate("auth") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            else -> { /* Initial state, do nothing */ }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "auth" // Always start with auth to check authentication state
    ) {        // Authentication screen
        composable("auth") {
            AuthScreen(
                viewModel = authViewModel,
                onAuthenticated = {
                    navController.navigate("home") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }// Home screen
        composable("home") {
            // Only accessible if authenticated
            if (authState is AuthState.Authenticated) {
                HomeScreen(navController = navController, authViewModel = authViewModel)
            } else {
                // Redirect to auth if not authenticated
                navController.navigate("auth") {
                    popUpTo("home") { inclusive = true }
                }
            }
        }

        // Lists screen (showing all todo lists)
        composable("lists") {
            if (authState is AuthState.Authenticated) {
                ListsScreen(navController = navController, authViewModel = authViewModel)
            } else {
                navController.navigate("auth") {
                    popUpTo("lists") { inclusive = true }
                }
            }
        }

        // Individual todo list screen (showing items in a specific list)
        composable(
            route = "list/{listId}",
            arguments = listOf(
                navArgument("listId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            if (authState is AuthState.Authenticated) {
                val listId = backStackEntry.arguments?.getInt("listId") ?: 1
                TodoListScreen(
                    listId = listId,
                    navController = navController,
                    authViewModel = authViewModel
                )
            } else {
                navController.navigate("auth") {
                    popUpTo("list/{listId}") { inclusive = true }
                }
            }
        }

        // Search screen
        composable("search") {
            if (authState is AuthState.Authenticated) {
                SearchScreen(navController = navController, authViewModel = authViewModel)
            } else {
                navController.navigate("auth") {
                    popUpTo("search") { inclusive = true }
                }
            }
        }        // Settings screen
        composable("settings") {
            if (authState is AuthState.Authenticated) {
                SettingsScreen(navController = navController, authViewModel = authViewModel)
            } else {
                navController.navigate("auth") {
                    popUpTo("settings") { inclusive = true }
                }
            }
        }

        // Notification Settings screen
        composable("notification_settings") {
            if (authState is AuthState.Authenticated) {
                NotificationSettingsScreen(
                    navController = navController
                )
            } else {
                navController.navigate("auth") {
                    popUpTo("notification_settings") { inclusive = true }
                }
            }
        }

        // Theme Settings screen
        composable("theme_settings") {
            if (authState is AuthState.Authenticated) {
                ThemeSettingsScreen(navController = navController)
            } else {
                navController.navigate("auth") {
                    popUpTo("theme_settings") { inclusive = true }
                }
            }
        }

        // Backup screen
        composable("backup") {
            if (authState is AuthState.Authenticated) {
                com.example.moderntodo.ui.backup.BackupScreen()
            } else {
                navController.navigate("auth") {
                    popUpTo("backup") { inclusive = true }
                }
            }
        }        // Privacy Policy screen
        composable("privacy_policy") {
            if (authState is AuthState.Authenticated) {
                PrivacyPolicyScreen(
                    onBack = { navController.navigateUp() }
                )
            } else {
                navController.navigate("auth") {
                    popUpTo("privacy_policy") { inclusive = true }
                }
            }
        }

        // Terms of Service screen
        composable("terms_of_service") {
            if (authState is AuthState.Authenticated) {
                TermsOfServiceScreen(
                    onBack = { navController.navigateUp() }
                )
            } else {
                navController.navigate("auth") {
                    popUpTo("terms_of_service") { inclusive = true }
                }
            }
        }
    }
}
