package com.example.moderntodo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "users",
    indices = [
        Index(value = ["username"], unique = true),
        Index(value = ["firebaseUid"], unique = true)
    ]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String = "",
    val passwordHash: String = "",
    val displayName: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis(),
    val firebaseUid: String? = null
) {
    // No-argument constructor required by Firebase Firestore
    constructor() : this(0, "", "", "", true, System.currentTimeMillis(), System.currentTimeMillis(), null)
}
