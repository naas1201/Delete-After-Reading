package com.example.notificationthriller.data

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * DAO for tracking choice consequences and their long-term impact
 * Enables AAA-level choice tracking and memory system
 */
@Dao
interface ChoiceConsequenceDao {
    @Query("SELECT * FROM choice_consequences ORDER BY timestamp DESC")
    fun getAllConsequences(): LiveData<List<ChoiceConsequence>>

    @Query("SELECT * FROM choice_consequences WHERE messageId = :messageId")
    suspend fun getConsequencesForMessage(messageId: Int): List<ChoiceConsequence>

    @Query("SELECT * FROM choice_consequences WHERE characterAffected = :characterName ORDER BY timestamp DESC")
    suspend fun getConsequencesForCharacter(characterName: String): List<ChoiceConsequence>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsequence(consequence: ChoiceConsequence)

    @Query("UPDATE choice_consequences SET wasRemembered = 1 WHERE id = :consequenceId")
    suspend fun markAsRemembered(consequenceId: Int)

    @Query("SELECT * FROM choice_consequences WHERE wasRemembered = 0 AND characterAffected = :characterName LIMIT 3")
    suspend fun getUnrememberedConsequences(characterName: String): List<ChoiceConsequence>

    @Query("DELETE FROM choice_consequences")
    suspend fun clearAll()
}

/**
 * DAO for character relationship management
 */
@Dao
interface CharacterRelationshipDao {
    @Query("SELECT * FROM character_relationships ORDER BY trustLevel DESC")
    fun getAllRelationships(): LiveData<List<CharacterRelationship>>

    @Query("SELECT * FROM character_relationships WHERE characterName = :name")
    suspend fun getRelationship(name: String): CharacterRelationship?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateRelationship(relationship: CharacterRelationship)

    @Query("UPDATE character_relationships SET trustLevel = :newTrust WHERE characterName = :name")
    suspend fun updateTrust(
        name: String,
        newTrust: Int,
    )

    @Query("UPDATE character_relationships SET relationshipStatus = :status WHERE characterName = :name")
    suspend fun updateStatus(
        name: String,
        status: String,
    )

    @Query("UPDATE character_relationships SET isAlive = 0 WHERE characterName = :name")
    suspend fun markCharacterDead(name: String)

    @Query("SELECT * FROM character_relationships WHERE trustLevel >= 80")
    suspend fun getTrustedAllies(): List<CharacterRelationship>

    @Query("SELECT * FROM character_relationships WHERE trustLevel <= 20")
    suspend fun getHostileCharacters(): List<CharacterRelationship>

    @Query("DELETE FROM character_relationships")
    suspend fun clearAll()
}

/**
 * DAO for story state tracking
 */
@Dao
interface StoryStateDao {
    @Query("SELECT * FROM story_state WHERE id = 1")
    fun getStoryState(): LiveData<StoryState>

    @Query("SELECT * FROM story_state WHERE id = 1")
    suspend fun getStoryStateOnce(): StoryState?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateStoryState(state: StoryState)

    @Query("UPDATE story_state SET moralityScore = :score WHERE id = 1")
    suspend fun updateMorality(score: Int)

    @Query("UPDATE story_state SET cautionScore = :score WHERE id = 1")
    suspend fun updateCaution(score: Int)

    @Query("UPDATE story_state SET conspiracyDepth = :depth WHERE id = 1")
    suspend fun updateConspiracyDepth(depth: Int)

    @Query("UPDATE story_state SET currentChapter = :chapter WHERE id = 1")
    suspend fun updateChapter(chapter: Int)

    @Query("DELETE FROM story_state")
    suspend fun clearAll()
}

/**
 * DAO for achievement system
 */
@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements ORDER BY unlockedTimestamp DESC")
    fun getAllAchievements(): LiveData<List<Achievement>>

    @Query("SELECT * FROM achievements WHERE isUnlocked = 1")
    fun getUnlockedAchievements(): LiveData<List<Achievement>>

    @Query("SELECT * FROM achievements WHERE isUnlocked = 0 AND isHidden = 0")
    fun getLockedVisibleAchievements(): LiveData<List<Achievement>>

    @Query("SELECT * FROM achievements WHERE achievementId = :id")
    suspend fun getAchievement(id: String): Achievement?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: Achievement)

    @Query("UPDATE achievements SET isUnlocked = 1, unlockedTimestamp = :timestamp WHERE achievementId = :id")
    suspend fun unlockAchievement(
        id: String,
        timestamp: Long,
    )

    @Query("SELECT COUNT(*) FROM achievements WHERE isUnlocked = 1")
    suspend fun getUnlockedCount(): Int

    @Query("SELECT COUNT(*) FROM achievements")
    suspend fun getTotalCount(): Int

    @Query("DELETE FROM achievements")
    suspend fun clearProgress()
}

/**
 * DAO for player statistics
 */
@Dao
interface PlayerStatsDao {
    @Query("SELECT * FROM player_stats WHERE id = 1")
    fun getStats(): LiveData<PlayerStats>

    @Query("SELECT * FROM player_stats WHERE id = 1")
    suspend fun getStatsOnce(): PlayerStats?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateStats(stats: PlayerStats)

    @Query("UPDATE player_stats SET totalChoicesMade = totalChoicesMade + 1 WHERE id = 1")
    suspend fun incrementChoicesMade()

    @Query("UPDATE player_stats SET messagesRead = messagesRead + 1 WHERE id = 1")
    suspend fun incrementMessagesRead()

    @Query("UPDATE player_stats SET completedPlaythroughs = completedPlaythroughs + 1 WHERE id = 1")
    suspend fun incrementPlaythroughs()

    @Query("UPDATE player_stats SET totalDeaths = totalDeaths + 1 WHERE id = 1")
    suspend fun incrementDeaths()

    @Query("UPDATE player_stats SET totalPlaytime = :playtime WHERE id = 1")
    suspend fun updatePlaytime(playtime: Long)

    @Query("UPDATE player_stats SET fastestCompletion = :time WHERE id = 1")
    suspend fun updateFastestTime(time: Long)

    @Query("DELETE FROM player_stats")
    suspend fun clearAll()
}
