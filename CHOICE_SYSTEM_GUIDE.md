# AAA Choice System - Developer Guide

## Overview

This guide explains how to integrate and use the advanced choice tracking system in The Notification Thriller. This system rivals AAA titles in depth and player agency.

## Quick Start

### 1. Basic Choice Handling in MainActivity

```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: ChatViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ... setup code ...
        
        // Observe consequence notifications
        viewModel.consequenceNotifications.observe(this) { notifications ->
            if (notifications.isNotEmpty()) {
                showConsequenceNotifications(notifications)
            }
        }
        
        // Observe character relationships
        viewModel.characterRelationships.observe(this) { relationships ->
            updateRelationshipUI(relationships)
        }
        
        // Observe story state for playstyle display
        viewModel.storyState.observe(this) { state ->
            updatePlaystyleIndicator(state)
        }
    }
    
    private fun onUserMakesChoice(messageId: Int, choiceId: Int, choiceText: String) {
        // This is the key method call
        viewModel.selectChoice(messageId, choiceId, choiceText)
    }
    
    private fun showConsequenceNotifications(notifications: List<ConsequenceNotification>) {
        ConsequenceDialog.showSequence(this, notifications) {
            // Clear notifications after showing
            viewModel.clearConsequenceNotifications()
        }
    }
    
    private fun updateRelationshipUI(relationships: List<CharacterRelationship>) {
        // Show relationship status in UI
        // Example: Update a sidebar or status bar
    }
    
    private fun updatePlaystyleIndicator(state: StoryState) {
        val playstyle = viewModel.getPlaystyleSummary()
        // Display: "Idealistic • Cautious" or similar
    }
}
```

### 2. Displaying Messages with Dynamic Text

```kotlin
private fun showMessage(message: Message) {
    // Get dynamic version of message that reflects player's choices
    val dynamicText = viewModel.getDynamicMessageText(message)
    
    // Display the personalized message
    messageTextView.text = dynamicText
}
```

### 3. Handling Choices in RecyclerView Adapter

```kotlin
class ChatAdapter : ListAdapter<Message, MessageViewHolder>(MessageDiffCallback()) {
    
    var onChoiceSelected: ((messageId: Int, choiceId: Int, choiceText: String) -> Unit)? = null
    
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = getItem(position)
        
        if (message.isChoiceBranch && message.choices != null) {
            // Show choice buttons
            message.choices.forEach { choice ->
                val button = createChoiceButton(choice)
                button.setOnClickListener {
                    onChoiceSelected?.invoke(message.id, choice.id, choice.text)
                }
                holder.choicesContainer.addView(button)
            }
        }
    }
}
```

## Advanced Features

### Viewing Character Relationships

```kotlin
fun showCharacterProfileDialog(characterName: String) {
    viewModel.characterRelationships.value?.find { it.characterName == characterName }?.let { rel ->
        ConsequenceDialog.showRelationshipStatus(
            context = this,
            characterName = characterName,
            trustLevel = rel.trustLevel,
            status = rel.relationshipStatus,
            recentChoices = getRecentChoicesForCharacter(characterName)
        )
    }
}
```

### Viewing Achievements

```kotlin
fun showAchievementsScreen() {
    viewModel.achievements.observe(this) { achievements ->
        val unlocked = achievements.filter { it.isUnlocked }
        val locked = achievements.filter { !it.isUnlocked && !it.isHidden }
        
        // Display in a list or grid
        displayAchievements(unlocked, locked)
    }
}
```

### Player Statistics Dashboard

```kotlin
fun showStatsDashboard() {
    val stats = viewModel.playerStats.value ?: return
    
    val statsText = """
        Total Choices Made: ${stats.totalChoicesMade}
        Messages Read: ${stats.messagesRead}
        Playtime: ${formatPlaytime(stats.totalPlaytime)}
        Completed Playthroughs: ${stats.completedPlaythroughs}
        Most Trusted: ${stats.mostTrustedCharacter ?: "None"}
        Achievements: ${stats.totalAchievements}
        Secrets Found: ${stats.secretsFound}
    """.trimIndent()
    
    showStatsDialog(statsText)
}
```

### New Game Plus

```kotlin
fun startNewGamePlus() {
    AlertDialog.Builder(this)
        .setTitle("Start New Game+?")
        .setMessage("Your achievements and stats will be preserved. Are you ready to replay with new insights?")
        .setPositiveButton("Yes") { _, _ ->
            viewModel.startNewGamePlus()
        }
        .setNegativeButton("Cancel", null)
        .show()
}
```

## Creating New Content

### Adding Messages with Choices

```json
{
  "id": 100,
  "sender": "Sarah",
  "message": "We need to decide our approach. What do you think?",
  "delaySeconds": 60,
  "isChoiceBranch": true,
  "choices": [
    {
      "id": 1,
      "text": "Let's be honest and direct",
      "nextMessageId": 101
    },
    {
      "id": 2,
      "text": "We should be more careful",
      "nextMessageId": 102
    },
    {
      "id": 3,
      "text": "Maybe we should take aggressive action",
      "nextMessageId": 103
    }
  ]
}
```

### Choice Keywords for Automatic Detection

The choice engine analyzes choice text for keywords to determine impact:

**Morality Keywords:**
- Empathetic: "help", "save", "protect", "care"
- Honest: "truth", "tell", "honest", "reveal"
- Aggressive: "fight", "attack", "force", "destroy"
- Selfish: "me", "I", "my advantage"

**Caution Keywords:**
- Cautious: "careful", "wait", "safe", "plan", "think"
- Reckless: "now", "immediately", "rush", "risk"

### Character-Specific Reactions

Define in `ChoiceEngine.kt`:

