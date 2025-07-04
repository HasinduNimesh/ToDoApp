// TaskEntity.kt
package com.example.moderntodo.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.moderntodo.domain.model.Task
import java.time.LocalDate
import java.time.ZoneId

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val isCompleted: Boolean,
    val dueDateMillis: Long?, // Store as milliseconds
    val createdAt: Long
) {
    fun toTask(): Task {
        return Task(
            id = id,
            title = title,
            isCompleted = isCompleted,
            dueDate = dueDateMillis?.let {
                LocalDate.ofInstant(
                    java.time.Instant.ofEpochMilli(it),
                    ZoneId.systemDefault()
                )
            },
            createdAt = createdAt
        )
    }

    companion object {
        fun fromTask(task: Task): TaskEntity {
            return TaskEntity(
                id = task.id,
                title = task.title,
                isCompleted = task.isCompleted,
                dueDateMillis = task.dueDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()
                    ?.toEpochMilli(),
                createdAt = task.createdAt
            )
        }
    }
}