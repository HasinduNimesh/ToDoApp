package com.example.moderntodo.ui.screens.todolist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.moderntodo.data.local.Priority
import com.example.moderntodo.data.local.ToDoItem
import com.example.moderntodo.ui.components.ModernDateTimePicker
import com.example.moderntodo.ui.components.ModernPrioritySelector
import com.example.moderntodo.ui.theme.GradientEnd
import com.example.moderntodo.ui.theme.GradientStart
import java.time.LocalDateTime

@Composable
fun AddEditItemDialog(
    item: ToDoItem?,
    onDismiss: () -> Unit,
    onSave: (String, LocalDateTime?, Priority, String?) -> Unit
) {
    ModernAddEditItemDialog(
        item = item,
        onDismiss = onDismiss,
        onSave = onSave
    )
}

@Composable
fun ModernAddEditItemDialog(
    item: ToDoItem?,
    onDismiss: () -> Unit,
    onSave: (String, LocalDateTime?, Priority, String?) -> Unit
) {
    val isEditMode = item != null
    var description by remember { mutableStateOf(item?.description ?: "") }
    var reminderDateTime by remember { mutableStateOf(item?.reminderDateTime) }
    var priority by remember { mutableStateOf(item?.priority ?: Priority.NORMAL) }
    var category by remember { mutableStateOf(item?.category ?: "") }
    var isError by remember { mutableStateOf(false) }

    // Animation states
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = scaleIn(
                initialScale = 0.8f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            ) + fadeIn(),
            exit = scaleOut(targetScale = 0.8f) + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header with icon and title
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            GradientStart,
                                            GradientEnd
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isEditMode) Icons.Default.Edit else Icons.Default.Create,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Text(
                            text = if (isEditMode) "Edit Task" else "Add New Task",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Input field
                    OutlinedTextField(
                        value = description,
                        onValueChange = {
                            description = it
                            isError = it.isBlank()
                        },
                        label = {
                            Text(
                                "Task Description",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        placeholder = {
                            Text(
                                "What needs to be done?",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        singleLine = false,
                        maxLines = 3,
                        isError = isError,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    AnimatedVisibility(
                        visible = isError,
                        enter = slideInVertically() + fadeIn(),
                        exit = slideOutVertically() + fadeOut()
                    ) {
                        Text(
                            text = "Please enter a task description",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, start = 16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Category input
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = {
                            Text(
                                "Category (Optional)",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        placeholder = {
                            Text(
                                "e.g., Work, Personal, Shopping",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Priority selector
                    ModernPrioritySelector(
                        selectedPriority = priority,
                        onPrioritySelected = { priority = it }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Date/Time picker
                    ModernDateTimePicker(
                        selectedDateTime = reminderDateTime,
                        onDateTimeSelected = { reminderDateTime = it }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                "Cancel",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        Button(
                            onClick = {
                                if (description.isBlank()) {
                                    isError = true
                                } else {
                                    val finalCategory = if (category.isBlank()) null else category
                                    onSave(description, reminderDateTime, priority, finalCategory)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                if (isEditMode) "Update" else "Add Task",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
