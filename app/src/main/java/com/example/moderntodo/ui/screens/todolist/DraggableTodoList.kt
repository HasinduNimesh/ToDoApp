package com.example.moderntodo.ui.screens.todolist

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.moderntodo.data.local.ToDoItem
import com.example.moderntodo.ui.components.ModernDragHandle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DraggableTodoList(
    items: List<ToDoItem>,
    onItemClick: (ToDoItem) -> Unit,
    onCheckboxClick: (ToDoItem) -> Unit,
    onDeleteClick: (ToDoItem) -> Unit,
    onReorder: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    
    // Simplified drag state
    var draggedIndex by remember { mutableStateOf<Int?>(null) }
    var targetIndex by remember { mutableStateOf<Int?>(null) }
    var isDragging by remember { mutableStateOf(false) }

    LazyColumn(
        state = lazyListState,
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(
            items = items,
            key = { _, item -> item.id }
        ) { index, item ->
            val isDraggedItem = draggedIndex == index
            val isTargetPosition = targetIndex == index && isDragging && targetIndex != draggedIndex
            
            SimpleDraggableItem(
                item = item,
                index = index,
                isDraggedItem = isDraggedItem,
                isTargetPosition = isTargetPosition,
                onItemClick = onItemClick,
                onCheckboxClick = onCheckboxClick,
                onDeleteClick = onDeleteClick,
                onDragStart = { startIndex ->
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    draggedIndex = startIndex
                    isDragging = true
                },
                onDragEnd = { fromIndex, toIndex ->
                    if (fromIndex != toIndex) {
                        onReorder(fromIndex, toIndex)
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                    draggedIndex = null
                    targetIndex = null
                    isDragging = false
                },
                onDragOver = { overIndex ->
                    if (overIndex != targetIndex && overIndex != draggedIndex) {
                        targetIndex = overIndex
                    }
                }
            )
        }
        
        // Add some bottom padding for the last item
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun SimpleDraggableItem(
    item: ToDoItem,
    index: Int,
    isDraggedItem: Boolean,
    isTargetPosition: Boolean,
    onItemClick: (ToDoItem) -> Unit,
    onCheckboxClick: (ToDoItem) -> Unit,
    onDeleteClick: (ToDoItem) -> Unit,
    onDragStart: (Int) -> Unit,
    onDragEnd: (Int, Int) -> Unit,
    onDragOver: (Int) -> Unit
) {
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var startPosition by remember { mutableStateOf(Offset.Zero) }
    
    val scale by animateFloatAsState(
        targetValue = if (isDraggedItem) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "item_scale"
    )
    
    val elevation by animateFloatAsState(
        targetValue = if (isDraggedItem) 8f else 2f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "item_elevation"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(
                elevation = elevation.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .then(
                if (isTargetPosition) {
                    Modifier.background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(16.dp)
                    )
                } else {
                    Modifier
                }
            )
            .then(
                if (isDraggedItem) {
                    Modifier.graphicsLayer {
                        translationY = dragOffset.y
                        alpha = 0.9f
                    }
                } else {
                    Modifier
                }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Todo item content
            Box(
                modifier = Modifier.weight(1f)
            ) {
                TodoItemRow(
                    item = item,
                    onItemClick = { onItemClick(item) },
                    onCheckboxClick = { onCheckboxClick(item) },
                    onDeleteClick = { onDeleteClick(item) }
                )
            }
            
            // Drag handle - only show for non-completed items
            if (!item.isCompleted) {
                ModernDragHandle(
                    isVisible = true,
                    isPressed = isDraggedItem,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .pointerInput(item.id) {
                            var startIndex = index
                            var currentTargetIndex = index
                            
                            detectDragGesturesAfterLongPress(
                                onDragStart = { offset ->
                                    startPosition = offset
                                    startIndex = index
                                    currentTargetIndex = index
                                    dragOffset = Offset.Zero
                                    onDragStart(index)
                                },
                                onDrag = { _, dragAmount ->
                                    dragOffset += dragAmount
                                    
                                    // Calculate target index based on cumulative drag distance
                                    val itemHeight = 72.dp.toPx() // More accurate item height estimate
                                    val dragDirection = dragOffset.y
                                    val indexOffset = (dragDirection / itemHeight).toInt()
                                    val newTargetIndex = (startIndex + indexOffset).coerceAtLeast(0)
                                    
                                    if (newTargetIndex != currentTargetIndex) {
                                        currentTargetIndex = newTargetIndex
                                        onDragOver(newTargetIndex)
                                    }
                                },
                                onDragEnd = {
                                    onDragEnd(startIndex, currentTargetIndex)
                                    dragOffset = Offset.Zero
                                    startPosition = Offset.Zero
                                }
                            )
                        }
                )
            }
        }
    }
}
