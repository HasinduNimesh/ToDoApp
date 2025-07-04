package com.example.moderntodo.ui.screens.todolist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moderntodo.data.local.Priority
import com.example.moderntodo.data.local.ToDoItem
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun TodoItemRow(
    item: ToDoItem,
    onItemClick: () -> Unit,
    onCheckboxClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "card_scale"
    )

    // Animate completion state
    val completionAlpha by animateFloatAsState(
        targetValue = if (item.isCompleted) 0.6f else 1f,
        animationSpec = tween(300),
        label = "completion_alpha"
    )

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable {
                isPressed = true
                onItemClick()
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (item.isCompleted) 1.dp else 2.dp
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (item.isCompleted)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Modern animated checkbox
            ModernCheckbox(
                checked = item.isCompleted,
                onCheckedChange = { onCheckboxClick() }
            )            // Task description and metadata
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Priority indicator and category row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Priority indicator
                    if (item.priority != Priority.NORMAL) {
                        PriorityChip(priority = item.priority)
                    }

                    // Category tag
                    item.category?.let { category ->
                        CategoryChip(category = category)
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Reminder indicator
                    item.reminderDateTime?.let { reminder ->
                        ReminderChip(
                            reminderDateTime = reminder,
                            isCompleted = item.isCompleted
                        )
                    }
                }

                if (item.priority != Priority.NORMAL || item.category != null || item.reminderDateTime != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (item.isCompleted) FontWeight.Normal else FontWeight.Medium,
                    textDecoration = if (item.isCompleted)
                        TextDecoration.LineThrough
                    else
                        TextDecoration.None,
                    color = (if (item.isCompleted)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        MaterialTheme.colorScheme.onSurface).copy(alpha = completionAlpha),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )

                if (item.isCompleted) {
                    Text(
                        text = "Completed",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Delete button (always enabled)
            ModernDeleteButton(
                onClick = onDeleteClick,
                enabled = true
            )
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}

@Composable
private fun ModernCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    var isAnimating by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isAnimating) 1.2f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "checkbox_scale"
    )

    Box(
        modifier = Modifier
            .size(32.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                if (checked) {
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.tertiary
                        )
                    )
                } else {
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                    )
                }
            )
            .clickable {
                isAnimating = true
                onCheckedChange(!checked)
            },
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = checked,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = "Completed",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        AnimatedVisibility(
            visible = !checked,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Icon(
                imageVector = Icons.Outlined.RadioButtonUnchecked,
                contentDescription = "Not completed",
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(20.dp)
            )
        }
    }

    LaunchedEffect(isAnimating) {
        if (isAnimating) {
            kotlinx.coroutines.delay(200)
            isAnimating = false
        }
    }
}

@Composable
private fun ModernDeleteButton(
    onClick: () -> Unit,
    enabled: Boolean
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.8f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "delete_scale"
    )

    FilledTonalIconButton(
        onClick = {
            pressed = true
            onClick()
        },
        modifier = Modifier
            .size(32.dp)
            .scale(scale),
        enabled = enabled,
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete Task",
            modifier = Modifier.size(16.dp)
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
private fun PriorityChip(priority: Priority) {
    val (color, icon) = when (priority) {
        Priority.LOW -> Color(0xFF4CAF50) to Icons.Default.ExpandMore
        Priority.NORMAL -> Color(0xFF2196F3) to Icons.Default.Remove
        Priority.HIGH -> Color(0xFFFF9800) to Icons.Default.ExpandLess
        Priority.URGENT -> Color(0xFFF44336) to Icons.Default.PriorityHigh
    }

    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.wrapContentSize()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = priority.name.lowercase().replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun CategoryChip(category: String) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.wrapContentSize()
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun ReminderChip(
    reminderDateTime: LocalDateTime,
    isCompleted: Boolean
) {
    val now = LocalDateTime.now()
    val isOverdue = reminderDateTime.isBefore(now) && !isCompleted
    val isToday = reminderDateTime.toLocalDate() == now.toLocalDate()

    val color = when {
        isOverdue -> Color(0xFFF44336) // Red
        isToday -> Color(0xFFFF9800) // Orange
        else -> Color(0xFF2196F3) // Blue
    }

    val timeFormat = if (isToday) {
        DateTimeFormatter.ofPattern("h:mm a")
    } else {
        DateTimeFormatter.ofPattern("MMM d")
    }

    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.wrapContentSize()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = reminderDateTime.format(timeFormat),
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
