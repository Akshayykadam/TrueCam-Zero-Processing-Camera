# Iris 📸
**Pure Photography. Zero Processing.**

Iris is a personal camera application built to capture the world exactly as your sensor sees it. No AI over-sharpening, no HDR ghosting, no "beauty" filters—just raw, high-resolution photography.

## ✨ Key Features

### 🚫 Zero Processing Philosophy
Bypass modern smartphone post-processing.
- **Pure Capture**: Get the raw pixel data your sensor sees.
- **Natural Detail**: Preserve grain and texture often lost to noise reduction.
- **True-to-Life Colors**: what you see is what you get.

### 🌟 High Resolution
Unlock the full potential of your hardware.
- **Max Resolution**: We aggressively target the maximum resolution available on your sensor (e.g., 50MP, 200MP), bypassing standard binning limits.
- **Resolution Selector**: Choose between **Standard (12MP)**, **Medium (24MP)**, or **Max** quality directly from Quick Settings.

### 🎛️ Pro-Grade Controls
A clean, distraction-free UI designed for photographers:
- **Manual Grid**: 3x3 overlay constrained to your aspect ratio for perfect composition.
- **Smart Zoom**: Dedicated **10x** button (rear) and specialized **2x limit** (front).
- **Video Controls**: Select 24, 30, or 60 FPS and up to 4K resolution.
- **Quick Settings**: Fast access to Flash, Timer, and Aspect Ratio (4:3, 16:9).
- **Haptic Feedback**: subtle vibrations for a premium feel.

### 🔒 Privacy First
- **Offline Only**: No internet permissions required.
- **Local Storage**: Your photos stay strictly on your device.

## 🛠️ Built With
- **Kotlin & Jetpack Compose**: Modern, reactive Android UI.
- **CameraX**: Robust camera stability across devices.
- **Material 3**: Beautiful, dark-themed aesthetics.

## 🚀 Building from Source

This project uses Gradle. To build the debug APK:

```bash
./gradlew assembleDebug
```

The output APK will be located at:
`app/build/outputs/apk/stable/debug/app-stable-debug.apk`

To build the optimized **Release APK** (signed with debug keys):
```bash
./gradlew assembleRelease
```
Output: `app/build/outputs/apk/stable/release/app-stable-release.apk`

## 📝 License
Designed for personal use. Based on Jetpack Camera App (Apache 2.0).
