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
import com.example.notificationthriller.data.*
import com.example.notificationthriller.engine.ChoiceEngine
import com.example.notificationthriller.workers.MessageNotificationWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * ViewModel for the chat screen following MVVM architecture
 * Manages message data and schedules notifications via WorkManager
 * Includes AAA-level branching narratives with meaningful consequences
 */
class ChatViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = MessageRepository(application)
    private val database = AppDatabase.getDatabase(application)
    private val choiceEngine = ChoiceEngine(application)
    private val gameInitializer = GameInitializer(application)
    
    val displayedMessages: LiveData<List<Message>> = repository.getDisplayedMessages()
    val savedGames: LiveData<List<GameState>> = repository.getAllSavedGames()
    val characterRelationships: LiveData<List<CharacterRelationship>> = 
        database.characterRelationshipDao().getAllRelationships()
    val storyState: LiveData<StoryState> = database.storyStateDao().getStoryState()
    val achievements: LiveData<List<Achievement>> = database.achievementDao().getAllAchievements()
    val playerStats: LiveData<PlayerStats> = database.playerStatsDao().getStats()
    
    private val _userChoices = MutableLiveData<MutableMap<Int, Int>>(mutableMapOf())
    val userChoices: LiveData<MutableMap<Int, Int>> = _userChoices
    
    private val _saveLoadResult = MutableLiveData<SaveLoadResult>()
    val saveLoadResult: LiveData<SaveLoadResult> = _saveLoadResult
    
    private val _consequenceNotifications = MutableLiveData<List<ConsequenceNotification>>()
    val consequenceNotifications: LiveData<List<ConsequenceNotification>> = _consequenceNotifications
    
    private val _choiceImpactSummary = MutableLiveData<String>()
    val choiceImpactSummary: LiveData<String> = _choiceImpactSummary
    
    sealed class SaveLoadResult {
        object Idle : SaveLoadResult()
        object SaveSuccess : SaveLoadResult()
        object LoadSuccess : SaveLoadResult()
        data class Error(val message: String) : SaveLoadResult()
    }
    
    /**
     * Initialize the game by loading messages from JSON and scheduling notifications
     * Now includes AAA-level systems: characters, achievements, story state
     */
    fun initializeGame() {
        viewModelScope.launch {
            // Initialize AAA game systems
            gameInitializer.initializeGame()
            
            // Load messages from JSON
            val messages = repository.loadMessagesFromJson(R.raw.game_messages)
            
            // Initialize database
            repository.initializeDatabase(messages)
            
            // Schedule notifications for all messages
            scheduleNotifications(messages)
            
            // Track game start
            database.playerStatsDao().incrementMessagesRead()
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
            
            // Full reset of AAA systems
            gameInitializer.resetForNewPlaythrough(keepProgress = false)
            
            // Re-initialize
            initializeGame()
        }
    }
    
    /**
     * Start New Game+ (keep achievements and stats)
     */
    fun startNewGamePlus() {
        viewModelScope.launch {
            // Cancel all pending work
            val workManager = WorkManager.getInstance(getApplication())
            workManager.cancelAllWorkByTag(WORK_TAG)
            
            // Clear user choices
            _userChoices.value = mutableMapOf()
            
            // Reset but keep progress
            gameInitializer.resetForNewPlaythrough(keepProgress = true)
            
            // Re-initialize
            initializeGame()
        }
    }
    
    /**
     * Get dynamic message text based on player's history
     */
    fun getDynamicMessageText(message: Message): String {
        val relationships = characterRelationships.value?.associateBy { it.characterName } ?: emptyMap()
        val currentStoryState = storyState.value ?: StoryState(id = 1)
        val playerChoices = _userChoices.value ?: emptyMap()
        
        return choiceEngine.getDynamicMessageText(
            baseMessage = message,
            playerChoices = playerChoices,
            relationships = relationships,
            storyState = currentStoryState
        )
    }
    
    /**
     * Get player's current playstyle summary
     */
    fun getPlaystyleSummary(): String {
        val currentStoryState = storyState.value ?: return "No data yet"
        
        val morality = when {
            currentStoryState.moralityScore >= 80 -> "Idealistic"
            currentStoryState.moralityScore <= 20 -> "Ruthless"
            else -> "Pragmatic"
        }
        
        val caution = when {
            currentStoryState.cautionScore >= 80 -> "Cautious"
            currentStoryState.cautionScore <= 20 -> "Reckless"
            else -> "Balanced"
        }
        
        return "$morality • $caution"
    }
    
    /**
     * Get completion percentage
     */
    fun getCompletionPercentage(): Int {
        val messagesRead = playerStats.value?.messagesRead ?: 0
        val totalMessages = displayedMessages.value?.size ?: 0
        return if (totalMessages > 0) {
            ((messagesRead.toFloat() / totalMessages) * 100).toInt()
        } else {
            0
        }
    }
    
    /**
     * Handle user choice selection with AAA-level consequence tracking
     */
    fun selectChoice(messageId: Int, choiceId: Int, choiceText: String) {
        viewModelScope.launch {
            // Track the choice
            val currentChoices = _userChoices.value ?: mutableMapOf()
            currentChoices[messageId] = choiceId
            _userChoices.value = currentChoices
            
            // Get current game state
            val relationships = database.characterRelationshipDao().getAllRelationships().value?.associateBy { it.characterName } ?: emptyMap()
            val currentStoryState = database.storyStateDao().getStoryStateOnce() 
                ?: StoryState(id = 1)
            
            // Process choice through the choice engine
            val result = choiceEngine.processChoice(
                messageId = messageId,
                choiceId = choiceId,
                choiceText = choiceText,
                currentRelationships = relationships,
                storyState = currentStoryState
            )
            
            // Update database with consequences
            result.consequences.forEach { consequence ->
                database.choiceConsequenceDao().insertConsequence(consequence)
            }
            
            // Update character relationships
            result.updatedRelationships.forEach { (_, relationship) ->
                database.characterRelationshipDao().insertOrUpdateRelationship(relationship)
            }
            
            // Update story state
            database.storyStateDao().updateStoryState(result.updatedStoryState)
            
            // Unlock achievements
            result.unlockedAchievements.forEach { achievementId ->
                val achievement = achievementId.split(" - ").firstOrNull()
                if (achievement != null) {
                    // Try to unlock by parsing the achievement
                    database.achievementDao().unlockAchievement(achievement, System.currentTimeMillis())
                }
            }
            
            // Update player stats
            database.playerStatsDao().incrementChoicesMade()
            
            // Show consequence notifications to player
            _consequenceNotifications.value = result.notifications
            
            // Generate impact summary
            if (result.notifications.isNotEmpty()) {
                val summary = generateChoiceImpactSummary(result)
                _choiceImpactSummary.value = summary
            }
        }
    }
    
    /**
     * Generate a human-readable summary of choice impact
     */
    private fun generateChoiceImpactSummary(result: com.example.notificationthriller.engine.ChoiceResult): String {
        val parts = mutableListOf<String>()
        
        // Relationship changes
        val relationshipChanges = result.notifications.filter { 
            it.type == ConsequenceType.TRUST_GAINED || it.type == ConsequenceType.TRUST_LOST 
        }
        if (relationshipChanges.isNotEmpty()) {
            parts.add("Relationships changed: ${relationshipChanges.size} character(s)")
        }
        
        // Endings unlocked
        val endingsUnlocked = result.notifications.count { it.type == ConsequenceType.ENDING_UNLOCKED }
        if (endingsUnlocked > 0) {
            parts.add("New endings available: $endingsUnlocked")
        }
        
        // Achievements
        if (result.unlockedAchievements.isNotEmpty()) {
            parts.add("Achievements unlocked: ${result.unlockedAchievements.size}")
        }
        
        return if (parts.isEmpty()) {
            "Your choice has been noted."
        } else {
            parts.joinToString(" • ")
        }
    }
    
    /**
     * Clear consequence notifications after they've been displayed
     */
    fun clearConsequenceNotifications() {
        _consequenceNotifications.value = emptyList()
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
