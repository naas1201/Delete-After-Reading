package com.example.notificationthriller.engine

import android.content.Context
import com.example.notificationthriller.data.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Advanced choice engine that creates deep, meaningful player agency
 * This is what makes the game feel AAA-level with choices that truly matter
 * 
 * Key Features:
 * - Long-term consequence tracking
 * - Dynamic character relationships
 * - Branching narratives that remember player decisions
 * - Achievement and replay value systems
 */
class ChoiceEngine(private val context: Context) {
    
    // Character relationship templates
    private val characterTraits = mapOf(
        "Sarah" to CharacterTraits(
            valuesHonesty = true,
            valuesCaution = true,
            remembersBetrayal = true,
            canBeSaved = true
        ),
        "Marcus Chen" to CharacterTraits(
            valuesHonesty = true,
            valuesCaution = false,
            remembersBetrayal = true,
            canBeSaved = true
        ),
        "The Architect" to CharacterTraits(
            valuesHonesty = false,
            valuesCaution = true,
            remembersBetrayal = true,
            canBeSaved = false
        ),
        "Agent Miller" to CharacterTraits(
            valuesHonesty = true,
            valuesCaution = true,
            remembersBetrayal = true,
            canBeSaved = true
        ),
        "Dr. Elena Rodriguez" to CharacterTraits(
            valuesHonesty = true,
            valuesCaution = true,
            remembersBetrayal = false,
            canBeSaved = true
        )
    )
    
    /**
     * Process a choice and generate all consequences
     * This is where player agency becomes reality
     */
    fun processChoice(
        messageId: Int,
        choiceId: Int,
        choiceText: String,
        currentRelationships: Map<String, CharacterRelationship>,
        storyState: StoryState
    ): ChoiceResult {
        
        val consequences = mutableListOf<ChoiceConsequence>()
        val notifications = mutableListOf<ConsequenceNotification>()
        val updatedRelationships = mutableMapOf<String, CharacterRelationship>()
        var updatedStoryState = storyState.copy()
        val unlockedAchievements = mutableListOf<String>()
        
        // Analyze choice impact based on context
        val choiceImpact = analyzeChoiceImpact(messageId, choiceId, choiceText)
        
        // Update morality and caution scores
        updatedStoryState = updatedStoryState.copy(
            moralityScore = (updatedStoryState.moralityScore + choiceImpact.moralityDelta).coerceIn(0, 100),
            cautionScore = (updatedStoryState.cautionScore + choiceImpact.cautionDelta).coerceIn(0, 100)
        )
        
        // Process character relationship changes
        choiceImpact.characterEffects.forEach { (characterName, effect) ->
            val currentRel = currentRelationships[characterName]
            if (currentRel != null) {
                val newTrust = (currentRel.trustLevel + effect.trustDelta).coerceIn(0, 100)
                val newStatus = determineRelationshipStatus(newTrust, effect)
                
                val updatedRel = currentRel.copy(
                    trustLevel = newTrust,
                    relationshipStatus = newStatus,
                    keychoicesMade = currentRel.keychoicesMade + choiceId,
                    lastInteractionTime = System.currentTimeMillis()
                )
                
                updatedRelationships[characterName] = updatedRel
                
                // Create consequence record
                val consequence = ChoiceConsequence(
                    messageId = messageId,
                    choiceId = choiceId,
                    characterAffected = characterName,
                    relationshipDelta = effect.trustDelta,
                    trustDelta = effect.trustDelta,
                    storyBranchUnlocked = effect.branchUnlocked,
                    achievementUnlocked = effect.achievementUnlocked,
                    timestamp = System.currentTimeMillis()
                )
                consequences.add(consequence)
                
                // Generate notification if significant change
                if (kotlin.math.abs(effect.trustDelta) >= 10) {
                    notifications.add(
                        ConsequenceNotification(
                            title = if (effect.trustDelta > 0) "Trust Gained" else "Trust Lost",
                            message = generateConsequenceMessage(characterName, effect.trustDelta, newStatus),
                            type = if (effect.trustDelta > 0) ConsequenceType.TRUST_GAINED else ConsequenceType.TRUST_LOST,
                            characterInvolved = characterName
                        )
                    )
                }
            }
        }
        
        // Check for unlocked endings based on relationship states
        val unlockedEndings = checkUnlockedEndings(updatedRelationships, updatedStoryState)
        if (unlockedEndings.isNotEmpty()) {
            updatedStoryState = updatedStoryState.copy(
                unlockedEndings = updatedStoryState.unlockedEndings + unlockedEndings
            )
            unlockedEndings.forEach { ending ->
                notifications.add(
                    ConsequenceNotification(
                        title = "New Ending Available",
                        message = "Your choices have unlocked the \"$ending\" ending path",
                        type = ConsequenceType.ENDING_UNLOCKED
                    )
                )
            }
        }
        
        // Check for achievements
        val achievements = checkAchievements(messageId, choiceId, updatedRelationships, updatedStoryState)
        unlockedAchievements.addAll(achievements)
        achievements.forEach { achievement ->
            notifications.add(
                ConsequenceNotification(
                    title = "Achievement Unlocked!",
                    message = achievement,
                    type = ConsequenceType.ACHIEVEMENT_UNLOCKED
                )
            )
        }
        
        return ChoiceResult(
            consequences = consequences,
            notifications = notifications,
            updatedRelationships = updatedRelationships,
            updatedStoryState = updatedStoryState,
            unlockedAchievements = unlockedAchievements
        )
    }
    
