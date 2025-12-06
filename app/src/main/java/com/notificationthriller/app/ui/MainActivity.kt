package com.notificationthriller.app.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.notificationthriller.app.R
import com.notificationthriller.app.databinding.ActivityMainBinding
import com.notificationthriller.app.utils.AnalyticsManager
import com.notificationthriller.app.utils.HapticManager
import com.notificationthriller.app.utils.SoundManager
import com.notificationthriller.app.utils.WaitingExperienceManager

/**
 * Main activity displaying the chat interface
 * Uses ViewBinding and MVVM architecture with AAA features
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ChatViewModel
    private lateinit var adapter: ChatAdapter
    private lateinit var hapticManager: HapticManager
    private lateinit var soundManager: SoundManager
    private lateinit var analyticsManager: AnalyticsManager
    private lateinit var tooltipManager: TooltipManager
    private lateinit var waitingExperienceManager: WaitingExperienceManager
    private var countdownTimer: android.os.CountDownTimer? = null
    private var messageCount = 0

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
            if (isGranted) {
                hapticManager.mediumTap()
                soundManager.playNotification()
                initializeGame()
            } else {
                // Show dialog explaining importance of notifications
                showNotificationImportanceDialog()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.app_name)

        // Initialize managers
        hapticManager = HapticManager(this)
        soundManager = SoundManager(this)
        analyticsManager = AnalyticsManager(this)
        tooltipManager = TooltipManager(this)
        waitingExperienceManager = WaitingExperienceManager(this)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]

        // Setup RecyclerView
        setupRecyclerView()

        // Observe messages
        observeMessages()

        // Setup FAB for quick actions
        setupFab()

        // Request notification permission and initialize game
        checkNotificationPermissionAndInitialize()

        // Check if coming from first contact for special handling
        handleFirstContactTransition()

        // Log analytics
        analyticsManager.logGameStart()
    }

    private fun handleFirstContactTransition() {
        val fromFirstContact = intent.getBooleanExtra("from_first_contact", false)
        if (fromFirstContact) {
            // Add welcoming animation or special message
            hapticManager.lightTap()
            soundManager.playSuccessChime()

            // Show a toast or snackbar welcoming them
            com.google.android.material.snackbar.Snackbar.make(
                binding.root,
                "Your story begins now...",
                com.google.android.material.snackbar.Snackbar.LENGTH_LONG,
            ).show()
        }
    }

    private fun setupFab() {
        binding.fabQuickActions.setOnClickListener {
            hapticManager.lightTap()
            showQuickActionsBottomSheet()
        }
    }

    private fun showQuickActionsBottomSheet() {
        val bottomSheet = com.google.android.material.bottomsheet.BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_quick_actions, null)

        view.findViewById<android.view.View>(R.id.actionArchive).setOnClickListener {
            hapticManager.lightTap()
            soundManager.playMessageSent()
            startActivity(android.content.Intent(this, ArchiveActivity::class.java))
            bottomSheet.dismiss()
        }

        view.findViewById<android.view.View>(R.id.actionProfiles).setOnClickListener {
            hapticManager.lightTap()
            soundManager.playMessageSent()
            // TODO: Open character profiles screen
            android.widget.Toast.makeText(this, R.string.feature_coming_soon_profiles, android.widget.Toast.LENGTH_SHORT).show()
            bottomSheet.dismiss()
        }

        view.findViewById<android.view.View>(R.id.actionAchievements).setOnClickListener {
            hapticManager.lightTap()
            soundManager.playMessageSent()
            // TODO: Open achievements screen
            android.widget.Toast.makeText(this, R.string.feature_coming_soon_achievements, android.widget.Toast.LENGTH_SHORT).show()
            bottomSheet.dismiss()
        }

        view.findViewById<android.view.View>(R.id.actionStatistics).setOnClickListener {
            hapticManager.lightTap()
            soundManager.playMessageSent()
            // TODO: Open statistics screen
            android.widget.Toast.makeText(this, R.string.feature_coming_soon_statistics, android.widget.Toast.LENGTH_SHORT).show()
            bottomSheet.dismiss()
        }

        bottomSheet.setContentView(view)
        bottomSheet.show()
    }

    private fun setupRecyclerView() {
        adapter = ChatAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun observeMessages() {
        viewModel.displayedMessages.observe(this) { messages ->
            adapter.submitList(messages)
            // Scroll to bottom when new message arrives
            if (messages.isNotEmpty()) {
                binding.recyclerView.smoothScrollToPosition(messages.size - 1)
                binding.emptyStateLayout.visibility = android.view.View.GONE
                binding.recyclerView.visibility = android.view.View.VISIBLE
                stopCountdown()

                // Show progressive tooltips based on message count
                val previousCount = messageCount
                messageCount = messages.size

                when {
                    previousCount == 0 && messageCount == 1 -> {
                        // First message received - animate FAB to draw attention
                        android.os.Handler(mainLooper).postDelayed({
                            val pulseAnim =
                                android.view.animation.AnimationUtils.loadAnimation(
                                    this,
                                    R.anim.fab_pulse,
                                )
                            binding.fabQuickActions.startAnimation(pulseAnim)
                        }, 2000)

                        // Show FAB tooltip after animation
                        android.os.Handler(mainLooper).postDelayed({
                            tooltipManager.showFabTooltip(binding.fabQuickActions) {
                                hapticManager.lightTap()
                            }
                        }, 4000)
                    }
                    messageCount >= 3 && previousCount < 3 -> {
                        // Third message - show archive tooltip
                        android.os.Handler(mainLooper).postDelayed({
                            tooltipManager.showArchiveTooltip(binding.root)
                        }, 2000)
                    }
                }
            } else {
                binding.emptyStateLayout.visibility = android.view.View.VISIBLE
                binding.recyclerView.visibility = android.view.View.GONE
                // Start countdown for first message (5 seconds from game_messages.json)
                startFirstMessageCountdown()
            }
        }
    }

    private fun startFirstMessageCountdown() {
        // First message arrives in 5 seconds according to game_messages.json
        val totalTimeMillis = 5000L
        val totalSeconds = (totalTimeMillis / 1000).toInt()

        // Show first wait explanation if needed
        if (waitingExperienceManager.shouldShowFirstWaitExplanation()) {
            showFirstWaitExplanation()
            waitingExperienceManager.markFirstWaitExplanationShown()
        }

        // Set initial context message
        updateWaitingContext(1, totalSeconds)

        countdownTimer =
            object : android.os.CountDownTimer(totalTimeMillis, 100) {
                override fun onTick(millisUntilFinished: Long) {
                    val secondsRemaining = millisUntilFinished / 1000
                    val centisecondsRemaining = (millisUntilFinished % 1000) / 10

                    // Update timer text with consistent two-digit formatting
                    binding.countdownTimer.text = String.format("%02d:%02d", secondsRemaining, centisecondsRemaining)

                    // Update progress bar
                    val progress = ((totalTimeMillis - millisUntilFinished).toFloat() / totalTimeMillis * 100).toInt()
                    binding.countdownProgress.progress = progress

                    // Update context every second
                    if (centisecondsRemaining == 0L) {
                        updateWaitingContext(1, secondsRemaining.toInt())
                    }

                    // Haptic feedback on each second for final countdown
                    if (centisecondsRemaining == 0L && secondsRemaining <= 3) {
                        hapticManager.lightTap()
                    }
                }

                override fun onFinish() {
                    binding.countdownTimer.text = "00:00"
                    binding.countdownProgress.progress = 100
                    hapticManager.mediumTap()
                    soundManager.playNotification()

                    // Record that the player waited naturally
                    waitingExperienceManager.recordNaturalWait()

                    // Check for patience achievement
                    if (waitingExperienceManager.shouldShowPatienceAchievement()) {
                        showPatienceAchievement()
                        waitingExperienceManager.markPatienceAchievementShown()
                    }

                    // Animate the card to show anticipation
                    binding.countdownCard.animate()
                        .scaleX(1.05f)
                        .scaleY(1.05f)
                        .setDuration(200)
                        .withEndAction {
                            binding.countdownCard.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(200)
                                .start()
                        }
                        .start()
                }
            }.start()
    }

    /**
     * Update the waiting experience with contextual information
     * This makes the wait feel purposeful and exciting
     */
    private fun updateWaitingContext(
        messageId: Int,
        remainingSeconds: Int,
    ) {
        // Check if countdown text view exists
        try {
            // Get tension-building text
            val tensionText = waitingExperienceManager.getTensionBuildingText()

            // Get story teaser (only show occasionally to maintain mystery)
            val showTeaser = remainingSeconds % 3 == 0
            if (showTeaser) {
                binding.countdownDescription?.text = waitingExperienceManager.getStoryTeaser(messageId)
            } else {
                binding.countdownDescription?.text = tensionText
            }
        } catch (e: Exception) {
            // Countdown views might not exist in all layouts
        }
    }

    /**
     * Show explanation dialog for first wait experience
     */
    private fun showFirstWaitExplanation() {
        AlertDialog.Builder(this)
            .setTitle("🎭 Real-Time Thriller")
            .setMessage(waitingExperienceManager.getFirstWaitExplanation())
            .setPositiveButton("I Understand") { dialog, _ ->
                hapticManager.lightTap()
                soundManager.playSuccessChime()
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    /**
     * Show patience achievement celebration
     */
    private fun showPatienceAchievement() {
        AlertDialog.Builder(this)
            .setTitle("🏆 Achievement Unlocked!")
            .setMessage(
                "Patient Observer\n\nYou've mastered the art of patience. You understand that the best stories unfold in their own time.\n\nThe tension is building...",
            )
            .setPositiveButton("Awesome!") { dialog, _ ->
                hapticManager.heavyTap()
                soundManager.playSuccessChime()
                analyticsManager.logEvent("achievement_patience", Bundle())
                dialog.dismiss()
            }
            .show()
    }

    private fun stopCountdown() {
        countdownTimer?.cancel()
        countdownTimer = null
    }

    private fun checkNotificationPermissionAndInitialize() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED -> {
                    initializeGame()
                }
                else -> {
                    // Show dialog explaining the importance of notifications
                    showNotificationImportanceDialog()
                }
            }
        } else {
            initializeGame()
        }
    }

    private fun showNotificationImportanceDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.notification_permission_title)
            .setMessage(R.string.notification_permission_message)
            .setPositiveButton(R.string.notification_permission_positive) { _, _ ->
                hapticManager.lightTap()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            .setNegativeButton(R.string.notification_permission_negative) { _, _ ->
                hapticManager.lightTap()
                initializeGame()
            }
            .setCancelable(false)
            .show()
    }

    private fun initializeGame() {
        viewModel.initializeGame()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        hapticManager.lightTap()
        return when (item.itemId) {
            R.id.action_save_game -> {
                showSaveGameDialog()
                true
            }
            R.id.action_load_game -> {
                showLoadGameDialog()
                true
            }
            R.id.action_reset -> {
                analyticsManager.logGameReset()
                viewModel.resetGame()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSaveGameDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_save_game, null)
        val saveNameEditText = dialogView.findViewById<android.widget.EditText>(R.id.saveNameEditText)

        AlertDialog.Builder(this)
            .setTitle(R.string.save_game_title)
            .setView(dialogView)
            .setPositiveButton(R.string.save) { _, _ ->
                val saveName = saveNameEditText.text.toString()
                if (saveName.isNotEmpty()) {
                    viewModel.saveGame(saveName)
                    hapticManager.mediumTap()
                    soundManager.playMessageSent()
                    analyticsManager.logGameSaved(saveName)
                    android.widget.Toast.makeText(this, R.string.game_saved, android.widget.Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showLoadGameDialog() {
        // TODO: Implement full RecyclerView adapter to show saved games
        // This is a simplified implementation. For production:
        // 1. Inflate dialog_load_game.xml
        // 2. Set up RecyclerView with saved games from viewModel.savedGames
        // 3. Handle item click to call viewModel.loadGame(gameStateId)
        // 4. Show confirmation with game details (save name, timestamp)

        // Simple placeholder implementation
        AlertDialog.Builder(this)
            .setTitle(R.string.load_game_title)
            .setMessage(R.string.no_saved_games)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopCountdown()
        soundManager.release()
    }

    override fun onPause() {
        super.onPause()
        stopCountdown()
    }

    override fun onResume() {
        super.onResume()
        // Restart countdown if still in empty state
        if (binding.emptyStateLayout.visibility == android.view.View.VISIBLE) {
            startFirstMessageCountdown()
        }
    }
}
