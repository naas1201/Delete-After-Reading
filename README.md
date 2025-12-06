# The Notification Thriller

A production-ready text-based RPG game implemented as a fake messenger app for Android. Experience a thrilling story that unfolds in real-time through scheduled notifications.

## 🎮 Game Concept

"The Notification Thriller" is an immersive text RPG where the story unfolds through a messenger-like interface. The game uses **real-time delays** to create suspense - when a message says "Wait 10m", you'll receive a notification after exactly 10 minutes of real time.

### Story Preview
You receive mysterious messages from an unknown sender asking for help. As the story unfolds, you discover Sarah, a researcher who has uncovered something dangerous at NexusCorp. The narrative progresses through timed notifications, creating an authentic messaging experience.

## 🏗️ Architecture

This project follows **Android best practices** and modern architecture patterns:

### Tech Stack
- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **UI**: ViewBinding for type-safe view access
- **Database**: Room for local persistence
- **Background Work**: WorkManager for scheduled notifications
- **UI Components**: RecyclerView with ListAdapter and DiffUtil

### Project Structure
```
com.example.notificationthriller/
├── data/
│   ├── Message.kt              # Data model and Room entity
│   ├── MessageDao.kt            # Database access object
│   ├── AppDatabase.kt           # Room database configuration
│   └── MessageRepository.kt     # Repository pattern implementation
├── ui/
│   ├── MainActivity.kt          # Main chat interface
│   ├── ChatViewModel.kt         # MVVM ViewModel
│   └── ChatAdapter.kt           # RecyclerView adapter
├── workers/
│   └── MessageNotificationWorker.kt  # WorkManager worker for notifications
└── NotificationThrillerApp.kt   # Application class
```

## 📊 Data Model

Messages are stored in a JSON file (`res/raw/game_messages.json`) with the following structure:

```json
{
  "id": 1,
  "sender": "Sarah",
  "message": "I need your help. Something terrible is happening.",
  "delaySeconds": 60
}
```

**Fields:**
- `id`: Unique message identifier
- `sender`: Name of the sender
- `message`: Message content
- `delaySeconds`: Real-time delay before notification (in seconds)

## 🔔 Real-Time Notification System

The game uses **WorkManager** to schedule notifications with precise timing:

1. On game start, all messages are loaded from JSON into Room database
2. WorkManager schedules a unique worker for each message based on `delaySeconds`
3. When the delay expires, the worker:
   - Marks the message as displayed in the database
   - Shows a notification with sender and message content
   - Updates the chat UI when the user opens the app

## 🚀 Features

### Core Gameplay
- ✅ **Real-time gameplay**: Authentic delays using actual time
- ✅ **Persistent storage**: Room database maintains game state
- ✅ **Background notifications**: WorkManager ensures reliable delivery
- ✅ **Material Design UI**: Modern messenger-like interface
- ✅ **MVVM architecture**: Separation of concerns and testability
- ✅ **Permission handling**: Runtime notification permissions (Android 13+)
- ✅ **Game reset**: Reset functionality to replay the story

### AAA Features
- ✅ **Branching narratives**: User choice system with multiple story paths
- ✅ **Save/Load system**: Save and resume game progress anytime
- ✅ **Sound effects**: Professional audio feedback for interactions
- ✅ **Haptic feedback**: Tactile responses for immersive experience
- ✅ **Dark mode**: Full dark theme with automatic switching
- ✅ **Localization**: Multi-language support (English, Spanish, French)
- ✅ **Analytics**: Firebase Analytics for tracking user engagement
- ✅ **In-app purchases**: Monetization ready with Google Play Billing

## 📱 Requirements

- **minSdk**: 24 (Android 7.0)
- **targetSdk**: 34 (Android 14)
- **Compile SDK**: 34

## 🛠️ Building the Project

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17 or higher
- Android SDK with API 34

### Build Commands
```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run tests
./gradlew test

# Install on connected device
./gradlew installDebug
```

## 📦 Dependencies

