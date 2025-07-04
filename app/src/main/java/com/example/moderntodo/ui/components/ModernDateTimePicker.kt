package com.example.moderntodo.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernDateTimePicker(
    selectedDateTime: LocalDateTime?,
    onDateTimeSelected: (LocalDateTime?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var tempDate by remember(selectedDateTime) { mutableStateOf(selectedDateTime?.toLocalDate()) }
    var tempTime by remember(selectedDateTime) { mutableStateOf(selectedDateTime?.toLocalTime()) }

    // Update temp variables when selectedDateTime changes
    LaunchedEffect(selectedDateTime) {
        tempDate = selectedDateTime?.toLocalDate()
        tempTime = selectedDateTime?.toLocalTime()
    }

    val dateTimeString = selectedDateTime?.let {
        "${it.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))} at ${
            it.format(
                DateTimeFormatter.ofPattern(
                    "hh:mm a"
                )
            )
        }"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Reminder",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (selectedDateTime != null) {
                IconButton(
                    onClick = {
                        onDateTimeSelected(null)
                        tempDate = null
                        tempTime = null
                    }
                ) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "Clear reminder",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        // DateTime Display/Selector Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true },
            colors = CardDefaults.cardColors(
                containerColor = if (selectedDateTime != null)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = if (selectedDateTime != null)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (selectedDateTime != null) "Reminder set for:" else "Set reminder",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (selectedDateTime != null)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (selectedDateTime != null) {
                        Text(
                            text = dateTimeString ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    } else {
                        Text(
                            text = "Tap to choose date and time",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Date Picker Dialog
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = tempDate?.atStartOfDay(java.time.ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
            )

            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                tempDate = java.time.Instant.ofEpochMilli(millis)
                                    .atZone(java.time.ZoneId.systemDefault())
                                    .toLocalDate()
                                showDatePicker = false
                                showTimePicker = true
                            }
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Time Picker Dialog
        if (showTimePicker) {
            val timePickerState = rememberTimePickerState(
                initialHour = tempTime?.hour ?: 9,
                initialMinute = tempTime?.minute ?: 0
            )

            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                title = {
                    Text(
                        "Select Time",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                text = {
                    TimePicker(
                        state = timePickerState,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            tempTime = java.time.LocalTime.of(
                                timePickerState.hour,
                                timePickerState.minute
                            )

                            // Combine date and time
                            val finalDate = tempDate ?: java.time.LocalDate.now()
                            val finalTime = tempTime ?: java.time.LocalTime.of(9, 0)
                            val combinedDateTime = LocalDateTime.of(finalDate, finalTime)

                            onDateTimeSelected(combinedDateTime)
                            showTimePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showTimePicker = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
