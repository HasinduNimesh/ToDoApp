package com.example.moderntodo.ui.screens.todolist

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moderntodo.data.local.ToDoItem
import com.example.moderntodo.ui.auth.AuthViewModel
import com.example.moderntodo.ui.components.DragAndDropHint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    listId: Int,
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: TodoListViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    // Set the current list ID in the ViewModel
    LaunchedEffect(listId) {
        viewModel.setCurrentListId(listId)
    }

    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("todo_hints", android.content.Context.MODE_PRIVATE) }
    
    val listTitle by viewModel.listTitle.collectAsState("")
    val items by viewModel.todoItems.collectAsState(initial = emptyList())
    var showAddItemDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<ToDoItem?>(null) }
    var showDragHint by remember { mutableStateOf(false) }

    // Show drag hint only once when there are multiple items
    LaunchedEffect(items.size) {
        val hasShownHint = prefs.getBoolean("drag_hint_shown", false)
        if (items.size >= 2 && !hasShownHint && !showDragHint) {
            kotlinx.coroutines.delay(1500) // Wait a bit longer before showing hint
            showDragHint = true
        }
    }

    // Animated FAB scale
    val fabScale by animateFloatAsState(
        targetValue = if (showAddItemDialog) 0.8f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "fab_scale"
    )

    // Gradient background
    val gradientColors = listOf(
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.05f),
        MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.02f)
    )

    Scaffold(
        topBar = {
            ModernTodoTopAppBar(
                title = listTitle.ifEmpty { "Loading..." },
                onBackClick = { navController.navigateUp() }
            )
        },
        floatingActionButton = {
            ModernTodoFAB(
                onClick = { showAddItemDialog = true },
                scale = fabScale
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(gradientColors)
                )
                .padding(paddingValues)
        ) {
            if (items.isEmpty()) {
                ModernTodoEmptyState(
                    onAddClick = { showAddItemDialog = true }
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Drag and drop hint
                    DragAndDropHint(
                        isVisible = showDragHint && items.size >= 2,
                        onDismiss = { 
                            showDragHint = false
                            // Save that hint has been shown
                            prefs.edit().putBoolean("drag_hint_shown", true).apply()
                        }
                    )
                    
                    // Content with proper spacing
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp)
                    ) {
                        // Statistics row
                        TodoStatsRow(
                            totalItems = items.size,
                            completedItems = items.count { it.isCompleted },
                            modifier = Modifier.padding(vertical = 16.dp)
                        )

                        // List of todo items with drag-and-drop reordering
                        DraggableTodoList(
                            items = items,
                            onItemClick = { editingItem = it },
                            onCheckboxClick = { viewModel.toggleItemCompletion(it) },
                            onDeleteClick = { viewModel.deleteItem(it) },
                            onReorder = { fromPosition, toPosition ->
                                viewModel.reorderItems(fromPosition, toPosition)
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }            // Add Item Dialog
            if (showAddItemDialog) {
                ModernAddEditItemDialog(
                    item = null,
                    onDismiss = { showAddItemDialog = false },
                    onSave = { description, reminderDateTime, priority, category ->
                        viewModel.addItem(description, reminderDateTime, priority, category)
                        showAddItemDialog = false
                    }
                )
            }

            // Edit Item Dialog
            editingItem?.let { item ->
                ModernAddEditItemDialog(
                    item = item,
                    onDismiss = { editingItem = null },
                    onSave = { description, reminderDateTime, priority, category ->
                        viewModel.updateItem(
                            item.copy(
                                description = description,
                                reminderDateTimeTimestamp = reminderDateTime?.atZone(java.time.ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
                                priority = priority,
                                category = category
                            )
                        )
                        editingItem = null
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernTodoTopAppBar(
    title: String,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        },
        navigationIcon = {
            var pressed by remember { mutableStateOf(false) }
            val scale by animateFloatAsState(
                targetValue = if (pressed) 0.9f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "back_scale"
            )

            IconButton(
                onClick = {
                    pressed = true
                    onBackClick()
                },
                modifier = Modifier.scale(scale)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            LaunchedEffect(pressed) {
                if (pressed) {
                    kotlinx.coroutines.delay(100)
                    pressed = false
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
private fun ModernTodoFAB(
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
            contentDescription = "Add Task",
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
private fun ModernTodoEmptyState(
    onAddClick: () -> Unit
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
                imageVector = Icons.Outlined.TaskAlt,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "No Tasks Yet",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Add your first task to get started!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onAddClick,
            modifier = Modifier.padding(horizontal = 32.dp),
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
                    text = "Add First Task",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun TodoStatsRow(
    totalItems: Int,
    completedItems: Int,
    modifier: Modifier = Modifier
) {
    val progress = if (totalItems > 0) completedItems.toFloat() / totalItems else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label = "progress"
    )

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progress",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$completedItems of $totalItems completed",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = when {
                    progress >= 1f -> MaterialTheme.colorScheme.tertiary
                    progress >= 0.7f -> MaterialTheme.colorScheme.primary
                    progress >= 0.3f -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.outline
                },
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}
