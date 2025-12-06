package com.example.notificationthriller.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Initializes game data for AAA experience
 * Sets up characters, achievements, and initial state
 */
class GameInitializer(private val context: Context) {
    private val database = AppDatabase.getDatabase(context)

    /**
     * Initialize all game systems for first playthrough
     */
    suspend fun initializeGame() =
        withContext(Dispatchers.IO) {
            initializeCharacters()
            initializeAchievements()
            initializeStoryState()
            initializePlayerStats()
        }

    /**
     * Set up all characters with starting relationships
     */
    private suspend fun initializeCharacters() {
        val characters =
            listOf(
                CharacterRelationship(
                    characterName = "Sarah",
                    trustLevel = 50,
                    relationshipStatus = "Neutral",
                    keychoicesMade = emptyList(),
                    isAlive = true,
                    lastInteractionTime = System.currentTimeMillis(),
                ),
                CharacterRelationship(
                    characterName = "Marcus Chen",
                    trustLevel = 50,
                    relationshipStatus = "Neutral",
                    keychoicesMade = emptyList(),
                    isAlive = true,
                    lastInteractionTime = 0L,
                ),
                CharacterRelationship(
                    characterName = "The Architect",
                    trustLevel = 30, // Starts mysterious
                    relationshipStatus = "Suspicious",
                    keychoicesMade = emptyList(),
                    isAlive = true,
                    lastInteractionTime = 0L,
                ),
                CharacterRelationship(
                    characterName = "Agent Miller",
                    trustLevel = 40,
                    relationshipStatus = "Neutral",
                    keychoicesMade = emptyList(),
                    isAlive = true,
                    lastInteractionTime = 0L,
                ),
                CharacterRelationship(
                    characterName = "Dr. Elena Rodriguez",
                    trustLevel = 55, // Slightly trustworthy from start
                    relationshipStatus = "Neutral",
                    keychoicesMade = emptyList(),
                    isAlive = true,
                    lastInteractionTime = 0L,
                ),
                CharacterRelationship(
                    characterName = "CEO Richard Blackwood",
                    trustLevel = 20, // Corporate suspicion
                    relationshipStatus = "Suspicious",
                    keychoicesMade = emptyList(),
                    isAlive = true,
                    lastInteractionTime = 0L,
                ),
                CharacterRelationship(
                    characterName = "The Whistleblower",
                    trustLevel = 45,
                    relationshipStatus = "Neutral",
                    keychoicesMade = emptyList(),
                    isAlive = true,
                    lastInteractionTime = 0L,
                ),
            )

        characters.forEach { character ->
            database.characterRelationshipDao().insertOrUpdateRelationship(character)
        }
    }

    /**
     * Set up achievement system
     */
    private suspend fun initializeAchievements() {
        val achievements =
            listOf(
                // Story Completion
                Achievement(
                    achievementId = "first_playthrough",
                    title = "The Beginning",
                    description = "Complete your first playthrough",
                    isUnlocked = false,
                    rarity = "Common",
                ),
                Achievement(
                    achievementId = "speedrun",
                    title = "Against The Clock",
                    description = "Complete the story in under 2 hours",
                    isUnlocked = false,
                    rarity = "Rare",
                ),
                // Relationship Achievements
                Achievement(
                    achievementId = "unbreakable_bond",
                    title = "Unbreakable Bond",
                    description = "Earn maximum trust with any character",
                    isUnlocked = false,
                    rarity = "Common",
                ),
                Achievement(
                    achievementId = "universal_love",
                    title = "Universal Love",
                    description = "Earn maximum trust with all characters",
                    isUnlocked = false,
                    rarity = "Legendary",
                ),
                Achievement(
                    achievementId = "lone_wolf_achievement",
                    title = "Lone Wolf",
                    description = "Complete the game trusting no one",
                    isUnlocked = false,
                    rarity = "Epic",
                ),
                Achievement(
                    achievementId = "betrayal",
                    title = "Betrayal",
                    description = "Have a trusted ally turn against you",
                    isUnlocked = false,
                    rarity = "Rare",
                ),
                // Morality Achievements
                Achievement(
                    achievementId = "paragon",
                    title = "Paragon",
                    description = "Maintain unwavering moral standards throughout",
                    isUnlocked = false,
                    rarity = "Epic",
                ),
                Achievement(
                    achievementId = "ruthless",
                    title = "Ruthless Pragmatist",
                    description = "Do whatever it takes to succeed",
                    isUnlocked = false,
                    rarity = "Epic",
                ),
                Achievement(
                    achievementId = "balanced",
                    title = "The Middle Path",
                    description = "Find balance between idealism and pragmatism",
                    isUnlocked = false,
                    rarity = "Rare",
                ),
                Achievement(
                    achievementId = "conflicted",
                    title = "Conflicted Soul",
                    description = "Change your moral stance 5+ times",
                    isUnlocked = false,
                    rarity = "Common",
                ),
                // Choice Achievements
                Achievement(
                    achievementId = "decisive",
                    title = "Decisive Leader",
                    description = "Make 10 major decisions",
                    isUnlocked = false,
                    rarity = "Common",
                ),
                Achievement(
                    achievementId = "master_tactician",
                    title = "Master Tactician",
                    description = "Make 50 choices",
                    isUnlocked = false,
                    rarity = "Rare",
                ),
                // Ending Achievements
                Achievement(
                    achievementId = "perfect_alliance",
                    title = "Perfect Alliance",
                    description = "Unite everyone and save the world",
                    isUnlocked = false,
                    rarity = "Epic",
                ),
                Achievement(
                    achievementId = "idealist_ending",
                    title = "True Idealist",
                    description = "Risk everything for your principles",
                    isUnlocked = false,
                    rarity = "Rare",
                ),
                Achievement(
                    achievementId = "survivor_ending",
                    title = "Ultimate Survivor",
                    description = "Outlast everyone through careful planning",
                    isUnlocked = false,
                    rarity = "Rare",
                ),
                Achievement(
                    achievementId = "all_endings",
                    title = "Every Possible Outcome",
                    description = "Unlock all story endings",
                    isUnlocked = false,
                    rarity = "Legendary",
                ),
                // Secret Achievements
                Achievement(
                    achievementId = "the_truth",
                    title = "The Truth",
                    description = "Discover the real conspiracy behind Project Midnight",
                    isUnlocked = false,
                    isHidden = true,
                    rarity = "Epic",
                ),
                Achievement(
                    achievementId = "puppet_master",
                    title = "Puppet Master",
                    description = "???",
                    isUnlocked = false,
                    isHidden = true,
                    rarity = "Legendary",
                ),
                Achievement(
                    achievementId = "prophet",
                    title = "The Prophet",
                    description = "???",
                    isUnlocked = false,
                    isHidden = true,
                    rarity = "Legendary",
                ),
                Achievement(
                    achievementId = "sacrifice",
                    title = "Ultimate Sacrifice",
                    description = "???",
                    isUnlocked = false,
                    isHidden = true,
                    rarity = "Epic",
                ),
                // Replayability
                Achievement(
                    achievementId = "second_chance",
                    title = "Second Chance",
                    description = "Complete a New Game+ playthrough",
                    isUnlocked = false,
                    rarity = "Rare",
                ),
                Achievement(
                    achievementId = "completionist",
                    title = "Completionist",
                    description = "Discover 100% of game content",
                    isUnlocked = false,
                    rarity = "Legendary",
                ),
                // Fun Achievements
                Achievement(
                    achievementId = "trust_issues",
                    title = "Trust Issues",
                    description = "Maintain low trust with everyone throughout",
                    isUnlocked = false,
                    rarity = "Rare",
                ),
                Achievement(
                    achievementId = "social_butterfly",
                    title = "Social Butterfly",
                    description = "Interact with every character at least once",
                    isUnlocked = false,
                    rarity = "Common",
                ),
                Achievement(
                    achievementId = "detective",
                    title = "Amateur Detective",
                    description = "Find 10 hidden secrets",
                    isUnlocked = false,
                    rarity = "Rare",
                ),
            )

        achievements.forEach { achievement ->
            database.achievementDao().insertAchievement(achievement)
        }
    }

