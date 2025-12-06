package com.notificationthriller.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.notificationthriller.app.R
import com.notificationthriller.app.databinding.ActivityWelcomeBinding

/**
 * Welcome/Tutorial Activity
 *
 * Introduces players to the game concept progressively:
 * - Explains what the game is about
 * - Shows how notifications work
 * - Sets expectations for gameplay
 * - Creates anticipation before first message
 */
class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var adapter: WelcomePagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if user has seen welcome screen
        val prefs = getSharedPreferences("game_prefs", MODE_PRIVATE)
        if (prefs.getBoolean("welcome_completed", false)) {
            // Check if they've done first contact
            if (prefs.getBoolean("first_contact_completed", false)) {
                // Skip to splash screen
                startActivity(Intent(this, SplashScreenActivity::class.java))
            } else {
                // Go to first contact
                startActivity(Intent(this, FirstContactActivity::class.java))
            }
            finish()
            return
        }

        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupButtons()
    }

    private fun setupViewPager() {
        adapter = WelcomePagerAdapter()
        binding.viewPager.adapter = adapter

        // Setup tab dots indicator
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ ->
            // Just show dots, no text
        }.attach()

        // Listen for page changes
        binding.viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    updateButtons(position)
                }
            },
        )
    }

    private fun setupButtons() {
        binding.buttonSkip.setOnClickListener {
            completeWelcome()
        }

        binding.buttonNext.setOnClickListener {
            if (binding.viewPager.currentItem < adapter.itemCount - 1) {
                binding.viewPager.currentItem = binding.viewPager.currentItem + 1
            } else {
                completeWelcome()
            }
        }

        binding.buttonBack.setOnClickListener {
            if (binding.viewPager.currentItem > 0) {
                binding.viewPager.currentItem = binding.viewPager.currentItem - 1
            }
        }

        updateButtons(0)
    }

    private fun updateButtons(position: Int) {
        // Update visibility and text based on current page
        binding.buttonBack.visibility = if (position > 0) View.VISIBLE else View.GONE

        if (position == adapter.itemCount - 1) {
            binding.buttonNext.text = getString(R.string.welcome_start_game)
            binding.buttonSkip.visibility = View.GONE
        } else {
            binding.buttonNext.text = getString(R.string.welcome_next)
            binding.buttonSkip.visibility = View.VISIBLE
        }

        // Animate button changes
        val fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        binding.buttonNext.startAnimation(fadeIn)
    }

    private fun completeWelcome() {
        // Mark welcome as completed
        getSharedPreferences("game_prefs", MODE_PRIVATE)
            .edit()
            .putBoolean("welcome_completed", true)
            .apply()

        // Proceed to first contact experience - THE HOOK!
        val intent = Intent(this, FirstContactActivity::class.java)
        startActivity(intent)
        finish()

        // Add smooth transition
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