    /**
     * Generate dynamic message text based on previous choices
     * Makes the game remember player decisions
     */
    fun getDynamicMessageText(
        baseMessage: Message,
        playerChoices: Map<Int, Int>,
        relationships: Map<String, CharacterRelationship>,
        storyState: StoryState
    ): String {
        var messageText = baseMessage.message
        
        // Add personalization based on relationship
        val sender = baseMessage.sender
        val relationship = relationships[sender]
        
        if (relationship != null) {
            messageText = when {
                relationship.trustLevel >= 80 -> {
                    // High trust adds warmth
                    addTrustfulTone(messageText, sender, relationship.relationshipStatus)
                }
                relationship.trustLevel <= 20 -> {
                    // Low trust adds coldness or hostility
                    addSuspiciousTone(messageText, sender)
                }
                else -> messageText
            }
        }
        
        // Reference past choices if relevant
        val relevantPastChoices = findRelevantPastChoices(baseMessage.id, playerChoices)
        if (relevantPastChoices.isNotEmpty()) {
            messageText = addChoiceReference(messageText, relevantPastChoices, relationships)
        }
        
        return messageText
    }
    
    /**
     * Analyze the philosophical and practical impact of a choice
     */
    private fun analyzeChoiceImpact(messageId: Int, choiceId: Int, choiceText: String): ChoiceImpact {
        // Analyze the choice text for keywords indicating player's approach
        val isHonest = choiceText.contains("truth", ignoreCase = true) || 
                      choiceText.contains("tell", ignoreCase = true)
        val isCautious = choiceText.contains("careful", ignoreCase = true) || 
                        choiceText.contains("wait", ignoreCase = true) ||
                        choiceText.contains("safe", ignoreCase = true)
        val isAggressive = choiceText.contains("fight", ignoreCase = true) || 
                          choiceText.contains("attack", ignoreCase = true) ||
                          choiceText.contains("stop", ignoreCase = true)
        val isEmpathetic = choiceText.contains("help", ignoreCase = true) || 
                          choiceText.contains("save", ignoreCase = true) ||
                          choiceText.contains("safe", ignoreCase = true)
        
        val moralityDelta = when {
            isEmpathetic -> 5
            isAggressive -> -5
            isHonest -> 3
            else -> 0
        }
        
        val cautionDelta = when {
            isCautious -> 5
            isAggressive -> -5
            else -> 0
        }
        
        // Determine which characters are affected
        val characterEffects = determineCharacterEffects(messageId, choiceId, choiceText)
        
        return ChoiceImpact(
            moralityDelta = moralityDelta,
            cautionDelta = cautionDelta,
            characterEffects = characterEffects
        )
    }
    
    /**
     * Determine how choice affects specific characters
     */
    private fun determineCharacterEffects(
        messageId: Int,
        choiceId: Int,
        choiceText: String
    ): Map<String, CharacterEffect> {
        val effects = mutableMapOf<String, CharacterEffect>()
        
        // Map choices to character reactions based on their traits
        when (messageId) {
            2 -> { // First major choice about trust
                when (choiceId) {
                    1 -> { // "Who is this?"
                        effects["Sarah"] = CharacterEffect(
                            trustDelta = -5,
                            note = "Sarah appreciates your caution but is slightly hurt"
                        )
                    }
                    2 -> { // "I'm listening"
                        effects["Sarah"] = CharacterEffect(
                            trustDelta = 10,
                            note = "Sarah deeply appreciates your immediate trust"
                        )
                    }
                    3 -> { // "This feels like a scam"
                        effects["Sarah"] = CharacterEffect(
                            trustDelta = -10,
                            note = "Sarah is hurt by your skepticism"
                        )
                    }
                }
            }
            // Add more message-specific effects here
        }
        
        return effects
    }
    
    /**
     * Determine relationship status based on trust level
     */
    private fun determineRelationshipStatus(trustLevel: Int, effect: CharacterEffect): String {
        return when {
            trustLevel >= 90 -> "Best Friend"
            trustLevel >= 75 -> "Trusted Ally"
            trustLevel >= 60 -> "Friend"
            trustLevel >= 40 -> "Neutral"
            trustLevel >= 25 -> "Suspicious"
            trustLevel >= 10 -> "Distrustful"
            else -> "Hostile"
        }
    }
    
