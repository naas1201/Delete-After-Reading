# Developer Quick Reference Guide

Quick reference for common development tasks and features.

## Quick Start

```bash
# Clone and build
git clone <repository-url>
cd Delete-After-Reading
./gradlew assembleDebug

# Run on device
./gradlew installDebug
```

## Architecture Overview

```
app/
├── billing/          # In-app purchase logic
├── data/            # Database, DAOs, models
├── ui/              # Activities, ViewModels, Adapters
├── utils/           # Managers (Sound, Haptic, Analytics)
└── workers/         # Background notification workers
```

## Key Classes

### Data Layer
- `Message.kt` - Message entity with choices
- `GameState.kt` - Save game state
- `Choice.kt` - User choice model
- `Converters.kt` - Room type converters
- `MessageDao.kt` - Database access
- `MessageRepository.kt` - Data repository

### UI Layer
- `MainActivity.kt` - Main chat interface
- `ChatViewModel.kt` - MVVM ViewModel
- `ChatAdapter.kt` - RecyclerView adapter

### Managers
- `SoundManager.kt` - Audio effects
- `HapticManager.kt` - Vibration feedback
- `AnalyticsManager.kt` - Firebase events
- `BillingManager.kt` - In-app purchases

## Common Tasks

### Add New Message with Choice

```json
{
  "id": 100,
  "sender": "Character",
  "message": "What will you do?",
  "delaySeconds": 30,
  "isChoiceBranch": true,
  "choices": [
    {
      "id": 1,
      "text": "Choice A",
      "nextMessageId": 101
    },
    {
      "id": 2,
      "text": "Choice B",
      "nextMessageId": 102
    }
  ]
}
```

### Track Analytics Event

```kotlin
analyticsManager.logCustomEvent("custom_event") {
    putString("param_key", "value")
    putInt("count", 42)
}
```

### Play Sound Effect

```kotlin
soundManager.playMessageReceived()
soundManager.playChoiceSelect()
soundManager.playNotification()
```

### Add Haptic Feedback

```kotlin
hapticManager.lightTap()     // Navigation
hapticManager.mediumTap()    // Button press
hapticManager.heavyTap()     // Important action
hapticManager.doubleClick()  // Special event
```

### Save Game State

```kotlin
viewModel.saveGame("Save Name")

// Observe result
viewModel.saveLoadResult.observe(this) { result ->
    when (result) {
        is SaveLoadResult.SaveSuccess -> {
            // Handle success
        }
        is SaveLoadResult.Error -> {
            // Handle error
        }
    }
}
```

### Load Game State

```kotlin
viewModel.loadGame(gameStateId)
```

### Handle User Choice

```kotlin
viewModel.selectChoice(messageId, choiceId)
```

## Localization

### Add New Language

1. Create directory: `res/values-{code}/`
2. Copy `strings.xml` from `values/`
3. Translate all strings
4. Test with device language

Example codes:
- Spanish: `es`
- French: `fr`
- German: `de`
- Japanese: `ja`
- Chinese: `zh`

### Extract New String

```xml
<!-- strings.xml -->
<string name="new_string">New Text</string>
```

```kotlin
// In code
textView.text = getString(R.string.new_string)
```

## Testing

### Unit Tests (when implemented)
```bash
./gradlew test
```

### Instrumentation Tests (when implemented)
```bash
./gradlew connectedAndroidTest
```

### Test Notifications
Set short delays in JSON for testing:
```json
{
  "delaySeconds": 5  // 5 seconds instead of 300
}
```

## Debugging

### Enable Verbose Logging

```kotlin
// In Application class
if (BuildConfig.DEBUG) {
    // Enable debug logging
}
```

### Check Database

Use Android Studio Database Inspector:
1. Run app on device
2. View → Tool Windows → App Inspection
3. Select Database Inspector
4. Inspect `messages` and `game_states` tables

### Monitor WorkManager

```bash
# Check scheduled work
adb shell dumpsys jobscheduler
```

## Performance Tips

### Reduce APK Size

1. Enable ProGuard/R8:
```kotlin
buildTypes {
    release {
        isMinifyEnabled = true
        shrinkResources = true
    }
}
```

2. Use WebP images instead of PNG
3. Remove unused resources
4. Use Android App Bundle

### Optimize Notifications

