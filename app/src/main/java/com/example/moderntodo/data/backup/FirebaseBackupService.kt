package com.example.moderntodo.data.backup

import android.util.Log
import com.example.moderntodo.data.local.ToDoItem
import com.example.moderntodo.data.local.ToDoList
import com.example.moderntodo.data.model.User
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

data class BackupData(
    val user: User? = null,
    val todoLists: List<ToDoList> = emptyList(),
    val todoItems: List<ToDoItem> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
) {
    // No-argument constructor required by Firebase Firestore
    constructor() : this(null, emptyList(), emptyList(), System.currentTimeMillis())
}

@Singleton
class FirebaseBackupService @Inject constructor() {
    
    private var _firestore: FirebaseFirestore? = null
    private var _auth: FirebaseAuth? = null
    private var _isInitialized = false
    
    companion object {
        private const val TAG = "FirebaseBackupService"
        private const val BACKUPS_COLLECTION = "user_backups"
        private const val TODO_LISTS_COLLECTION = "todo_lists"
        private const val TODO_ITEMS_COLLECTION = "todo_items"
    }
    
    // Initialize Firebase services on a background thread
    suspend fun initialize() {
        if (_isInitialized) return
        
        withContext(Dispatchers.IO) {
            try {
                // Test if Firebase is properly configured
                FirebaseApp.getInstance()
                
                _firestore = FirebaseFirestore.getInstance()
                _auth = FirebaseAuth.getInstance()
                _isInitialized = true
                Log.d(TAG, "Firebase services initialized successfully")
            } catch (e: Exception) {
                // Firebase not properly configured, leave instances as null
                _firestore = null
                _auth = null
                _isInitialized = false
                Log.e(TAG, "Failed to initialize Firebase services", e)
            }
        }
    }
    
    private val firestore: FirebaseFirestore?
        get() = _firestore
        
    private val auth: FirebaseAuth?
        get() = _auth
    
    suspend fun signInAnonymously(): Result<String> {
        return try {
            val authInstance = auth ?: return Result.failure(Exception("Firebase not properly configured. Please check google-services.json file."))
            val result = authInstance.signInAnonymously().await()
            val uid = result.user?.uid ?: throw Exception("Failed to get user ID")
            Result.success(uid)
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("API key not valid") == true -> 
                    "Invalid Firebase API key. Please check your Firebase project settings."
                e.message?.contains("not found") == true ->
                    "Firebase project not found. Please check your project configuration."
                e.message?.contains("SIGN_IN_DISABLED") == true ->
                    "Anonymous sign-in is disabled. Please enable it in Firebase Console under Authentication > Sign-in method."
                e.message?.contains("network") == true || e.message?.contains("NetworkError") == true -> 
                    "Network error. Please check your internet connection."
                e.message?.contains("FirebaseException") == true ->
                    "Firebase service error: ${e.message}"
                else -> "Authentication failed: ${e.message ?: "Unknown error"}"
            }
            Result.failure(Exception(errorMessage))
        }
    }
    
    suspend fun backupData(
        user: User,
        todoLists: List<ToDoList>,
        todoItems: List<ToDoItem>
    ): Result<String> {
        return try {
            val authInstance = auth ?: return Result.failure(Exception("Firebase not properly configured. Please check google-services.json file."))
            val firestoreInstance = firestore ?: return Result.failure(Exception("Firebase not properly configured. Please check google-services.json file."))
            
            val uid = authInstance.currentUser?.uid ?: throw Exception("User not authenticated")
            
            val backupData = BackupData(
                user = user,
                todoLists = todoLists,
                todoItems = todoItems
            )
            
            // Store backup data in Firestore
            firestoreInstance.collection("user_backups")
                .document(uid)
                .collection("backups")
                .document(System.currentTimeMillis().toString())
                .set(backupData)
                .await()
            
            Result.success("Backup completed successfully")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getBackups(): Result<List<BackupData>> {
        return try {
            val authInstance = auth ?: return Result.failure(Exception("Firebase not properly configured. Please check google-services.json file."))
            val firestoreInstance = firestore ?: return Result.failure(Exception("Firebase not properly configured. Please check google-services.json file."))
            val uid = authInstance.currentUser?.uid ?: throw Exception("User not authenticated")
            
            val snapshot = firestoreInstance.collection("user_backups")
                .document(uid)
                .collection("backups")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            
            val backups = snapshot.documents.mapNotNull { doc ->
                doc.toObject(BackupData::class.java)
            }
            
            Result.success(backups)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun restoreData(backupData: BackupData): Result<BackupData> {
        return try {
            // The actual restoration will be handled by the repository
            Result.success(backupData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getCurrentUserId(): String? {
        return if (_isInitialized) _auth?.currentUser?.uid else null
    }
    
    fun isSignedIn(): Boolean {
        // Only check if we've been properly initialized to avoid lazy loading
        return _isInitialized && _auth?.currentUser != null
    }
    
    fun signOut() {
        _auth?.signOut()
    }
    
    fun isFirebaseConfigured(): Boolean {
        return try {
            val app = FirebaseApp.getInstance()
            val apiKey = app.options.apiKey
            // Check if it's not a placeholder key and is valid length
            apiKey.isNotEmpty() && apiKey.length > 30 && !apiKey.contains("Placeholder")
        } catch (e: Exception) {
            false
        }
    }
    
    fun getFirebaseConfigStatus(): String {
        return when {
            !isFirebaseConfigured() -> "Firebase not configured properly. Check your google-services.json file."
            !_isInitialized -> "Firebase services not initialized yet."
            _auth == null -> "Firebase Authentication not available."
            _firestore == null -> "Firebase Firestore not available."
            else -> "Firebase is properly configured."
        }
    }
}
