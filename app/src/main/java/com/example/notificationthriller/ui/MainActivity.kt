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
import com.example.notificationthriller.utils.HapticManager
import com.example.notificationthriller.utils.SoundManager
import com.example.notificationthriller.utils.AnalyticsManager

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
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
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
        
        // Request notification permission and initialize game
        checkNotificationPermissionAndInitialize()
        
        // Log analytics
        analyticsManager.logGameStart()
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
            }
        }
    }
    
    private fun checkNotificationPermissionAndInitialize() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
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
            R.id.action_reset -> {
                analyticsManager.logGameReset()
                viewModel.resetGame()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        soundManager.release()
    }
}
