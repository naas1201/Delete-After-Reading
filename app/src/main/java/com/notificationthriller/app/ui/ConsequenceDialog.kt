package com.notificationthriller.app.ui

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.widget.TextView
import com.notificationthriller.app.R
import com.notificationthriller.app.data.ConsequenceNotification
import com.notificationthriller.app.data.ConsequenceType

/**
 * Shows consequence notifications to the player
 * Makes it clear that their choices have impact
 * AAA-level feedback system
 */
object ConsequenceDialog {
    /**
     * Show a single consequence notification
     */
    fun show(
        context: Context,
        notification: ConsequenceNotification,
        onDismiss: (() -> Unit)? = null,
    ) {
        val builder = AlertDialog.Builder(context)

        // Set title with emoji based on type
        val titleWithEmoji =
            when (notification.type) {
                ConsequenceType.TRUST_GAINED -> "✓ ${notification.title}"
                ConsequenceType.TRUST_LOST -> "⚠ ${notification.title}"
                ConsequenceType.RELATIONSHIP_IMPROVED -> "💚 ${notification.title}"
                ConsequenceType.RELATIONSHIP_DAMAGED -> "💔 ${notification.title}"
                ConsequenceType.ACHIEVEMENT_UNLOCKED -> "🏆 ${notification.title}"
                ConsequenceType.ENDING_UNLOCKED -> "🔓 ${notification.title}"
                ConsequenceType.ENDING_CLOSED -> "🔒 ${notification.title}"
                ConsequenceType.STORY_BRANCH_UNLOCKED -> "🌿 ${notification.title}"
                ConsequenceType.CHARACTER_REMEMBERS -> "💭 ${notification.title}"
                ConsequenceType.SECRET_REVEALED -> "🔍 ${notification.title}"
                ConsequenceType.DANGER_INCREASED -> "⚡ ${notification.title}"
            }

        builder.setTitle(titleWithEmoji)
        builder.setMessage(notification.message)

        // Set button text based on type
        val buttonText =
            when (notification.type) {
                ConsequenceType.ACHIEVEMENT_UNLOCKED -> "Awesome!"
                ConsequenceType.ENDING_UNLOCKED -> "Interesting..."
                ConsequenceType.TRUST_GAINED -> "Good"
                ConsequenceType.TRUST_LOST -> "I see..."
                else -> "Continue"
            }

        builder.setPositiveButton(buttonText) { dialog, _ ->
            dialog.dismiss()
            onDismiss?.invoke()
        }

        val dialog = builder.create()
        dialog.show()

        // Customize appearance based on consequence type
        val messageView = dialog.findViewById<TextView>(android.R.id.message)
        messageView?.setTextColor(getColorForType(notification.type))
    }

    /**
     * Show multiple consequences in sequence
     */
    fun showSequence(
        context: Context,
        notifications: List<ConsequenceNotification>,
        onComplete: (() -> Unit)? = null,
    ) {
        if (notifications.isEmpty()) {
            onComplete?.invoke()
            return
        }

        showNextInSequence(context, notifications, 0, onComplete)
    }

    private fun showNextInSequence(
        context: Context,
        notifications: List<ConsequenceNotification>,
        index: Int,
        onComplete: (() -> Unit)?,
    ) {
        if (index >= notifications.size) {
            onComplete?.invoke()
            return
        }

        show(context, notifications[index]) {
            // Show next notification after a short delay
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                showNextInSequence(context, notifications, index + 1, onComplete)
            }, 300)
        }
    }

    /**
     * Show a summary of all consequences
     */
    fun showSummary(
        context: Context,
        notifications: List<ConsequenceNotification>,
        summary: String,
        onDismiss: (() -> Unit)? = null,
    ) {
        if (notifications.isEmpty()) {
            onDismiss?.invoke()
            return
        }

        val builder = AlertDialog.Builder(context)
        builder.setTitle("💫 Choice Impact")

        // Build message with all consequences
        val message =
            buildString {
                append(summary)
                append("\n\n")
                append("Details:\n")
                notifications.forEachIndexed { index, notification ->
                    append("${index + 1}. ${notification.title}\n")
                    if (notification.characterInvolved != null) {
                        append("   → ${notification.characterInvolved}\n")
                    }
                }
            }

        builder.setMessage(message)
        builder.setPositiveButton("Understood") { dialog, _ ->
            dialog.dismiss()
            onDismiss?.invoke()
        }

        builder.create().show()
    }

    /**
     * Show character relationship status
     */
    fun showRelationshipStatus(
        context: Context,
        characterName: String,
        trustLevel: Int,
        status: String,
        recentChoices: List<String>,
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("👤 $characterName")

        val message =
            buildString {
                append("Relationship: $status\n")
                append("Trust Level: $trustLevel/100\n\n")

                // Visual trust indicator
                val bars = trustLevel / 10
                append("Trust: ")
                append("█".repeat(bars))
                append("░".repeat(10 - bars))
                append("\n\n")

                if (recentChoices.isNotEmpty()) {
                    append("Recent interactions:\n")
                    recentChoices.takeLast(3).forEach { choice ->
                        append("• $choice\n")
                    }
                }
            }

        builder.setMessage(message)
        builder.setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }

        builder.create().show()
    }

    /**
     * Show achievement unlock
     */
    fun showAchievement(
        context: Context,
        title: String,
        description: String,
        rarity: String,
    ) {
        val builder = AlertDialog.Builder(context)

        val rarityEmoji =
            when (rarity) {
                "Legendary" -> "💎"
                "Epic" -> "⭐"
                "Rare" -> "🌟"
                else -> "🏅"
            }

        builder.setTitle("$rarityEmoji Achievement Unlocked!")
        builder.setMessage("$title\n\n$description\n\nRarity: $rarity")
        builder.setPositiveButton("Awesome!") { dialog, _ -> dialog.dismiss() }

        builder.create().show()
    }

    /**
     * Show ending unlocked
     */
    fun showEndingUnlocked(
        context: Context,
        endingName: String,
        description: String,
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("🔓 New Ending Unlocked")
        builder.setMessage("$endingName\n\n$description\n\nYour choices have opened a new path to the finale.")
        builder.setPositiveButton("Exciting!") { dialog, _ -> dialog.dismiss() }

        builder.create().show()
    }

    /**
     * Get color for consequence type
     */
    private fun getColorForType(type: ConsequenceType): Int {
        return when (type) {
            ConsequenceType.TRUST_GAINED,
            ConsequenceType.RELATIONSHIP_IMPROVED,
            ConsequenceType.ACHIEVEMENT_UNLOCKED,
            ConsequenceType.ENDING_UNLOCKED,
            ConsequenceType.STORY_BRANCH_UNLOCKED,
            ConsequenceType.SECRET_REVEALED,
            -> Color.parseColor("#4CAF50") // Green

            ConsequenceType.TRUST_LOST,
            ConsequenceType.RELATIONSHIP_DAMAGED,
            ConsequenceType.ENDING_CLOSED,
            ConsequenceType.DANGER_INCREASED,
            -> Color.parseColor("#F44336") // Red

            ConsequenceType.CHARACTER_REMEMBERS -> Color.parseColor("#2196F3") // Blue
        }
    }
}
