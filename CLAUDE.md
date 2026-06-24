# final-examp-project — "Bloom" Flower Delivery App (Vibe Coding Exam)

Kotlin + Jetpack Compose multi-role flower-delivery marketplace for the "Mobile Application
Development (Android)" final exam. This file is the contract — follow it exactly.

> The earlier **Habit Tracker** version of this exam lives on branch `habit-tracker-backup`.
> `main` is now Bloom.

## ⛔ Hard rules (breaking any of these ZEROES the grade)
- **100% Jetpack Compose. NO XML layouts for UI. ABSOLUTELY NO `findViewById`.**
- **Kotlin only.**
- Project uploaded to **git in full** — no `.zip` / `.rar`.

## App overview
Three roles, routed after login by `users/{uid}.role` in Firestore:
- **Customer** — browse flowers, cart, delivery address, pay (Stripe test), track orders, history, complaint tickets.
- **Courier** — accept orders, pick up, deliver (live tracking + chat are a later phase).
- **Admin** — product CRUD, manage users, live orders, statistics dashboard, complaint tickets.

## Stack & conventions
- Jetpack Compose + Material 3; brand theme (rose pink + leaf green), package `ge.btu.flowershop`.
- MVVM: Composable ← ViewModel (StateFlow) ← Repository ← Firebase.
- Navigation Compose; bottom-nav menu per role.
- Firebase **Auth** (email + Google), **Firestore** (users/products/orders/tickets), **Storage** (images).
- Payments: **Stripe test mode** (Phase 3; needs a small backend holding the secret key).
- Images: Coil; demo catalog uses Unsplash CDN URLs (Higgsfield was out of credits).
- Repos guard against a missing Firebase config so the app builds/runs before setup; the
  login screen shows a dev "Preview as <role>" shortcut while `google-services.json` is absent.
- Versions: minSdk 24, compile/target 35, JDK 17, Kotlin 2.0.21, AGP 8.7, Gradle 8.9.

## Build phases
1. ✅ Foundation — auth + role routing
2. ✅ Catalog & cart — product list/detail, admin product CRUD, cart
3. ✅ Checkout (Stripe-test) + orders + customer tracking + courier flow + admin live orders
4. ✅ Admin statistics + complaint tickets
5. ⏳ (stretch) real-time chat + live courier map tracking (map needs a Google Maps API key)

Orders/tickets degrade to an in-memory store (`LocalOrderStore`/`LocalTicketStore`) before
Firebase exists, so the whole flow is demoable across role previews in one app run.

## Build & run (local, headless)
- JDK 17: Homebrew `openjdk@17` (`JAVA_HOME=/opt/homebrew/opt/openjdk@17`).
- Android SDK at `~/Library/Android/sdk`; build: `./gradlew :app:assembleDebug`.
- **Emulator:** AVD `exam_pixel_api35`. Launch WITH a DNS server or it has no internet
  (images/Firebase fail):
  `$ANDROID_HOME/emulator/emulator -avd exam_pixel_api35 -dns-server 8.8.8.8,8.8.4.4`
- Firebase needs `app/google-services.json` (added late, per the user's plan).
- Debug SHA-1 (for Google sign-in): `66:7E:C1:F3:69:F7:8E:58:BD:E5:37:02:2E:07:82:04:F0:A1:0E:64`.

## Git
- `origin` = friend's GitHub repo `https://github.com/Ekuna645645/final-examp-project.git`
  (HTTPS + her token; commits attributed to her by design).
