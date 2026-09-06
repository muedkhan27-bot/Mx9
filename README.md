# J.A.R.V.I.S. (Just A Rather Very Intelligent System)

An Iron Man-inspired holographic AI assistant for Android built with modern Jetpack Compose, Kotlin Coroutines, and dynamic hardware telemetry. Features an Arc Reactor visualizer, continuous voice wake-up (`"Jarvis wake up"`), offline knowledge & math engines, device controls, and online Gemini AI reasoning.

---

## ⚡ Key Features

- **Holographic Arc Reactor HUD**: Animated multi-ring core with rotating reactor containment rings, pulse animations, audio waveform visualizers, and protocol status modes (House Party, Clean Slate, Sentry, Diagnostics, Stealth).
- **Wake-Word & Continuous Voice**: Background service listening for `"Jarvis wake up"` or `"Hey Jarvis"` with a British-accented text-to-speech feedback engine.
- **Offline Intelligence Engine**: Local knowledge base containing physics constants, astronomy data, world capitals, historical milestones, and Stark Industries lore — answers instantly without an internet connection.
- **Offline Calculation & Conversion**: Evaluates mathematical expressions, powers, roots, percentages, and performs physical unit conversions (Celsius to Fahrenheit, km to miles, kg to lbs, etc.).
- **Hardware & Device Automation**: Hands-free flashlight toggle, app launcher, phone dialer, SMS messaging, system volume adjustment, battery & memory telemetry, and system alarms.
- **Online Gemini AI Fallback**: Integrates Google Gemini API for complex queries, creative tasks, and open-ended dialogue when an active network connection is available.

---

## 📱 How to Download & Install APK

### Option 1: Direct Download from AI Studio
1. Open the project in **Google AI Studio Build**.
2. Click the **Export / Settings** menu in the top navigation bar.
3. Select **Download APK** (or **Generate APK / AAB**) to download the ready-to-install `.apk` directly to your device or computer.

### Option 2: Push to GitHub & Create a Release
1. In the AI Studio project menu, choose **Push to GitHub** to link your repository.
2. In your GitHub repository, navigate to **Releases** > **Draft a new release**.
3. Attach the downloaded `app-debug.apk` under **Attach binaries by dropping them here or selecting them**.
4. Publish your release for users to download and install.

### Option 3: Build Locally via Gradle
```bash
# Clone the repository
git clone https://github.com/<your-username>/<repo-name>.git
cd <repo-name>

# Assemble debug APK
./gradlew assembleDebug

# The APK will be located at:
# app/build/outputs/apk/debug/app-debug.apk
```

---

## 🛠️ Tech Stack & Architecture

- **UI Framework**: Jetpack Compose with Material Design 3 (M3)
- **Language**: Kotlin 2.0+
- **Architecture**: MVVM (Model-View-ViewModel) with StateFlow
- **AI / LLM**: Google Gemini API via server-side AI Studio integration
- **Speech**: Android `SpeechRecognizer` + `TextToSpeech` (British English `en-GB`)
- **System Telemetry**: BatteryManager, ActivityManager, Hardware CameraManager

---

## 📄 License
This project is open-source and intended for personal and educational use.
