package com.example.moderntodo.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ToDoListDao {
    @Query("SELECT * FROM todo_lists WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllListsForUser(userId: Long): Flow<List<ToDoList>>

    @Query("SELECT * FROM todo_lists WHERE id = :listId AND userId = :userId")
    suspend fun getListById(listId: Int, userId: Long): ToDoList?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(list: ToDoList): Long

    @Update
    suspend fun updateList(list: ToDoList)

    @Delete
    suspend fun deleteList(list: ToDoList)

    @Query("DELETE FROM todo_lists WHERE id = :listId AND userId = :userId")
    suspend fun deleteListById(listId: Int, userId: Long)
      @Query("SELECT COUNT(*) FROM todo_lists WHERE userId = :userId")
    suspend fun getListCountForUser(userId: Long): Int
    
    @Query("DELETE FROM todo_lists WHERE userId = :userId")
    suspend fun deleteAllListsForUser(userId: Long)
}
