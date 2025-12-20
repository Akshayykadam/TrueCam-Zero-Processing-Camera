<div align="center">
  <img src="https://upload.wikimedia.org/wikipedia/commons/7/74/Kotlin_Icon.png" width="80" alt="Iris Logo"/>
  <h1>Iris Camera</h1>
  <h3>Pure Photography. Zero Processing.</h3>

  [![Kotlin](https://img.shields.io/badge/Kotlin-1.9-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
  [![Compose](https://img.shields.io/badge/Compose-Material3-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
  [![CameraX](https://img.shields.io/badge/CameraX-Jetpack-4285F4?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/training/camerax)
  [![License](https://img.shields.io/badge/License-Apache%202.0-green.svg?style=for-the-badge)](LICENSE)

  <br />
  <br />
</div>

---

**Iris** is a personal camera application built to capture the world **exactly as your sensor sees it**. No AI over-sharpening, no HDR ghosting, no "beauty" filters—just raw, high-resolution photography.

## ✨ Key Features

### 🚫 Zero Processing Philosophy
> _"What you see is what you get."_
Bypass modern smartphone post-processing algorithms.
- **Pure Capture**: Get the raw pixel data your sensor sees.
- **Natural Detail**: Preserve grain and texture often lost to noise reduction.
- **True-to-Life Colors**: Authentic color reproduction without saturation boosting.

### 🌟 High Resolution
Unlock the full potential of your hardware.
- **Max Resolution**: Aggressively targets specific maximum resolutions (e.g., 50MP, 200MP), bypassing standard binning.
- **Resolution Selector**: 
  - `Standard` (12MP)
  - `Medium` (24MP)
  - `Max` (Sensor Limit)

### 🎛️ Pro-Grade Controls
A clean, distraction-free UI designed for photographers.
- **Smart Zoom**: Dedicated **10x** button (rear) and specialized **2x limit** (front).
- **Video Controls**: 24/30/60 FPS selection up to 4K.
- **Quick Settings**: Instant access to Flash, Timer, and Grid.
- **Tactile Feel**: Subtle haptic feedback for a premium experience.

### 🔒 Privacy First
- **Offline Only**: No internet permissions required.
- **Local Storage**: Your photos stay strictly on your device.

---

## 🛠️ Built With

| Tech | Description |
| :--- | :--- |
| **Kotlin & Compose** | Modern, reactive Android UI based on Material 3. |
| **CameraX** | Robust camera stability and lifecycle management. |
| **Coroutines** | Efficient background processing for image capture. |

---

## 🚀 Building from Source

This project uses Gradle. 

### Debug Build
Great for testing and development.
```bash
./gradlew assembleDebug
```
📦 **Artifact**: `app/build/outputs/apk/stable/debug/app-stable-debug.apk`

### Release Build
Optimized, minified, and signed (debug keys).
```bash
./gradlew assembleRelease
```
📦 **Artifact**: `app/build/outputs/apk/stable/release/app-stable-release.apk`

---

<div align="center">
  <sub>Designed for personal use. Based on Jetpack Camera App.</sub>
</div>
