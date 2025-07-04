package com.example.moderntodo.ui.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker

@Composable
fun NotificationPermissionHandler(
    onPermissionResult: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    var showRationale by remember { mutableStateOf(false) }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        onPermissionResult(isGranted)
        if (!isGranted) {
            showRationale = true
        }
    }

    // Check if notification permission is required (Android 13+)
    val needsPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    
    // Check current permission status
    val hasPermission = if (needsPermission) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PermissionChecker.PERMISSION_GRANTED
    } else {
        true // No permission needed for Android < 13
    }

    LaunchedEffect(needsPermission) {
        if (needsPermission && !hasPermission) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Show rationale dialog if permission was denied
    if (showRationale && needsPermission) {
        AlertDialog(
            onDismissRequest = { showRationale = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = {
                Text(
                    text = "Notification Permission",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "To receive reminders for your todos, please allow notifications in your device settings.",
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRationale = false
                        // Open app settings
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }
                ) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showRationale = false }
                ) {
                    Text("Maybe Later")
                }
            }
        )
    }
}

// Helper function to check notification permission
fun Context.hasNotificationPermission(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PermissionChecker.PERMISSION_GRANTED
    } else {
        true
    }
}
