# Contributing to The Notification Thriller

Thank you for your interest in contributing! This document provides guidelines for contributing to the project.

## Development Setup

### Prerequisites
1. **Android Studio** Hedgehog (2023.1.1) or newer
2. **JDK 17** or higher
3. **Android SDK** with API level 34
4. **Git** for version control

### Initial Setup
```bash
# Clone the repository
git clone https://github.com/naas1201/Delete-After-Reading.git
cd Delete-After-Reading

# Open in Android Studio
# File -> Open -> Select the project directory

# Sync Gradle
# Android Studio will prompt you to sync
# Or: File -> Sync Project with Gradle Files
```

## Project Structure

```
app/src/main/
├── java/com/example/notificationthriller/
│   ├── data/              # Data layer (Room, Repository)
│   ├── ui/                # UI layer (Activities, ViewModels, Adapters)
│   ├── workers/           # Background workers (WorkManager)
│   └── NotificationThrillerApp.kt
├── res/
│   ├── layout/            # XML layouts
│   ├── values/            # Strings, themes, colors
│   ├── drawable/          # Icons and images
│   ├── menu/              # Menu definitions
│   └── raw/               # JSON data files
└── AndroidManifest.xml
```

## Coding Standards

### Kotlin Style Guide
- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Add KDoc comments for public APIs
- Keep functions small and focused

### Example
```kotlin
/**
 * Marks a message as displayed and records the timestamp
 * 
 * @param messageId The ID of the message to mark
 * @throws IllegalArgumentException if messageId is invalid
 */
suspend fun markMessageAsDisplayed(messageId: Int) {
    require(messageId > 0) { "Message ID must be positive" }
    val timestamp = System.currentTimeMillis()
    messageDao.markMessageAsDisplayed(messageId, timestamp)
}
```

## Architecture Guidelines

### MVVM Pattern
- **Model**: Data classes, Room entities, Repository
- **View**: Activities, Fragments, XML layouts
- **ViewModel**: Business logic, LiveData exposure

### Data Layer
- Use Room for database operations
- Repository pattern for data abstraction
- Suspend functions for async operations
- LiveData for reactive updates

### UI Layer
- ViewBinding for view access (no findViewById)
- ViewModel for state management
- ListAdapter with DiffUtil for RecyclerView
- Material Design components

### Background Work
- WorkManager for scheduled tasks
- CoroutineWorker for suspend functions
- OneTimeWorkRequest for one-time tasks

## Making Changes

### Branch Naming
- `feature/description` - New features
- `fix/description` - Bug fixes
- `refactor/description` - Code refactoring
- `docs/description` - Documentation updates

### Commit Messages
Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
type(scope): subject

body (optional)

footer (optional)
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation
- `style`: Code style (formatting)
- `refactor`: Code refactoring
- `test`: Adding tests
- `chore`: Maintenance tasks

**Examples:**
```
feat(ui): add dark mode theme

fix(notifications): resolve notification timing issue
Fixes issue where notifications were delayed by 1 second

docs(readme): update installation instructions
```

### Pull Request Process

1. **Create a branch**
   ```bash
   git checkout -b feature/my-new-feature
   ```

2. **Make your changes**
   - Write clean, documented code
   - Follow coding standards
   - Add tests if applicable

3. **Test your changes**
   ```bash
   ./gradlew test
   ./gradlew build
   ```

4. **Commit your changes**
   ```bash
   git add .
   git commit -m "feat(scope): description"
   ```

5. **Push to your fork**
   ```bash
   git push origin feature/my-new-feature
   ```

6. **Create Pull Request**
   - Go to GitHub
   - Click "New Pull Request"
   - Fill in PR template
   - Link related issues

### PR Checklist
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex code
- [ ] Documentation updated
- [ ] No new warnings introduced
- [ ] Tests added/updated
- [ ] All tests pass
- [ ] PR description is clear

## Testing

### Unit Tests
```kotlin
@Test
fun `markMessageAsDisplayed should update database`() = runTest {
    val messageId = 1
    repository.markMessageAsDisplayed(messageId)
    
    val message = repository.getMessageById(messageId)
    assertTrue(message?.isDisplayed == true)
    assertTrue(message?.timestamp > 0)
}
```

### Running Tests
```bash
# Run all tests
./gradlew test

# Run specific test
./gradlew test --tests "MessageRepositoryTest"

# Run with coverage
./gradlew testDebugUnitTest jacocoTestReport
```

## Adding New Features

### Adding a New Message Type
1. Update `Message.kt` data class if needed
2. Modify `game_messages.json`
3. Update UI layouts if special display needed
4. Test with different delay values

### Adding User Choices
1. Add `choices` field to `Message` model
2. Create choice UI in `ChatAdapter`
3. Handle choice selection in `ViewModel`
4. Update WorkManager to schedule based on choice

### Adding Sound Effects
1. Add sound files to `res/raw/`
2. Create `SoundManager` class
3. Play sounds in `MessageNotificationWorker`
4. Add user preference for sound on/off

## Common Tasks

### Modifying Game Data
Edit `app/src/main/res/raw/game_messages.json`:
```json
{
  "id": 16,
  "sender": "New Character",
  "message": "Your message here",
  "delaySeconds": 60
}
```

### Changing Colors
Edit `app/src/main/res/values/themes.xml`:
```xml
<item name="colorPrimary">#YOUR_COLOR</item>
```

### Updating Dependencies
Edit `app/build.gradle.kts`:
```kotlin
dependencies {
    implementation("androidx.room:room-runtime:2.6.1")
}
```

## Troubleshooting

### Build Issues
```bash
# Clean build
./gradlew clean

# Clear Gradle cache
rm -rf ~/.gradle/caches/

# Invalidate Android Studio cache
# File -> Invalidate Caches / Restart
```

### Database Issues
```bash
# Uninstall app to clear database
adb uninstall com.example.notificationthriller

# Or increment database version in AppDatabase.kt
@Database(entities = [Message::class], version = 2)
```

### WorkManager Issues
```bash
# Check scheduled work
adb shell dumpsys jobscheduler

# Clear WorkManager data (uninstall app)
```

## Code Review Guidelines

### For Reviewers
- Be constructive and respectful
- Explain reasoning behind suggestions
- Approve if changes are acceptable
- Request changes if issues found

### For Authors
- Respond to all comments
- Make requested changes
- Explain your decisions
- Re-request review after changes

## Resources

### Android Documentation
- [Android Developers](https://developer.android.com/)
- [Kotlin Docs](https://kotlinlang.org/docs/home.html)
- [Material Design](https://material.io/design)

### Architecture
- [Guide to App Architecture](https://developer.android.com/topic/architecture)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)

### Best Practices
- [Android Best Practices](https://developer.android.com/topic/performance/best-practices)
- [Kotlin Style Guide](https://developer.android.com/kotlin/style-guide)

## Getting Help

- **Questions**: Open a GitHub Discussion
- **Bugs**: Create an Issue with reproduction steps
- **Features**: Propose via Issue before implementing
- **Documentation**: Ask in Discussions

## License

By contributing, you agree that your contributions will be licensed under the same license as the project.

---

Thank you for contributing to The Notification Thriller! 🎮
