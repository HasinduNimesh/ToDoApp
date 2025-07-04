package com.example.moderntodo.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.moderntodo.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?
    
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Long): User?
    
    @Query("SELECT * FROM users WHERE firebaseUid = :firebaseUid LIMIT 1")
    suspend fun getUserByFirebaseUid(firebaseUid: String): User?
    
    @Query("SELECT * FROM users ORDER BY lastLoginAt DESC")
    fun getAllUsers(): Flow<List<User>>
    
    @Insert
    suspend fun insertUser(user: User): Long
    
    @Update
    suspend fun updateUser(user: User)
    
    @Query("UPDATE users SET lastLoginAt = :lastLoginAt WHERE id = :userId")
    suspend fun updateLastLogin(userId: Long, lastLoginAt: Long = System.currentTimeMillis())
    
    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: Long)
    
    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
}
