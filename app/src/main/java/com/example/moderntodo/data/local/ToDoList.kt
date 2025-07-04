package com.example.moderntodo.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.moderntodo.data.model.User
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "todo_lists",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class ToDoList(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Long = 0,
    val title: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    // No-argument constructor required by Firebase Firestore
    constructor() : this(0, 0, "", System.currentTimeMillis())
}
