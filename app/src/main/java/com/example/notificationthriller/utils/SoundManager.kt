package com.example.notificationthriller.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

/**
 * Manager for game sound effects
 * Provides AAA-quality audio feedback for user interactions
 *
 * Note: Context parameter is reserved for future use to load sound resources.
 * Currently, sound files are optional and the manager initializes without loading them.
 * TODO: Add actual sound files to res/raw/ and load them using context
 */
class SoundManager(@Suppress("UNUSED_PARAMETER") context: Context) {
    private val soundPool: SoundPool
    private val sounds = mutableMapOf<String, Int>()
    private var enabled = true

    init {
        val audioAttributes =
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

        soundPool =
            SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(audioAttributes)
                .build()

        // TODO: Add actual sound files to res/raw/ and load them here
        // Example:
        // sounds["message_received"] = soundPool.load(context, R.raw.sound_message_received, 1)
        // sounds["message_sent"] = soundPool.load(context, R.raw.sound_message_sent, 1)
        // sounds["choice_select"] = soundPool.load(context, R.raw.sound_choice_select, 1)
        // sounds["notification"] = soundPool.load(context, R.raw.sound_notification, 1)
        //
        // For now, sounds are optional and fail gracefully if not loaded
        // The app will function without sound effects
    }

    fun playMessageReceived() {
        playSound("message_received")
    }

    fun playMessageSent() {
        playSound("message_sent")
    }

    fun playChoiceSelect() {
        playSound("choice_select")
    }

    fun playNotification() {
        playSound("notification")
    }

    fun playAchievementUnlocked() {
        playSound("achievement")
    }

    fun playUIClick() {
        playSound("ui_click")
    }

    fun playDramaticMoment() {
        playSound("dramatic")
    }

    fun playSuccessChime() {
        playSound("success")
    }

    fun playErrorSound() {
        playSound("error")
    }

    private fun playSound(soundName: String) {
        if (!enabled) return
        sounds[soundName]?.let { soundId ->
            soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f)
        }
    }

    fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
    }

    fun release() {
        soundPool.release()
    }
}
