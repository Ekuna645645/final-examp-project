# final-examp-project — Android Habit Tracker (Vibe Coding Exam)

Kotlin + Jetpack Compose habit/reminder tracker built for the "Mobile Application
Development (Android)" final exam. This file is the contract for how the project is
built — follow it exactly.

## ⛔ Hard rules (breaking any of these ZEROES the grade)
- **100% Jetpack Compose. NO XML layouts for UI. ABSOLUTELY NO `findViewById` anywhere.**
  The grader explicitly said: the moment they see `findViewById`, the work is nullified.
- **Kotlin only.**
- Project must be uploaded to **git in full** — no `.zip` / `.rar`.

## ✅ Required features (every one must be present and demonstrable)
1. **A menu** → Compose bottom navigation bar.
2. **A list** → `LazyColumn`.
3. **MVVM architecture** → Composable screen ← ViewModel (StateFlow) ← Repository ← Firebase.
4. **A database connection** → **Firebase Realtime Database**.
5. **One feature not used in class before** → **scheduled local notifications** (daily habit reminders).
6. **README** describing the app, its technical details, and its content.

## Stack & conventions
- Jetpack Compose + Material 3.
- MVVM with Repository pattern; UI state via `StateFlow` / Compose `State`.
- Navigation Compose for the bottom-nav menu.
- Kotlin Coroutines + Flow for async work.
- Firebase Realtime Database (`google-services.json` lives in `app/`).
- Reminders: `AlarmManager` + `BroadcastReceiver` + `NotificationManager` (POST_NOTIFICATIONS permission on Android 13+).
- Versions: `minSdk 24`, `compileSdk/targetSdk 35`, JDK 17, Kotlin 2.x, AGP 8.x.
- Package: `ge.btu.habittracker`.

## Build & run (local, headless)
- JDK 17: Homebrew `openjdk@17`.
- Android SDK: `android-commandlinetools`; `ANDROID_HOME` set in shell + `local.properties`.
- Build: `./gradlew assembleDebug`
- Firebase requires `app/google-services.json` from the Firebase console.

## Git
- `origin` = friend's GitHub repo `https://github.com/Ekuna645645/final-examp-project.git`
  (HTTPS + her access token; commits are attributed to her account by design).
