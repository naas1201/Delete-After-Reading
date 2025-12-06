# Firebase Setup

## About google-services.json

The `google-services.json` file in this directory is a **PLACEHOLDER** for building purposes.

### For Development/Testing

The placeholder file allows the project to build successfully without Firebase configuration. The Firebase features will not work, but the app will compile and run.

### For Production Use

1. Create your Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Add an Android app with package name: `com.example.notificationthriller`
3. Download your **real** `google-services.json` file
4. Replace the placeholder file with your real file
5. **IMPORTANT**: Do not commit your real `google-services.json` to version control

See [SETUP_GUIDE.md](../SETUP_GUIDE.md) for detailed Firebase setup instructions.

## Security Note

The placeholder file contains dummy values and does not connect to any real Firebase project. Your real `google-services.json` contains sensitive configuration and should never be committed to a public repository.
