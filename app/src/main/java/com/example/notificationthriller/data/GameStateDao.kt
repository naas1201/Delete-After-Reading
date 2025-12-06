package com.example.notificationthriller.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete

/**
 * Data Access Object for GameState entities
 */
@Dao
interface GameStateDao {
    
    @Query("SELECT * FROM game_states ORDER BY saveTimestamp DESC")
    fun getAllSavedGames(): LiveData<List<GameState>>
    
    @Query("SELECT * FROM game_states WHERE id = :id")
    suspend fun getGameStateById(id: Int): GameState?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameState(gameState: GameState): Long
    
    @Delete
    suspend fun deleteGameState(gameState: GameState)
    
    @Query("DELETE FROM game_states")
    suspend fun deleteAll()
}
