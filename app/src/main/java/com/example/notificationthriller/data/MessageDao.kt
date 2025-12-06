package com.example.notificationthriller.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

/**
 * Data Access Object for Message entities
 */
@Dao
interface MessageDao {
    @Query("SELECT * FROM messages ORDER BY timestamp ASC")
    fun getAllMessages(): LiveData<List<Message>>

    @Query("SELECT * FROM messages WHERE isDisplayed = 1 ORDER BY timestamp ASC")
    fun getDisplayedMessages(): LiveData<List<Message>>

    @Query("SELECT * FROM messages WHERE id = :messageId")
    suspend fun getMessageById(messageId: Int): Message?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(messages: List<Message>)

    @Update
    suspend fun updateMessage(message: Message)

    @Query("DELETE FROM messages")
    suspend fun deleteAll()

    @Query("UPDATE messages SET isDisplayed = 1, timestamp = :timestamp WHERE id = :messageId")
    suspend fun markMessageAsDisplayed(
        messageId: Int,
        timestamp: Long,
    )
}
