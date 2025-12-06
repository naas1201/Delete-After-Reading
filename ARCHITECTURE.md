# Architecture Documentation

## Overview

The Notification Thriller follows **MVVM (Model-View-ViewModel)** architecture pattern with a **Repository** layer for data access. This document explains the architecture decisions and component interactions.

## Architecture Layers

### 1. Data Layer (`data/` package)

#### Message.kt - Data Model
```kotlin
@Entity(tableName = "messages")
data class Message(
    @PrimaryKey val id: Int,
    val sender: String,
    val message: String,
    val delaySeconds: Long,
    val isDisplayed: Boolean = false,
    val timestamp: Long = 0L
)
```

**Purpose**: Serves dual role as:
- JSON parsing model (with Gson)
- Room database entity

**Fields**:
- `id`: Unique identifier from JSON
- `sender`: Display name in chat
- `message`: Message content
- `delaySeconds`: Real-time delay for notification
- `isDisplayed`: Flag to track if message has been shown
- `timestamp`: When message was displayed (epoch millis)

#### MessageDao.kt - Database Access
```kotlin
@Dao
interface MessageDao {
    fun getDisplayedMessages(): LiveData<List<Message>>
    suspend fun markMessageAsDisplayed(messageId: Int, timestamp: Long)
    // ... other CRUD operations
}
```

**Purpose**: Defines Room database operations
- Uses LiveData for reactive UI updates
- Suspend functions for coroutine-based async operations
- Efficient queries with SQL annotations

#### AppDatabase.kt - Database Configuration
```kotlin
@Database(entities = [Message::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
}
```

**Purpose**: Room database singleton
- Thread-safe instance creation
- Single source of truth for data

#### MessageRepository.kt - Repository Pattern
```kotlin
class MessageRepository(private val context: Context) {
    private val messageDao: MessageDao
    
    suspend fun loadMessagesFromJson(resourceId: Int): List<Message>
    suspend fun initializeDatabase(messages: List<Message>)
    fun getDisplayedMessages(): LiveData<List<Message>>
}
```

**Purpose**: Abstracts data sources
- Handles JSON parsing from raw resources
- Manages database initialization
- Provides clean API for ViewModels
- Coordinates between different data sources

**Key Methods**:
1. `loadMessagesFromJson()`: Parses game_messages.json using Gson
2. `initializeDatabase()`: Populates Room with game data
3. `getDisplayedMessages()`: Returns LiveData for reactive UI

### 2. UI Layer (`ui/` package)

#### MainActivity.kt - View
```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ChatViewModel
    private lateinit var adapter: ChatAdapter
}
```

**Purpose**: Main activity and View layer
- Uses ViewBinding for type-safe view access
- Observes LiveData from ViewModel
- Handles permission requests (Android 13+ notifications)
- Manages RecyclerView setup

**Lifecycle**:
1. `onCreate()`: Initialize ViewBinding, ViewModel, RecyclerView
2. `checkNotificationPermissionAndInitialize()`: Request permissions
3. `observeMessages()`: Set up LiveData observation
4. `onCreateOptionsMenu()`: Add reset game option

#### ChatViewModel.kt - ViewModel
```kotlin
class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MessageRepository(application)
    val displayedMessages: LiveData<List<Message>>
    
    fun initializeGame()
    fun resetGame()
}
```

**Purpose**: Business logic and state management
- Survives configuration changes
- Manages WorkManager scheduling
- Exposes LiveData to UI
- Handles game initialization and reset

**Key Methods**:
1. `initializeGame()`: Load JSON → Initialize DB → Schedule notifications
2. `scheduleNotifications()`: Create WorkManager tasks for each message
3. `resetGame()`: Cancel all work → Re-initialize

#### ChatAdapter.kt - RecyclerView Adapter
```kotlin
class ChatAdapter : ListAdapter<Message, MessageViewHolder>(MessageDiffCallback())
```

**Purpose**: Efficient list rendering
- Uses ListAdapter with DiffUtil for optimal updates
- ViewHolder pattern for view recycling
- Material Card design for message bubbles

**DiffUtil Benefits**:
- Only updates changed items
- Smooth animations
- Reduced UI lag

### 3. Workers Layer (`workers/` package)

#### MessageNotificationWorker.kt - Background Work
```kotlin
class MessageNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        // Get message ID from input data
        // Update database
        // Show notification
    }
}
```

**Purpose**: Scheduled background tasks
- Extends CoroutineWorker for suspend function support
- Triggered by WorkManager after specified delay
- Updates database (marks message as displayed)
- Creates and shows notification

**Workflow**:
1. Receives `MESSAGE_ID_KEY` from WorkManager
2. Fetches message from Repository
3. Marks message as displayed with timestamp
4. Shows notification via NotificationManager
5. Creates notification channel (Android O+)

## Data Flow

### Initialization Flow
```
MainActivity.onCreate()
    ↓
ChatViewModel.initializeGame()
    ↓
Repository.loadMessagesFromJson()
    ↓
Repository.initializeDatabase()
    ↓
ChatViewModel.scheduleNotifications()
    ↓
WorkManager.enqueue() × N messages
```

