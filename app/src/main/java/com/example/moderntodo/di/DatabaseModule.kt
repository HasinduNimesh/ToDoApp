package com.example.moderntodo.di

import android.content.Context
import com.example.moderntodo.data.TaskRepository
import com.example.moderntodo.data.TodoItemRepository
import com.example.moderntodo.data.TodoListRepository
import com.example.moderntodo.data.local.TaskDao
import com.example.moderntodo.data.local.TaskDatabase
import com.example.moderntodo.data.local.ToDoItemDao
import com.example.moderntodo.data.local.ToDoListDao
import com.example.moderntodo.data.local.TodoDatabase
import com.example.moderntodo.data.local.UserDao
import com.example.moderntodo.data.repository.AuthRepository
import com.example.moderntodo.data.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideCoroutineScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob())
    }

    @Singleton
    @Provides
    fun provideTodoDatabase(
        @ApplicationContext context: Context,
        scope: CoroutineScope
    ): TodoDatabase {
        return TodoDatabase.getDatabase(context, scope)
    }

    @Singleton
    @Provides
    fun provideTaskDatabase(
        @ApplicationContext context: Context
    ): TaskDatabase {
        return TaskDatabase.getDatabase(context)
    }

    @Singleton
    @Provides
    fun provideTodoListDao(database: TodoDatabase): ToDoListDao {
        return database.todoListDao()
    }    @Singleton
    @Provides
    fun provideTodoItemDao(database: TodoDatabase): ToDoItemDao {
        return database.todoItemDao()
    }

    @Singleton
    @Provides
    fun provideUserDao(database: TodoDatabase): UserDao {
        return database.userDao()
    }

    @Singleton
    @Provides
    fun provideTaskDao(database: TaskDatabase): TaskDao {
        return database.taskDao()
    }@Singleton
    @Provides
    fun provideTodoListRepository(todoListDao: ToDoListDao, authRepository: AuthRepository): TodoListRepository {
        return TodoListRepository(todoListDao, authRepository)
    }

    @Singleton
    @Provides
    fun provideTodoItemRepository(
        todoItemDao: ToDoItemDao, 
        authRepository: AuthRepository,
        notificationService: com.example.moderntodo.service.NotificationService,
        settingsRepository: com.example.moderntodo.data.repository.SettingsRepository
    ): TodoItemRepository {
        return TodoItemRepository(todoItemDao, authRepository, notificationService, settingsRepository)
    }    @Singleton
    @Provides
    fun provideTaskRepository(taskDao: TaskDao): TaskRepository {
        return TaskRepository(taskDao)
    }

    @Singleton
    @Provides
    fun provideFirebaseBackupService(): com.example.moderntodo.data.backup.FirebaseBackupService {
        return com.example.moderntodo.data.backup.FirebaseBackupService()
    }

    @Singleton
    @Provides
    fun provideBackupRepository(
        firebaseBackupService: com.example.moderntodo.data.backup.FirebaseBackupService,
        authRepository: AuthRepository,
        todoListRepository: TodoListRepository,
        todoItemRepository: TodoItemRepository,
        userDao: UserDao
    ): com.example.moderntodo.data.repository.BackupRepository {
        return com.example.moderntodo.data.repository.BackupRepository(
            firebaseBackupService,
            authRepository,
            todoListRepository,
            todoItemRepository,
            userDao
        )
    }

    @Singleton
    @Provides
    fun provideSettingsRepository(
        @ApplicationContext context: Context
    ): SettingsRepository {
        return SettingsRepository(context)
    }
}