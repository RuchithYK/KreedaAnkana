# 🏏 Kreeda Ankana — Village Sports Hub

> A community sports management Android app built for villages to organize ground bookings, post match challenges, track scores, and manage teams — all in real time.

---

## 📱 About the Project

**Kreeda Ankana** (meaning "Sports Ground" in Sanskrit) solves a real problem in Indian villages — sports grounds are often occupied by the same group all day with no organized system to book slots or find opponents.

This app turns any village ground into a **digital sports hub** where teams can:
- Reserve time slots and prevent double bookings
- Post challenges and find opponents for friendly matches
- Track match results and scores
- Manage team rosters and match history

Built as an internship project under the theme **"Android App Development using GenAI"**.

---

## ✨ Features

### 🔐 Authentication
- Google Sign In
- Email & Password Sign Up / Sign In
- Forgot Password via email reset
- Secure session management with Firebase Auth

### 📅 Ground Calendar
- Book ground slots with date and time picker
- Conflict detection — prevents two teams from booking the same time slot
- Edit and delete your own bookings
- Real-time updates across all users

### ⚔️ Challenge Board
- Post match challenges to other teams
- OPEN / ACCEPTED status system
- First team to accept locks the challenge
- Reply system — only the accepted team can respond
- Edit and delete your own challenges
- Live updates in real time

### 🏆 Score Wall
- Post match results with scoreboard UI
- Filter by sport — ALL / Cricket / Volleyball
- Winning team highlighted in green
- Edit and delete your own scores
- Real-time sync

### 🏟️ Team System
- Create a new team with sport type
- Browse and join existing teams
- Leave your current team
- View all team members with join dates
- Captain badge for team creators
- Team match history with WIN / LOST badges

### 👤 Profile
- View and edit team name
- Logout
- Delete account — removes all user data from Firestore including bookings, challenges, scores, and team memberships

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI Framework | Jetpack Compose + Material3 |
| Architecture | MVVM (Model-View-ViewModel) |
| Navigation | Jetpack Navigation Compose |
| Authentication | Firebase Authentication |
| Database | Firebase Firestore (real-time) |
| Image Loading | Coil |
| Async | Kotlin Coroutines + Flow |
| State Management | StateFlow + MutableStateOf |

---

## 🏗️ Architecture

The app follows **MVVM architecture** with a clean separation of concerns:

```
UI Layer (Compose Screens)
        ↓
ViewModel Layer (state + business logic)
        ↓
Repository Layer (single source of truth)
        ↓
Firebase Firestore (cloud database)
```

### Project Structure

```
app/
└── java/com/example/kreedaankana/
    ├── data/
    │   └── Models.kt               # Data classes: Booking, Challenge, Score, Team, UserProfile
    ├── repository/
    │   └── FirebaseRepository.kt   # All Firestore operations
    ├── viewmodel/
    │   ├── AuthViewModel.kt        # Login, signup, forgot password
    │   ├── BookingViewModel.kt     # Slot booking + conflict detection
    │   ├── ChallengeViewModel.kt   # Challenge board logic
    │   ├── ScoreViewModel.kt       # Score wall + filters
    │   ├── TeamViewModel.kt        # Team management
    │   ├── ProfileViewModel.kt     # User profile + account deletion
    │   └── HomeViewModel.kt        # Home screen data
    └── ui/theme/
        ├── screens/                # All composable screens
        └── components/             # Reusable components (DatePicker, TimePicker)
```

---

## 🔥 Firebase Structure

```
firestore/
├── users/{userId}
│   ├── displayName, email, teamId, teamName, photoUrl
├── bookings/{bookingId}
│   ├── userId, teamName, sport, date, startTime, endTime
├── challenges/{challengeId}
│   ├── userId, teamName, sport, message, date
│   ├── status (OPEN / ACCEPTED)
│   ├── acceptedByTeam, acceptedByUserId, reply
├── scores/{scoreId}
│   ├── userId, team1, team2, score1, score2, sport, date
└── teams/{teamId}
    ├── teamName, sport, captainId, captainName, createdAt
    └── members: [{userId, displayName, joinedAt}]
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or newer
- Android SDK API 24+
- A Firebase project with Firestore and Authentication enabled

### Setup

**1. Clone the repository**
```bash
git clone https://github.com/yourusername/kreeda-ankana.git
cd kreeda-ankana
```

**2. Connect Firebase**
- Go to [Firebase Console](https://console.firebase.google.com)
- Create a new project
- Add an Android app with package name `com.example.kreedaankana`
- Download `google-services.json` and place it in the `app/` folder

**3. Enable Firebase Services**
- Authentication → Enable **Google** and **Email/Password** sign-in methods
- Firestore Database → Create database in **test mode**
- Add your SHA-1 fingerprint under Project Settings → Your Apps

**4. Update Web Client ID**

In `MainActivity.kt`, replace the placeholder:
```kotlin
val webClientId = "YOUR_WEB_CLIENT_ID_HERE"
```
Get this from Firebase Console → Project Settings → Your Apps → Web client ID

**5. Run the app**
```bash
./gradlew assembleDebug
```
Or press **Run** in Android Studio.

---

## 📦 Dependencies

```kotlin
// Firebase
implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
implementation("com.google.firebase:firebase-auth-ktx")
implementation("com.google.firebase:firebase-firestore-ktx")

// Google Sign In (Credential Manager)
implementation("androidx.credentials:credentials:1.3.0")
implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

// Navigation
implementation("androidx.navigation:navigation-compose:2.8.9")

// ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")

// Coil (image loading)
implementation("io.coil-kt:coil-compose:2.6.0")
```

---

## 🎯 Key Implementation Highlights

### Real-time Updates
All data uses Firestore's `addSnapshotListener` converted to Kotlin `Flow` via `callbackFlow`. Any change in Firestore instantly reflects across all users without manual refresh.

### Booking Conflict Detection
Before saving a slot, the app checks all existing bookings on the same date for time overlap using a custom `timesOverlap()` function that converts AM/PM time strings to minutes and compares ranges mathematically.

### OPEN / ACCEPTED Challenge System
Challenges start as `OPEN`. The first team to tap **Accept** locks the challenge to `ACCEPTED` status. Only the accepted team can send a reply message. This prevents multiple teams from accepting the same challenge.

### Manual Dependency Injection
ViewModels receive their Repository via constructor — no Hilt or Dagger used, keeping the architecture simple and educational.

---

---

## 📸 Screenshots

| Home | Calendar | Challenge Board | Score Wall |
|:---:|:---:|:---:|:---:|
| <img src="https://github.com/user-attachments/assets/51a4c92c-6257-47e0-974e-bc0daa338cea" width="200" /> | <img src="https://github.com/user-attachments/assets/8de4a3ea-9c82-404c-a5f9-aef195cc3fda" width="200" /> | <img src="https://github.com/user-attachments/assets/284f073b-7831-486c-981d-01fb821c5111" width="200" /> | <img src="https://github.com/user-attachments/assets/9c9991ec-3978-4d44-bcf7-7d29e12937eb" width="200" /> |

---

## 🤝 Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

---

## 📄 License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Author

**Ruchith Y K**
- Built during Android development internship
- Learned from Google's Android Basics with Compose course (Units 1–6)

---

## 🙏 Acknowledgements

- [Google Android Basics with Compose](https://developer.android.com/courses/android-basics-compose/course) — the learning foundation
- [Firebase Documentation](https://firebase.google.com/docs) — backend services
- [Material Design 3](https://m3.material.io/) — UI components and theming
- [JSONPlaceholder](https://jsonplaceholder.typicode.com/) — used for API learning exercises
