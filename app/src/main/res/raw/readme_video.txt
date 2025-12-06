# Splash Screen Video Setup

## Overview
The Notification Thriller supports a custom splash screen video that plays when the app launches.

## Adding Your Custom Video

### Method 1: Built-in Video (Recommended for Distribution)
1. Create or obtain your splash screen video in MP4 format
2. Name the file `template.mp4`
3. Place it in this directory: `app/src/main/res/raw/`
4. Rebuild the app

**Video Requirements:**
- Format: MP4 (H.264 codec recommended)
- Resolution: 1920x1080 or 1080x1920 (portrait recommended)
- Duration: 3-10 seconds recommended
- File size: Keep under 10MB for optimal APK size
- Audio: Optional (background music/sound effects)

### Method 2: User-Replaceable Video (For End Users)
Users can replace the splash video without rebuilding:

1. Connect device to computer via USB
2. Navigate to: `/Android/data/com.example.notificationthriller/files/`
3. Copy your `template.mp4` file to this location
4. Restart the app

**Priority:** If a custom video exists in the external files directory, it will be used instead of the built-in one.

## Fallback Behavior
If no video is found (neither built-in nor custom), the splash screen will be skipped and the app will launch directly to the main screen.

## Video Specifications

### Recommended Settings
- **Codec**: H.264 (AVC)
- **Container**: MP4
- **Resolution**: 
  - Phone: 1080x1920 (9:16 aspect ratio)
  - Tablet: 1920x1080 (16:9 aspect ratio)
- **Bitrate**: 5-8 Mbps for good quality
- **Frame Rate**: 24, 30, or 60 FPS
- **Audio Codec**: AAC (if audio included)
- **Audio Bitrate**: 128-192 kbps

### Creating Your Video

#### Using FFmpeg (Command Line)
```bash
# Convert any video to compatible format
ffmpeg -i input.mp4 -c:v libx264 -profile:v main -preset slow \
       -crf 23 -c:a aac -b:a 128k -vf scale=1080:1920 \
       -movflags +faststart template.mp4

# For videos without audio
ffmpeg -i input.mp4 -c:v libx264 -profile:v main -preset slow \
       -crf 23 -an -vf scale=1080:1920 \
       -movflags +faststart template.mp4
```

#### Using Video Editing Software
- **Adobe Premiere Pro**: Export as H.264, 1080x1920
- **DaVinci Resolve**: Export as MP4, H.264 codec
- **HandBrake**: Use "Android" preset and adjust resolution
- **iMovie**: Export as HD 1080p, choose MP4 format

## Design Guidelines

### Visual Content
- **Branding**: Display game logo/title
- **Theme**: Match game's dark, thriller aesthetic
- **Animation**: Smooth, professional transitions
- **Text**: Minimal text, focus on visual impact
- **Colors**: Use game's color palette (dark blues, blacks, accent colors)

### Technical Guidelines
- Keep it short: 3-5 seconds is ideal
- Optimize file size: Use appropriate compression
- Test on multiple devices: Ensure compatibility
- Consider orientation: Portrait works best for mobile games
- Add fade out: Smooth transition to main screen

## Examples

### Minimalist Approach
- Black background
- Game logo fades in
- Subtle particle effects
- Studio name appears
- Fade to main menu

### Cinematic Approach
- Brief narrative snippet
- Atmospheric music
- Character silhouettes
- Mysterious messaging
- Build anticipation

### Technical Demo
- Matrix-style code rain
- "System initializing" effects
- Glitch effects
- Notification animation
- Connects to game theme

## Testing Your Video

1. Place your video in the appropriate location
2. Build and install the app
3. Clear app data to simulate first launch
4. Launch the app and observe:
   - Video plays smoothly (no stuttering)
   - Audio (if any) is clear
   - Tap to skip works
   - Transition to main screen is smooth
5. Test on different devices:
   - Various screen sizes
   - Different Android versions
   - Low-end and high-end devices

## Troubleshooting

### Video Doesn't Play
- Check file name is exactly `template.mp4` (case-sensitive on some systems)
- Verify video codec is H.264
- Ensure file is not corrupted
- Check logcat for error messages

### Video Stutters
- Reduce video bitrate
- Lower resolution if necessary
- Use faster encoding preset (e.g., "faster" in FFmpeg)
- Optimize video with `-movflags +faststart`

### Large APK Size
- Compress video more aggressively (higher CRF value)
- Reduce resolution
- Shorten duration
- Remove audio if not essential
- Consider using built-in Android animations instead

### Audio Out of Sync
- Re-encode with audio and video together
- Use constant frame rate (CFR) instead of variable (VFR)
- Ensure audio sample rate is 44100 Hz or 48000 Hz

## Legal Considerations

- Ensure you have rights to all content in the video
- Music: Use royalty-free or licensed music only
- Images/Footage: Use original content or properly licensed assets
- Trademarks: Don't use others' logos without permission
- Privacy: Don't include identifiable people without consent

## Performance Impact

- Video playback is hardware-accelerated on modern devices
- Minimal battery impact (3-10 seconds)
- Memory usage: ~20-50MB during playback
- App size increase: Based on video file size
- First launch might be slightly slower

## Accessibility

Consider users with:
- **Visual impairments**: Add audio description if needed
- **Hearing impairments**: Don't rely solely on audio
- **Motion sensitivity**: Avoid rapid flashing or extreme motion
- **Cognitive load**: Keep it simple and clear

The splash screen can be skipped by tapping, ensuring accessibility for all users.

---

**Remember**: The splash screen creates the first impression. Make it count! 🎬
