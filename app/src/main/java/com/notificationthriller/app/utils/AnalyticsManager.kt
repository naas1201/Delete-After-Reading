package com.notificationthriller.app.utils

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

/**
 * Manager for Firebase Analytics integration
 * Tracks game events and user behavior
 *
 * Note: Context parameter matches SoundManager API pattern for consistency.
 * Currently unused as Firebase.analytics is initialized globally.
 */
class AnalyticsManager(
    @Suppress("UNUSED_PARAMETER") context: Context,
) {
    private val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    fun logGameStart() {
        val bundle =
            Bundle().apply {
                putString("action", "game_started")
            }
        firebaseAnalytics.logEvent("game_event", bundle)
    }

    fun logMessageRead(
        messageId: Int,
        sender: String,
    ) {
        val bundle =
            Bundle().apply {
                putInt("message_id", messageId)
                putString("sender", sender)
            }
        firebaseAnalytics.logEvent("message_read", bundle)
    }

    fun logChoiceMade(
        messageId: Int,
        choiceId: Int,
        choiceText: String,
    ) {
        val bundle =
            Bundle().apply {
                putInt("message_id", messageId)
                putInt("choice_id", choiceId)
                putString("choice_text", choiceText)
            }
        firebaseAnalytics.logEvent("choice_made", bundle)
    }

    fun logGameSaved(saveName: String) {
        val bundle =
            Bundle().apply {
                putString("save_name", saveName)
            }
        firebaseAnalytics.logEvent("game_saved", bundle)
    }

    fun logGameLoaded(saveName: String) {
        val bundle =
            Bundle().apply {
                putString("save_name", saveName)
            }
        firebaseAnalytics.logEvent("game_loaded", bundle)
    }

    fun logGameReset() {
        firebaseAnalytics.logEvent("game_reset", Bundle())
    }

    fun logNotificationReceived(messageId: Int) {
        val bundle =
            Bundle().apply {
                putInt("message_id", messageId)
            }
        firebaseAnalytics.logEvent("notification_received", bundle)
    }

    fun logPurchaseAttempt(productId: String) {
        val bundle =
            Bundle().apply {
                putString("product_id", productId)
            }
        firebaseAnalytics.logEvent("purchase_attempt", bundle)
    }

    fun logPurchaseSuccess(
        productId: String,
        price: Double,
    ) {
        val bundle =
            Bundle().apply {
                putString("product_id", productId)
                putDouble("price", price)
            }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE, bundle)
    }

    /**
     * Generic event logging
     */
    fun logEvent(
        eventName: String,
        params: Bundle,
    ) {
        firebaseAnalytics.logEvent(eventName, params)
    }
}