```kotlin
when (messageId) {
    100 -> { // Your message ID
        when (choiceId) {
            1 -> { // Honest choice
                effects["Sarah"] = CharacterEffect(
                    trustDelta = 10,
                    note = "Sarah deeply appreciates your honesty"
                )
            }
            2 -> { // Cautious choice
                effects["Sarah"] = CharacterEffect(
                    trustDelta = 5,
                    note = "Sarah respects your caution"
                )
            }
            3 -> { // Aggressive choice
                effects["Sarah"] = CharacterEffect(
                    trustDelta = -5,
                    note = "Sarah is concerned by your aggression"
                )
            }
        }
    }
}
```

## Testing Your Choices

### Test Script Example

```kotlin
// Test choice impact
fun testChoiceSystem() {
    viewModelScope.launch {
        // Make a choice
        selectChoice(messageId = 2, choiceId = 1, choiceText = "I'm listening")
        
        // Check immediate consequences
        delay(100)
        val sarah = database.characterRelationshipDao().getRelationship("Sarah")
        assert(sarah?.trustLevel == 60) // Started at 50, +10 for trusting choice
        
        // Check story state
        val state = database.storyStateDao().getStoryStateOnce()
        assert(state?.moralityScore != 50) // Should have changed
    }
}
```

### Manual Testing Checklist

- [ ] Make a choice and verify consequence notification appears
- [ ] Check character relationship changes in UI
- [ ] Verify morality/caution scores update
- [ ] Confirm achievements unlock when conditions met
- [ ] Test that characters remember past choices in dialogue
- [ ] Verify different playthroughs feel unique
- [ ] Check New Game+ preserves achievements
- [ ] Test save/load maintains all state

## Performance Considerations

### Database Optimization

```kotlin
// Use coroutines for all database operations
viewModelScope.launch(Dispatchers.IO) {
    database.choiceConsequenceDao().insertConsequence(consequence)
}

// Batch updates when possible
database.withTransaction {
    consequences.forEach { database.choiceConsequenceDao().insertConsequence(it) }
}
```

### Memory Management

```kotlin
// Clear old consequence notifications
private val notificationLimit = 10
if (notifications.size > notificationLimit) {
    _consequenceNotifications.value = notifications.takeLast(notificationLimit)
}
```

## Debugging

### Enable Detailed Logging

```kotlin
class ChoiceEngine(private val context: Context) {
    private val debug = true // Set to true for debugging
    
    fun processChoice(...): ChoiceResult {
        if (debug) {
            Log.d("ChoiceEngine", "Processing choice $choiceId for message $messageId")
            Log.d("ChoiceEngine", "Current morality: ${storyState.moralityScore}")
            Log.d("ChoiceEngine", "Current caution: ${storyState.cautionScore}")
        }
        // ... rest of implementation
    }
}
```

### View Database Contents

```kotlin
// In MainActivity for debugging
private fun dumpDatabaseContents() {
    viewModelScope.launch {
        val consequences = database.choiceConsequenceDao().getAllConsequences().value
        val relationships = database.characterRelationshipDao().getAllRelationships().value
        val state = database.storyStateDao().getStoryStateOnce()
        
        Log.d("Database", "Consequences: ${consequences?.size}")
        Log.d("Database", "Relationships: ${relationships?.size}")
        Log.d("Database", "Story State: $state")
    }
}
```

## Common Issues and Solutions

### Issue: Choices Don't Affect Relationships

**Solution:** Verify choice effects are defined in `ChoiceEngine.determineCharacterEffects()`

### Issue: Achievements Not Unlocking

**Solution:** Check achievement IDs match exactly between `GameInitializer` and unlock calls

### Issue: Dynamic Messages Not Showing

**Solution:** Ensure you're calling `viewModel.getDynamicMessageText()` instead of using raw message text

### Issue: Performance Slowdown

**Solution:** 
- Use pagination for consequence lists
- Limit LiveData observations
- Use Dispatchers.IO for database operations

## Best Practices

### 1. Always Provide Feedback
```kotlin
// BAD: Silent choice selection
viewModel.selectChoice(messageId, choiceId, choiceText)

// GOOD: Show immediate feedback
viewModel.selectChoice(messageId, choiceId, choiceText)
ConsequenceDialog.showSequence(this, notifications)
```

### 2. Make Consequences Visible
```kotlin
// Show trust changes in UI
binding.trustIndicator.progress = relationship.trustLevel
binding.relationshipStatus.text = relationship.relationshipStatus
```

### 3. Guide Player Understanding
```kotlin
// Show tooltip explaining the system
if (isFirstChoice) {
    showTutorialDialog("Your choices affect character relationships and story outcomes")
}
```

### 4. Reward Exploration
```kotlin
// Unlock secret for trying all paths
if (completedPlaythroughs >= 3) {
    unlockAchievement("Master of Choices")
}
```

## Integration Checklist

- [ ] Import all new data classes and DAOs
- [ ] Update database version number
- [ ] Initialize ChoiceEngine in ViewModel
- [ ] Add consequence notification observer
- [ ] Implement choice selection UI
- [ ] Add relationship status display
- [ ] Create achievement screen
- [ ] Add player stats screen
- [ ] Implement New Game+ option
- [ ] Test all features thoroughly
- [ ] Add analytics tracking (optional)

## Next Steps

1. **Phase 1:** Implement basic choice handling
2. **Phase 2:** Add consequence notifications
3. **Phase 3:** Display character relationships
4. **Phase 4:** Show achievements and stats
5. **Phase 5:** Implement New Game+
6. **Phase 6:** Polish and balance

---

**Remember:** This system is designed to make players feel like their choices truly matter. Every decision should have weight, consequences should be clear, and replayability should be rewarding.
