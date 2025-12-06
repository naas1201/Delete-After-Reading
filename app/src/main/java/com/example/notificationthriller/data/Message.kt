package com.example.notificationthriller.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

/**
 * Data model for a message in the game
 * Represents both the JSON structure and Room database entity
 */
@Entity(tableName = "messages")
@TypeConverters(Converters::class)
data class Message(
    @PrimaryKey
    val id: Int,
    val sender: String,
    val message: String,
    val delaySeconds: Long,
    val isDisplayed: Boolean = false,
    val timestamp: Long = 0L,
    val choices: List<Choice>? = null,  // Choices for branching narrative
    val parentChoiceId: Int? = null,     // Which choice led to this message
    val isChoiceBranch: Boolean = false  // Whether this message requires user choice
)
