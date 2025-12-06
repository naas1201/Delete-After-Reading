# Google Play Store Publication Assets

## Overview
This directory contains all assets required for publishing **The Notification Thriller** on Google Play Store and other Android app stores.

## Directory Structure

```
store_assets/
├── screenshots/
│   ├── phone/              # Phone screenshots (required)
│   ├── tablet_7inch/       # 7" tablet screenshots (optional but recommended)
│   └── tablet_10inch/      # 10" tablet screenshots (optional but recommended)
├── graphics/               # Feature graphics, promo images
├── descriptions/           # Store listing text in multiple languages
└── README.md              # This file
```

## Required Assets Checklist

### ✅ Mandatory Assets

#### 1. App Icon
- **Format**: PNG (32-bit)
- **Size**: 512 x 512 pixels
- **Location**: See `app/icons/` directory
- **Notes**: High-resolution version of launcher icon

#### 2. Feature Graphic
- **Format**: PNG or JPEG
- **Size**: 1024 x 500 pixels
- **Location**: `graphics/feature_graphic.png`
- **Usage**: Top of store listing, Google Play promotions
- **Design**: Showcases game's thriller theme and notification mechanic

#### 3. Phone Screenshots (Minimum 2, Maximum 8)
- **Format**: PNG or JPEG (no alpha)
- **Dimensions**: 
  - 16:9 aspect ratio (e.g., 1920x1080, 2560x1440)
  - OR 9:16 aspect ratio (e.g., 1080x1920, 1440x2560)
- **Location**: `screenshots/phone/`
- **Required**: At least 2 screenshots
- **Recommended**: 4-8 screenshots showing key features

### 📱 Optional but Recommended Assets

#### 4. Tablet Screenshots
- **7-inch Tablet**: 
  - Size: 1920x1200 or 1200x1920
  - Location: `screenshots/tablet_7inch/`
- **10-inch Tablet**: 
  - Size: 2560x1600 or 1600x2560
  - Location: `screenshots/tablet_10inch/`
- **Notes**: Increases visibility on tablet devices

#### 5. Promo Graphic
- **Format**: PNG or JPEG
- **Size**: 180 x 120 pixels
- **Location**: `graphics/promo_graphic.png`
- **Usage**: Older promotional campaigns (less common now)

#### 6. TV Banner
- **Format**: PNG (no alpha)
- **Size**: 1280 x 720 pixels
- **Location**: `graphics/tv_banner.png`
- **Usage**: Android TV devices

#### 7. Promo Video (YouTube)
- **Format**: YouTube video link
- **Duration**: 30 seconds to 2 minutes
- **Location**: Add link to `descriptions/promo_video.txt`
- **Content**: Gameplay trailer, app overview

## Screenshot Requirements

### Content Guidelines

#### Phone Screenshots - Recommended Set
Create 6-8 screenshots showing:

1. **Welcome/Splash Screen**
   - File: `phone/01_splash.png`
   - Shows: App branding, initial impression
   - Caption: "Welcome to The Notification Thriller"

2. **Main Chat Interface**
   - File: `phone/02_main_chat.png`
   - Shows: Message history with mysterious conversation
   - Caption: "Real-time narrative unfolds through notifications"

3. **Notification Example**
   - File: `phone/03_notification.png`
   - Shows: System notification with game message
   - Caption: "Story delivered directly to your notifications"

4. **Character Introduction**
   - File: `phone/04_character.png`
   - Shows: Message from key character (Sarah, The Architect)
   - Caption: "Meet intriguing characters in a thrilling mystery"

5. **Intense Moment**
   - File: `phone/05_thriller.png`
   - Shows: High-stakes conversation moment
   - Caption: "Experience edge-of-your-seat storytelling"

6. **Game Features**
   - File: `phone/06_features.png`
   - Shows: Menu or settings showing game options
   - Caption: "100+ hours of immersive gameplay"

7. **Multiple Messages**
   - File: `phone/07_conversation.png`
   - Shows: Long conversation thread
   - Caption: "Follow the mystery as it unfolds"

8. **Ending/Resolution**
   - File: `phone/08_conclusion.png`
   - Shows: Satisfying story moment (no spoilers)
   - Caption: "Your choices shape the narrative"

### Technical Specifications

