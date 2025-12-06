# GitHub Copilot Instructions for The Notification Thriller

## Project Overview

**The Notification Thriller** is a production-ready text-based RPG game implemented as a fake messenger app for Android. The game creates an immersive story experience that unfolds in real-time through scheduled notifications.

### Tech Stack
- **Language**: Kotlin (100% native)
- **Platform**: Android (minSdk 24, targetSdk 34)
- **Architecture**: MVVM (Model-View-ViewModel) + Repository Pattern
- **UI**: ViewBinding with Material Design 3
- **Database**: Room (SQLite)
- **Background Work**: WorkManager for scheduled notifications
- **Build System**: Gradle with Kotlin DSL
- **Coroutines**: For asynchronous operations
- **LiveData**: For reactive UI updates

## Project Structure

```
app/src/main/
├── java/com/example/notificationthriller/
│   ├── data/              # Data layer: Room entities, DAOs, Repository
│   ├── ui/                # UI layer: Activities, ViewModels, Adapters
│   ├── workers/           # Background workers using WorkManager
│   ├── utils/             # Utility managers (Sound, Haptic, Analytics)
│   ├── billing/           # In-app purchase logic
│   └── NotificationThrillerApp.kt
├── res/
│   ├── layout/            # XML layouts
│   ├── values/            # Strings, themes, colors (with localization)
│   ├── drawable/          # Icons and images
│   ├── menu/              # Menu definitions
│   └── raw/               # JSON data files (game_messages.json)
└── AndroidManifest.xml
```

### Key Components

#### Data Layer (`data/`)
- `Message.kt` - Room entity for messages with choice branches
- `GameState.kt` - Save game state entity
- `Choice.kt` - User choice model
- `MessageDao.kt` - Database access object
- `MessageRepository.kt` - Repository pattern implementation
- `Converters.kt` - Room type converters for complex types
- `AppDatabase.kt` - Room database configuration

#### UI Layer (`ui/`)
- `MainActivity.kt` - Main chat interface
- `ChatViewModel.kt` - MVVM ViewModel with business logic
- `ChatAdapter.kt` - RecyclerView adapter with DiffUtil

#### Workers (`workers/`)
- `MessageNotificationWorker.kt` - Handles scheduled notifications with real-time delays

#### Managers (`utils/`)
- `SoundManager.kt` - Audio effects management
- `HapticManager.kt` - Vibration feedback
- `AnalyticsManager.kt` - Firebase Analytics tracking

## Development Setup

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17 or higher
- Android SDK with API level 34

### Build Commands
```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run tests (when implemented)
./gradlew test

# Install on connected device
./gradlew installDebug

# Clean build
./gradlew clean
```

### Important Files
- `app/build.gradle.kts` - Build configuration and dependencies
- `app/src/main/res/raw/game_messages.json` - Game narrative content
- `app/google-services.json` - Firebase configuration (placeholder included)

## Coding Standards and Conventions

### Kotlin Style
- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names (camelCase)
- Classes use PascalCase
- Constants use UPPER_SNAKE_CASE
- Add KDoc comments for public APIs
- Keep functions small and focused

### Architecture Patterns
- **MVVM**: Strict separation between View, ViewModel, and Model
- **Repository Pattern**: All data access goes through repositories
- **Suspend Functions**: Use for all async operations
- **LiveData**: For reactive UI updates
- **ViewBinding**: Always use ViewBinding (no findViewById)
- **Coroutines**: Use for background operations with proper scope management

### File Organization
1. Package declaration
2. Imports (grouped and sorted)
3. Class documentation (KDoc)
4. Class declaration
5. Companion object (if any)
6. Properties
7. Init blocks
8. Lifecycle methods (in order)
9. Public methods
10. Private methods
11. Nested classes

### Naming Conventions
- **Classes**: PascalCase (`MessageAdapter`)
- **Functions**: camelCase (`loadMessages()`)
- **Constants**: UPPER_SNAKE_CASE (`MAX_RETRY_COUNT`)
- **Resources**: snake_case (`activity_main.xml`, `fragment_chat.xml`)
- **Layout IDs**: component_purpose (`textView_message`, `button_send`)

### Comments
```kotlin
/**
 * JavaDoc/KDoc style for classes and public methods.
 * Include @param and @return tags when appropriate.
 */
class MyClass {
    // Single line for implementation details
    
    /* Block comment for complex logic explanations */
}
```

## Common Development Tasks

### Adding New Messages
Edit `app/src/main/res/raw/game_messages.json`:
```json
{
  "id": 100,
  "sender": "Character Name",
  "message": "Message text",
  "delaySeconds": 60,
  "isChoiceBranch": false,
  "choices": []
}
```

### Adding Messages with Choices
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

### Adding Strings (Localization-Ready)
Always add strings to `res/values/strings.xml` for localization support:
```xml
<string name="new_feature">Text here</string>
```

