package com.example.moderntodo.data.backup

import android.content.Context
import com.example.moderntodo.data.local.ToDoItem
import com.example.moderntodo.data.local.ToDoList
import com.example.moderntodo.data.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class LocalBackupData(
    val user: User? = null,
    val todoLists: List<ToDoList> = emptyList(),
    val todoItems: List<ToDoItem> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(),
    val version: Int = 1
)

@Singleton
class LocalBackupService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    companion object {
        private const val BACKUP_DIRECTORY = "todo_backups"
        private const val BACKUP_FILE_PREFIX = "todo_backup_"
        private const val BACKUP_FILE_EXTENSION = ".json"
    }
    
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }
    
    suspend fun createBackup(
        user: User?,
        todoLists: List<ToDoList>,
        todoItems: List<ToDoItem>
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val backupData = LocalBackupData(
                user = user,
                todoLists = todoLists,
                todoItems = todoItems,
                timestamp = System.currentTimeMillis()
            )
            
            val backupDirectory = File(context.filesDir, BACKUP_DIRECTORY)
            if (!backupDirectory.exists()) {
                backupDirectory.mkdirs()
            }
            
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val backupFile = File(backupDirectory, "$BACKUP_FILE_PREFIX$timestamp$BACKUP_FILE_EXTENSION")
            
            val jsonString = json.encodeToString(backupData)
            backupFile.writeText(jsonString)
            
            Result.success("Backup created successfully at ${backupFile.name}")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAvailableBackups(): Result<List<BackupFileInfo>> = withContext(Dispatchers.IO) {
        try {
            val backupDirectory = File(context.filesDir, BACKUP_DIRECTORY)
            if (!backupDirectory.exists()) {
                return@withContext Result.success(emptyList())
            }
            
            val backupFiles = backupDirectory.listFiles { file ->
                file.name.startsWith(BACKUP_FILE_PREFIX) && file.name.endsWith(BACKUP_FILE_EXTENSION)
            }
            
            if (backupFiles == null) {
                return@withContext Result.success(emptyList())
            }
            
            val sortedFiles = backupFiles.sortedByDescending { it.lastModified() }
            
            val backupInfoList = sortedFiles.map { file ->
                BackupFileInfo(
                    fileName = file.name,
                    filePath = file.absolutePath,
                    timestamp = file.lastModified(),
                    size = file.length()
                )
            }
            
            Result.success(backupInfoList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun restoreFromBackup(backupFilePath: String): Result<LocalBackupData> = withContext(Dispatchers.IO) {
        try {
            val backupFile = File(backupFilePath)
            if (!backupFile.exists()) {
                return@withContext Result.failure(Exception("Backup file not found"))
            }
            
            val jsonString = backupFile.readText()
            val backupData = json.decodeFromString<LocalBackupData>(jsonString)
            
            Result.success(backupData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteBackup(backupFilePath: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val backupFile = File(backupFilePath)
            if (backupFile.exists() && backupFile.delete()) {
                Result.success("Backup deleted successfully")
            } else {
                Result.failure(Exception("Failed to delete backup file"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getBackupDirectorySize(): Long {
        val backupDirectory = File(context.filesDir, BACKUP_DIRECTORY)
        if (!backupDirectory.exists()) return 0L
        
        return backupDirectory.walkTopDown().filter { it.isFile }.map { it.length() }.sum()
    }
}

@Serializable
data class BackupFileInfo(
    val fileName: String,
    val filePath: String,
    val timestamp: Long,
    val size: Long
)
