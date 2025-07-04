package com.example.moderntodo.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.moderntodo.data.local.UserDao
import com.example.moderntodo.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val userDao: UserDao,
    @ApplicationContext private val context: Context
) {
    private val sharedPrefs: SharedPreferences = 
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val CURRENT_USER_ID_KEY = "current_user_id"

    companion object {
        private const val TAG = "AuthRepository"
        private const val USERS_COLLECTION = "users"
    }

    // StateFlow to track Firebase user changes
    private val _firebaseUserState = MutableStateFlow<FirebaseUser?>(null)
    val firebaseUserState: StateFlow<FirebaseUser?> = _firebaseUserState.asStateFlow()

    init {
        // Listen to Firebase Auth state changes
        firebaseAuth.addAuthStateListener { auth ->
            _firebaseUserState.value = auth.currentUser
            Log.d(TAG, "Firebase auth state changed: ${auth.currentUser?.email}")
        }
    }

    fun getCurrentUserId(): Long? {
        val userId = sharedPrefs.getLong(CURRENT_USER_ID_KEY, -1L)
        return if (userId == -1L) null else userId
    }

    val currentUserFlow: Flow<User?> = userDao.getAllUsers().map { users ->
        val currentUserId = getCurrentUserId()
        if (currentUserId != null) {
            users.find { it.id == currentUserId }
        } else {
            null
        }
    }

    suspend fun getCurrentUser(): User? {
        val userId = getCurrentUserId() ?: return null
        return userDao.getUserById(userId)
    }

    suspend fun getAllUsers(): List<User> {
        return userDao.getAllUsers().first()
    }

    suspend fun register(email: String, password: String, displayName: String): Result<User> {
        return try {
            Log.d(TAG, "Attempting to register user with email: $email")
            
            // Create user with Firebase Authentication
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Failed to create Firebase user")
            
            Log.d(TAG, "Firebase user created successfully: ${firebaseUser.uid}")
            
            // Update Firebase user profile with display name
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName.ifBlank { email.substringBefore("@") })
                .build()
            firebaseUser.updateProfile(profileUpdates).await()
            
            // Create user document in Firestore
            val userDocument = mapOf(
                "uid" to firebaseUser.uid,
                "email" to email,
                "displayName" to displayName.ifBlank { email.substringBefore("@") },
                "createdAt" to System.currentTimeMillis(),
                "lastLoginAt" to System.currentTimeMillis(),
                "isActive" to true
            )
            
            firestore.collection(USERS_COLLECTION)
                .document(firebaseUser.uid)
                .set(userDocument)
                .await()
            
            Log.d(TAG, "User document created in Firestore")
            
            // Create local user record
            val user = User(
                username = email, // Use email as username for local compatibility
                passwordHash = "", // Not needed with Firebase auth
                displayName = displayName.ifBlank { email.substringBefore("@") },
                isActive = true,
                createdAt = System.currentTimeMillis(),
                lastLoginAt = System.currentTimeMillis(),
                firebaseUid = firebaseUser.uid
            )

            val userId = userDao.insertUser(user)
            val createdUser = user.copy(id = userId)
            
            // Set as current user
            setCurrentUser(createdUser)
            
            Log.d(TAG, "User registered successfully: $email")
            Result.success(createdUser)
        } catch (e: Exception) {
            Log.e(TAG, "Registration failed for $email", e)
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            Log.d(TAG, "Attempting to login user with email: $email")
            
            // Sign in with Firebase Authentication
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Failed to sign in")
            
            Log.d(TAG, "Firebase sign in successful: ${firebaseUser.uid}")
            
            // Update last login in Firestore
            firestore.collection(USERS_COLLECTION)
                .document(firebaseUser.uid)
                .update(
                    mapOf(
                        "lastLoginAt" to System.currentTimeMillis(),
                        "isActive" to true
                    )
                )
                .await()
            
            // Find or create local user record
            var user = userDao.getUserByFirebaseUid(firebaseUser.uid)
            
            if (user == null) {
                // Create local user if doesn't exist (for existing Firebase users)
                user = User(
                    username = email,
                    passwordHash = "",
                    displayName = firebaseUser.displayName ?: email.substringBefore("@"),
                    isActive = true,
                    createdAt = System.currentTimeMillis(),
                    lastLoginAt = System.currentTimeMillis(),
                    firebaseUid = firebaseUser.uid
                )
                val userId = userDao.insertUser(user)
                user = user.copy(id = userId)
            } else {
                // Update last login time
                user = user.copy(
                    lastLoginAt = System.currentTimeMillis(),
                    isActive = true,
                    displayName = firebaseUser.displayName ?: user.displayName
                )
                userDao.updateUser(user)
            }
            
            // Set as current user
            setCurrentUser(user)
            
            Log.d(TAG, "User logged in successfully: $email")
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Login failed for $email", e)
            Result.failure(e)
        }
    }

    suspend fun switchUser(user: User) {
        try {
            // This function is for local user switching
            // We don't change Firebase auth here, just local state
            val updatedUser = user.copy(
                lastLoginAt = System.currentTimeMillis(),
                isActive = true
            )
            userDao.updateUser(updatedUser)
            
            // Set as current user
            setCurrentUser(updatedUser)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun logout() {
        try {
            Log.d(TAG, "Logging out user")
            
            // Update Firestore
            val currentFirebaseUser = firebaseAuth.currentUser
            if (currentFirebaseUser != null) {
                firestore.collection(USERS_COLLECTION)
                    .document(currentFirebaseUser.uid)
                    .update("isActive", false)
                    .await()
            }
            
            // Sign out from Firebase
            firebaseAuth.signOut()
            
            // Clear local session
            sharedPrefs.edit().remove(CURRENT_USER_ID_KEY).apply()
            
            Log.d(TAG, "User logged out successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Logout failed", e)
            throw e
        }
    }

    suspend fun syncUserWithFirestore(user: User) {
        try {
            if (user.firebaseUid != null) {
                val userDocument = mapOf(
                    "uid" to user.firebaseUid,
                    "email" to user.username,
                    "displayName" to user.displayName,
                    "lastLoginAt" to user.lastLoginAt,
                    "isActive" to user.isActive
                )
                
                firestore.collection(USERS_COLLECTION)
                    .document(user.firebaseUid)
                    .set(userDocument)
                    .await()
                    
                Log.d(TAG, "User synced with Firestore: ${user.username}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync user with Firestore", e)
        }
    }

    suspend fun deleteAccount(): Result<Unit> {
        return try {
            val currentFirebaseUser = firebaseAuth.currentUser
            if (currentFirebaseUser != null) {
                // Delete user document from Firestore
                firestore.collection(USERS_COLLECTION)
                    .document(currentFirebaseUser.uid)
                    .delete()
                    .await()
                
                // Delete local user data
                val localUser = getCurrentUser()
                if (localUser != null) {
                    userDao.deleteUser(localUser.id)
                }
                
                // Delete Firebase Auth account
                currentFirebaseUser.delete().await()
                
                // Clear local session
                sharedPrefs.edit().remove(CURRENT_USER_ID_KEY).apply()
                
                Log.d(TAG, "Account deleted successfully")
                Result.success(Unit)
            } else {
                Result.failure(Exception("No user currently signed in"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Account deletion failed", e)
            Result.failure(e)
        }
    }

    fun isUserSignedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun getCurrentFirebaseUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    private fun setCurrentUser(user: User) {
        sharedPrefs.edit().putLong(CURRENT_USER_ID_KEY, user.id).apply()
    }
}
