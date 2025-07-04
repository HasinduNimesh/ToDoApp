package com.example.moderntodo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.moderntodo.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [User::class, ToDoList::class, ToDoItem::class],
    version = 6, // Increment version due to ToDoItem schema change (LocalDateTime to timestamp)
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun todoListDao(): ToDoListDao
    abstract fun todoItemDao(): ToDoItemDao

    companion object {
        @Volatile
        private var INSTANCE: TodoDatabase? = null

        // Migration from version 4 to 5 - add firebaseUid column to users table
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE users ADD COLUMN firebaseUid TEXT")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_users_firebaseUid ON users(firebaseUid)")
            }
        }

        // Migration from version 5 to 6 - change LocalDateTime fields to timestamps in todo_items
        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create new table with timestamp fields
                db.execSQL("""
                    CREATE TABLE todo_items_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        listId INTEGER NOT NULL,
                        userId INTEGER NOT NULL,
                        description TEXT NOT NULL,
                        `order` INTEGER NOT NULL,
                        isCompleted INTEGER NOT NULL,
                        createdAtTimestamp INTEGER NOT NULL,
                        reminderDateTimeTimestamp INTEGER,
                        priority TEXT NOT NULL,
                        category TEXT,
                        FOREIGN KEY(listId) REFERENCES todo_lists(id) ON DELETE CASCADE,
                        FOREIGN KEY(userId) REFERENCES users(id) ON DELETE CASCADE
                    )
                """.trimIndent())
                
                // Copy data from old table, converting timestamps (assuming existing timestamps are in milliseconds)
                db.execSQL("""
                    INSERT INTO todo_items_new (id, listId, userId, description, `order`, isCompleted, createdAtTimestamp, reminderDateTimeTimestamp, priority, category)
                    SELECT id, listId, userId, description, `order`, isCompleted, 
                           CASE WHEN createdAt IS NOT NULL THEN strftime('%s', createdAt) * 1000 ELSE ${System.currentTimeMillis()} END,
                           CASE WHEN reminderDateTime IS NOT NULL THEN strftime('%s', reminderDateTime) * 1000 ELSE NULL END,
                           priority, category
                    FROM todo_items
                """.trimIndent())
                
                // Drop old table and rename new one
                db.execSQL("DROP TABLE todo_items")
                db.execSQL("ALTER TABLE todo_items_new RENAME TO todo_items")
                
                // Recreate indices
                db.execSQL("CREATE INDEX index_todo_items_listId ON todo_items(listId)")
                db.execSQL("CREATE INDEX index_todo_items_userId ON todo_items(userId)")
            }
        }

        fun getDatabase(context: Context, scope: CoroutineScope): TodoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TodoDatabase::class.java,
                    "todo_database"
                )
                    .addMigrations(MIGRATION_4_5, MIGRATION_5_6)
                    .addCallback(TodoDatabaseCallback(scope))
                    .build()

                INSTANCE = instance
                instance
            }
        }        private class TodoDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Database is created, but we don't add default data anymore
                // Users will create their own lists after authentication
            }
        }
    }
}
