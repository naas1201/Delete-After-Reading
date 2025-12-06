package com.example.notificationthriller.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

/**
 * Entity representing a saved game state
 */
@Entity(tableName = "game_states")
@TypeConverters(Converters::class)
data class GameState(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val saveName: String,
    val saveTimestamp: Long,
    val currentMessageId: Int,
    val userChoices: Map<Int, Int>, // Map of messageId to choiceId
    val completedMessages: List<Int> // List of completed message IDs
)
