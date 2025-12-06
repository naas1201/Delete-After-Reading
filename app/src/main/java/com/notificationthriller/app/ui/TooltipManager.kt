package com.notificationthriller.app.ui

import android.content.Context
import android.view.View
import com.google.android.material.snackbar.Snackbar

/**
 * Manager for progressive disclosure tooltips
 * 
 * Psychological principle: Don't overwhelm new users.
 * Show hints progressively as they explore features.
 * This builds confidence and reduces cognitive load.
 */
class TooltipManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("tooltip_prefs", Context.MODE_PRIVATE)
    
    companion object {
        private const val TOOLTIP_FAB_SHOWN = "tooltip_fab_shown"
        private const val TOOLTIP_ARCHIVE_SHOWN = "tooltip_archive_shown"
        private const val TOOLTIP_CHOICES_SHOWN = "tooltip_choices_shown"
        private const val TOOLTIP_NOTIFICATIONS_SHOWN = "tooltip_notifications_shown"
    }
    
    /**
     * Show FAB tooltip (after first message arrives)
     */
    fun showFabTooltip(anchorView: View, callback: () -> Unit = {}) {
        if (!prefs.getBoolean(TOOLTIP_FAB_SHOWN, false)) {
            Snackbar.make(
                anchorView,
                "💡 Tip: Tap here to explore while waiting for messages",
                Snackbar.LENGTH_LONG
            ).apply {
                setAction("Got it") {
                    markTooltipShown(TOOLTIP_FAB_SHOWN)
                    callback()
                }
                show()
            }
        }
    }
    
    /**
     * Show archive tooltip (when user receives 3+ messages)
     */
    fun showArchiveTooltip(anchorView: View) {
        if (!prefs.getBoolean(TOOLTIP_ARCHIVE_SHOWN, false)) {
            Snackbar.make(
                anchorView,
                "💡 Tip: Review past messages anytime in the Archive",
                Snackbar.LENGTH_LONG
            ).apply {
                setAction("Thanks") {
                    markTooltipShown(TOOLTIP_ARCHIVE_SHOWN)
                }
                show()
            }
        }
    }
    
    /**
     * Show choices tooltip (before first choice)
     */
    fun showChoicesTooltip(anchorView: View) {
        if (!prefs.getBoolean(TOOLTIP_CHOICES_SHOWN, false)) {
            Snackbar.make(
                anchorView,
                "⚠️ Your choices have real consequences. Choose wisely!",
                Snackbar.LENGTH_LONG
            ).apply {
                setAction("Understood") {
                    markTooltipShown(TOOLTIP_CHOICES_SHOWN)
                }
                show()
            }
        }
    }
    
    /**
     * Show notifications importance tooltip
     */
    fun showNotificationsTooltip(anchorView: View) {
        if (!prefs.getBoolean(TOOLTIP_NOTIFICATIONS_SHOWN, false)) {
            Snackbar.make(
                anchorView,
                "🔔 Keep notifications on to never miss critical messages",
                Snackbar.LENGTH_LONG
            ).apply {
                setAction("OK") {
                    markTooltipShown(TOOLTIP_NOTIFICATIONS_SHOWN)
                }
                show()
            }
        }
    }
    
    private fun markTooltipShown(key: String) {
        prefs.edit().putBoolean(key, true).apply()
    }
    
    /**
     * Reset all tooltips (useful for testing or new game+)
     */
    fun resetAllTooltips() {
        prefs.edit().clear().apply()
    }
}
