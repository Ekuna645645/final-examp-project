# Habit Tracker — Android (Jetpack Compose + Firebase)

> Final project for **Mobile Application Development (Android)**, BTU.
> A daily habit/reminder tracker built 100% in **Kotlin + Jetpack Compose** with an
> **MVVM** architecture and a **Firebase Realtime Database** backend.

**მოკლე აღწერა (KA):** აპლიკაცია გეხმარება ყოველდღიური ჩვევების შექმნაში — ამატებ ჩვევას,
ნიშნავ შესრულებულად, ადევნებ თვალს სტრიქს (streak) და აყენებ ყოველდღიურ ლოკალურ
შეხსენებას (ნოტიფიკაციას). მონაცემები ინახება Firebase Realtime Database-ში.

---

## ✨ Features

| Requirement | How it's implemented |
|---|---|
| **Menu** | Material 3 **bottom-navigation bar** with three tabs (Today / Habits / Stats), powered by Navigation Compose. |
| **List** | `LazyColumn` lists of habits on every tab. |
| **MVVM architecture** | Composable screens observe a `StateFlow` exposed by `HabitViewModel`, which talks to a `HabitRepository`. |
| **Database connection** | **Firebase Realtime Database** — real-time, cloud-synced storage with offline persistence. |
| **A feature not used before** | **Scheduled local notifications** — each habit can have a daily reminder delivered via `AlarmManager` + `BroadcastReceiver` + `NotificationManager`. |
| **README** | This file. |
| **No XML UI / no `findViewById`** | The entire UI is Jetpack Compose. There is not a single layout XML or `findViewById` call in the project. |

### What the app does
- **Today** – the habits for today with a checkbox to mark them done and a daily progress bar.
- **Habits** – create, edit and delete habits. Each habit has a name, description, icon, color and an optional daily reminder time. Add via the floating **+** button.
- **Stats** – an overall summary (done today, best streak, total check-ins) plus a per-habit **last-7-days strip** and current streak.
- **Reminders** – when a habit's reminder is on, the app schedules a repeating daily alarm that posts a local notification at the chosen time.

---

## 🧱 Tech stack

- **Language:** Kotlin
- **UI:** Jetpack Compose, Material 3 (dynamic color on Android 12+)
- **Architecture:** MVVM (View → ViewModel → Repository → Firebase)
- **Async:** Kotlin Coroutines + Flow (`callbackFlow`, `StateFlow`)
- **Navigation:** Navigation Compose
- **Backend:** Firebase Realtime Database (with offline persistence)
- **Notifications:** `AlarmManager`, `BroadcastReceiver`, `NotificationCompat`
- **Min SDK:** 24 · **Target/Compile SDK:** 35 · **JDK:** 17 · **AGP:** 8.7 · **Gradle:** 8.9

---

## 🏛️ Architecture (MVVM)

```
┌──────────────────────────────┐
│  UI (Composable screens)     │   Today / Habits / AddEdit / Stats
│  observe StateFlow, emit      │
│  user events                  │
└───────────────┬──────────────┘
                │  events ▲ state
┌───────────────▼──────────────┐
│  HabitViewModel               │   exposes HabitUiState (StateFlow),
│  (AndroidViewModel)           │   handles add/edit/delete/toggle,
│                               │   keeps reminders in sync
└───────────────┬──────────────┘
                │
┌───────────────▼──────────────┐        ┌────────────────────────┐
│  HabitRepository              │───────▶│ Firebase Realtime DB    │
│  (single source of truth)     │◀───────│  /habits/{id}           │
└───────────────┬──────────────┘        └────────────────────────┘
                │
┌───────────────▼──────────────┐
│  ReminderScheduler            │   schedules daily AlarmManager
│  + ReminderReceiver           │   alarms → local notifications
└──────────────────────────────┘
```

The UI never touches Firebase directly — it only reads `HabitUiState` and calls
`HabitViewModel` functions. The repository converts Realtime Database snapshots into a
reactive `Flow<List<Habit>>`, so the UI updates the instant the cloud data changes.

---

## 📁 Project structure

```
app/src/main/java/ge/btu/habittracker/
├── HabitApplication.kt          # Firebase persistence + notification channel
├── MainActivity.kt              # single Activity, hosts Compose
├── data/
│   ├── model/Habit.kt           # data class stored in Firebase
│   ├── model/HabitStats.kt      # streak / completion-rate helpers
│   ├── util/DateUtils.kt        # date keys & labels
│   └── repository/HabitRepository.kt   # Firebase Realtime Database access
├── notifications/               # ← the "new" feature
│   ├── NotificationHelper.kt    # channel + posting notifications
│   ├── ReminderScheduler.kt     # AlarmManager scheduling
│   ├── ReminderReceiver.kt      # fires the notification
│   └── BootReceiver.kt          # re-schedules after reboot
└── ui/
    ├── HabitTrackerApp.kt       # Scaffold + bottom-nav menu + NavHost
    ├── HabitViewModel.kt        # MVVM ViewModel
    ├── navigation/Destination.kt
    ├── components/              # reusable UI (badges, headers, empty states)
    ├── today/TodayScreen.kt
    ├── habits/HabitsScreen.kt
    ├── habits/AddEditHabitScreen.kt
    └── stats/StatsScreen.kt
```

---

## 🔔 The "new" feature: scheduled local notifications

Each habit can carry a daily reminder. When you enable it and pick a time:

1. `HabitViewModel.saveHabit()` calls `ReminderScheduler.sync(habit)`.
2. `ReminderScheduler` registers a **repeating daily** alarm with `AlarmManager`
   (`setInexactRepeating`, so no special exact-alarm permission is required).
3. When the alarm fires, `ReminderReceiver` posts a notification through
   `NotificationHelper`.
4. `BootReceiver` re-creates all alarms after a device reboot.

On Android 13+ the app requests the `POST_NOTIFICATIONS` runtime permission on first launch.

---

## 🔥 Firebase setup (required to run)

The app needs an `app/google-services.json` file from your own Firebase project:

1. Open the [Firebase console](https://console.firebase.google.com/) and **Add project**.
2. Inside the project, **Add app → Android**. Use the package name **`ge.btu.habittracker`**.
3. Download the generated **`google-services.json`** and place it in the **`app/`** folder.
4. In the console, go to **Build → Realtime Database → Create database** (start in **test mode**
   for development).
5. Re-download `google-services.json` if the database URL was added after you first downloaded it.

> Until `google-services.json` is added the app still launches and shows a
> "Firebase isn't connected" banner instead of crashing.

---

## ▶️ Build & run

**Requirements:** JDK 17, Android SDK (platform 35, build-tools 35), an emulator or device.

```bash
# Build the debug APK from the command line
./gradlew :app:assembleDebug

# Install on a connected device / running emulator
./gradlew :app:installDebug
```

Or open the project in **Android Studio** and press **Run**.

---

## ✅ Requirements checklist

- [x] Menu (bottom navigation)
- [x] List (`LazyColumn`)
- [x] MVVM architecture
- [x] Database connection (Firebase Realtime Database)
- [x] A feature we hadn't used before (scheduled local notifications)
- [x] README with description, technical details and content
- [x] 100% Kotlin + Jetpack Compose — **no XML layouts, no `findViewById`**
- [x] Uploaded to git in full (no `.zip` / `.rar`)
