<h1 align="center">🧘 FlowBit</h1>
<p align="center">Your minimalist productivity companion — Focus timer with ambient noise + Distraction blocker</p>

<p align="center">
  <img src="https://img.shields.io/badge/Built%20With-Kotlin-7F52FF.svg?style=flat&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/Jetpack%20Compose-4285F4.svg?style=flat&logo=jetpackcompose&logoColor=white" />
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84.svg?style=flat&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Made%20by-Nitesh%20Ray-orange.svg" />
</p>

---

## ✨ Overview

**FlowBit** is an open-source Android productivity app designed to help you stay in the zone. Combine a clean Pomodoro timer with ambient focus sounds and powerful distraction blocking — all in one minimal, beautiful interface.

> Built for creators, students, developers, and anyone who wants to reclaim their focus.

---

## 🚀 Features

### 🍅 Focus Timer (Pomodoro)
- Clean, minimal 25-minute Pomodoro timer
- Beautiful circular progress animation
- Auto-transitions between Focus and Break sessions
- Session counter to track completed pomodoros

### 🎧 Focus Sounds (Ambient Noise)
- **Programmatically generated** — No audio files needed!
- **White Noise** — Balanced, static-like sound for concentration
- **Brown Noise** — Deep, rumbling sound like wind or waterfall
- **Pink Noise** — Softer, balanced frequencies for relaxation
- Smooth transitions between noise types

### 🛡️ Distraction Blocker
- **Block Apps** — Prevent distracting apps from opening
- **Block Shorts** — Stop doom-scrolling on YouTube Shorts, Instagram Reels, Snapchat Spotlight
- **Block Websites** — Add custom websites to your blocklist

### 🎨 Modern UI
- Dark theme with OLED-friendly pure black background
- Floating bottom navigation with glow effects
- Smooth animations throughout
- Minimal, clean design

---

## 📸 Screenshots

| Focus Timer | Focus Sounds | Block Screen |
|-------------|--------------|--------------|
| ![](screenshots/focus.png) | ![](screenshots/noise.png) | ![](screenshots/block.png) |

---

## 🛠️ Tech Stack

| Technology | Usage |
|------------|-------|
| **Kotlin** | Primary language |
| **Jetpack Compose** | Modern declarative UI |
| **Material 3** | Design system |
| **AudioTrack API** | Programmatic noise generation |
| **Coroutines** | Async audio streaming |
| **Accessibility Service** | App & shorts blocking |
| **MVVM** | Architecture pattern |

---

## 📁 Project Structure

```
app/src/main/java/xcom/niteshray/xapps/xblockit/
├── MainActivity.kt
├── component/
│   └── FloatingBottomNav.kt       # Modern floating navigation
├── feature/
│   ├── MainScreen/                # App navigation
│   ├── focus/                     # Pomodoro timer
│   │   ├── FocusScreen.kt
│   │   ├── audio/                 # Noise generator
│   │   │   ├── NoiseType.kt
│   │   │   ├── NoiseGenerator.kt
│   │   │   └── FocusNoisePlayer.kt
│   │   └── components/
│   │       └── NoiseSelector.kt
│   └── block/                     # Distraction blocker
│       ├── presentation/
│       ├── viewmodels/
│       └── components/
├── model/
├── ui/theme/
└── util/
```

---

## ⚙️ Installation

1. Clone the repository
```bash
git clone https://github.com/mrniteshray/FlowBit.git
```

2. Open in Android Studio

3. Build and run on your device

---

## 📥 Download

Download the latest APK from [Releases](https://github.com/mrniteshray/FlowBit/releases)

---

## 🤝 Contributing

Contributions are welcome! Feel free to open issues or submit pull requests.

---

## 📄 License

This project is open source. See the LICENSE file for details.

---

<p align="center">Made with ❤️ by <a href="https://github.com/mrniteshray">Nitesh Ray</a></p>
