<div align="center">
  <img src="https://github.com/user-attachments/assets/9295d37f-b4bb-4740-9e86-d41754fb7a9b" width="120" alt="TrueCam Logo"/>
  <h1>TrueCam</h1>
  <h3>Pure Photography. Photo Only. Zero Processing.</h3>

  [![Kotlin](https://img.shields.io/badge/Kotlin-1.9-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
  [![Compose](https://img.shields.io/badge/Compose-Material3-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
  [![CameraX](https://img.shields.io/badge/CameraX-Jetpack-4285F4?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/training/camerax)
  [![License](https://img.shields.io/badge/License-Apache%202.0-green.svg?style=for-the-badge)](LICENSE)

  <br />
  <br />
</div>

---

**TrueCam** is a dedicated camera application built to capture the world **exactly as your sensor sees it**. No AI over-sharpening, no HDR ghosting, no "beauty" filters—just raw, high-resolution photography. We stripped away video recording to focus entirely on the art of the still image.

## UI
<p align="center">
  <img src="https://github.com/user-attachments/assets/b5f7b40d-5da2-42f6-bc4e-4f7816a3acbe" width="120" />
  <img src="https://github.com/user-attachments/assets/91a69da0-f9b6-47d0-8a30-ee1cd7f4cf5b" width="120" />
  <img src="https://github.com/user-attachments/assets/7207c155-9b2e-454b-a112-5c58836bd768" width="120" />
  <img src="https://github.com/user-attachments/assets/4109c280-2cd4-4956-885a-acfb115cb382" width="120" />  
</p>



## ✨ Key Features

### 📸 Photo Only Focus
> _"Stillness in motion."_
- **Zero Video Distractions**: All video recording logic, permissions, and UI have been removed.
- **Dedicated Shutter**: A single, responsive button for instant capture.

### 🚫 Zero Processing
- **Pure Capture**: Get the raw pixel data your sensor sees.
- **Natural Detail**: Preserve grain and texture often lost to noise reduction.
- **True-to-Life Colors**: Authentic color reproduction without saturation boosting.

### 🎛️ Pro-Grade Controls
A clean, distraction-free UI designed for photographers.
- **Tap-to-Lock**: Tap anywhere to lock focus and exposure.
- **Precision Exposure**: A slim, vertical slider allows for fine-tuned exposure compensation.
- **Quick Settings**: Instant access to Flash, Timer (3s, 10s), Aspect Ratio, and Grid.
- **Tactile Feel**: Subtle haptic feedback for a premium experience.

### 🔒 Privacy First
- **Offline Only**: No internet permissions required.
- **No Mic Permission**: Since video is removed, we don't even ask for your microphone.
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
