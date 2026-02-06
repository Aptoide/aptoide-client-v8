# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build debug (development)
./gradlew app:assembleDevDebug

# Build release (production)
./gradlew app:assembleProdRelease

# Run all unit tests
./gradlew test

# Run tests for a specific module
./gradlew :feature_search:test

# Run a single test class
./gradlew :feature_search:test --tests "com.example.MyTest"

# Lint check
./gradlew lint

# Clean build
./gradlew clean
```

## Architecture Overview

This is a **multi-module Android app** using **Clean Architecture with MVVM** and **Jetpack Compose**.

### Module Types

- **`:app`** - Aptoide Vanilla (flavors: `dev`, `prod`)
- **`:app-games`** - Aptoide Games
- **`:app-dt`** - Digital Turbine GamesHub (variant of Aptoide Games)
- **Feature modules** (`:feature_search`, `:feature_apps`, `:feature_appview`, etc.)
- **Core modules** (`:aptoide-network`, `:aptoide-installer`, `:aptoide-ui`)
- **`:payments:*`** - Payment system submodules

### Feature Module Structure

Feature modules with UI follow this package structure:
```
feature_xxx/
  data/           # Repository implementations, network services, mappers
  di/             # Hilt modules (RepositoryModule, UseCaseModule)
  domain/         # Use cases, domain models, repository interfaces
  presentation/   # ViewModels, Compose UI, UI state classes
```

**Not all feature modules have UI.** Some are data/domain-only (e.g., `feature-bonus`, `feature_campaigns`). When multiple product variants need different UIs for the same domain logic, the UI lives in product modules instead:

```
app-games/src/main/java/.../feature_apps/presentation/  # UI for app-games
app-dt/src/main/java/.../feature_apps/presentation/     # UI for app-dt (reuses patterns)
```

This allows `:app-dt` (GamesHub) and `:app-games` (Aptoide Games) to share domain logic from feature modules while customizing their UI independently.

### Key Patterns

- **Dependency Injection**: Hilt with `@HiltViewModel` for ViewModels
- **UI**: Jetpack Compose with Material Design
- **State Management**: `StateFlow` / `MutableStateFlow` with sealed classes for UI state
- **Async**: Kotlin Coroutines and Flow
- **Network**: Retrofit + OkHttp with custom interceptors
- **Database**: Room (schema version 106, name: `aptoide.db`)
- **Navigation**: Navigation Compose with Hilt integration

## Gradle Convention Plugins

Custom plugins in `build-logic/convention/` auto-configure modules:

- **`android-module`** - Base Android config (SDK versions, signing, ProGuard)
- **`composable`** - Enables Compose with all required dependencies
- **`hilt`** - Sets up Hilt + KSP
- **`tests`** - Configures JUnit 5 with test module dependencies

Apply in module's `build.gradle.kts`:
```kotlin
plugins {
    id("android-module")
    id("composable")
    id("hilt")
    id("tests")
}
```

## Code Style

- **2-space indentation** (defined in `codestyle/SquareAndroid.xml`)
- **100-character line limit**
- Java 17 source/target compatibility

## Testing

- **JUnit 5** for unit tests
- **Turbine** for Flow testing
- **Coroutines Test** for suspend function testing
- Shared test dependencies in `:test` module

## Key SDK Versions

- Compile/Target SDK: 35
- Min SDK: 26
- Kotlin: 2.1.10
- Compose: 1.8.1
- Hilt: 2.55
- Room: 2.7.1

## App Variants

| Module | App ID | Description |
|--------|--------|-------------|
| `:app` | `cm.aptoide.pt.v10` | Aptoide Vanilla |
| `:app-games` | `com.aptoide.android.aptoidegames` | Aptoide Games |
| `:app-dt` | `com.dti.hub` | Digital Turbine GamesHub (variant of Aptoide Games) |
