# Quick Start Guide

Get "The Notification Thriller" running on your device in 5 minutes!

## Prerequisites Check

Before you begin, ensure you have:
- ✅ Android device or emulator (Android 7.0+ / API 24+)
- ✅ Android Studio installed
- ✅ JDK 17 or higher
- ✅ USB debugging enabled (for physical device)

## Step 1: Clone and Open

```bash
# Clone the repository
git clone https://github.com/naas1201/Delete-After-Reading.git

# Navigate to project
cd Delete-After-Reading

# Open in Android Studio
# File -> Open -> Select project folder
```

## Step 2: Sync and Build

1. Wait for Android Studio to index the project
2. Click **"Sync Project with Gradle Files"** (elephant icon in toolbar)
3. Wait for dependencies to download (~2-3 minutes on first run)

## Step 3: Configure Device

### Option A: Physical Device
1. Enable Developer Options:
   - Settings → About Phone → Tap "Build Number" 7 times
2. Enable USB Debugging:
   - Settings → Developer Options → USB Debugging
3. Connect device via USB
4. Accept "Allow USB Debugging" prompt on device

### Option B: Emulator
1. Open AVD Manager: Tools → Device Manager
2. Create device if needed (recommended: Pixel 6, API 34)
3. Click ▶️ to start emulator

## Step 4: Run the App

1. Select your device from dropdown in toolbar
2. Click **Run** (green ▶️ button) or press `Shift + F10`
3. Wait for app to install and launch

## Step 5: Grant Permissions

When the app launches:
1. Tap **"Allow"** when prompted for notification permission
2. The game will initialize automatically

## Playing the Game

### Understanding the Interface
- **Messenger UI**: Displays messages as they arrive
- **Toolbar**: Shows game title
- **Menu**: Tap three dots → "Reset Game" to restart

### How It Works
1. **Initialization**: Game loads 15 messages from JSON
2. **Scheduling**: Each message is scheduled with real delays
3. **Notifications**: You'll receive notifications at specified times
4. **Display**: Messages appear in the chat when notifications arrive

### Timeline Example
```
0:05 → "Hey... you there?"
0:15 → "I need your help..."
0:30 → "They're watching me..."
0:45 → "My name is Sarah..."
...continues for ~7 minutes total
```

## Testing Mode (Quick Testing)

Want to test without waiting? Modify delays in the JSON:

1. Open `app/src/main/res/raw/game_messages.json`
2. Change all `delaySeconds` to small values:
   ```json
   {
     "id": 1,
     "delaySeconds": 5    // 5 seconds instead of 5
   }
   ```
3. Rebuild and run

## Customization Quick Tips

### Change Story
Edit `app/src/main/res/raw/game_messages.json`:
```json
{
  "id": 16,
  "sender": "Your Character",
  "message": "Your custom message",
  "delaySeconds": 60
}
```

### Change Colors
Edit `app/src/main/res/values/themes.xml`:
```xml
<item name="colorPrimary">#FF6B6B</item>  <!-- Red theme -->
```

### Change App Name
Edit `app/src/main/res/values/strings.xml`:
```xml
<string name="app_name">My Custom Game</string>
```

## Troubleshooting

### App Won't Build
```bash
# Clean and rebuild
./gradlew clean build
```

### Notifications Not Appearing
1. Check app settings → Notifications → Ensure enabled
2. Check battery optimization → Allow app to run in background
3. On Android 13+: Ensure notification permission was granted

### Messages Not Updating
- Pull down to refresh (not implemented) or reopen app
- Check if notifications were delivered
- Reset game from menu

### Gradle Sync Failed
1. Check internet connection
2. Try: File → Invalidate Caches / Restart
3. Delete `.gradle` folder and sync again

### Device Not Detected
```bash
# Check ADB connection
adb devices

# Restart ADB
adb kill-server
adb start-server
```

## Build Variants

### Debug Build (Default)
- Fast compilation
- Includes debugging symbols
- Slightly larger APK
```bash
./gradlew assembleDebug
```

### Release Build
- Optimized and minified
- Smaller APK size
- Requires signing configuration
```bash
./gradlew assembleRelease
```

## Project Structure Overview

```
📦 NotificationThriller
├── 📂 app/src/main/
│   ├── 📂 java/.../
│   │   ├── 📂 data/          ← Room database & Repository
│   │   ├── 📂 ui/            ← Activities, ViewModels, Adapters
│   │   └── 📂 workers/       ← Background notification worker
│   ├── 📂 res/
│   │   ├── 📂 layout/        ← UI layouts (XML)
│   │   ├── 📂 values/        ← Strings, colors, themes
│   │   └── 📂 raw/           ← game_messages.json ⭐
│   └── AndroidManifest.xml
├── 📄 build.gradle.kts       ← Project dependencies
└── 📄 README.md              ← Full documentation
```

## Key Files to Know

### Must-Read Files
1. **game_messages.json** - Story content and timing
2. **MainActivity.kt** - Main UI entry point
3. **ChatViewModel.kt** - Business logic
4. **MessageNotificationWorker.kt** - Notification handler

### Can Modify
- `res/values/strings.xml` - Text strings
- `res/values/themes.xml` - Colors and styles
- `res/layout/*.xml` - UI layouts
- `res/raw/game_messages.json` - Game content

### Don't Modify (Unless You Know What You're Doing)
- `AndroidManifest.xml` - App configuration
- `build.gradle.kts` - Build configuration
- `data/` package files - Database structure

## Development Workflow

### Making Changes
```bash
# 1. Edit files in Android Studio
# 2. Save all (Ctrl+S / Cmd+S)
# 3. Build -> Make Project (Ctrl+F9 / Cmd+F9)
# 4. Run app (Shift+F10 / Ctrl+R)
```

### Running Tests
```bash
# Unit tests
./gradlew test

# See results in:
# app/build/reports/tests/testDebugUnitTest/index.html
```

### Viewing Logs
```bash
# In Android Studio: Logcat tab at bottom
# Or via command line:
adb logcat | grep NotificationThriller
```

## Next Steps

### Learn More
- 📖 Read [ARCHITECTURE.md](ARCHITECTURE.md) for deep dive
- 🤝 Check [CONTRIBUTING.md](CONTRIBUTING.md) for contribution guide
- 📚 Review [README.md](README.md) for complete documentation

### Extend the Game
- Add user choice system
- Create multiple story paths
- Add sound effects
- Implement save/load
- Add achievements

### Share Your Creation
- Fork the repository
- Make your changes
- Submit a pull request
- Share with the community!

## Need Help?

### Resources
- 📖 [Android Developer Docs](https://developer.android.com/)
- 💬 [Stack Overflow](https://stackoverflow.com/questions/tagged/android)
- 🎮 Open an Issue on GitHub

### Common Questions

**Q: Can I change the story?**  
A: Yes! Edit `game_messages.json`

**Q: How do I make delays shorter for testing?**  
A: Change `delaySeconds` values in JSON to 5-10 seconds

**Q: Can I add images?**  
A: Yes, but requires code changes to the adapter

**Q: Does this work offline?**  
A: Yes! Completely offline after installation

**Q: Can I publish this to Play Store?**  
A: Yes, but change package name and customize content

---

**🎉 Congratulations!** You're now ready to experience The Notification Thriller!

If you encounter any issues, check the troubleshooting section or open an issue on GitHub.

Happy gaming! 🎮
