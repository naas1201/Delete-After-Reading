package com.example.notificationthriller.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

/**
 * Manager for game sound effects
 * Provides AAA-quality audio feedback for user interactions
 */
class SoundManager(context: Context) {
    
    private val soundPool: SoundPool
    private val sounds = mutableMapOf<String, Int>()
    private var enabled = true
    
    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        
        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()
        
        // Sound resources will be added to res/raw/ directory
        // For now, sounds are optional and fail gracefully if not found
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
