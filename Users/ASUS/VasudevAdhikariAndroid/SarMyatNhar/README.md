# SarMyatNhar

SarMyatNhar is a comprehensive Android application designed for reading, uploading, and managing digital books. The app provides a platform for users to explore a library of books, authors to upload and manage their works, and administrators to oversee user activity. Built with modern Android technologies such as Jetpack Compose and Room Database, SarMyatNhar offers a smooth, dynamic, and visually appealing user experience.

## Objectives & Purpose
- **Empower Readers:** Provide a digital library where users can browse, read, and download books in various categories.
- **Support Authors:** Enable authors to upload, manage, and track the status of their books, including cover images, categories, and pricing.
- **Admin Oversight:** Allow administrators to manage users, monitor content, and ensure the quality and safety of the platform.
- **Gamified Experience:** Introduce a wallet/coin system to reward reading and author contributions, encouraging engagement and activity.
- **Modern Android Practices:** Demonstrate the use of Jetpack Compose, Room, and Material Design 3 in a real-world, multi-role application.

## Features
### User Features
- **Registration & Login:** Secure sign-up and login with email and password. User sessions are managed locally.
- **Profile Management:** Users can view and edit their profile, including uploading a profile picture, updating contact info, and changing their password.
- **Book Browsing:** Explore books by category, popularity, rating, and more. Search and filter functionality is available.
- **Wallet System:** Users earn and spend coins (read_points and income_points) for reading and uploading books.

### Author Features
- **Book Upload:** Authors can upload new books with cover images, descriptions, categories, and set a price in coins.
- **Book Management:** View, edit, or delete uploaded books. Track approval status (pending, approved, rejected).
- **Profile & Payment:** Manage author profile, pen name, bio, and payment method for receiving earnings.

### Admin Features
- **User Management:** View all registered users, including their profiles and contact information.
- **Content Oversight:** Monitor uploaded books and author activity. (Further admin features can be extended.)

### General Features
- **Dynamic UI:** All screens are built with Jetpack Compose for a modern, responsive interface.
- **Image Loading:** Book covers and profile images are loaded efficiently using Coil.
- **Local Data Storage:** All data is stored locally using Room Database. No external APIs or cloud services are used.

## Limitations
- **Local-Only:** All data is stored on the device using Room. There is no cloud sync, remote API, or multi-device support.
- **No Real Payments:** The wallet/coin system is for demonstration and does not involve real money or external payment gateways.
- **Single Device:** User data, books, and coins are not shared across devices.
- **No Push Notifications:** All notifications and updates are local; there is no server-side messaging.
- **No Social Features:** No chat, comments, or social sharing is implemented.

## Project Structure
```
SarMyatNhar/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/application/sarmyatnhar/
│   │       │   ├── data/
│   │       │   │   ├── AppDatabase.kt           # Room database setup
│   │       │   │   ├── dao/                    # Data Access Objects (UserDao, BookDao, WalletDao, etc.)
│   │       │   │   └── entity/                 # Data models/entities (User, Book, AuthorProfile, Wallet, etc.)
│   │       │   ├── ui/
│   │       │   │   ├── activities/             # Main app screens (Login, Register, Home, Profile, Writer, BookDetails, etc.)
│   │       │   │   ├── admin/activities/       # Admin panel screens (UsersActivity, etc.)
│   │       │   │   ├── components/             # Reusable Jetpack Compose UI components (HeaderSection, BottomNavigationBar, etc.)
│   │       │   │   └── theme/                  # Theme, color, and style definitions
│   │       │   └── MainActivity.kt             # App entry point
│   │       └── res/
│   │           ├── layout/                     # XML layouts for legacy or non-Compose screens
│   │           └── drawable/                   # App icons, images, and vector assets
│   └── build.gradle
└── README.md
```

## Detailed Module Descriptions
- **data/entity/**: Contains all Room entity data classes, such as User, Book, AuthorProfile, Wallet, and PaymentMethod. These define the schema for local storage.
- **data/dao/**: Contains DAO interfaces for Room, providing methods for CRUD operations on each entity (e.g., UserDao, BookDao, WalletDao).
- **data/AppDatabase.kt**: The Room database class that ties together all entities and DAOs.
- **ui/activities/**: Contains all main user-facing screens, including authentication (LoginActivity, RegisterActivity), main app navigation (HomeActivity), user profile (ProfileActivity), book upload (WriterActivity), and book details.
- **ui/admin/activities/**: Admin-only screens for managing users and content.
- **ui/components/**: Reusable Jetpack Compose UI components, such as navigation bars, headers, dialogs, and custom widgets.
- **ui/theme/**: Color palettes, typography, and theming for consistent app appearance.

## Getting Started
1. **Clone the repository:**
   ```
   git clone <repo-url>
   ```
2. **Open in Android Studio:**
   - Open the project folder in Android Studio.
3. **Build and Run:**
   - Connect an Android device or start an emulator.
   - Click 'Run' to build and launch the app.

## Tech Stack
- **Kotlin**: Main programming language
- **Jetpack Compose**: Modern UI toolkit for building native Android interfaces
- **Room Database**: Local data persistence
- **Material Design 3**: UI/UX design system
- **Coil**: Image loading and caching
- **Coroutines**: Asynchronous programming

## Contributing
This project is for educational and demonstration purposes. Contributions are welcome for learning, bug fixes, or feature suggestions.

## License
This project is for educational purposes only. No commercial use or redistribution is permitted.