    /**
     * Initialize story state
     */
    private suspend fun initializeStoryState() {
        val initialState =
            StoryState(
                id = 1,
                currentChapter = 1,
                storyPath = "Main",
                moralityScore = 50,
                cautionScore = 50,
                conspiracyDepth = 0,
                majorChoicesMade = emptyList(),
                unlockedEndings = emptyList(),
                playthrough = 1,
                lastSaveTimestamp = System.currentTimeMillis(),
            )

        database.storyStateDao().updateStoryState(initialState)
    }

    /**
     * Initialize player statistics
     */
    private suspend fun initializePlayerStats() {
        val initialStats =
            PlayerStats(
                id = 1,
                totalChoicesMade = 0,
                totalPlaytime = 0L,
                messagesRead = 0,
                completedPlaythroughs = 0,
                fastestCompletion = 0L,
                mostTrustedCharacter = null,
                favoriteEnding = null,
                totalAchievements = 0,
                secretsFound = 0,
                totalDeaths = 0,
            )

        database.playerStatsDao().updateStats(initialStats)
    }

    /**
     * Reset game for new playthrough
     */
    suspend fun resetForNewPlaythrough(keepProgress: Boolean = false) =
        withContext(Dispatchers.IO) {
            if (!keepProgress) {
                // Full reset
                database.choiceConsequenceDao().clearAll()
                database.characterRelationshipDao().clearAll()
                database.storyStateDao().clearAll()
                database.achievementDao().clearProgress()

                // Reinitialize
                initializeGame()
            } else {
                // New Game+ - keep achievements and stats
                database.choiceConsequenceDao().clearAll()
                database.characterRelationshipDao().clearAll()
                database.storyStateDao().clearAll()

                // Reinitialize characters and story
                initializeCharacters()

                // Increment playthrough counter
                val currentStats = database.playerStatsDao().getStatsOnce()
                if (currentStats != null) {
                    database.playerStatsDao().updateStats(
                        currentStats.copy(
                            completedPlaythroughs = currentStats.completedPlaythroughs + 1,
                        ),
                    )
                }

                // Start new story state with increased playthrough number
                val currentState = database.storyStateDao().getStoryStateOnce()
                val newPlaythrough = (currentState?.playthrough ?: 0) + 1
                initializeStoryState()
                database.storyStateDao().updateStoryState(
                    StoryState(
                        id = 1,
                        currentChapter = 1,
                        storyPath = "Main",
                        moralityScore = 50,
                        cautionScore = 50,
                        conspiracyDepth = 0,
                        majorChoicesMade = emptyList(),
                        unlockedEndings = emptyList(),
                        playthrough = newPlaythrough,
                        lastSaveTimestamp = System.currentTimeMillis(),
                    ),
                )
            }
        }
}
