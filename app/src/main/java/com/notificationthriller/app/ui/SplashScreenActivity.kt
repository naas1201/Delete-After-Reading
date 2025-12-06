package com.notificationthriller.app.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.notificationthriller.app.R
import java.io.File

/**
 * Splash Screen Activity with video playback support
 *
 * Features:
 * - Plays template.mp4 from raw resources or external storage
 * - Automatically transitions to MainActivity after video completes
 * - Skippable by tapping the screen
 * - Falls back gracefully if video is not found
 * - Supports custom video replacement (users can provide their own mp4)
 */
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var videoView: VideoView
    private var videoCompleted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Handle back button for API 33+
        onBackPressedDispatcher.addCallback(
            this,
            object : androidx.activity.OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Do nothing - prevent back button during splash
                    // User can still tap screen to skip
                }
            },
        )

        setContentView(R.layout.activity_splash_screen)

        videoView = findViewById(R.id.splashVideoView)
        setupVideoPlayback()
    }

    /**
     * Sets up video playback with fallback mechanism
     * Priority:
     * 1. Custom video from app's external files directory (template.mp4)
     * 2. Built-in video from raw resources
     * 3. Skip splash screen if no video found
     */
    private fun setupVideoPlayback() {
        val customVideoPath = getCustomVideoPath()
        val videoUri =
            when {
                customVideoPath != null -> customVideoPath
                hasBuiltInVideo() -> getBuiltInVideoUri()
                else -> {
                    // No video available, proceed to main activity immediately
                    proceedToMainActivity()
                    return
                }
            }

        try {
            videoView.setVideoURI(videoUri)

            // Set completion listener
            videoView.setOnCompletionListener {
                videoCompleted = true
                proceedToMainActivity()
            }

            // Set error listener for graceful fallback
            videoView.setOnErrorListener { _, what, extra ->
                android.util.Log.e("SplashScreen", "Video playback error: what=$what, extra=$extra")
                proceedToMainActivity()
                true
            }

            // Make video clickable to skip
            videoView.setOnClickListener {
                if (!videoCompleted) {
                    proceedToMainActivity()
                }
            }

            // Start playback
            videoView.start()
        } catch (e: Exception) {
            android.util.Log.e("SplashScreen", "Failed to setup video", e)
            proceedToMainActivity()
        }
    }

    /**
     * Checks for custom video in external files directory
     * Users can place their own template.mp4 here to customize splash screen
     *
     * Path: /Android/data/com.notificationthriller.app/files/template.mp4
     */
    private fun getCustomVideoPath(): Uri? {
        val customVideo = File(getExternalFilesDir(null), "template.mp4")
        return if (customVideo.exists() && customVideo.canRead()) {
            Uri.fromFile(customVideo)
        } else {
            null
        }
    }

    /**
     * Checks if built-in video resource exists
     */
    private fun hasBuiltInVideo(): Boolean {
        return try {
            // Check if template video exists in raw resources
            val resourceId = resources.getIdentifier("template", "raw", packageName)
            if (resourceId != 0) {
                resources.openRawResource(resourceId).close()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Gets URI for built-in video from raw resources
     */
    private fun getBuiltInVideoUri(): Uri {
        val resourceId = resources.getIdentifier("template", "raw", packageName)
        return Uri.parse("android.resource://$packageName/$resourceId")
    }

    /**
     * Transitions to main activity
     */
    private fun proceedToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()

        // Add smooth transition animation (API 34+ uses overrideActivityTransition)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(
                android.app.Activity.OVERRIDE_TRANSITION_OPEN,
                android.R.anim.fade_in,
                android.R.anim.fade_out,
            )
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    override fun onPause() {
        super.onPause()
        if (::videoView.isInitialized && videoView.isPlaying) {
            videoView.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::videoView.isInitialized) {
            videoView.stopPlayback()
        }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        // Prevent back button during splash screen (for older APIs)
        // User can still tap to skip
        // For API 33+, this is handled by OnBackPressedDispatcher in onCreate
        // Intentionally not calling super to prevent back navigation during splash
    }
}
