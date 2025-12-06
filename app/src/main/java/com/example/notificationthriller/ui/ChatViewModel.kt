package com.example.notificationthriller.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.notificationthriller.R
import com.example.notificationthriller.data.GameState
import com.example.notificationthriller.data.Message
import com.example.notificationthriller.data.MessageRepository
import com.example.notificationthriller.workers.MessageNotificationWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * ViewModel for the chat screen following MVVM architecture
 * Manages message data and schedules notifications via WorkManager
 * Includes branching narratives and save/load functionality
 */
class ChatViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = MessageRepository(application)
    val displayedMessages: LiveData<List<Message>> = repository.getDisplayedMessages()
    val savedGames: LiveData<List<GameState>> = repository.getAllSavedGames()
    
    private val _userChoices = MutableLiveData<MutableMap<Int, Int>>(mutableMapOf())
    val userChoices: LiveData<MutableMap<Int, Int>> = _userChoices
    
    private val _saveLoadResult = MutableLiveData<SaveLoadResult>()
    val saveLoadResult: LiveData<SaveLoadResult> = _saveLoadResult
    
    sealed class SaveLoadResult {
        object Idle : SaveLoadResult()
        object SaveSuccess : SaveLoadResult()
        object LoadSuccess : SaveLoadResult()
        data class Error(val message: String) : SaveLoadResult()
    }
    
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
            
            // Clear user choices
            _userChoices.value = mutableMapOf()
            
            // Re-initialize
            initializeGame()
        }
    }
    
    /**
     * Handle user choice selection
     */
    fun selectChoice(messageId: Int, choiceId: Int) {
        val currentChoices = _userChoices.value ?: mutableMapOf()
        currentChoices[messageId] = choiceId
        _userChoices.value = currentChoices
    }
    
    /**
     * Save current game state
     */
    fun saveGame(saveName: String) {
        viewModelScope.launch {
            try {
                val completedMessageIds = displayedMessages.value?.map { it.id } ?: emptyList()
                val currentMessageId = completedMessageIds.lastOrNull() ?: 0
                val choices = _userChoices.value ?: mutableMapOf()
                
                repository.saveGameState(
                    saveName = saveName,
                    currentMessageId = currentMessageId,
                    userChoices = choices,
                    completedMessages = completedMessageIds
                )
                
                _saveLoadResult.value = SaveLoadResult.SaveSuccess
            } catch (e: Exception) {
                _saveLoadResult.value = SaveLoadResult.Error("Failed to save game: ${e.message}")
            }
        }
    }
    
    /**
     * Load saved game state
     */
    fun loadGame(gameStateId: Int) {
        viewModelScope.launch {
            try {
                val gameState = repository.loadGameState(gameStateId)
                if (gameState != null) {
                    // Cancel current work
                    val workManager = WorkManager.getInstance(getApplication())
                    workManager.cancelAllWorkByTag(WORK_TAG)
                    
                    // Restore user choices
                    _userChoices.value = gameState.userChoices.toMutableMap()
                    
                    // Restore messages based on completed list
                    // Note: In production, you'd need to restore message states in the database
                    
                    _saveLoadResult.value = SaveLoadResult.LoadSuccess
                } else {
                    _saveLoadResult.value = SaveLoadResult.Error("Save not found")
                }
            } catch (e: Exception) {
                _saveLoadResult.value = SaveLoadResult.Error("Failed to load game: ${e.message}")
            }
        }
    }
    
    /**
     * Reset save/load result state
     */
    fun resetSaveLoadResult() {
        _saveLoadResult.value = SaveLoadResult.Idle
    }
    
    companion object {
        private const val WORK_TAG = "notification_thriller_messages"
    }
}
