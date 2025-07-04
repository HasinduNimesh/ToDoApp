package com.example.moderntodo.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.moderntodo.data.model.User
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.ZoneOffset

@Serializable
@Entity(
    tableName = "todo_items",
    foreignKeys = [
        ForeignKey(
            entity = ToDoList::class,
            parentColumns = ["id"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("listId"), Index("userId")]
)
data class ToDoItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val listId: Int = 0,
    val userId: Long = 0, // Add userId for direct user filtering
    val description: String = "",
    val order: Int = 0,
    val isCompleted: Boolean = false,
    // Use timestamp for Firebase compatibility
    val createdAtTimestamp: Long = System.currentTimeMillis(),
    val reminderDateTimeTimestamp: Long? = null,
    val priority: Priority = Priority.NORMAL,
    val category: String? = null
) {
    // No-argument constructor required by Firebase Firestore
    constructor() : this(0, 0, 0, "", 0, false, System.currentTimeMillis(), null, Priority.NORMAL, null)
    
    // Convenience properties for LocalDateTime (for Room/local use)
    val createdAt: LocalDateTime
        get() = java.time.Instant.ofEpochMilli(createdAtTimestamp)
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDateTime()
    
    val reminderDateTime: LocalDateTime?
        get() = reminderDateTimeTimestamp?.let { 
            java.time.Instant.ofEpochMilli(it)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime()
        }
}

@Serializable
enum class Priority {
    LOW, NORMAL, HIGH, URGENT
}
