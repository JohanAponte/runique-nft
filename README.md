# <img src="assets/logo.png" alt="Vista previa" width="30" height="30"/> Runique App

Runique is a fitness tracking Android application built with Kotlin and Jetpack Compose. The
application enables users to track running activities with GPS location tracking, view run history,
and synchronize data across devices. The app follows modern Android development practices
including multi-module architecture, clean architecture patterns, and offline-first data management.

## 🚀 Features

- **User Authentication**: Secure registration and login with encrypted session storage
- **GPS Run Tracking**: Real-time activity tracking with distance, pace, and elevation metrics
- **Offline-First Architecture**: All data persists locally first, with background synchronization
- **Run History**: View and analyze past running activities
- **Analytics Dashboard**: Detailed statistics and insights (dynamic feature module)
- **Background Service**: Continuous tracking even when app is in background

## 🖲️ Tech Stack

### Core Technologies

- **Kotlin** 2.1.21 with Coroutines 1.9.0
- **Jetpack Compose** with Material Design 3
- **Koin** 4.0.0 for dependency injection

### Data & Networking

- **Room** 2.8.3 for local database
- **Ktor Client** 2.3.11 for API communication
- **WorkManager** for background synchronization
- **Security Crypto** for encrypted storage

### Location & Maps

- **Google Play Services Location**
- **Google Maps Compose** for map visualization

### Testing

- **JUnit 5** with AssertK, Turbine, and MockK

# 🏛️ Architecture

Runique follows a multi-module, clean architecture approach with these core principles:

- **Feature Isolation**:    Each feature (auth, run, analytics) is a self-contained set of modules
- **Dependency Inversion**:    All features depend on core domain interfaces, not concrete
  implementations
- **Layer Separation**:    Strict separation between `presentation` → `domain` ← `data` layers
- **Offline-First**:    Local data sources are the source of truth; remote sync is secondary
- **Modular Testing**:    Test utilities in `core:test` and `core:android-test` are shared across
  modules
- **Build Standardization**:    Custom Gradle convention plugins enforce consistent configurations

### Module Structure

runique/
├── app/ # Application entry point
├── auth/ # Authentication feature
│ ├── data/ # Auth data sources & repositories
│ ├── domain/ # Auth business logic
│ └── presentation/ # Auth UI screens
├── run/ # Run tracking feature
│ ├── data/ # Run data sources & repositories
│ ├── domain/ # Run business logic
│ ├── presentation/ # Run UI screens
│ ├── location/ # GPS tracking implementation
│ └── network/ # Run API clients
├── analytics/ # Analytics feature (dynamic)
│ ├── data/
│ ├── domain/
│ ├── presentation/
│ └── analytics_feature/ # Dynamic feature module
└── core/ # Shared infrastructure
├── data/ # HTTP client, session storage
├── database/ # Room database configuration
├── domain/ # Shared domain models
├── presentation/
│ ├── designsystem/ # UI components & theme
│ └── ui/ # Shared UI utilities
├── test/ # Test utilities
└── android-test/ # Android test utilities