#### Image Quality
- **Resolution**: Use actual device screenshots (native resolution)
- **Format**: PNG preferred (lossless), JPEG acceptable
- **Color Space**: sRGB
- **Compression**: Minimal (high quality)
- **Max File Size**: 8 MB per image

#### Framing
- **Status Bar**: Can include or hide (device frame optional)
- **Navigation Bar**: Can include or hide
- **Device Frame**: Optional mockup frame (looks professional)
- **Orientation**: Portrait recommended for story-based games

#### Content Rules
- **No Profanity**: Keep content appropriate
- **No Misleading Images**: Must represent actual gameplay
- **No Copyright Violation**: Use only owned/licensed content
- **No Personal Info**: Remove any PII from screenshots

### Taking Screenshots

#### On Android Device
1. Navigate to the screen you want to capture
2. Press **Power + Volume Down** simultaneously
3. Screenshots saved to `Pictures/Screenshots/`
4. Transfer to computer via USB or cloud

#### Using Android Studio Emulator
1. Launch app in emulator
2. Click camera icon in emulator toolbar
3. Screenshot saved to desktop
4. Resize if needed for store requirements

#### Adding Device Frames
Use tools like:
- **Device Art Generator**: https://developer.android.com/distribute/marketing-tools/device-art-generator
- **Mockup Generator**: https://mockuphone.com/
- **Figma/Photoshop**: Custom mockups

## Feature Graphic Design

### Design Elements
- **Dimensions**: 1024 x 500 pixels (exact)
- **Safe Zone**: Keep important elements in center 922 x 450 pixels
- **Aspect Ratio**: 2.048:1

### Content Guidelines
- **Main Visual**: App icon or key character
- **Text**: Game title "The Notification Thriller"
- **Tagline**: "A Real-Time Mystery That Finds You"
- **Theme**: Dark, mysterious, suspenseful
- **Colors**: Match app theme (dark blues, cyan accents)
- **Readable**: Clear at thumbnail size

### Example Layout
```
┌─────────────────────────────────────────────────────┐
│                                                       │
│  [APP ICON]    THE NOTIFICATION THRILLER              │
│                                                       │
│           A Real-Time Mystery That Finds You          │
│                                                       │
│      [Character Silhouette] [Notification Bell]       │
│                                                       │
└─────────────────────────────────────────────────────┘
```

## Store Listing Text

### Short Description (80 characters max)
Location: `descriptions/short_description.txt`

Example:
```
A thrilling story delivered through real-time notifications. Mystery awaits.
```

### Full Description (4000 characters max)
Location: `descriptions/full_description.txt`

Structure:
1. **Hook** (1-2 sentences): Grab attention
2. **Concept** (1 paragraph): Explain unique gameplay
3. **Features** (bullet points): Key selling points
4. **Story Teaser** (1 paragraph): Intrigue without spoilers
5. **Technical Highlights** (1 paragraph): Quality and polish
6. **Call to Action**: Encourage download

### Example Full Description

```
🔔 THE NOTIFICATION THRILLER 🔔

Experience storytelling like never before. A gripping thriller that unfolds in real-time through your device's notification system. When Sarah messages you asking for help, you don't just read about it – you live it.

⚡ UNIQUE GAMEPLAY
• Real-time story delivery through notifications
• 100+ hours of immersive narrative content
• Multiple characters with distinct personalities
• Edge-of-your-seat mystery and suspense
• Choices that shape your experience

📱 PREMIUM FEATURES
• Stunning Material Design interface
• Optimized for all Android devices
• Offline gameplay – no internet required
• Privacy-focused – zero data collection
• Battery-friendly notification system

🎭 THE STORY
You receive a mysterious message. Someone needs your help. As you dig deeper, you uncover a conspiracy that threatens everything. Project Midnight. The Architect. The Rogue AI. The truth is out there – but are you ready for it?

🏆 AAA-QUALITY PRODUCTION
• Enterprise-grade MVVM architecture
• 90%+ test coverage for reliability
• Guaranteed notification delivery
• Supports Android 7.0 and above
• Professional design and polish

Download now and begin your journey. The notification thriller that finds you – no matter where you are.

━━━━━━━━━━━━━━━━━━━━━━━━━━━
Made with ❤️ for thriller and mystery fans
```

## Localization

### Supported Languages
For international reach, provide translations:

