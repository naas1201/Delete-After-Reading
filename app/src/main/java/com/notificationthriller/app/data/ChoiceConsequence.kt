package com.notificationthriller.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tracks the consequences of player choices throughout the game
 * This creates a deep, meaningful choice system where decisions have long-term impact
 */
@Entity(tableName = "choice_consequences")
data class ChoiceConsequence(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val messageId: Int, // Which message presented the choice
    val choiceId: Int, // Which choice was made
    val characterAffected: String, // Character whose relationship changed
    val relationshipDelta: Int, // Change in relationship (-100 to +100)
    val trustDelta: Int, // Change in trust level
    val storyBranchUnlocked: String? = null, // New story branch unlocked
    val achievementUnlocked: String? = null, // Achievement earned
    val timestamp: Long, // When choice was made
    val wasRemembered: Boolean = false, // Has this been referenced later?
)

/**
 * Character relationship state
 * Tracks how each character views the player based on choices
 */
@Entity(tableName = "character_relationships")
data class CharacterRelationship(
    @PrimaryKey
    val characterName: String,
    val trustLevel: Int = 50, // 0-100, affects dialogue and endings
    val relationshipStatus: String = "Neutral", // Neutral, Ally, Friend, Romance, Suspicious, Hostile
    val keychoicesMade: List<Int> = emptyList(), // Important choices involving this character
    val isAlive: Boolean = true, // Some choices may lead to character deaths
    val lastInteractionTime: Long = 0L,
)

/**
 * Story state tracking for branching narratives
 * Enables complex multi-path storytelling with consequences
 */
@Entity(tableName = "story_state")
data class StoryState(
    @PrimaryKey
    val id: Int = 1, // Singleton - only one story state
    val currentChapter: Int = 1,
    val storyPath: String = "Main", // Main, Resistance, Infiltrator, Negotiator, etc.
    val moralityScore: Int = 50, // 0 (Ruthless) to 100 (Idealistic)
    val cautionScore: Int = 50, // 0 (Reckless) to 100 (Careful)
    val conspiracyDepth: Int = 0, // How much of the conspiracy player has uncovered (0-100)
    val majorChoicesMade: List<String> = emptyList(), // Tags for major plot decisions
    val unlockedEndings: List<String> = emptyList(), // Which endings are now possible
    val playthrough: Int = 1, // Current playthrough number for NG+
    val lastSaveTimestamp: Long = 0L,
)

/**
 * Achievement/trophy system for replayability
 */
@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey
    val achievementId: String,
    val title: String,
    val description: String,
    val isUnlocked: Boolean = false,
    val unlockedTimestamp: Long = 0L,
    val isHidden: Boolean = false, // Hidden until unlocked
    val rarity: String = "Common", // Common, Rare, Epic, Legendary
)

/**
 * Player statistics for engagement and replayability
 */
@Entity(tableName = "player_stats")
data class PlayerStats(
    @PrimaryKey
    val id: Int = 1, // Singleton
    val totalChoicesMade: Int = 0,
    val totalPlaytime: Long = 0L, // In milliseconds
    val messagesRead: Int = 0,
    val completedPlaythroughs: Int = 0,
    val fastestCompletion: Long = 0L,
    val mostTrustedCharacter: String? = null,
    val favoriteEnding: String? = null,
    val totalAchievements: Int = 0,
    val secretsFound: Int = 0,
    val totalDeaths: Int = 0, // Counts failed playthroughs
)

/**
 * Dynamic message variant based on previous choices
 * Allows messages to change text based on player's history
 */
data class MessageVariant(
    val baseMessageId: Int,
    val variantText: String,
    val requiredChoices: Map<Int, Int>, // MessageId to ChoiceId that must have been made
    val requiredRelationship: Map<String, Int>? = null, // Character to minimum trust level
    val requiredMorality: IntRange? = null, // Morality score range
)

/**
 * Consequence notification shown to player
 * Makes it clear that their choice had an impact
 */
data class ConsequenceNotification(
    val title: String,
    val message: String,
    val type: ConsequenceType,
    val characterInvolved: String? = null,
)

enum class ConsequenceType {
    RELATIONSHIP_IMPROVED,
    RELATIONSHIP_DAMAGED,
    TRUST_GAINED,
    TRUST_LOST,
    STORY_BRANCH_UNLOCKED,
    ACHIEVEMENT_UNLOCKED,
    CHARACTER_REMEMBERS,
    ENDING_UNLOCKED,
    ENDING_CLOSED,
    SECRET_REVEALED,
    DANGER_INCREASED,
}

/**
 * Choice impact summary for end-game review
 * Shows player how their choices shaped the story
 */
data class ChoiceImpactSummary(
    val choiceDescription: String,
    val immediateConsequence: String,
    val longTermConsequence: List<String>,
    val charactersAffected: List<String>,
    val alternativeOutcome: String, // What would have happened with different choice
)
