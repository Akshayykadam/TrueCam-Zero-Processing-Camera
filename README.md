<div align="center">
  <img src="https://github.com/user-attachments/assets/9295d37f-b4bb-4740-9e86-d41754fb7a9b" width="120" alt="Iris Logo"/>
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

## UI
<p align="center">
  <img src="https://github.com/user-attachments/assets/a79feaf1-64d2-4ecc-86f2-f240ba85b86b" width="120" />
  <img src="https://github.com/user-attachments/assets/5f061527-5b38-4147-b5b3-cac252247c98" width="120" />
  <img src="https://github.com/user-attachments/assets/775fbb2a-bf09-4500-9e50-f2cf0837690f" width="120" />
  <img src="https://github.com/user-attachments/assets/b3831c29-0aaf-4c4a-a65d-fe11dab37aff" width="120" />
  <img src="https://github.com/user-attachments/assets/2d4072fd-32b9-401a-b3bc-ab281b933a95" width="120" />
</p>


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
