package com.example.notificationthriller.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.notificationthriller.R
import com.example.notificationthriller.data.Message
import com.example.notificationthriller.data.MessageRepository
import com.example.notificationthriller.workers.MessageNotificationWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * ViewModel for the chat screen following MVVM architecture
 * Manages message data and schedules notifications via WorkManager
 */
class ChatViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = MessageRepository(application)
    val displayedMessages: LiveData<List<Message>> = repository.getDisplayedMessages()
    
    /**
     * Initialize the game by loading messages from JSON and scheduling notifications
     */
    fun initializeGame() {
        viewModelScope.launch {
            // Load messages from JSON
            val messages = repository.loadMessagesFromJson(R.raw.game_messages)
            
            // Initialize database
            repository.initializeDatabase(messages)
            
            // Schedule notifications for all messages
            scheduleNotifications(messages)
        }
    }
    
    /**
     * Schedule WorkManager tasks for each message based on delaySeconds
     */
    private fun scheduleNotifications(messages: List<Message>) {
        val workManager = WorkManager.getInstance(getApplication())
        
        messages.forEach { message ->
            val inputData = Data.Builder()
                .putInt(MessageNotificationWorker.MESSAGE_ID_KEY, message.id)
                .build()
            
            val notificationWork = OneTimeWorkRequestBuilder<MessageNotificationWorker>()
                .setInitialDelay(message.delaySeconds, TimeUnit.SECONDS)
                .setInputData(inputData)
                .addTag(WORK_TAG)
                .addTag("message_${message.id}")
                .build()
            
            workManager.enqueue(notificationWork)
        }
    }
    
    /**
     * Reset the game - clear all data and reschedule
     */
    fun resetGame() {
        viewModelScope.launch {
            // Cancel all pending work for this game only
            val workManager = WorkManager.getInstance(getApplication())
            workManager.cancelAllWorkByTag(WORK_TAG)
            
            // Re-initialize
            initializeGame()
        }
    }
    
    companion object {
        private const val WORK_TAG = "notification_thriller_messages"
    }
}
