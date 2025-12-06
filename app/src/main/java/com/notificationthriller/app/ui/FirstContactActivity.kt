package com.notificationthriller.app.ui

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.notificationthriller.app.R
import com.notificationthriller.app.databinding.ActivityFirstContactBinding
import com.notificationthriller.app.utils.AnalyticsManager
import com.notificationthriller.app.utils.HapticManager
import com.notificationthriller.app.utils.SoundManager

/**
 * First Contact Activity - The Hook
 * 
 * This is the critical "love at first sight" moment.
 * Psychological techniques used:
 * 1. Immediate tension/mystery (hook within 3 seconds)
 * 2. Player agency (interactive choice immediately)
 * 3. Sensory engagement (sound + haptics + animations)
 * 4. Quick win (achievement after first choice)
 * 5. Anticipation (countdown to game start)
 * 6. Emotional investment (helping someone in danger)
 */
class FirstContactActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFirstContactBinding
    private lateinit var hapticManager: HapticManager
    private lateinit var soundManager: SoundManager
    private lateinit var analyticsManager: AnalyticsManager
    private var countdownTimer: CountDownTimer? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Handle back button - prevent exit during first contact
        onBackPressedDispatcher.addCallback(
            this,
            object : androidx.activity.OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Don't allow back during first contact - commit to the experience
                    // This increases investment
                }
            },
        )
        
        binding = ActivityFirstContactBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Initialize managers
        hapticManager = HapticManager(this)
        soundManager = SoundManager(this)
        analyticsManager = AnalyticsManager(this)
        
        // Log this critical engagement moment
        analyticsManager.logEvent("first_contact_viewed", Bundle())
        
        // Start the experience
        setupInitialState()
        playIntroSequence()
    }
    
    private fun setupInitialState() {
        // Hide everything initially
        binding.messageContainer.visibility = View.GONE
        binding.choicesLayout.visibility = View.GONE
        binding.countdownLayout.visibility = View.GONE
        binding.achievementLayout.visibility = View.GONE
    }
    
    private fun playIntroSequence() {
        // Play incoming message sound immediately
        soundManager.playNotification()
        hapticManager.mediumTap()
        
        // Show "Incoming Message" with typing effect
        binding.incomingTitle.alpha = 0f
        binding.incomingTitle.visibility = View.VISIBLE
        binding.incomingTitle.animate()
            .alpha(1f)
            .setDuration(500)
            .withEndAction {
                // Show the message after a dramatic pause
                android.os.Handler(mainLooper).postDelayed({
                    showFirstMessage()
                }, 1000)
            }
            .start()
    }
    
    private fun showFirstMessage() {
        // Fade in message container
        binding.messageContainer.visibility = View.VISIBLE
        binding.messageContainer.alpha = 0f
        
        // Play message received sound
        soundManager.playMessageReceived()
        hapticManager.lightTap()
        
        binding.messageContainer.animate()
            .alpha(1f)
            .setDuration(600)
            .withEndAction {
                // Brief pause to let player read, then show choices
                android.os.Handler(mainLooper).postDelayed({
                    showChoices()
                }, 2000)
            }
            .start()
    }
    
    private fun showChoices() {
        binding.choicesLayout.visibility = View.VISIBLE
        binding.choicesLayout.alpha = 0f
        
        binding.choicesLayout.animate()
            .alpha(1f)
            .setDuration(400)
            .start()
        
        // Setup click listeners for choices
        binding.choice1Button.setOnClickListener {
            handleChoice(1, binding.choice1Button.text.toString())
        }
        
        binding.choice2Button.setOnClickListener {
            handleChoice(2, binding.choice2Button.text.toString())
        }
        
        binding.choice3Button.setOnClickListener {
            handleChoice(3, binding.choice3Button.text.toString())
        }
    }
    
    private fun handleChoice(choiceNumber: Int, choiceText: String) {
        // Immediate feedback
        soundManager.playMessageSent()
        hapticManager.mediumTap()
        
        // Log which choice was made
        analyticsManager.logEvent("first_contact_choice", Bundle().apply {
            putInt("choice_number", choiceNumber)
            putString("choice_text", choiceText)
        })
        
        // Hide choices
        binding.choicesLayout.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                binding.choicesLayout.visibility = View.GONE
                showResponse(choiceNumber)
            }
            .start()
    }
    
    private fun showResponse(choiceNumber: Int) {
        // Update message with response
        val responseText = when(choiceNumber) {
            1 -> getString(R.string.first_contact_response_1)
            2 -> getString(R.string.first_contact_response_2)
            else -> getString(R.string.first_contact_response_3)
        }
        
        // Play typing indicator
        soundManager.playMessageReceived()
        hapticManager.lightTap()
        
        // Animate text change
        binding.messageText.animate()
            .alpha(0f)
            .setDuration(200)
            .withEndAction {
                binding.messageText.text = responseText
                binding.messageText.animate()
                    .alpha(1f)
                    .setDuration(400)
                    .withEndAction {
                        // Show achievement after response
                        android.os.Handler(mainLooper).postDelayed({
                            showAchievement()
                        }, 1500)
                    }
                    .start()
            }
            .start()
    }
    
    private fun showAchievement() {
        // Show achievement notification - QUICK WIN!
        binding.achievementLayout.visibility = View.VISIBLE
        binding.achievementLayout.alpha = 0f
        binding.achievementLayout.translationY = 50f
        
        // Special achievement sound
        soundManager.playNotification()
        hapticManager.heavyTap()
        
        binding.achievementLayout.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(500)
            .withEndAction {
                // Show countdown after achievement
                android.os.Handler(mainLooper).postDelayed({
                    showCountdown()
                }, 2000)
            }
            .start()
        
        analyticsManager.logEvent("first_contact_achievement_earned", Bundle())
    }
    
    private fun showCountdown() {
        // Hide achievement
        binding.achievementLayout.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                binding.achievementLayout.visibility = View.GONE
            }
            .start()
        
        // Hide message
        binding.messageContainer.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                binding.messageContainer.visibility = View.GONE
            }
            .start()
        
        // Show countdown - building anticipation
        binding.countdownLayout.visibility = View.VISIBLE
        binding.countdownLayout.alpha = 0f
        
        binding.countdownLayout.animate()
            .alpha(1f)
            .setDuration(600)
            .withEndAction {
                startCountdown()
            }
            .start()
    }
    
    private fun startCountdown() {
        // 10 second countdown to build anticipation
        countdownTimer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                binding.countdownText.text = getString(R.string.first_contact_continue, seconds)
                
                // Pulse on each second
                if (seconds <= 3) {
                    hapticManager.lightTap()
                }
            }
            
            override fun onFinish() {
                proceedToGame()
            }
        }.start()
    }
    
    private fun proceedToGame() {
        // Mark first contact as completed
        getSharedPreferences("game_prefs", MODE_PRIVATE)
            .edit()
            .putBoolean("first_contact_completed", true)
            .apply()
        
        // Heavy haptic for dramatic transition
        hapticManager.heavyTap()
        soundManager.playNotification()
        
        // Fade to game
        binding.root.animate()
            .alpha(0f)
            .setDuration(500)
            .withEndAction {
                // Navigate to splash then main game
                val intent = Intent(this, SplashScreenActivity::class.java)
                startActivity(intent)
                finish()
                
                // Smooth transition
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
            .start()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        countdownTimer?.cancel()
        soundManager.release()
    }
}
