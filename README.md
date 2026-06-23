# Bloom — Flower Delivery Marketplace (Android)

> Final project for **Mobile Application Development (Android)**, BTU.
> A multi-role flower-shop & delivery app built 100% in **Kotlin + Jetpack Compose**,
> **MVVM**, with **Firebase** (Auth, Firestore, Storage) and **Stripe** test payments.

**მოკლე აღწერა (KA):** ყვავილების მაღაზიის აპლიკაცია სამი როლით — **მომხმარებელი**
ყიდულობს ყვავილებს და ადევნებს თვალს შეკვეთას, **კურიერი** იღებს და აწვდის შეკვეთებს,
**ადმინი** მართავს პროდუქტებს, მომხმარებლებსა და სტატისტიკას. ავტორიზაცია — email/Google,
მონაცემები — Firebase Firestore, გადახდა — Stripe (test mode).

---

## 👥 Roles

| Role | Can do |
|---|---|
| **Customer** | Browse flowers, add to cart, set delivery address, pay (Stripe test), track current orders, see order history, file complaint tickets |
| **Courier** | See available orders, accept, pick up from storage, mark delivered *(live map tracking + chat: later phase)* |
| **Admin** | Manage products (CRUD + images), manage users, see live orders, view statistics dashboard, handle complaint tickets |

After sign-in, the app reads `users/{uid}.role` from Firestore and routes to the matching experience.

---

## 🧱 Tech stack

- **Language:** Kotlin · **UI:** Jetpack Compose + Material 3 (no XML layouts, no `findViewById`)
- **Architecture:** MVVM (Composable → ViewModel/StateFlow → Repository → Firebase)
- **Auth:** Firebase Authentication (Email/Password + Google)
- **Database:** Cloud Firestore (users, products, orders, tickets)
- **Storage:** Firebase Storage (product images)
- **Payments:** Stripe (test mode)
- **Images:** Coil; AI-generated / stock flower photos
- **Min SDK** 24 · **Target/Compile** 35 · JDK 17 · AGP 8.7 · Gradle 8.9

---

## 🏛️ Architecture (MVVM)

```
Composable screens  ──events──▶  ViewModel (StateFlow)  ──▶  Repository  ──▶  Firebase
       ▲                                                                        │
       └──────────────────────────  UI state  ◀────────────────────────────────┘
```

`SessionViewModel` exposes a single `SessionState` (Loading / SignedOut / SignedIn(user))
that the whole app reacts to, so auth and role routing are fully reactive.

---

## 🚧 Build status (phased)

- [x] **Phase 1 — Foundation:** Firebase Auth (email + Google), role-based routing, session state
- [x] **Phase 2 — Catalog & cart:** product list/detail, admin product CRUD, images (Coil), cart
- [ ] **Phase 3 — Checkout & orders:** Stripe test payment, orders, customer tracking, courier flow, admin live orders
- [ ] **Phase 4 — Admin statistics + complaint tickets**
- [ ] **Phase 5 (stretch):** live courier map tracking + real-time chat

---

## 🔧 Setup

### Firebase
1. Create a Firebase project at https://console.firebase.google.com/.
2. **Authentication** → enable **Email/Password** and **Google**.
3. **Firestore Database** → create (test mode).
4. **Storage** → get started (test mode).
5. **Project settings → Your apps → Android**, package **`ge.btu.flowershop`**, add the debug
   **SHA-1**, then download **`google-services.json`** into **`app/`**.

### Stripe (test mode)
- Create a Stripe account, stay in **Test mode**, grab the **publishable** (`pk_test_…`) key.
  (Used in Phase 3 with a small backend that holds the secret key.)

### Build & run
```bash
./gradlew :app:assembleDebug      # build
./gradlew :app:installDebug       # install on device/emulator
```
The app compiles and runs **before** Firebase is configured (it shows a "Firebase isn't
configured" banner); add `google-services.json` to enable login and data.

---

## ✅ Exam requirements mapping

- **Menu** → bottom navigation per role · **List** → `LazyColumn` (products, orders, …)
- **MVVM** → ViewModel + Repository + StateFlow
- **Database** → Firebase Firestore + Storage
- **New feature** → Stripe online payments + (later) live courier tracking
- **README** → this file · **Kotlin + Compose only**, no XML/`findViewById`