```kotlin
// Batch notifications when possible
// Use appropriate priority levels
// Respect user notification preferences
```

### Database Best Practices

```kotlin
// Use coroutines for DB operations
viewModelScope.launch {
    withContext(Dispatchers.IO) {
        // Database operation
    }
}

// Use LiveData for automatic updates
// Implement proper indexing
// Clean up old data periodically
```

## Common Issues & Solutions

### Issue: Build Fails with Firebase Error

**Solution**: Ensure `google-services.json` exists (placeholder or real)

### Issue: Notifications Not Appearing

**Check**:
1. Permission granted: `POST_NOTIFICATIONS`
2. WorkManager scheduled correctly
3. Device not in Do Not Disturb mode
4. Notification channel created

### Issue: Haptic Feedback Not Working

**Check**:
1. `VIBRATE` permission in manifest
2. Device supports vibration
3. System vibration enabled
4. Battery saver not blocking

### Issue: Sounds Not Playing

**Check**:
1. Audio files exist in `res/raw/`
2. Device volume not muted
3. Audio focus handled correctly
4. SoundPool initialized properly

### Issue: Billing Not Working

**Check**:
1. App uploaded to Play Console test track
2. Test account added
3. Products configured and Active
4. 24 hours passed since product creation
5. Correct package name and signing

## Git Workflow

### Feature Branch

```bash
git checkout -b feature/new-feature
# Make changes
git add .
git commit -m "Add new feature"
git push origin feature/new-feature
```

### Before Committing

```bash
# Format code
./gradlew ktlintFormat  # if ktlint configured

# Run tests
./gradlew test

# Build release
./gradlew assembleRelease
```

## Release Checklist

- [ ] Update version code and name
- [ ] Update CHANGELOG.md
- [ ] Test all features
- [ ] Test on multiple devices/screen sizes
- [ ] Verify translations
- [ ] Update store listing
- [ ] Generate signed release build
- [ ] Test release build
- [ ] Upload to Play Console
- [ ] Monitor crash reports
- [ ] Monitor analytics

## Useful Commands

### Gradle

```bash
# Clean build
./gradlew clean

# Dependency tree
./gradlew app:dependencies

# Check for updates
./gradlew dependencyUpdates  # with gradle-versions-plugin

# Analyze APK
./gradlew app:analyzeReleaseBundle
```

### ADB

```bash
# Clear app data
adb shell pm clear com.example.notificationthriller

# Force stop app
adb shell am force-stop com.example.notificationthriller

# Take screenshot
adb shell screencap -p /sdcard/screen.png
adb pull /sdcard/screen.png

# Record screen
adb shell screenrecord /sdcard/demo.mp4

# View logs
adb logcat -s NotificationThriller
```

## Resources

### Documentation
- [FEATURES.md](FEATURES.md) - Feature details
- [SETUP_GUIDE.md](SETUP_GUIDE.md) - Firebase & Billing setup
- [README.md](README.md) - Project overview

### External
- [Android Developers](https://developer.android.com)
- [Kotlin Docs](https://kotlinlang.org/docs)
- [Material Design](https://material.io)
- [Firebase Docs](https://firebase.google.com/docs)

## Code Style

### Naming Conventions

- **Classes**: PascalCase (`MessageAdapter`)
- **Functions**: camelCase (`loadMessages()`)
- **Constants**: UPPER_SNAKE_CASE (`MAX_RETRY_COUNT`)
- **Resources**: snake_case (`activity_main.xml`)

### Comments

```kotlin
/**
 * JavaDoc style for classes and public methods
 */
class MyClass {
    // Single line for implementation details
    private fun helper() {
        /* Block comment for complex logic */
    }
}
```

### File Organization

1. Package declaration
2. Imports (grouped and sorted)
3. Class documentation
4. Class declaration
5. Companion object (if any)
6. Properties
7. Init blocks
8. Lifecycle methods
9. Public methods
10. Private methods
11. Nested classes

## Getting Help

1. Check documentation files
2. Search existing issues on GitHub
3. Review Android Developer docs
4. Stack Overflow with relevant tags
5. Open new issue with reproduction steps

## Contributing

1. Fork repository
2. Create feature branch
3. Make changes
4. Add tests
5. Update documentation
6. Submit pull request

---

**Happy Coding!** 🚀
