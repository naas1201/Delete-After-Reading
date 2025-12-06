# App Icons Guide

## Overview
This directory contains app icons for The Notification Thriller in various densities and formats.

## Icon Densities for Android

| Density | Folder | Size (px) | DPI | Usage |
|---------|--------|-----------|-----|-------|
| ldpi | drawable-ldpi | 36x36 | 120 | Low density screens (rare) |
| mdpi | drawable-mdpi | 48x48 | 160 | Medium density (baseline) |
| hdpi | drawable-hdpi | 72x72 | 240 | High density |
| xhdpi | drawable-xhdpi | 96x96 | 320 | Extra high density |
| xxhdpi | drawable-xxhdpi | 144x144 | 480 | Extra extra high density |
| xxxhdpi | drawable-xxxhdpi | 192x192 | 640 | Extra extra extra high density |

## Additional Icon Requirements

### Google Play Store
- **Feature Graphic**: 1024x500 (required for store listing)
- **App Icon**: 512x512 (high-resolution)
- **Promo Graphic**: 180x120 (optional)

### Adaptive Icons (Android 8.0+)
- **Foreground Layer**: 108x108 dp (with 72x72 dp safe zone)
- **Background Layer**: 108x108 dp
- Format: Vector drawable or PNG

## Icon Design Guidelines

### Visual Elements
- **Primary**: Notification bell (represents core gameplay mechanic)
- **Secondary**: Alert dot (thriller/urgency theme)
- **Accent**: Mystery lines (atmospheric)
- **Colors**: Dark background with cyan/blue accents
- **Style**: Modern, minimalist, professional

### Design Principles
1. **Recognizable**: Clear at small sizes (24x24)
2. **Unique**: Distinguishable from other apps
3. **On-Brand**: Matches game theme and colors
4. **Simple**: Not overly complex
5. **Scalable**: Vector-based when possible

### Color Palette
- **Background**: #1a1a2e to #16213e (dark gradient)
- **Primary Icon**: #0f3460 to #2196F3(blue gradient)
- **Accent**: #03DAC5 (cyan)
- **Alert**: #e53935 (red)

## Creating Custom Icons

### Using SVG Templates
1. Open `icon_template.svg` in vector editor (Inkscape, Adobe Illustrator)
2. Modify colors, shapes, or add elements
3. Export to required sizes
4. Place in appropriate drawable folders

### Automated Generation
Use Android Asset Studio or similar tools:
- https://romannurik.github.io/AndroidAssetStudio/
- Upload your base icon
- Generate all densities automatically
- Download and place in project

### Manual Export (From SVG)
Using Inkscape (command line):
```bash
# Export to different sizes
inkscape -w 36 -h 36 icon_template.svg -o drawable-ldpi/ic_launcher.png
inkscape -w 48 -h 48 icon_template.svg -o drawable-mdpi/ic_launcher.png
inkscape -w 72 -h 72 icon_template.svg -o drawable-hdpi/ic_launcher.png
inkscape -w 96 -h 96 icon_template.svg -o drawable-xhdpi/ic_launcher.png
inkscape -w 144 -h 144 icon_template.svg -o drawable-xxhdpi/ic_launcher.png
inkscape -w 192 -h 192 icon_template.svg -o drawable-xxxhdpi/ic_launcher.png
inkscape -w 512 -h 512 icon_template.svg -o ic_launcher_store.png
```

## Adaptive Icon Implementation

### XML Configuration
Create `res/mipmap-anydpi-v26/ic_launcher.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
</adaptive-icon>
```

### Layers
- **Background**: Solid color or simple gradient
- **Foreground**: Main icon content in 72x72 dp safe zone
- **Safe Zone**: Keep all important elements within center 72x72 dp
- **Full Bleed**: 108x108 dp canvas for proper cropping

## Testing Icons

### Android Studio
1. Right-click `res` folder
2. New → Image Asset
3. Preview icons in different shapes:
   - Circle (Pixel devices)
   - Rounded square (Samsung)
   - Square (Sony)
   - Squircle (others)

### Device Testing
Test on real devices to ensure:
- Visibility on different launchers
- Clarity at various sizes
- Proper adaptive icon behavior
- Notification icon visibility (small size)

## Icon Specifications by Use Case

### Launcher Icon (Main)
- **Size**: Multiple densities (see table above)
- **Format**: PNG (24-bit) with alpha channel
- **Shape**: Square with rounded corners
- **Padding**: Minimal, fill canvas

### Notification Icon (Monochrome)
- **Size**: 24x24 dp
- **Color**: White/transparent only
- **Style**: Simple, recognizable silhouette
- **Location**: `drawable/ic_notification.xml`

### Foreground Service Icon
- **Size**: Same as notification icon
- **Style**: Distinct from regular notifications
- **Priority**: High visibility

## Best Practices

### Do's ✅
- Use vector drawables when possible
- Test on multiple device sizes
- Maintain consistent branding
- Follow Material Design guidelines
- Keep file sizes reasonable (<100KB per icon)
- Use proper naming conventions

### Don'ts ❌
- Don't use photos or complex gradients
- Don't include text that's hard to read when small
- Don't copy other apps' icon designs
- Don't use too many colors (limit to 3-4)
- Don't forget to update adaptive icon variants

## Icon Updates

When updating icons:
1. Update all density variants
2. Update adaptive icon layers
3. Update store listing icons
4. Increment app version
5. Test on multiple devices
6. Document changes in CHANGELOG.md

## Resources

### Design Tools
- **Figma**: https://figma.com (free, web-based)
- **Inkscape**: https://inkscape.org (free, vector editor)
- **GIMP**: https://gimp.org (free, raster editor)
- **Adobe Illustrator**: Professional vector design

### Icon Generators
- **Android Asset Studio**: https://romannurik.github.io/AndroidAssetStudio/
- **App Icon Generator**: https://appicon.co/
- **Icon Kitchen**: https://icon.kitchen/

### Guidelines
- **Material Design Icons**: https://material.io/design/iconography/
- **Android Icon Guide**: https://developer.android.com/guide/practices/ui_guidelines/icon_design_launcher

---

**Pro Tip**: Create icons at the highest resolution first (512x512 or higher), then scale down. This ensures better quality at all sizes.