### Core
- AndroidX Core KTX 1.12.0
- AppCompat 1.6.1
- Material Components 1.11.0
- ConstraintLayout 2.1.4

### Architecture
- Lifecycle ViewModel 2.7.0
- Lifecycle LiveData 2.7.0
- Lifecycle Runtime 2.7.0

### Database
- Room Runtime 2.6.1
- Room KTX 2.6.1
- Room Compiler (KSP) 2.6.1

### Background Work
- WorkManager 2.9.0

### Utilities
- Gson 2.10.1
- RecyclerView 1.3.2
- Preference KTX 1.2.1

### Analytics & Monetization
- Firebase BOM 32.7.0
- Firebase Analytics KTX
- Google Play Billing KTX 6.1.0

## 🎯 Key Implementation Details

### 1. JSON Parsing
`MessageRepository` loads messages from `res/raw/game_messages.json` using Gson:
```kotlin
suspend fun loadMessagesFromJson(resourceId: Int): List<Message>
```

### 2. ChatAdapter (RecyclerView)
Uses `ListAdapter` with `DiffUtil` for efficient updates:
```kotlin
class ChatAdapter : ListAdapter<Message, MessageViewHolder>(MessageDiffCallback())
```

### 3. Repository Pattern
Abstracts data sources and provides a clean API:
```kotlin
class MessageRepository(private val context: Context)
```

### 4. WorkManager Worker
Handles scheduled notifications with real-time delays:
```kotlin
class MessageNotificationWorker : CoroutineWorker
```

## 🎨 Customization

### Adding New Messages
Edit `app/src/main/res/raw/game_messages.json`:
```json
{
  "id": 16,
  "sender": "New Character",
  "message": "Your custom message here",
  "delaySeconds": 300
}
```

### Modifying Delays
- **Quick testing**: Use small values (5, 10, 15 seconds)
- **Production**: Use realistic delays (60, 120, 300 seconds)
- **Extended gameplay**: Use larger delays (600, 1800, 3600 seconds)

### Changing Theme
Edit `app/src/main/res/values/themes.xml` to customize colors:
```xml
<item name="colorPrimary">#2196F3</item>
<item name="colorPrimaryVariant">#1976D2</item>
```

## 🔐 Permissions

The app requires the following permissions:
- `POST_NOTIFICATIONS` - To display notifications (Android 13+)
- `SCHEDULE_EXACT_ALARM` - For precise timing of notifications
- `VIBRATE` - For haptic feedback
- `INTERNET` - For Firebase Analytics and Google Play Billing

## 🧪 Testing

The project structure supports unit and instrumentation testing:
- Repository logic can be tested with mock Room DAOs
- ViewModel can be tested with LiveData observers
- WorkManager workers can be tested with WorkManagerTestInitHelper

## 📚 Documentation

For detailed information about the new features:
- See [FEATURES.md](FEATURES.md) for comprehensive feature documentation
- Check `game_messages_with_choices_example.json` for branching narrative examples
- Review the code comments for implementation details

## 🎮 New Feature Highlights

### Branching Narratives
Create multiple story paths with player choices that affect the outcome. See FEATURES.md for JSON format and implementation details.

### Save/Load System
Players can save their progress and return anytime. All choices and story progress are preserved.

### AAA Polish
- Professional sound effects for all interactions
- Haptic feedback for tactile immersion
- Smooth animations and transitions
- Dark mode for comfortable viewing

### Global Reach
Multi-language support ensures accessibility for international audiences. Currently supporting English, Spanish, and French.

### Monetization Ready
Integrated Google Play Billing for in-app purchases. Ready for premium content, chapter unlocks, or ad removal.

## 📄 License

This is a demonstration project created for educational purposes.

## 👨‍💻 Development

This project demonstrates production-level Android development practices:
- Clean Architecture principles
- SOLID principles
- Kotlin best practices
- Material Design guidelines
- Android Jetpack components
- Coroutines for asynchronous operations

---

**Built with ❤️ using Kotlin and Android Jetpack**