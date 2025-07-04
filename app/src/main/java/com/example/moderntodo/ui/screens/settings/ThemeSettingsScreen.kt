package com.example.moderntodo.ui.screens.settings

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moderntodo.data.model.AppSettings
import com.example.moderntodo.data.model.ThemeMode
import com.example.moderntodo.ui.theme.GradientEnd
import com.example.moderntodo.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val currentSettings by settingsViewModel.settings.collectAsState(initial = AppSettings())
    
    // Floating animations for background elements
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val floatingOffsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating"
    )

    Box(
        modifier = Modifier
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
        repeat(2) { index ->
            val animatedScale by infiniteTransition.animateFloat(
                initialValue = 0.8f,
                targetValue = 1.1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 4000 + index * 1000,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "scale"
            )
            
            Box(
                modifier = Modifier
                    .size((100 + index * 50).dp)
                    .offset(
                        x = (50 + index * 200).dp,
                        y = (100 + index * 300).dp + floatingOffsetY.dp
                    )
                    .alpha(0.1f)
                    .background(
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f),
                        CircleShape
                    )
                    .alpha(animatedScale)
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            "Theme Settings",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.SemiBold
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack, 
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f),
                                            Color.Transparent
                                        )
                                    )
                                )
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Palette,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Choose Your Theme",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        
                        Text(
                            text = "Customize the appearance to match your style",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                }

                // Theme Selection Section
                item {
                    ThemeSelectionSection(
                        currentTheme = currentSettings.theme,
                        onThemeSelected = { theme ->
                            settingsViewModel.updateTheme(theme)
                        }
                    )
                }
                
                // Additional Info Section
                item {
                    ModernInfoCard(
                        title = "Dynamic Colors",
                        subtitle = "Colors adapt to your device wallpaper on Android 12+",
                        icon = Icons.Default.ColorLens
                    )
                }
            }
        }
    }
}

@Composable
fun ThemeSelectionSection(
    currentTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = "App Theme",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Column(
                modifier = Modifier.selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ThemeMode.entries.forEach { themeMode ->
                    ThemeOptionCard(
                        themeMode = themeMode,
                        isSelected = currentTheme == themeMode,
                        onSelect = { onThemeSelected(themeMode) }
                    )
                }
            }
        }
    }
}

@Composable
fun ThemeOptionCard(
    themeMode: ThemeMode,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.7f,
        animationSpec = tween(200),
        label = "alpha"
    )
    
    val animatedBackgroundColor by animateColorAsState(
        targetValue = if (isSelected) 
            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        else 
            MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
        animationSpec = tween(200),
        label = "backgroundColor"
    )

    Card(
        onClick = onSelect,
        modifier = Modifier
            .fillMaxWidth()
            .alpha(animatedAlpha),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = animatedBackgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Theme icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (isSelected) 
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        else 
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (themeMode) {
                        ThemeMode.LIGHT -> Icons.Default.LightMode
                        ThemeMode.DARK -> Icons.Default.DarkMode
                        ThemeMode.SYSTEM -> Icons.Default.SettingsBrightness
                    },
                    contentDescription = null,
                    tint = if (isSelected) 
                        MaterialTheme.colorScheme.primary
                    else 
                        MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(20.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = when (themeMode) {
                        ThemeMode.LIGHT -> "Light Theme"
                        ThemeMode.DARK -> "Dark Theme"
                        ThemeMode.SYSTEM -> "System Default"
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = when (themeMode) {
                        ThemeMode.LIGHT -> "Always use light colors"
                        ThemeMode.DARK -> "Always use dark colors"
                        ThemeMode.SYSTEM -> "Follow system theme"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            RadioButton(
                selected = isSelected,
                onClick = null,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary,
                    unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            )
        }
    }
}

@Composable
fun ModernInfoCard(
    title: String,
    subtitle: String,
    icon: ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.15f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(20.dp))
            
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
        }
    }
}
