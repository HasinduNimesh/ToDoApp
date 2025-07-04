package com.example.moderntodo.ui.screens.settings

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moderntodo.ui.auth.AuthViewModel
import com.example.moderntodo.ui.theme.GradientEnd
import com.example.moderntodo.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {    val context = LocalContext.current
    var showBackupConfirmation by remember { mutableStateOf(false) }
    var showRestoreConfirmation by remember { mutableStateOf(false) }
    
    // Version animation states
    var versionClickCount by remember { mutableStateOf(0) }
    var showVersionAnimation by remember { mutableStateOf(false) }
    var animationPhase by remember { mutableStateOf(0) }

    val backupState by settingsViewModel.backupState.collectAsState()
    val restoreState by settingsViewModel.restoreState.collectAsState()

    // Floating animations
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val floatingOffsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating"
    )

    val floatingOffsetX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        GradientStart,
                        GradientEnd
                    )
                )
            )
    ) {
        // Animated background elements
        repeat(3) { index ->
            val animatedScale by infiniteTransition.animateFloat(
                initialValue = 0.8f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 3000 + index * 500,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "scale$index"
            )

            Box(
                modifier = Modifier
                    .offset(
                        x = (50 + index * 120).dp + floatingOffsetX.dp,
                        y = (100 + index * 200).dp + floatingOffsetY.dp
                    )
                    .size((60 + index * 20).dp)
                    .scale(animatedScale)
                    .clip(CircleShape)                    .background(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.1f)
                    )
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 24.dp)
        ) {
            item {
                // Header with animated title
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f),
                                            Color.Transparent
                                        )
                                    )
                                )
                                .padding(8.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                          Text(
                            text = "Settings",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        
                        Text(
                            text = "Customize your todo experience",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Data Management Section
            item {
                SettingsSection(
                    title = "Data Management",
                    icon = Icons.Default.Storage
                ) {
                    SettingsCard(
                        title = "Local Backup",
                        subtitle = "Backup your todos to local storage",
                        icon = Icons.Default.Backup,
                        onClick = { 
                            showBackupConfirmation = true
                        },
                        isLoading = backupState == BackupState.Loading
                    )
                    
                    SettingsCard(
                        title = "Restore Data",
                        subtitle = "Restore todos from local backup",
                        icon = Icons.Default.Restore,
                        onClick = { 
                            showRestoreConfirmation = true
                        },
                        isLoading = restoreState == RestoreState.Loading
                    )
                }
            }

            // App Preferences Section
            item {
                SettingsSection(
                    title = "Preferences",
                    icon = Icons.Default.Tune
                ) {                    SettingsCard(
                        title = "Notifications",
                        subtitle = "Manage notification settings",
                        icon = Icons.Default.Notifications,
                        onClick = { 
                            navController.navigate("notification_settings")
                        }
                    )
                    
                    SettingsCard(
                        title = "Theme",
                        subtitle = "Choose your app theme",
                        icon = Icons.Default.Palette,
                        onClick = { 
                            navController.navigate("theme_settings")
                        }
                    )
                }
            }

            // Data Section
            item {
                SettingsSection(
                    title = "Data",
                    icon = Icons.Default.Cloud
                ) {
                    SettingsCard(
                        title = "Backup & Restore",
                        subtitle = "Backup your data to Firebase or restore from previous backups",
                        icon = Icons.Default.CloudUpload,
                        onClick = { 
                            navController.navigate("backup")
                        }
                    )
                }
            }

            // Account Section
            item {
                SettingsSection(
                    title = "Account",
                    icon = Icons.Default.Person
                ) {
                    SettingsCard(
                        title = "Switch User",
                        subtitle = "Change to another user account",
                        icon = Icons.Default.SwitchAccount,
                        onClick = { 
                            authViewModel.logout()
                            navController.navigate("auth") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                    
                    SettingsCard(
                        title = "Sign Out",
                        subtitle = "Sign out of your account",
                        icon = Icons.Default.ExitToApp,
                        onClick = { 
                            authViewModel.logout()
                            navController.navigate("auth") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }

            // About Section
            item {
                SettingsSection(
                    title = "About",
                    icon = Icons.Default.Info
                ) {                    SettingsCard(
                        title = "App Version",
                        subtitle = if (versionClickCount >= 7) "🎉 Developer Mode!" else "1.0.0",
                        icon = Icons.Default.AppRegistration,
                        onClick = { 
                            versionClickCount++
                            when (versionClickCount) {
                                3 -> {
                                    Toast.makeText(context, "Keep tapping...", Toast.LENGTH_SHORT).show()
                                }
                                5 -> {
                                    Toast.makeText(context, "Almost there!", Toast.LENGTH_SHORT).show()
                                }
                                7 -> {
                                    showVersionAnimation = true
                                    animationPhase = 1
                                    Toast.makeText(context, "🎉 You found the Easter egg!", Toast.LENGTH_LONG).show()
                                }
                                10 -> {
                                    animationPhase = 2
                                    Toast.makeText(context, "🚀 Super Secret Mode!", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    )
                    
                    SettingsCard(
                        title = "Privacy Policy",
                        subtitle = "Read our privacy policy",
                        icon = Icons.Default.PrivacyTip,
                        onClick = { 
                            navController.navigate("privacy_policy")
                        }
                    )
                    
                    SettingsCard(
                        title = "Terms of Service",
                        subtitle = "Read our terms of service",
                        icon = Icons.Default.Assignment,
                        onClick = {
                            navController.navigate("terms_of_service")
                        }
                    )
                }
            }
        }
    }

    // Backup Confirmation Dialog
    if (showBackupConfirmation) {
        ConfirmationDialog(
            title = "Create Local Backup",
            message = "This will create a backup of all your todos in local storage. Continue?",
            onConfirm = {
                settingsViewModel.createLocalBackup()
                showBackupConfirmation = false
            },
            onDismiss = {
                showBackupConfirmation = false
            }
        )
    }

    // Restore Confirmation Dialog
    if (showRestoreConfirmation) {
        ConfirmationDialog(
            title = "Restore from Local Backup",
            message = "This will replace all current todos with data from local backup. This action cannot be undone. Continue?",
            onConfirm = {
                settingsViewModel.restoreFromLocalBackup()
                showRestoreConfirmation = false
            },
            onDismiss = {
                showRestoreConfirmation = false
            }
        )
    }    // Handle backup/restore state changes
    LaunchedEffect(backupState) {
        when (val state = backupState) {
            is BackupState.Success -> {
                Toast.makeText(context, "Local backup created successfully", Toast.LENGTH_SHORT).show()
            }
            is BackupState.Error -> {
                Toast.makeText(context, "Backup failed: ${state.message}", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    LaunchedEffect(restoreState) {        when (val state = restoreState) {
            is RestoreState.Success -> {
                Toast.makeText(context, "Data restored successfully", Toast.LENGTH_SHORT).show()
            }
            is RestoreState.Error -> {
                Toast.makeText(context, "Restore failed: ${state.message}", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }
    
    // Version Animation
    if (showVersionAnimation) {
        VersionEasterEggAnimation(
            animationPhase = animationPhase,
            onDismiss = { 
                showVersionAnimation = false 
                animationPhase = 0
            }
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            content()
        }
    }
}

@Composable
fun SettingsCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    isLoading: Boolean = false
) {
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isLoading) 0.6f else 1f,
        animationSpec = tween(300),
        label = "alpha"
    )

    Card(
        onClick = if (!isLoading) onClick else { {} },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .alpha(animatedAlpha),
        shape = RoundedCornerShape(12.dp),        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.15f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {            Box(
                modifier = Modifier
                    .size(40.dp)                    .background(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onSurface,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
              if (!isLoading) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(onClick = onConfirm) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

@Composable
fun VersionEasterEggAnimation(
    animationPhase: Int,
    onDismiss: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "todo_productivity_animation")
    
    // Todo-themed animations
    val taskCompletionProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "task_completion"
    )
    
    // Productivity meter animation
    val productivityLevel by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "productivity"
    )
    
    // Color cycling through productivity-themed colors
    val colorPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "color_cycle"
    )
    
    // Entry animation
    val entryScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "entry"
    )
    
    // Todo-themed gradient colors (productivity colors)
    val productivityColors = listOf(
        Color(0xFF4CAF50), // Success Green
        Color(0xFF2196F3), // Focus Blue
        Color(0xFF9C27B0), // Creative Purple
        Color(0xFFFF9800), // Energy Orange
        Color(0xFF607D8B), // Professional Gray-Blue
        Color(0xFF00BCD4)  // Achievement Cyan
    )
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF1A1A2E).copy(alpha = 0.95f),
                            Color(0xFF16213E).copy(alpha = 0.98f)
                        ),
                        radius = 1000f
                    )
                )
                .scale(entryScale),
            contentAlignment = Alignment.Center
        ) {
            // Floating task items (checkboxes) animation
            repeat(8) { index ->
                val taskDelay = index * 0.3f
                val taskProgress = ((taskCompletionProgress + taskDelay) % 1f)
                val isCompleted = taskProgress > 0.7f
                
                val taskX by infiniteTransition.animateFloat(
                    initialValue = -200f + index * 50f,
                    targetValue = 200f - index * 50f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 8000 + index * 500,
                            easing = LinearEasing
                        ),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "taskX$index"
                )
                
                val taskY by infiniteTransition.animateFloat(
                    initialValue = -150f + index * 30f,
                    targetValue = 150f - index * 30f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 6000 + index * 200,
                            easing = FastOutSlowInEasing
                        ),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "taskY$index"
                )
                
                // Animated task item
                Row(
                    modifier = Modifier
                        .offset(taskX.dp, taskY.dp)
                        .alpha(0.3f + taskProgress * 0.4f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Checkbox animation
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(
                                color = if (isCompleted) 
                                    productivityColors[index % productivityColors.size] 
                                else 
                                    Color.White.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(3.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = Color.White
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Task text line
                    Box(
                        modifier = Modifier
                            .width((40 + index * 15).dp)
                            .height(2.dp)
                            .background(
                                color = Color.White.copy(
                                    alpha = if (isCompleted) 0.3f else 0.6f
                                ),
                                shape = RoundedCornerShape(1.dp)
                            )
                    )
                }
            }
            
            // Productivity progress bars floating around
            repeat(6) { barIndex ->
                val barRotation by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 15000 + barIndex * 2000,
                            easing = LinearEasing
                        ),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "barRotation$barIndex"
                )
                
                val radius = 180f + barIndex * 25f
                val x = radius * kotlin.math.cos(Math.toRadians(barRotation.toDouble())).toFloat()
                val y = radius * kotlin.math.sin(Math.toRadians(barRotation.toDouble())).toFloat()
                
                Box(
                    modifier = Modifier
                        .offset(x.dp, y.dp)
                        .size(width = 60.dp, height = 8.dp)
                        .alpha(0.4f)
                ) {
                    // Background bar
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = Color.White.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                    // Progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(productivityLevel)
                            .background(
                                color = productivityColors[barIndex % productivityColors.size].copy(alpha = 0.8f),
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }
            }
            
            // Main content area
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Central productivity dashboard
                Card(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(1f + kotlin.math.sin(productivityLevel * kotlin.math.PI.toFloat()) * 0.05f),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        productivityColors[(colorPhase * productivityColors.size).toInt() % productivityColors.size].copy(alpha = 0.3f),
                                        Color.Transparent
                                    ),
                                    radius = 100f
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Main icon based on animation phase
                            Icon(
                                imageVector = when (animationPhase) {
                                    1 -> Icons.Default.CheckCircle
                                    2 -> Icons.Default.Star
                                    else -> Icons.Default.TrendingUp
                                },
                                contentDescription = null,
                                modifier = Modifier
                                    .size(48.dp)
                                    .graphicsLayer(
                                        rotationZ = taskCompletionProgress * 360f,
                                        scaleX = 1f + productivityLevel * 0.2f,
                                        scaleY = 1f + productivityLevel * 0.2f
                                    ),
                                tint = productivityColors[(colorPhase * productivityColors.size).toInt() % productivityColors.size]
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Productivity percentage
                            Text(
                                text = "${(productivityLevel * 100).toInt()}%",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))
                
                // Information card with glass morphism effect
                Card(
                    modifier = Modifier.padding(horizontal = 32.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.05f),
                                        Color.White.copy(alpha = 0.15f)
                                    )
                                )
                            )
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = when (animationPhase) {
                                1 -> "🚀 Productivity Boost!"
                                2 -> "🎯 Master Achiever!"
                                else -> "✨ Hidden Feature!"
                            },
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = when (animationPhase) {
                                1 -> "You've unlocked advanced productivity features! Your task management just got supercharged."
                                2 -> "Maximum efficiency achieved! You're now a certified ModernTodo power user."
                                else -> "Keep exploring to discover more hidden productivity gems!"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        // Action button
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(44.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = productivityColors[(colorPhase * productivityColors.size).toInt() % productivityColors.size].copy(alpha = 0.8f)
                            ),
                            shape = RoundedCornerShape(22.dp)
                        ) {
                            Text(
                                text = "Get Productive!",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