Current supported languages:
- English (default): `res/values/`
- Spanish: `res/values-es/`
- French: `res/values-fr/`

### Database Changes
If you modify Room entities:
1. Update the data class
2. Increment version in `AppDatabase.kt`
3. Provide migration strategy or use `fallbackToDestructiveMigration()`

### Working with WorkManager
Schedule notifications with delays:
```kotlin
val workRequest = OneTimeWorkRequestBuilder<MessageNotificationWorker>()
    .setInitialDelay(delaySeconds, TimeUnit.SECONDS)
    .setInputData(workDataOf("MESSAGE_ID" to messageId))
    .build()
WorkManager.getInstance(context).enqueue(workRequest)
```

## Testing Guidelines

### For New Features
- Write unit tests for Repository and ViewModel logic
- Test Room DAO operations
- Test WorkManager scheduling
- Verify notification behavior

### Testing Notifications
Use shorter delays during development:
- Development: 5-15 seconds
- Production: 60+ seconds

## Permissions Required
The app uses these permissions (already declared):
- `POST_NOTIFICATIONS` - For notifications (Android 13+)
- `SCHEDULE_EXACT_ALARM` - For precise notification timing
- `VIBRATE` - For haptic feedback
- `INTERNET` - For Firebase Analytics and Billing

## Dependencies Management

### Core Dependencies (Do not modify without good reason)
- AndroidX Core KTX, AppCompat, Material Components
- Lifecycle (ViewModel, LiveData, Runtime)
- Room (Runtime, KTX, Compiler via KSP)
- WorkManager
- Gson
- Firebase BOM and Analytics
- Google Play Billing

### Adding New Dependencies
1. Add to `app/build.gradle.kts`
2. Sync Gradle
3. Test build
4. Document why the dependency is needed

## Important Notes

### Firebase Configuration
- Project includes placeholder `google-services.json` for building
- For production, replace with real Firebase config
- See `SETUP_GUIDE.md` for detailed Firebase setup

### Performance Considerations
- Use Kotlin Coroutines with appropriate dispatchers (IO for database, Main for UI)
- RecyclerView uses ListAdapter with DiffUtil for efficiency
- Room queries return LiveData for automatic updates
- WorkManager ensures reliable background execution

### Security Best Practices
- Never commit real API keys or secrets
- Use BuildConfig for configuration
- Validate user input
- Follow Android security best practices

## Branch and Commit Conventions

### Branch Naming
- `feature/description` - New features
- `fix/description` - Bug fixes
- `refactor/description` - Code refactoring
- `docs/description` - Documentation updates

### Commit Messages
Follow [Conventional Commits](https://www.conventionalcommits.org/):
```
type(scope): subject

Examples:
feat(ui): add dark mode theme
fix(notifications): resolve notification timing issue
docs(readme): update installation instructions
refactor(repository): simplify message loading logic
```

## Resources and Documentation

### Internal Documentation
- `README.md` - Project overview and features
- `ARCHITECTURE.md` - Detailed technical architecture (comprehensive)
- `FEATURES.md` - Feature documentation
- `DEVELOPER_GUIDE.md` - Quick reference for developers
- `CONTRIBUTING.md` - Contribution guidelines
- `SETUP_GUIDE.md` - Firebase and billing setup

### External Resources
- [Android Developers](https://developer.android.com/)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Room Database Guide](https://developer.android.com/training/data-storage/room)
- [WorkManager Guide](https://developer.android.com/topic/libraries/architecture/workmanager)
- [Material Design](https://material.io/design)

## When Making Changes

1. **Understand the architecture**: Review `ARCHITECTURE.md` for detailed component interactions
2. **Follow MVVM**: Keep business logic in ViewModels, UI in Activities/Fragments
3. **Use existing patterns**: Look at similar code before implementing new features
4. **Minimize changes**: Make targeted, surgical modifications
5. **Test locally**: Build and test before committing
6. **Update documentation**: If adding significant features, update relevant docs
7. **Preserve existing behavior**: Don't break working functionality
8. **Consider localization**: Use string resources for user-facing text

## Common Pitfalls to Avoid

- ❌ Don't use `findViewById` - always use ViewBinding
- ❌ Don't do database operations on main thread
- ❌ Don't hardcode strings - use `strings.xml`
- ❌ Don't modify WorkManager behavior without understanding timing implications
- ❌ Don't add dependencies without checking compatibility
- ❌ Don't break existing message format in JSON files
- ❌ Don't modify core architecture patterns without discussion
- ❌ Don't commit real Firebase credentials

## Questions or Issues?

- Review the extensive documentation in the repository root
- Check `DEVELOPER_GUIDE.md` for quick reference
- See `CONTRIBUTING.md` for detailed contribution guidelines
- The codebase is well-documented with inline comments for complex logic
