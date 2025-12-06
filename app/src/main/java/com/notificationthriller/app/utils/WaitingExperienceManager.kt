package com.notificationthriller.app.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Waiting Experience Manager
 *
 * AAA-quality solution to make waiting for notifications feel engaging and exciting
 * rather than frustrating. This addresses the core gameplay challenge: how to make
 * players WANT to wait instead of feeling blocked.
 *
 * Psychological Principles Applied:
 * 1. **Reframing**: Wait time = tension building, not dead time
 * 2. **Context**: In-universe explanations for delays create authenticity
 * 3. **Agency**: Players understand WHY they're waiting
 * 4. **Anticipation**: Visual cues show something exciting is coming
 * 5. **Reward**: Recognition for patience creates positive reinforcement
 */
class WaitingExperienceManager(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("waiting_experience", Context.MODE_PRIVATE)

    companion object {
        // Tracking keys
        private const val KEY_NATURAL_WAITS = "natural_waits_count"
        private const val KEY_FIRST_WAIT_EXPLAINED = "first_wait_explained"
        private const val KEY_PATIENCE_ACHIEVEMENT = "patience_achievement_shown"

        // Thresholds for achievements
        private const val PATIENCE_THRESHOLD = 5 // Wait naturally 5 times
    }

    /**
     * Get contextual explanation for why the player needs to wait
     * These are in-character, story-driven reasons that make the wait feel natural
     */
    fun getWaitingContextMessage(
        messageId: Int,
        delaySeconds: Int,
    ): String {
        val contexts =
            when {
                delaySeconds < 30 ->
                    listOf(
                        "Typing...",
                        "Checking something...",
                        "One moment...",
                    )
                delaySeconds < 60 ->
                    listOf(
                        "Give me a minute to think about this...",
                        "Let me check the files...",
                        "I need to be careful. They might be watching...",
                        "Hang on, getting more info...",
                    )
                delaySeconds < 300 ->
                    listOf(
                        "This is risky. I need to wait for the right moment...",
                        "I'm gathering evidence. Check back in a few minutes...",
                        "I have to be sure before I tell you more...",
                        "Let me verify this information first...",
                        "I need some time to decrypt this file...",
                    )
                else ->
                    listOf(
                        "This is too dangerous to do right now. I'll reach out when it's safe...",
                        "I need to wait until they're not monitoring...",
                        "Trust me, the timing has to be perfect for this...",
                        "Give me some time to set this up properly...",
                        "I'm working on something big. You'll know when it's ready...",
                    )
            }
        return contexts.random()
    }

    /**
     * Get motivational message that reframes waiting as positive
     * These turn frustration into anticipation
     */
    fun getAnticipationMessage(delaySeconds: Int): String {
        return when {
            delaySeconds < 60 -> "Something's about to happen..."
            delaySeconds < 300 -> "The story deepens. Stay alert..."
            delaySeconds < 1800 -> "Big revelations coming. The wait will be worth it..."
            else -> "Something major is unfolding. This is the calm before the storm..."
        }
    }

    /**
     * Get a teaser hint about what's coming next (without spoilers)
     * Creates curiosity and anticipation
     */
    fun getStoryTeaser(messageId: Int): String {
        val teasers =
            listOf(
                "Someone is about to reveal their true identity...",
                "A decision is coming that will change everything...",
                "The conspiracy runs deeper than you thought...",
                "You're about to learn something shocking...",
                "Trust will be tested in the next message...",
                "A new piece of the puzzle is about to fall into place...",
                "The danger level is about to escalate...",
                "Someone needs your help, and time is running out...",
                "You'll soon face a choice that matters...",
                "The truth is closer than you think...",
            )
        return teasers.random()
    }

    /**
     * Get tension-building flavor text
     * Makes the wait feel like part of the thriller experience
     */
    fun getTensionBuildingText(): String {
        val tensionTexts =
            listOf(
                "🔴 LIVE: Encrypted channel active",
                "⚠️ Monitoring threat level...",
                "📡 Secure connection established",
                "🔒 Message incoming (encrypted)",
                "⏳ Timing is critical...",
                "👁️ Stay vigilant",
                "🌐 Network traffic detected",
                "🔐 Decryption in progress...",
                "⚡ Standby for urgent update",
                "🎯 Operation in progress...",
            )
        return tensionTexts.random()
    }

    /**
     * Track that the player waited naturally (didn't force-skip or check constantly)
     * This is for analytics and achievement tracking
     */
    fun recordNaturalWait() {
        val currentCount = prefs.getInt(KEY_NATURAL_WAITS, 0)
        prefs.edit().putInt(KEY_NATURAL_WAITS, currentCount + 1).apply()
    }

    /**
     * Check if player has earned the patience achievement
     */
    fun shouldShowPatienceAchievement(): Boolean {
        val naturalWaits = prefs.getInt(KEY_NATURAL_WAITS, 0)
        val alreadyShown = prefs.getBoolean(KEY_PATIENCE_ACHIEVEMENT, false)

        return naturalWaits >= PATIENCE_THRESHOLD && !alreadyShown
    }

    /**
     * Mark patience achievement as shown
     */
    fun markPatienceAchievementShown() {
        prefs.edit().putBoolean(KEY_PATIENCE_ACHIEVEMENT, true).apply()
    }

    /**
     * Check if we should show the "first wait" tutorial
     * This explains WHY waiting is part of the experience
     */
    fun shouldShowFirstWaitExplanation(): Boolean {
        return !prefs.getBoolean(KEY_FIRST_WAIT_EXPLAINED, false)
    }

    /**
     * Mark first wait explanation as shown
     */
    fun markFirstWaitExplanationShown() {
        prefs.edit().putBoolean(KEY_FIRST_WAIT_EXPLAINED, true).apply()
    }

    /**
     * Get the full "first wait" explanation
     * This is critical for player understanding and buy-in
     */
    fun getFirstWaitExplanation(): String {
        return """
            🎭 Welcome to Real-Time Storytelling
            
            This isn't your typical game. The story unfolds in REAL TIME.
            
            When Sarah says "Give me 10 minutes," you'll actually wait 10 minutes.
            
            Why? Because this creates REAL tension. Real anticipation. Real emotion.
            
            Just like waiting for a crucial text from a friend, every notification 
            matters. Every delay builds suspense.
            
            Trust the process. The wait is part of the thriller.
            
            Turn on notifications and let the story come to you... 🔔
            """.trimIndent()
    }

    /**
     * Generate a progress description for the wait time
     * Makes abstract time feel concrete
     */
    fun getProgressDescription(
        remainingSeconds: Int,
        totalSeconds: Int,
    ): String {
        val progress = ((totalSeconds - remainingSeconds).toFloat() / totalSeconds * 100).toInt()

        return when {
            progress < 10 -> "⏳ Just getting started..."
            progress < 25 -> "⏳ Patience is a virtue..."
            progress < 50 -> "⏳ Halfway there..."
            progress < 75 -> "⏳ Almost ready..."
            progress < 90 -> "⏳ Final moments..."
            else -> "⏳ Any second now..."
        }
    }

    /**
     * Get emoji indicator for wait time
     * Visual cue for urgency level
     */
    fun getWaitTimeEmoji(delaySeconds: Int): String {
        return when {
            delaySeconds < 30 -> "💬" // Quick reply
            delaySeconds < 120 -> "⏱️" // Few minutes
            delaySeconds < 600 -> "⏰" // Several minutes
            delaySeconds < 3600 -> "🕐" // Under an hour
            else -> "📅" // Long wait
        }
    }

    /**
     * Check if this is a "critical" message that deserves special treatment
     * Critical messages get extra hype in the waiting UI
     */
    fun isCriticalMessage(messageId: Int): Boolean {
        // Messages with IDs divisible by 10 are usually story milestones
        return messageId % 10 == 0 || messageId < 5
    }
}