```
descriptions/
├── en-US/              # English (United States) - Required
│   ├── short_description.txt
│   ├── full_description.txt
│   └── title.txt
├── es-ES/              # Spanish (Spain)
├── fr-FR/              # French (France)
├── de-DE/              # German (Germany)
├── ja-JP/              # Japanese (Japan)
├── ko-KR/              # Korean (South Korea)
├── pt-BR/              # Portuguese (Brazil)
├── ru-RU/              # Russian (Russia)
└── zh-CN/              # Chinese (Simplified)
```

### Translation Guidelines
- Maintain tone and style of original
- Keep within character limits
- Consider cultural appropriateness
- Test text in UI to ensure it fits
- Use professional translation services if possible

## Content Rating

### IARC Questionnaire
Complete the IARC questionnaire in Play Console:
- **Violence**: None/Minimal
- **Sexual Content**: None
- **Language**: Mild (if applicable)
- **Drugs**: None
- **Gambling**: None
- **In-App Purchases**: None/Indicate if present

### Expected Rating
- **ESRB**: Teen (T)
- **PEGI**: 12
- **USK**: 12
- **Reason**: Thriller themes, suspense

## App Categories

### Primary Category
- **Games** → **Puzzle**
- OR **Games** → **Adventure**
- OR **Games** → **Story-Driven**

### Tags
Include relevant tags:
- Narrative
- Story-driven
- Thriller
- Mystery
- Puzzle
- Text-based
- Offline
- Singleplayer

## Pre-Launch Checklist

Before submitting to Play Store:

### Technical
- [ ] All required assets uploaded
- [ ] Screenshots showcase key features
- [ ] Feature graphic is compelling
- [ ] App icon is high-resolution
- [ ] Descriptions are error-free
- [ ] Translations are complete (if applicable)
- [ ] Privacy policy URL provided
- [ ] Contact email set

### Content
- [ ] No copyright violations
- [ ] No misleading claims
- [ ] Age rating is appropriate
- [ ] Screenshots represent actual app
- [ ] All text is proofread
- [ ] SEO keywords included naturally

### Quality
- [ ] App has been tested thoroughly
- [ ] No critical bugs present
- [ ] Performance is optimized
- [ ] UI/UX is polished
- [ ] All features work as described

### Legal
- [ ] Privacy policy complies with laws
- [ ] Terms of service (if applicable)
- [ ] GDPR compliance (if targeting EU)
- [ ] COPPA compliance (if targeting children)
- [ ] All required permissions justified

## Screenshot Creation Tools

### Professional Tools
- **Adobe Photoshop**: Industry standard
- **Figma**: Web-based design tool
- **Sketch**: macOS design tool
- **Affinity Designer**: Affordable alternative

### Screenshot Enhancement
- **Cleanshot**: Mac screenshot tool
- **Snagit**: Windows/Mac screenshot tool
- **Lightshot**: Simple screenshot tool
- **Flameshot**: Linux screenshot tool

### Mockup Generators
- **Previewed**: https://previewed.app/
- **Mockuphone**: https://mockuphone.com/
- **Smartmockups**: https://smartmockups.com/
- **Placeit**: https://placeit.net/

### Video Creation
- **DaVinci Resolve**: Free video editor
- **Adobe Premiere Pro**: Professional editor
- **Final Cut Pro**: Mac video editor
- **OBS Studio**: Screen recording

## Resources

### Official Guidelines
- **Play Store Asset Guidelines**: https://support.google.com/googleplay/android-developer/answer/9866151
- **App Listing Best Practices**: https://developer.android.com/distribute/best-practices/launch/store-listing
- **Localization Guidelines**: https://developer.android.com/distribute/best-practices/launch/localization

### Helpful Tools
- **Play Console**: https://play.google.com/console
- **Android Asset Studio**: https://romannurik.github.io/AndroidAssetStudio/
- **App Preview Video Maker**: https://developer.android.com/distribute/marketing-tools/device-art-generator

### Inspiration
- Browse top-rated apps in similar categories
- Study successful indie game listings
- Analyze competitor store presentations
- Follow Material Design guidelines

## Support

For questions about store assets:
1. Check Google Play Console documentation
2. Review Android Developer guides
3. Contact Google Play support
4. Consult app marketing resources

---

**Remember**: Your store listing is often the first impression users have of your app. Invest time in creating compelling, professional assets that accurately represent your game's quality and uniqueness.

**Pro Tip**: A/B test different screenshots and descriptions to find what resonates best with your target audience.
