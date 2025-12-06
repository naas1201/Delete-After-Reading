package com.notificationthriller.app.workers

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
import com.notificationthriller.app.R
import com.notificationthriller.app.data.MessageRepository
import com.notificationthriller.app.ui.MainActivity

/**
 * WorkManager Worker that handles scheduled message notifications
 * Triggered after real-time delays specified in the message data
 */
class MessageNotificationWorker(
    context: Context,
    params: WorkerParameters,
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

    private fun showNotification(
        sender: String,
        messageText: String,
        messageId: Int,
    ) {
        createNotificationChannel()

        val intent =
            Intent(applicationContext, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

        val pendingIntent =
            PendingIntent.getActivity(
                applicationContext,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )

        // Enhanced notification title with urgency indicators
        val enhancedTitle = when {
            messageId % 10 == 0 -> "🔴 URGENT: $sender" // Critical story moments
            sender.contains("Unknown", ignoreCase = true) -> "⚠️ $sender"
            sender.contains("System", ignoreCase = true) -> "📡 $sender"
            messageId < 5 -> "🔔 NEW: $sender" // Early messages
            else -> "💬 $sender"
        }
        
        // Add engaging subtext
        val subtext = when {
            messageId % 10 == 0 -> "Critical Update" // Major story beats
            messageId < 5 -> "Your story begins..."
            else -> "Tap to read"
        }

        val notification =
            NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(enhancedTitle)
                .setContentText(messageText)
                .setSubText(subtext)
                .setStyle(NotificationCompat.BigTextStyle().bigText(messageText))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(0, 250, 100, 250)) // Custom vibration pattern
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build()

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        notificationManager.notify(NOTIFICATION_ID_BASE + messageId, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Message Notifications"
            val descriptionText = "Notifications for new messages in the game"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel =
                NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }

            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
