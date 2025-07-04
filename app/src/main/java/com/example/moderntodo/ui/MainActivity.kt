package com.example.moderntodo.ui

import android.os.Bundle
import android.os.StrictMode
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.Keep
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.moderntodo.BuildConfig
import com.example.moderntodo.data.model.AppSettings
import com.example.moderntodo.ui.components.NotificationPermissionHandler
import com.example.moderntodo.ui.navigation.TodoNavHost
import com.example.moderntodo.ui.screens.settings.SettingsViewModel
import com.example.moderntodo.ui.theme.ModernTodoTheme
import dagger.hilt.android.AndroidEntryPoint

@Keep
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable StrictMode in debug builds to detect main thread violations
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .permitDiskReads() // Allow disk reads for Firebase initialization
                    .build()
            )
        }

        // Edge-to-edge layout
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val settings by settingsViewModel.settings.collectAsState(initial = AppSettings())
            
            ModernTodoTheme(
                themeMode = settings.theme
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    // Handle notification permission
                    NotificationPermissionHandler()
                    
                    // Use LaunchedEffect to avoid blocking the main thread during navigation setup
                    LaunchedEffect(Unit) {
                        // Any heavy initialization can go here
                    }
                    
                    TodoNavHost(navController = navController)
                }
            }
        }
    }
}