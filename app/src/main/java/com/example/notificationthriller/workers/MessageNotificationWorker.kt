package com.example.notificationthriller.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.notificationthriller.R
import com.example.notificationthriller.data.MessageRepository
import com.example.notificationthriller.ui.MainActivity

/**
 * WorkManager Worker that handles scheduled message notifications
 * Triggered after real-time delays specified in the message data
 */
class MessageNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    companion object {
        const val MESSAGE_ID_KEY = "message_id"
        const val CHANNEL_ID = "message_channel"
        const val NOTIFICATION_ID_BASE = 1000
    }
    
    override suspend fun doWork(): Result {
        val messageId = inputData.getInt(MESSAGE_ID_KEY, -1)
        
        if (messageId == -1) {
            val errorData = workDataOf("error" to "Invalid message ID")
            return Result.failure(errorData)
        }
        
        val repository = MessageRepository(applicationContext)
        val message = repository.getMessageById(messageId)
        
        if (message == null) {
            val errorData = workDataOf("error" to "Message not found for ID: $messageId")
            return Result.failure(errorData)
        }
        
        // Mark message as displayed
        repository.markMessageAsDisplayed(messageId)
        
        // Show notification
        showNotification(message.sender, message.message, messageId)
        
        return Result.success()
    }
    
    private fun showNotification(sender: String, messageText: String, messageId: Int) {
        createNotificationChannel()
        
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(sender)
            .setContentText(messageText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) 
                as NotificationManager
        notificationManager.notify(NOTIFICATION_ID_BASE + messageId, notification)
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Message Notifications"
            val descriptionText = "Notifications for new messages in the game"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) 
                    as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
