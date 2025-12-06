package com.example.notificationthriller.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data model for a message in the game
 * Represents both the JSON structure and Room database entity
 */
@Entity(tableName = "messages")
data class Message(
    @PrimaryKey
    val id: Int,
    val sender: String,
    val message: String,
    val delaySeconds: Long,
    val isDisplayed: Boolean = false,
    val timestamp: Long = 0L
)
