# Setup Guide for Firebase and Google Play Billing

This guide will help you configure Firebase Analytics and Google Play Billing for production use.

## Firebase Analytics Setup

### 1. Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project"
3. Enter project name: "Notification Thriller" (or your preferred name)
4. Accept terms and click "Continue"
5. Enable Google Analytics (recommended)
6. Choose or create Analytics account
7. Click "Create project"

### 2. Add Android App

1. In your Firebase project, click the Android icon
2. Register app:
   - **Package name**: `com.example.notificationthriller`
   - **App nickname**: Notification Thriller
   - **Debug signing certificate SHA-1**: (optional for development)
3. Click "Register app"

### 3. Download Configuration File

1. Download `google-services.json`
2. Replace the placeholder file in your project:
   ```
   app/google-services.json
   ```
3. **Important**: Never commit the real `google-services.json` to public repositories

### 4. Verify Integration

1. Build and run your app
2. Open Firebase Console
3. Go to Analytics → Dashboard
4. You should see events appearing within 24 hours

### 5. Configure Analytics

In Firebase Console:
1. Go to Analytics → Events
2. Mark important events as conversion events:
   - `game_start`
   - `choice_made`
   - `purchase` (if using IAP)

## Google Play Billing Setup

### 1. Create Google Play Console Account

1. Go to [Google Play Console](https://play.google.com/console)
2. Sign in with your Google account
3. Accept Developer Agreement
4. Pay $25 one-time registration fee
5. Complete account details

### 2. Create App

1. Click "Create app"
2. Fill in app details:
   - **App name**: The Notification Thriller
   - **Default language**: English (United States)
   - **App or game**: Game
   - **Free or paid**: Free (with in-app purchases)
3. Accept declarations and click "Create app"

### 3. Set Up In-App Products

#### Create Products

1. Go to Monetize → Products → In-app products
2. Click "Create product" for each product:

**Product 1: Remove Ads**
- Product ID: `remove_ads`
- Name: Remove Ads
- Description: Remove all advertisements from the game
- Status: Active
- Price: $2.99 (or your preferred price)

**Product 2: Unlock Chapters**
- Product ID: `unlock_chapters`
- Name: Unlock All Chapters
- Description: Get instant access to all story chapters
- Status: Active
- Price: $4.99

**Product 3: Premium Content**
- Product ID: `premium_content`
- Name: Premium Content
- Description: Unlock exclusive premium storylines and features
- Status: Active
- Price: $9.99

#### Configure Pricing

1. For each product, click "Set pricing"
2. Choose countries/regions
3. Set local prices (Google suggests based on USD)
4. Save pricing

### 4. Test In-App Purchases

#### Add Test Accounts

1. Go to Setup → License testing
2. Add email addresses for test accounts
3. These accounts can make test purchases without charges

#### Create Internal Test Track

1. Go to Testing → Internal testing
2. Create new release
3. Upload signed APK or AAB
4. Add test accounts
5. Distribute to testers

#### Test Purchase Flow

1. Install app from internal test track
2. Attempt purchase with test account
3. Verify purchase completes successfully
4. Check that content unlocks properly

### 5. Prepare for Production

#### Upload Release Build

1. Generate signed APK/AAB:
   ```bash
   ./gradlew bundleRelease
   ```
2. Go to Production → Releases
3. Create new release
4. Upload bundle
5. Complete store listing
6. Submit for review

#### Configure Store Listing

Required information:
- App description
- Screenshots (at least 2)
- Feature graphic
- App icon (512x512)
- Privacy policy URL
- Content rating

## Environment-Specific Configuration

### Development

```kotlin
// Use sandbox/test environment
val billingClient = BillingClient.newBuilder(context)
    .enablePendingPurchases()
    .build()
```

### Production

Ensure:
- Real `google-services.json` is in place
- Release signing is configured
- Product IDs match Play Console
- Test all purchase flows

## Troubleshooting

### Firebase Not Working

**Issue**: Events not appearing in Firebase Console

**Solutions**:
1. Check `google-services.json` is correctly placed
2. Verify package name matches Firebase project
3. Wait 24 hours for initial data
4. Check internet connectivity
5. Verify Firebase Analytics dependency version

### Billing Issues

**Issue**: Purchase flow not starting

**Solutions**:
1. Verify product IDs match exactly
2. Check app is signed correctly
3. Ensure billing library version is current
4. Test account must be added to internal testing
5. Product must be Active in Play Console

**Issue**: Purchase not completing

**Solutions**:
1. Check purchase acknowledgment code
2. Verify `PurchasesUpdatedListener` is implemented
3. Test on physical device (not emulator)
4. Check Google Play Services is updated

### Common Errors

**Error**: `google-services.json is missing`

**Fix**: Download from Firebase Console and place in `app/` directory

**Error**: `Product not found`

**Fix**: 
1. Product must be Active in Play Console
2. App must be uploaded to test track
3. Wait up to 24 hours for products to propagate

**Error**: `App not licensed`

**Fix**: 
1. Add test account to license testing
2. Install from Play Store test track
3. Clear Play Store cache

## Security Best Practices

### 1. Secure API Keys

Never commit sensitive keys to version control:
```gitignore
# Add to .gitignore
google-services.json
app/src/release/
*.keystore
*.jks
local.properties
```

### 2. Validate Purchases Server-Side

For production, implement server-side purchase validation:
1. Send purchase token to your server
2. Verify with Google Play Developer API
3. Grant content only after verification

### 3. Obfuscate Code

Enable ProGuard/R8 for release builds:
```kotlin
buildTypes {
    release {
        isMinifyEnabled = true
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
}
```

## Additional Resources

### Documentation

- [Firebase Android Setup](https://firebase.google.com/docs/android/setup)
- [Firebase Analytics](https://firebase.google.com/docs/analytics)
- [Google Play Billing](https://developer.android.com/google/play/billing)
- [In-App Purchase Testing](https://developer.android.com/google/play/billing/test)

### Support

- [Firebase Support](https://firebase.google.com/support)
- [Play Console Help](https://support.google.com/googleplay/android-developer)
- [Stack Overflow - Firebase](https://stackoverflow.com/questions/tagged/firebase)
- [Stack Overflow - Android Billing](https://stackoverflow.com/questions/tagged/android-billing)

## Next Steps

After completing setup:

1. ✅ Test all features in development
2. ✅ Configure internal testing
3. ✅ Add test accounts
4. ✅ Test purchase flows
5. ✅ Submit for review
6. ✅ Monitor analytics dashboard
7. ✅ Iterate based on user data

## Checklist

Before going to production:

- [ ] Firebase project created
- [ ] `google-services.json` configured
- [ ] Analytics events tested
- [ ] Google Play Console account created
- [ ] In-app products configured
- [ ] Pricing set for all regions
- [ ] Test accounts added
- [ ] Internal testing completed
- [ ] All purchase flows tested
- [ ] Store listing completed
- [ ] Privacy policy published
- [ ] Release build generated
- [ ] App submitted for review

## Cost Summary

### Firebase
- **Analytics**: Free for unlimited events
- **Crashlytics**: Free
- **Cloud Messaging**: Free for unlimited messages

### Google Play
- **Developer Account**: $25 one-time fee
- **Transaction Fee**: 15% for first $1M revenue/year, 30% after
- **No monthly fees**: Pay only on successful transactions

### Total Initial Investment
- Google Play Developer: $25
- Firebase: $0
- **Total**: $25

Your app is ready for monetization! 🚀
