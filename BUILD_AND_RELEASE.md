# Build and Release Guide

## Production-Ready Build Setup

This project is configured with comprehensive code quality tools and a production-ready release build process.

## Prerequisites

- **Android Studio**: Hedgehog (2023.1.1) or newer
- **JDK**: 17 or higher
- **Android SDK**: API level 34
- **Gradle**: 8.1.4+ (included via wrapper)

## Code Quality Tools

The project uses multiple tools to ensure code quality:

### 1. **ktlint** - Kotlin Code Style
- Enforces Kotlin coding conventions
- Checks for consistent formatting
- Run: `./gradlew ktlintCheck`
- Auto-fix: `./gradlew ktlintFormat`

### 2. **Detekt** - Static Code Analysis
- Detects code smells and potential bugs
- Enforces best practices
- Run: `./gradlew detekt`
- Reports: `app/build/reports/detekt/`

### 3. **Android Lint** - Android-specific checks
- Validates resources, layouts, and Android APIs
- Checks for performance issues
- Run: `./gradlew lintDebug`
- Reports: `app/build/reports/lint-results-debug.html`

## Build Commands

### Development Builds

```bash
# Clean build
./gradlew clean

# Debug build
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug
```

### Quality Checks

```bash
# Run all pre-build checks (ktlint, detekt, lint)
./gradlew preBuildCheck

# Individual checks
./gradlew ktlintCheck      # Code style
./gradlew detekt           # Static analysis
./gradlew lintDebug        # Android Lint
```

### Production Release Build

```bash
# Full production readiness check and signed APK
./gradlew productionReadyCheck

# Just build signed release APK
./gradlew assembleRelease

# Build signed AAB (for Play Store)
./gradlew bundleRelease
```

## Release Signing Configuration

### Current Setup (Demo/Testing)

The project is configured to use the debug keystore for demonstration purposes:
- **Keystore**: `~/.android/debug.keystore`
- **Alias**: `androiddebugkey`
- **Password**: `android`

### Production Setup

For production releases, configure your own keystore using environment variables:

1. **Create a release keystore** (one-time setup):
```bash
keytool -genkey -v -keystore my-release-key.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias my-key-alias
```

2. **Set environment variables**:
```bash
export RELEASE_KEYSTORE_PATH="/path/to/my-release-key.jks"
export RELEASE_KEYSTORE_PASSWORD="your_keystore_password"
export RELEASE_KEY_ALIAS="my-key-alias"
export RELEASE_KEY_PASSWORD="your_key_password"
```

3. **Build signed release**:
```bash
./gradlew assembleRelease
```

### Alternative: keystore.properties File

Create `keystore.properties` in the project root:
```properties
storeFile=/path/to/my-release-key.jks
storePassword=your_keystore_password
keyAlias=my-key-alias
keyPassword=your_key_password
```

> **Security Note**: Never commit `keystore.properties` or your keystore file to version control!

## Build Outputs

### Debug Builds
- **Location**: `app/build/outputs/apk/debug/`
- **File**: `app-debug.apk`
- **Signed**: Yes (with debug key)
- **Optimized**: No

### Release Builds
- **APK Location**: `app/build/outputs/apk/release/`
- **AAB Location**: `app/build/outputs/bundle/release/`
- **Files**: 
  - `app-release.apk` (for direct installation)
  - `app-release.aab` (for Play Store upload)
- **Signed**: Yes (with release key)
- **Optimized**: Yes (R8 minification + resource shrinking)
- **Signing Scheme**: APK Signature Scheme v2/v3

## Verification

### Verify APK is Signed

The release APK uses APK Signature Scheme v2/v3:
```bash
file app/build/outputs/apk/release/app-release.apk
# Output should show: "with APK Signing Block"
```

### Verify Code Quality

All checks should pass:
```bash
./gradlew preBuildCheck
# Should show: ✓ All pre-build checks passed successfully!
```

### Install and Test

```bash
# Install release APK on device
adb install app/build/outputs/apk/release/app-release.apk

# Or use Gradle
./gradlew installRelease
```

## CI/CD Integration

### GitHub Actions Example

```yaml
name: Build and Test

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      
      - name: Set up JDK 17
        uses: actions/setup-java@v2
        with:
          java-version: '17'
          
      - name: Run quality checks
        run: ./gradlew preBuildCheck
        
      - name: Build release
        run: ./gradlew assembleRelease
        env:
          RELEASE_KEYSTORE_PATH: ${{ secrets.KEYSTORE_PATH }}
          RELEASE_KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
          RELEASE_KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
          RELEASE_KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
```

## Gradle Configuration Details

### Key Features

1. **Code Quality**:
   - ktlint plugin for Kotlin style checking
   - Detekt for static analysis
   - Android Lint with custom configuration

2. **Build Optimization**:
   - R8 code shrinking and obfuscation
   - Resource shrinking
   - ProGuard rules included

3. **Multi-language Support**:
   - English (default)
   - Spanish (es)
   - French (fr)

4. **Custom Tasks**:
   - `preBuildCheck`: Run all quality checks
   - `productionReadyCheck`: Complete production verification

## Troubleshooting

### Build Fails with "Keystore not found"

**Solution**: Create the debug keystore or configure your release keystore:
```bash
# Create debug keystore
keytool -genkey -v -keystore ~/.android/debug.keystore \
  -storepass android -alias androiddebugkey \
  -keypass android -keyalg RSA -keysize 2048 \
  -validity 10000 -dname "CN=Android Debug,O=Android,C=US"
```

### Lint Errors

**Solution**: Check the lint report and fix issues:
```bash
./gradlew lintDebug
# Open: app/build/reports/lint-results-debug.html
```

### ktlint Style Violations

**Solution**: Auto-format the code:
```bash
./gradlew ktlintFormat
```

### Detekt Issues

**Solution**: Review the Detekt report:
```bash
./gradlew detekt
# Open: app/build/reports/detekt/detekt.html
```

## Play Store Deployment

1. **Build AAB**:
```bash
./gradlew bundleRelease
```

2. **Test locally** (requires bundletool):
```bash
bundletool build-apks --bundle=app/build/outputs/bundle/release/app-release.aab \
  --output=app.apks --mode=universal
bundletool install-apks --apks=app.apks
```

3. **Upload to Play Console**:
   - Go to Google Play Console
   - Navigate to your app
   - Create a new release
   - Upload `app-release.aab`
   - Follow the release process

## Best Practices

1. **Always run quality checks before committing**:
   ```bash
   ./gradlew preBuildCheck
   ```

2. **Test release builds locally** before deploying

3. **Keep your keystore secure** - never commit it to version control

4. **Use AAB for Play Store** - APK for direct distribution only

5. **Update dependencies regularly** but test thoroughly

6. **Run full test suite** before major releases

## Additional Resources

- [Android Developers - Build and Deploy](https://developer.android.com/studio/build)
- [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- [Detekt Documentation](https://detekt.dev/)
- [ktlint Documentation](https://pinterest.github.io/ktlint/)

---

**Last Updated**: 2024-12-06
**Project**: Notification Thriller (Delete After Reading)