### Notification Flow
```
WorkManager (after delay)
    ↓
MessageNotificationWorker.doWork()
    ↓
Repository.getMessageById()
    ↓
Repository.markMessageAsDisplayed()
    ↓
Room Database Update
    ↓
LiveData triggers
    ↓
MainActivity observes change
    ↓
ChatAdapter.submitList()
    ↓
RecyclerView updates UI
```

## Design Patterns

### 1. MVVM (Model-View-ViewModel)
- **Model**: Room entities + Repository
- **View**: MainActivity + XML layouts
- **ViewModel**: ChatViewModel

**Benefits**:
- Clear separation of concerns
- Testable business logic
- Lifecycle-aware components

### 2. Repository Pattern
- Single source of truth
- Abstracts data sources (JSON + Room)
- Easy to mock for testing

### 3. Observer Pattern
- LiveData for reactive updates
- Automatic lifecycle management
- No memory leaks

### 4. ViewHolder Pattern
- Efficient RecyclerView rendering
- View recycling for performance
- Reduces memory usage

## Threading Model

### Main Thread
- UI updates (MainActivity, RecyclerView)
- LiveData observation
- ViewBinding access

### Background Threads
- Room database operations (via coroutines)
- JSON parsing
- WorkManager execution
- Repository operations

### Coroutines Usage
```kotlin
viewModelScope.launch {
    // Automatically cancelled when ViewModel cleared
    repository.initializeDatabase(messages)
}
```

**Benefits**:
- Structured concurrency
- Automatic cancellation
- No callback hell

## WorkManager Configuration

### OneTimeWorkRequest
```kotlin
val notificationWork = OneTimeWorkRequestBuilder<MessageNotificationWorker>()
    .setInitialDelay(message.delaySeconds, TimeUnit.SECONDS)
    .setInputData(inputData)
    .addTag("message_${message.id}")
    .build()
```

**Features**:
- Exact delays via `setInitialDelay()`
- Input data passed to worker
- Tags for work identification
- Guaranteed execution (survives app restart)

### Constraints
- No network required
- No battery constraints
- Runs even in Doze mode (with SCHEDULE_EXACT_ALARM permission)

## ViewBinding

### Setup
```kotlin
android {
    buildFeatures {
        viewBinding = true
    }
}
```

### Usage
```kotlin
private lateinit var binding: ActivityMainBinding

override fun onCreate(savedInstanceState: Bundle?) {
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    
    // Type-safe access
    binding.recyclerView.adapter = adapter
    binding.toolbar.title = "Game Title"
}
```

**Benefits**:
- Null safety
- Type safety
- No findViewById() calls
- Compile-time verification

## Notification System

### Channel Creation (Android O+)
```kotlin
val channel = NotificationChannel(
    CHANNEL_ID,
    "Message Notifications",
    NotificationManager.IMPORTANCE_HIGH
)
```

### Notification Building
```kotlin
NotificationCompat.Builder(context, CHANNEL_ID)
    .setSmallIcon(R.drawable.ic_launcher)
    .setContentTitle(sender)
    .setContentText(message)
    .setStyle(BigTextStyle().bigText(message))
    .setPriority(PRIORITY_HIGH)
    .setContentIntent(pendingIntent)
    .setAutoCancel(true)
```

**Features**:
- High priority for heads-up display
- BigTextStyle for long messages
- PendingIntent to open app
- Auto-dismiss on tap

## Testing Strategy

### Unit Tests
- **Repository**: Mock Room DAO
- **ViewModel**: Mock Repository
- **Worker**: WorkManagerTestInitHelper

### Integration Tests
- Database operations
- JSON parsing
- End-to-end message flow

### UI Tests
- RecyclerView display
- Permission handling
- Navigation

## Performance Considerations

### Database
- Room uses SQLite with optimized queries
- LiveData prevents unnecessary updates
- Indexed primary keys for fast lookups

### UI
- ListAdapter with DiffUtil calculates minimal changes
- ViewHolder pattern recycles views
- CardView with elevation uses hardware acceleration

### Background Work
- WorkManager handles battery optimization
- Doze mode compatibility
- Guaranteed execution with constraints

## Security

### Permissions
- Runtime permission request for notifications
- Exact alarm permissions declared
- No internet required (offline game)

### Data
- Local storage only (Room database)
- No external API calls
- No user data collection

## Scalability

### Adding Features
- **User choices**: Add choice_id field to Message
- **Multiple endings**: Branch logic in ViewModel
- **Save states**: Add user profile entity
- **Analytics**: Inject analytics repository

### Performance at Scale
- Room supports thousands of messages
- WorkManager handles concurrent workers
- RecyclerView efficiently renders large lists

## Dependencies Injection (Future)

Current: Manual dependency creation
```kotlin
private val repository = MessageRepository(application)
```

Future: Use Hilt/Dagger
```kotlin
@Inject lateinit var repository: MessageRepository
```

**Benefits**:
- Easier testing with mocks
- Better separation of concerns
- Automatic lifecycle management

## Conclusion

This architecture provides:
- ✅ Maintainability through separation of concerns
- ✅ Testability via MVVM and Repository patterns
- ✅ Scalability with Room and WorkManager
- ✅ Performance through efficient UI rendering
- ✅ Reliability with guaranteed background execution

The app demonstrates production-ready Android development with modern architecture components and best practices.
