package com.example.notificationthriller

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager

/**
 * Application class for the Notification Thriller game
 */
class NotificationThrillerApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize WorkManager with custom configuration if needed
        // Default configuration is sufficient for our use case
    }
}
