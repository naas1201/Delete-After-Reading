package com.notificationthriller.app.data

import android.content.Context
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

/**
 * Repository pattern for managing message data
 * Handles both JSON parsing and database operations
 */
class MessageRepository(private val context: Context) {
    private val messageDao: MessageDao = AppDatabase.getDatabase(context).messageDao()
    private val gson = Gson()

    /**
     * Get all displayed messages as LiveData
     */
    fun getDisplayedMessages(): LiveData<List<Message>> {
        return messageDao.getDisplayedMessages()
    }

    /**
     * Load messages from local JSON file
     */
    suspend fun loadMessagesFromJson(resourceId: Int): List<Message> =
        withContext(Dispatchers.IO) {
            try {
                val inputStream = context.resources.openRawResource(resourceId)
                val reader = InputStreamReader(inputStream)
                val type = object : TypeToken<List<Message>>() {}.type
                val messages: List<Message> = gson.fromJson(reader, type)
                reader.close()
                messages
            } catch (e: Exception) {
                android.util.Log.e("MessageRepository", "Error loading messages from JSON", e)
                emptyList()
            }
        }

    /**
     * Initialize database with messages from JSON
     */
    suspend fun initializeDatabase(messages: List<Message>) =
        withContext(Dispatchers.IO) {
            messageDao.deleteAll()
            messageDao.insertAll(messages)
        }

    /**
     * Get message by ID
     */
    suspend fun getMessageById(messageId: Int): Message? =
        withContext(Dispatchers.IO) {
            messageDao.getMessageById(messageId)
        }

    /**
     * Mark message as displayed with current timestamp
     */
    suspend fun markMessageAsDisplayed(messageId: Int) =
        withContext(Dispatchers.IO) {
            val timestamp = System.currentTimeMillis()
            messageDao.markMessageAsDisplayed(messageId, timestamp)
        }

    /**
     * Update message
     */
    suspend fun updateMessage(message: Message) =
        withContext(Dispatchers.IO) {
            messageDao.updateMessage(message)
        }

    // Game State operations
    private val gameStateDao: GameStateDao = AppDatabase.getDatabase(context).gameStateDao()

    /**
     * Get all saved games
     */
    fun getAllSavedGames(): LiveData<List<GameState>> {
        return gameStateDao.getAllSavedGames()
    }

    /**
     * Save current game state
     */
    suspend fun saveGameState(
        saveName: String,
        currentMessageId: Int,
        userChoices: Map<Int, Int>,
        completedMessages: List<Int>,
    ): Long =
        withContext(Dispatchers.IO) {
            val gameState =
                GameState(
                    saveName = saveName,
                    saveTimestamp = System.currentTimeMillis(),
                    currentMessageId = currentMessageId,
                    userChoices = userChoices,
                    completedMessages = completedMessages,
                )
            gameStateDao.insertGameState(gameState)
        }

    /**
     * Load game state by ID
     */
    suspend fun loadGameState(id: Int): GameState? =
        withContext(Dispatchers.IO) {
            gameStateDao.getGameStateById(id)
        }

    /**
     * Delete game state
     */
    suspend fun deleteGameState(gameState: GameState) =
        withContext(Dispatchers.IO) {
            gameStateDao.deleteGameState(gameState)
        }
}
