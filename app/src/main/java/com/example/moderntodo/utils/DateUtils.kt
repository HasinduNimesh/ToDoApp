package com.example.moderntodo.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

// Format LocalDate to string
fun LocalDate.formatDate(): String {
    val today = LocalDate.now()
    val tomorrow = today.plusDays(1)

    return when {
        this.isEqual(today) -> "Today"
        this.isEqual(tomorrow) -> "Tomorrow"
        this.year == today.year -> this.format(DateTimeFormatter.ofPattern("MMM d"))
        else -> this.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
    }
}

// Get relative time description
fun LocalDate.getRelativeTimeDescription(): String {
    val today = LocalDate.now()
    val daysBetween = ChronoUnit.DAYS.between(today, this)

    return when {
        daysBetween < 0 -> {
            val days = -daysBetween
            if (days == 1L) "Overdue by 1 day" else "Overdue by $days days"
        }

        daysBetween == 0L -> "Due today"
        daysBetween == 1L -> "Due tomorrow"
        daysBetween < 7L -> "Due in $daysBetween days"
        daysBetween < 30L -> "Due in ${daysBetween / 7} week(s)"
        else -> "Due on ${this.format(DateTimeFormatter.ofPattern("MMM d"))}"
    }
}

// Check if a date is past due
fun LocalDate.isPastDue(): Boolean {
    return this.isBefore(LocalDate.now())
}