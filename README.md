# Marxist Reader

An **offline-first** Android app for reading Marxist and leftist content. Built with Kotlin and Jetpack Compose.

## Features

- **Feed** — Browse articles and posts
- **Article detail** — Full content with categories, tags, and read-time (Tamil-oriented)
- **Saved** — Bookmark and read saved posts offline
- **Books** — Books section
- **Settings** — App preferences
- **Welcome** — Onboarding flow

Content is stored locally (Room) and synced in the background; the app works without connectivity.

## Tech stack

- **Language:** Kotlin 2.x
- **UI:** Jetpack Compose, Material 3
- **DI:** Hilt
- **Local storage:** Room, DataStore
- **Background sync:** WorkManager
- **Networking:** Retrofit, OkHttp (sync only)

## Architecture

- **Modular:** Separate modules for app, UI (theme, common), network, navigation, core, data, domain, use-cases, and feature modules (feed, feeddetails, saved, books, more, settings, welcome).
- **Offline-first:** Local DB is the source of truth; UI reads only from local storage. Network is used for sync; features work without internet.

## Requirements

- Android SDK 26+ (min), 36 (target)
- JDK 17
- Android Studio or compatible IDE

## Build & run

```bash
./gradlew assembleDebug
./gradlew installDebug
```

Or open the project in Android Studio and run the **app** configuration.

## Project structure

| Layer        | Modules |
|-------------|---------|
| App         | `app`   |
| UI          | `ui:theme`, `ui:common` |
| Features    | `feature:feed`, `feature:feeddetails`, `feature:saved`, `feature:books`, `feature:more`, `feature:settings`, `feature:welcome` |
| Data / flow | `data`, `domain`, `use-cases`, `network`, `navigation`, `core` |

## License

This project is licensed under the **GNU General Public License v3.0**. See [LICENSE](LICENSE) for the full text.
