# Changelog

All notable changes to The Notification Thriller will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2024-12-06

### Added - Initial Release

#### Core Features
- Text-based RPG game in messenger format
- Real-time notification system with actual time delays
- MVVM architecture with ViewBinding
- Room database for persistent storage
- WorkManager for background notification scheduling
- 15-message thriller story about Sarah and Project Midnight

#### Data Layer
- `Message` data model (id, sender, message, delaySeconds, isDisplayed, timestamp)
- `MessageDao` with Room database operations
- `AppDatabase` singleton configuration
- `MessageRepository` with JSON parsing and database management
- Local JSON file (`game_messages.json`) for story content

#### UI Layer
- `MainActivity` with RecyclerView and Material Design
- `ChatViewModel` for business logic and state management
- `ChatAdapter` with ListAdapter and DiffUtil for efficient updates
- Material Card design for message bubbles
- Toolbar with app title and menu options
- Reset game functionality

#### Background Work
- `MessageNotificationWorker` for scheduled notifications
- Notification channel creation (Android O+)
- High-priority notifications with BigTextStyle
- PendingIntent to open app from notification

#### Permissions
- POST_NOTIFICATIONS permission (Android 13+)
- SCHEDULE_EXACT_ALARM permission for precise timing
- Runtime permission handling with ActivityResultContracts

#### UI/UX
- Clean messenger-like interface
- Auto-scroll to newest message
- Timestamp display for each message
- Sender name with blue accent color
- Material Design theming with customizable colors

#### Documentation
- Comprehensive README with tech stack and features
- ARCHITECTURE.md explaining MVVM pattern and data flow
- QUICKSTART.md for 5-minute setup
- CONTRIBUTING.md with coding standards and PR guidelines
- In-code KDoc comments for public APIs

#### Development Tools
- GitHub Actions CI/CD workflow
- Gradle 8.2 with Kotlin DSL
- Android Gradle Plugin 8.1.0
- Kotlin 1.9.0
- KSP for Room annotation processing

#### Dependencies
- AndroidX Core KTX 1.12.0
- AppCompat 1.6.1
- Material Components 1.11.0
- ConstraintLayout 2.1.4
- Lifecycle (ViewModel, LiveData, Runtime) 2.7.0
- Room (Runtime, KTX, Compiler) 2.6.1
- WorkManager 2.9.0
- Gson 2.10.1
- RecyclerView 1.3.2

#### Project Configuration
- Min SDK 24 (Android 7.0)
- Target SDK 34 (Android 14)
- Compile SDK 34
- JDK 17 compatibility
- ViewBinding enabled
- ProGuard configuration ready

#### Story Content
- 15 sequential messages
- Multiple characters: Unknown, Sarah, System, The Architect
- Delays ranging from 5 seconds to 7 minutes
- Thriller narrative about corporate conspiracy
- Cliffhanger ending for future expansion

### Technical Highlights

#### Architecture Patterns
- MVVM (Model-View-ViewModel)
- Repository Pattern
- Observer Pattern (LiveData)
- ViewHolder Pattern (RecyclerView)
- Singleton Pattern (Database)

#### Threading
- Coroutines for async operations
- Main thread for UI updates
- Background threads for database and WorkManager
- viewModelScope for automatic cancellation

#### Code Quality
- Kotlin coding conventions
- Clean code principles
- SOLID principles
- Comprehensive documentation
- Type-safe view access

## [Unreleased]

### Planned Features
- [ ] User choice system (branching narratives)
- [ ] Multiple story paths based on decisions
- [ ] Save/load game states
- [ ] Dark mode theme
- [ ] Sound effects and haptic feedback
- [ ] Achievement system
- [ ] Localization support (multiple languages)
- [ ] Analytics integration
- [ ] Unit and instrumentation tests
- [ ] Encrypted message mini-game
- [ ] Character profiles
- [ ] Image attachments in messages

### Future Improvements
- [ ] Database migration strategy
- [ ] Custom notification sounds
- [ ] Widget support for quick access
- [ ] Wear OS companion app
- [ ] Cloud backup for game progress
- [ ] Share story moments
- [ ] Alternative endings
- [ ] Time-skip feature for testing

## Version History

### Version Format
- Major.Minor.Patch (e.g., 1.2.3)
- Major: Breaking changes, major new features
- Minor: New features, backward compatible
- Patch: Bug fixes, small improvements

### Release Notes Template
```
## [X.Y.Z] - YYYY-MM-DD

### Added
- New features

### Changed
- Changes to existing functionality

### Deprecated
- Soon-to-be removed features

### Removed
- Removed features

### Fixed
- Bug fixes

### Security
- Security improvements
```

## Development Milestones

### Milestone 1: MVP ✅
- [x] Basic game structure
- [x] Real-time notifications
- [x] Database persistence
- [x] Message display UI

### Milestone 2: Documentation ✅
- [x] README with full details
- [x] Architecture documentation
- [x] Quick start guide
- [x] Contributing guidelines

### Milestone 3: Polish (Future)
- [ ] Animations and transitions
- [ ] Sound design
- [ ] Advanced UI features
- [ ] Testing coverage

### Milestone 4: Expansion (Future)
- [ ] Multiple stories
- [ ] User-generated content
- [ ] Community features
- [ ] Advanced game mechanics

## Links

- [Repository](https://github.com/naas1201/Delete-After-Reading)
- [Issues](https://github.com/naas1201/Delete-After-Reading/issues)
- [Pull Requests](https://github.com/naas1201/Delete-After-Reading/pulls)
- [Discussions](https://github.com/naas1201/Delete-After-Reading/discussions)

## Credits

### Technologies
- Android Platform by Google
- Kotlin Programming Language by JetBrains
- Material Design by Google
- Room Persistence Library by Google
- WorkManager by Google
- Gson by Google

### Inspiration
- Text-based adventure games
- Messenger apps
- Interactive fiction
- Thriller novels
- ARG (Alternate Reality Games)

---

**Note**: This project is under active development. Features and APIs may change.
