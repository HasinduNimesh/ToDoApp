package com.example.moderntodo.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ToDoItemDao {    @Query("SELECT * FROM todo_items WHERE listId = :listId AND userId = :userId ORDER BY `order` ASC")
    fun getItemsByListId(listId: Int, userId: Long): Flow<List<ToDoItem>>

    @Query("SELECT * FROM todo_items WHERE userId = :userId AND description LIKE '%' || :query || '%'")
    fun searchItems(query: String, userId: Long): Flow<List<ToDoItem>>

    @Query("SELECT * FROM todo_items WHERE userId = :userId ORDER BY createdAtTimestamp DESC")
    fun getAllItemsForUser(userId: Long): Flow<List<ToDoItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ToDoItem): Long

    @Update
    suspend fun updateItem(item: ToDoItem)

    @Delete
    suspend fun deleteItem(item: ToDoItem)    @Query("DELETE FROM todo_items WHERE id = :itemId AND userId = :userId")
    suspend fun deleteItemById(itemId: Int, userId: Long)

    @Query("SELECT MAX(`order`) FROM todo_items WHERE listId = :listId AND userId = :userId")
    suspend fun getMaxOrderByListId(listId: Int, userId: Long): Int?

    @Query("UPDATE todo_items SET `order` = `order` + 1 WHERE listId = :listId AND userId = :userId AND `order` >= :fromOrder")
    suspend fun shiftOrdersDown(listId: Int, userId: Long, fromOrder: Int)

    @Transaction
    suspend fun reorderItems(listId: Int, userId: Long, fromPosition: Int, toPosition: Int) {
        // Get all items in the list ordered by their current order
        val items = getItemsByListIdAndUserId(listId, userId)
        
        if (fromPosition >= items.size || toPosition >= items.size || fromPosition < 0 || toPosition < 0) {
            return // Invalid positions
        }
        
        // Create a mutable list to reorder
        val mutableItems = items.toMutableList()
        val movedItem = mutableItems.removeAt(fromPosition)
        mutableItems.add(toPosition, movedItem)
        
        // Update all items with new order values
        mutableItems.forEachIndexed { index, item ->
            updateItemOrder(item.id, userId, index)
        }
    }

    @Query("SELECT * FROM todo_items WHERE listId = :listId AND userId = :userId ORDER BY `order` ASC")
    suspend fun getItemsByListIdAndUserId(listId: Int, userId: Long): List<ToDoItem>

    @Query("UPDATE todo_items SET `order` = :newOrder WHERE id = :itemId AND userId = :userId")
    suspend fun updateItemOrder(itemId: Int, userId: Long, newOrder: Int)

    @Query("UPDATE todo_items SET `order` = `order` + :delta WHERE listId = :listId AND userId = :userId AND `order` >= :startOrder AND `order` <= :endOrder")
    suspend fun updateOrdersInRange(listId: Int, userId: Long, startOrder: Int, endOrder: Int, delta: Int)

    // Additional methods for notification system
    @Query("SELECT * FROM todo_items WHERE id = :todoId")
    suspend fun getTodoById(todoId: Int): ToDoItem?

    @Query("SELECT * FROM todo_items")
    suspend fun getAllTodos(): List<ToDoItem>

    @Query("SELECT * FROM todo_items WHERE reminderDateTimeTimestamp IS NOT NULL AND reminderDateTimeTimestamp > :currentTime")
    suspend fun getAllPendingTodos(currentTime: Long): List<ToDoItem>

    @Query("UPDATE todo_items SET isCompleted = :isCompleted WHERE id = :todoId")
    suspend fun updateTodoCompletion(todoId: Int, isCompleted: Boolean)
}
