package com.example.moderntodo.ui.screens.lists

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moderntodo.data.local.ToDoList
import com.example.moderntodo.ui.auth.AuthViewModel
import com.example.moderntodo.ui.screens.lists.ModernAddEditListDialog
import com.example.moderntodo.ui.theme.GradientEnd
import com.example.moderntodo.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListsScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: ListsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val lists by viewModel.allLists.collectAsState(initial = emptyList())
    var showAddListDialog by remember { mutableStateOf(false) }
    var editingList by remember { mutableStateOf<ToDoList?>(null) }

    // Animated FAB scale
    val fabScale by animateFloatAsState(
        targetValue = if (showAddListDialog) 0.8f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "fab_scale"
    )    // Floating animations
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val floatingAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating"
    )

    Scaffold(
        topBar = {
            ModernTopAppBar(
                title = "My Lists",
                onSearchClick = { navController.navigate("search") },
                onSettingsClick = { navController.navigate("settings") }
            )
        },
        floatingActionButton = {
            ModernFAB(
                onClick = { showAddListDialog = true },
                scale = fabScale
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            GradientStart,
                            GradientEnd,
                            MaterialTheme.colorScheme.primary
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            // Floating background decorations
            FloatingDecorations(floatingAnimation)

            if (lists.isEmpty()) {
                ModernEmptyState(
                    icon = Icons.Outlined.FormatListBulleted,
                    title = "No Lists Yet",
                    subtitle = "Create your first list to get organized!",
                    onCreateClick = { showAddListDialog = true }
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp)
                ) {
                    items(lists, key = { it.id }) { list ->
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically(
                                initialOffsetY = { it / 2 },
                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                            ) + fadeIn(),
                            exit = slideOutVertically() + fadeOut()
                        ) {
                            ModernListItem(
                                list = list,
                                onListClick = { navController.navigate("list/${list.id}") },
                                onEditClick = { editingList = list },
                                onDeleteClick = { viewModel.deleteList(list) }
                            )
                        }
                    }
                }
            }

            // Add List Dialog
            if (showAddListDialog) {
                ModernAddEditListDialog(
                    list = null,
                    onDismiss = { showAddListDialog = false },                    onSave = { title ->
                        viewModel.addList(title)
                        showAddListDialog = false
                    }
                )
            }

            // Edit List Dialog
            editingList?.let { list ->
                ModernAddEditListDialog(
                    list = list,
                    onDismiss = { editingList = null },
                    onSave = { title ->
                        viewModel.updateList(list.copy(title = title))
                        editingList = null
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernTopAppBar(
    title: String,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        actions = {
            ModernIconButton(
                icon = Icons.Default.Search,
                contentDescription = "Search",
                onClick = onSearchClick
            )
            ModernIconButton(
                icon = Icons.Default.Settings,
                contentDescription = "Settings",
                onClick = onSettingsClick
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
private fun ModernIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "icon_scale"
    )

    IconButton(
        onClick = {
            pressed = true
            onClick()
        },
        modifier = Modifier.scale(scale)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.primary
        )
    }

    LaunchedEffect(pressed) {
        if (pressed) {
            kotlinx.coroutines.delay(100)
            pressed = false
        }
    }
}

@Composable
private fun ModernFAB(
    onClick: () -> Unit,
    scale: Float
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier.scale(scale),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 8.dp,
            pressedElevation = 12.dp
        )
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add List",
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
private fun ModernEmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onCreateClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animated icon
        val infiniteTransition = rememberInfiniteTransition(label = "empty_state")
        val iconScale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000),
                repeatMode = RepeatMode.Reverse
            ),
            label = "icon_scale"
        )

        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                    )
                )
                .scale(iconScale),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onCreateClick,
            modifier = Modifier
                .padding(horizontal = 32.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Create First List",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun FloatingDecorations(floatingAnimation: Float) {
    // Floating circles for decoration
    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(0.1f)
    ) {
        // Top-left circle
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(
                    x = (-50).dp + (10 * floatingAnimation).dp,
                    y = (-50).dp + (15 * floatingAnimation).dp
                )
                .background(
                    Color.White,
                    RoundedCornerShape(50)
                )
        )

        // Bottom-right circle
        Box(
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.BottomEnd)
                .offset(
                    x = 50.dp - (8 * floatingAnimation).dp,
                    y = 50.dp - (12 * floatingAnimation).dp
                )
                .background(
                    Color.White,
                    RoundedCornerShape(50)
                )
        )
    }
}
