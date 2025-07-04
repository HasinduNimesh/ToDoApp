package com.example.moderntodo

import android.app.Application
import android.util.Log
import androidx.annotation.Keep
import com.example.moderntodo.service.NotificationManager
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@Keep
@HiltAndroidApp
class TodoApplication : Application() {

    @Inject
    lateinit var notificationManager: NotificationManager

    companion object {
        private const val TAG = "TodoApplication"
    }

    override fun onCreate() {
        super.onCreate()
        
        try {
            // Initialize Firebase first
            FirebaseApp.initializeApp(this)
            
            // Set Firebase Auth language to system locale to avoid null X-Firebase-Locale header
            setupFirebaseAuthLocale()
            
            // Initialize notification manager (will start monitoring settings changes)
            // The @Inject will be handled by Hilt automatically after super.onCreate()
            
        } catch (e: Exception) {
            Log.e(TAG, "Error during Firebase initialization", e)
        }
    }
    
    private fun setupFirebaseAuthLocale() {
        try {
            val defaultLocale = Locale.getDefault()
            val languageTag = if (defaultLocale.toLanguageTag().isNotEmpty()) {
                defaultLocale.toLanguageTag()
            } else {
                "en-US" // Fallback to English US
            }
            FirebaseAuth.getInstance().setLanguageCode(languageTag)
            Log.d(TAG, "Firebase Auth language set to: $languageTag")
        } catch (e: Exception) {
            // Fallback to English if there's any issue
            try {
                FirebaseAuth.getInstance().setLanguageCode("en-US")
                Log.d(TAG, "Firebase Auth language set to fallback: en-US")
            } catch (fallbackException: Exception) {
                Log.w(TAG, "Failed to set Firebase Auth language", fallbackException)
            }
        }
    }
}
