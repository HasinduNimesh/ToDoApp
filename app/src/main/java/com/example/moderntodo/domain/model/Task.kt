package com.example.moderntodo.domain.model

import java.time.LocalDate
import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val isCompleted: Boolean = false,
    val dueDate: LocalDate? = null,
    val createdAt: Long = System.currentTimeMillis()
)