    /**
     * Check if player's choices have unlocked special endings
     */
    private fun checkUnlockedEndings(
        relationships: Map<String, CharacterRelationship>,
        storyState: StoryState
    ): List<String> {
        val unlockedEndings = mutableListOf<String>()
        
        // Perfect Ending: All allies trusted, high morality
        if (relationships.values.all { it.trustLevel >= 70 } && storyState.moralityScore >= 80) {
            unlockedEndings.add("Perfect Alliance")
        }
        
        // Lone Wolf: Low trust with everyone but succeeded
        if (relationships.values.all { it.trustLevel <= 30 } && storyState.conspiracyDepth >= 80) {
            unlockedEndings.add("Lone Wolf")
        }
        
        // Pragmatist: Balanced approach
        if (storyState.moralityScore in 40..60 && storyState.cautionScore in 40..60) {
            unlockedEndings.add("Pragmatist")
        }
        
        // Idealist: High morality, took risks
        if (storyState.moralityScore >= 80 && storyState.cautionScore <= 30) {
            unlockedEndings.add("Idealist")
        }
        
        // Survivor: High caution, made it through
        if (storyState.cautionScore >= 80) {
            unlockedEndings.add("Survivor")
        }
        
        return unlockedEndings
    }
    
    /**
     * Check if choice unlocked any achievements
     */
    private fun checkAchievements(
        messageId: Int,
        choiceId: Int,
        relationships: Map<String, CharacterRelationship>,
        storyState: StoryState
    ): List<String> {
        val achievements = mutableListOf<String>()
        
        // Trust-based achievements
        relationships.values.firstOrNull { it.trustLevel >= 90 }?.let {
            achievements.add("Unbreakable Bond - Earned ${it.characterName}'s complete trust")
        }
        
        // Morality achievements
        when {
            storyState.moralityScore >= 90 -> achievements.add("Paragon - Maintained unwavering moral standards")
            storyState.moralityScore <= 10 -> achievements.add("Ruthless - Did whatever it took to succeed")
        }
        
        // Choice count achievements
        if (storyState.majorChoicesMade.size == 10) {
            achievements.add("Decisive - Made 10 major decisions")
        }
        
        return achievements
    }
    
    /**
     * Generate a personalized consequence message
     */
    private fun generateConsequenceMessage(
        characterName: String,
        trustDelta: Int,
        newStatus: String
    ): String {
        return when {
            trustDelta >= 20 -> "$characterName deeply appreciates your choice. You are now considered a $newStatus."
            trustDelta >= 10 -> "$characterName's trust in you has grown. Status: $newStatus."
            trustDelta <= -20 -> "$characterName feels betrayed by your decision. You are now seen as $newStatus."
            trustDelta <= -10 -> "$characterName's trust has diminished. Status: $newStatus."
            else -> "$characterName has noted your choice."
        }
    }
    
    /**
     * Add trustful tone to message
     */
    private fun addTrustfulTone(message: String, sender: String, status: String): String {
        val prefixes = when (status) {
            "Best Friend" -> listOf("Hey friend, ", "Listen, ", "I trust you completely, ")
            "Trusted Ally" -> listOf("I know I can count on you. ", "Thank you for everything. ")
            else -> listOf("I appreciate you. ")
        }
        return prefixes.random() + message
    }
    
    /**
     * Add suspicious tone to message
     */
    private fun addSuspiciousTone(message: String, sender: String): String {
        val prefixes = listOf(
            "I'm not sure I can trust you, but... ",
            "Against my better judgment... ",
            "I have my doubts about you, but... "
        )
        return prefixes.random() + message
    }
    
    /**
     * Find relevant past choices to reference
     */
    private fun findRelevantPastChoices(
        currentMessageId: Int,
        playerChoices: Map<Int, Int>
    ): List<Pair<Int, Int>> {
        // Find choices made 5-10 messages ago that are relevant to current message
        return playerChoices.filter { (msgId, _) ->
            msgId in (currentMessageId - 10)..(currentMessageId - 5)
        }.toList()
    }
    
    /**
     * Add reference to past choice in message
     */
    private fun addChoiceReference(
        message: String,
        pastChoices: List<Pair<Int, Int>>,
        relationships: Map<String, CharacterRelationship>
    ): String {
        val reference = when (pastChoices.size) {
            in 1..2 -> " I remember how you handled that situation before."
            in 3..5 -> " Your past decisions have shown me who you really are."
            else -> " Everything you've done has led to this moment."
        }
        return message + reference
    }
}

/**
 * Result of processing a choice
 */
data class ChoiceResult(
    val consequences: List<ChoiceConsequence>,
    val notifications: List<ConsequenceNotification>,
    val updatedRelationships: Map<String, CharacterRelationship>,
    val updatedStoryState: StoryState,
    val unlockedAchievements: List<String>
)

/**
 * Impact analysis of a choice
 */
data class ChoiceImpact(
    val moralityDelta: Int,
    val cautionDelta: Int,
    val characterEffects: Map<String, CharacterEffect>
)

/**
 * Effect on a specific character
 */
data class CharacterEffect(
    val trustDelta: Int,
    val note: String,
    val branchUnlocked: String? = null,
    val achievementUnlocked: String? = null
)

/**
 * Character personality traits that influence reactions
 */
data class CharacterTraits(
    val valuesHonesty: Boolean,
    val valuesCaution: Boolean,
    val remembersBetrayal: Boolean,
    val canBeSaved: Boolean
)
