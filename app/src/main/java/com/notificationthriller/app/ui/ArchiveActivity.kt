package com.notificationthriller.app.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.notificationthriller.app.R
import com.notificationthriller.app.databinding.ActivityArchiveBinding

/**
 * Archive Activity - Review past messages
 * 
 * Provides players something to do while waiting:
 * - Search through message history
 * - Filter by character
 * - Review important messages
 * - Investigate story clues
 */
class ArchiveActivity : AppCompatActivity() {
    private lateinit var binding: ActivityArchiveBinding
    private lateinit var viewModel: ChatViewModel
    private lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityArchiveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.archive_title)

        // Initialize ViewModel (shared with MainActivity)
        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]

        // Setup RecyclerView
        setupRecyclerView()

        // Observe all displayed messages
        observeMessages()
    }

    private fun setupRecyclerView() {
        adapter = ChatAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ArchiveActivity)
            adapter = this@ArchiveActivity.adapter
        }
    }

    private fun observeMessages() {
        viewModel.displayedMessages.observe(this) { messages ->
            // Show all messages in archive
            adapter.submitList(messages)
            
            // Update empty state
            if (messages.isEmpty()) {
                binding.emptyStateText.visibility = android.view.View.VISIBLE
                binding.recyclerView.visibility = android.view.View.GONE
            } else {
                binding.emptyStateText.visibility = android.view.View.GONE
                binding.recyclerView.visibility = android.view.View.VISIBLE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.archive_menu, menu)
        
        // Setup search
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView
        searchView?.queryHint = getString(R.string.archive_search_hint)
        
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterMessages(newText ?: "")
                return true
            }
        })
        
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun filterMessages(query: String) {
        viewModel.displayedMessages.value?.let { messages ->
            val filtered = if (query.isEmpty()) {
                messages
            } else {
                messages.filter { 
                    it.message.contains(query, ignoreCase = true) ||
                    it.sender.contains(query, ignoreCase = true)
                }
            }
            adapter.submitList(filtered)
        }
    }
}
