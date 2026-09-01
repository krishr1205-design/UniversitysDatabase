<div align="center">

# 🎓 University's Database

**A clean Android student directory app built with Jetpack Compose.**

![Android](https://img.shields.io/badge/Platform-Android-brightgreen?logo=android)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose)
![Min SDK](https://img.shields.io/badge/Min%20SDK-24-orange)
![License](https://img.shields.io/badge/License-MIT-blue)

</div>

---

## ✨ Features

| Feature | Description |
|---|---|
| 🔐 **CAPTCHA Gate** | Canvas-rendered CAPTCHA with noise, distortion, and refresh support |
| 🏛️ **Multi-University** | Add, edit, and delete universities — each fully independent |
| 👥 **Groups / Classes** | Organise students into named groups per university |
| 🎓 **Student CRUD** | Add, view, edit, and delete students with full validation |
| 🖼️ **Photo Picker** | Pick a photo from device storage; persisted via URI permissions |
| 🎨 **5 App Themes** | Ocean Blue, Dark, Forest Green, Royal Purple, Sunset Orange |
| 💾 **Local Persistence** | All data saved to SharedPreferences — no internet needed |
| 📱 **Offline-first** | Fully functional without any backend or account |

---

## 📱 Screenshots

> _Add screenshots here after building the app._

| Welcome | CAPTCHA | Universities |
|---|---|---|
| *(screenshot)* | *(screenshot)* | *(screenshot)* |

| Groups | Students | Student Detail |
|---|---|---|
| *(screenshot)* | *(screenshot)* | *(screenshot)* |

---

## 🛠️ Tech Stack

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose + Material 3
- **Architecture:** Single-activity, state-hoisting composable pattern
- **Persistence:** SharedPreferences + JSON (org.json)
- **Image loading:** Manual BitmapFactory on IO dispatcher (no third-party image libraries)
- **Build system:** Gradle with Kotlin DSL (`.gradle.kts`)
- **Min SDK:** 24 (Android 7.0 Nougat)
- **Target SDK:** 36

---

## 🏗️ Project Structure

```
app/src/main/java/com/example/program1/
│
├── MainActivity.kt              # Activity + StudentApp() navigation state machine
│
├── data/
│   ├── Models.kt                # Student, GroupInfo data classes
│   └── Persistence.kt           # SharedPreferences save/load helpers
│
└── ui/
    ├── theme/
    │   ├── AppTheme.kt          # Themes, color schemes, palettes
    │   ├── Theme.kt             # Material3 Program1Theme wrapper
    │   ├── Color.kt             # Base color tokens
    │   └── Type.kt              # Typography
    │
    ├── common/
    │   └── ImageComposables.kt  # StudentImage + LocalPhoto composables
    │
    └── screens/
        ├── WelcomeScreen.kt     # Welcome + ThemeDialog
        ├── CaptchaScreen.kt     # CAPTCHA screen + canvas renderer
        ├── UniversityScreen.kt  # University list + row (edit/delete)
        ├── GroupScreen.kt       # Group list + row (edit/delete)
        ├── StudentListScreen.kt # Student list + card
        ├── StudentFormScreen.kt # Add student + Edit student (shared fields)
        └── StudentDetailsScreen.kt  # Full student profile view
```

---

## 🚀 Getting Started

### Prerequisites

- Android Studio **Hedgehog** or newer
- JDK 11+
- Android device or emulator running API 24+

### Build & Run

1. **Clone the repo**
   ```bash
   git clone https://github.com/<your-username>/UniversitysDatabase.git
   cd UniversitysDatabase
   ```

2. **Open in Android Studio**
   - File → Open → select the `UniversitysDatabase` folder

3. **Sync Gradle**
   - Android Studio will automatically prompt to sync. Click **Sync Now**.

4. **Run**
   - Select your device/emulator and press **▶ Run**

---

## 📖 How It Works

```
Welcome Screen
    │ START
    ▼
CAPTCHA Screen ──── (verify) ────▶ University Screen
                                        │ select university
                                        ▼
                                   Group Screen
                                        │ select group
                                        ▼
                                   Student List
                                    │         │
                               (tap card)  (+ ADD)
                                    │         │
                               Student     Add Student
                               Details       Form
                                    │
                                 (EDIT)
                                    │
                               Edit Student Form
```

---

## 🗂️ Data Model

```kotlin
data class Student(
    val id: String,          // 6-digit unique ID
    val name: String,
    val college: String,     // Parent university
    val phone: String,
    val group: String,       // Parent group
    val color: Color,        // Card accent color
    val imageResource: Int?, // Bundled drawable (demo data)
    val imageUri: String?    // User-picked content URI
)

data class GroupInfo(
    val university: String,
    val name: String
)
```

---

## 🤝 Contributing

Pull requests are welcome! For major changes, please open an issue first to discuss what you'd like to change.

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -m 'Add my feature'`
4. Push: `git push origin feature/my-feature`
5. Open a pull request

---

## 📝 License

This project is licensed under the [MIT License](LICENSE).
