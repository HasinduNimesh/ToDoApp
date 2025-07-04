package com.example.moderntodo.ui.screens.search

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moderntodo.ui.auth.AuthViewModel
import com.example.moderntodo.ui.screens.todolist.TodoItemRow
import com.example.moderntodo.ui.theme.GradientEnd
import com.example.moderntodo.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun SearchScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: SearchViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val searchResults by viewModel.searchResults.collectAsState(initial = emptyList())
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotEmpty()) {
            viewModel.searchItems(searchQuery)
        }
    }    // Floating animations
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
            ModernSearchTopAppBar(
                onBackClick = { navController.navigateUp() }
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Modern search bar
                ModernSearchBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Search results
                AnimatedContent(
                    targetState = when {
                        searchQuery.isEmpty() -> SearchState.INITIAL
                        searchResults.isEmpty() -> SearchState.NO_RESULTS
                        else -> SearchState.RESULTS
                    },
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) with
                                fadeOut(animationSpec = tween(300))
                    },
                    label = "search_content"
                ) { state ->
                    when (state) {
                        SearchState.INITIAL -> {
                            ModernSearchPrompt()
                        }

                        SearchState.NO_RESULTS -> {
                            ModernNoResults(query = searchQuery)
                        }

                        SearchState.RESULTS -> {
                            ModernSearchResults(
                                results = searchResults,
                                onItemClick = { item ->
                                    navController.navigate("list/${item.listId}")
                                },
                                onToggleComplete = { item ->
                                    viewModel.toggleItemCompletion(item)
                                },
                                onDeleteItem = { item ->
                                    viewModel.deleteItem(item)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

private enum class SearchState {
    INITIAL, NO_RESULTS, RESULTS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernSearchTopAppBar(
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Search Tasks",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
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
private fun ModernSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        placeholder = {
            Text(
                "Search your tasks...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(
                    onClick = { onSearchQueryChange("") }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        singleLine = true
    )
}

@Composable
private fun ModernSearchPrompt() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animated search icon
        val infiniteTransition = rememberInfiniteTransition(label = "search_prompt")
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
                .size(100.dp)
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
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Search Your Tasks",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Type in the search bar to find your tasks",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ModernNoResults(query: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.SearchOff,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.outline
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "No Results Found",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "No tasks found matching '$query'",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ModernSearchResults(
    results: List<com.example.moderntodo.data.local.ToDoItem>,
    onItemClick: (com.example.moderntodo.data.local.ToDoItem) -> Unit,
    onToggleComplete: (com.example.moderntodo.data.local.ToDoItem) -> Unit,
    onDeleteItem: (com.example.moderntodo.data.local.ToDoItem) -> Unit
) {
    Column {
        Text(
            text = "${results.size} ${if (results.size == 1) "task" else "tasks"} found",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(results, key = { it.id }) { item ->
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                    ) + fadeIn(),
                    exit = slideOutVertically() + fadeOut()
                ) {
                    TodoItemRow(
                        item = item,
                        onItemClick = { onItemClick(item) },
                        onCheckboxClick = { onToggleComplete(item) },
                        onDeleteClick = { onDeleteItem(item) }
                    )
                }
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
