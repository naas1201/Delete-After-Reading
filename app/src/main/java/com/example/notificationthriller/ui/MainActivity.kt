package com.example.notificationthriller.ui

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
import com.example.notificationthriller.R
import com.example.notificationthriller.databinding.ActivityMainBinding
import com.example.notificationthriller.utils.AnalyticsManager
import com.example.notificationthriller.utils.HapticManager
import com.example.notificationthriller.utils.SoundManager

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

        // Log analytics
        analyticsManager.logGameStart()
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
            } else {
                binding.emptyStateLayout.visibility = android.view.View.VISIBLE
                binding.recyclerView.visibility = android.view.View.GONE
            }
        }
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
        soundManager.release()
    }
}
