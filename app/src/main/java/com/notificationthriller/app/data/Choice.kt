package com.notificationthriller.app.data

/**
 * Represents a user choice in branching narrative
 */
data class Choice(
    val id: Int,
    val text: String,
    val nextMessageId: Int? = null, // ID of the next message if this choice is selected
)
