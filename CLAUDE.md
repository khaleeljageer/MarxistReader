# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Marxist Reader — an offline-first Android app (Kotlin, Jetpack Compose, Material 3) for reading Marxist/leftist content (Tamil-oriented). Content is fetched from a WordPress REST API, stored in Room, and the UI reads only from local storage. Licensed GPL-3.0.

## Build & run

```bash
./gradlew assembleDebug
./gradlew installDebug
```

Or run the **app** configuration in Android Studio. Requires JDK 17, min SDK 26, target/compile SDK 36/37.

Each module's `build.gradle.kts` should read `compileSdk` via `libs.versions.compileSdk.get().toInt()` rather than hardcoding a number — a module left on a stale hardcoded value will fail `checkDebugAarMetadata` once another module it depends on (or an AndroidX library) requires a newer one.

## Testing

```bash
./gradlew test                                   # all unit tests (JVM)
./gradlew :feature:feed:testDebugUnitTest         # single module
./gradlew :feature:feed:testDebugUnitTest --tests "*.FeedViewModelTest"   # single class
./gradlew connectedAndroidTest                    # instrumented tests (needs device/emulator)
```

Unit tests use JUnit4 + MockK + `kotlinx-coroutines-test`. `feature/feed` has a `MainCoroutineRule` for swapping `Dispatchers.Main` in tests — reuse that pattern for other ViewModel tests rather than reinventing it. Test coverage today is thin (`use-cases`, `data` repository, `feature:feed` ViewModel); most other modules only have the default template test.

There is no configured linter/formatter (no ktlint/detekt) and no CI workflow in this repo — verify changes by building and running tests locally.

## Module architecture (Clean Architecture, enforced by dependency direction)

This is a multi-module Gradle project. **Dependency direction is a hard rule, not a suggestion** — see `docs/MODULE_DEPENDENCIES.md` for the authoritative table. Summary:

```
app ──> navigation, data, ui:theme, feature:*
navigation ──> core, ui:theme            (NEVER feature modules, data, domain)
feature:* ──> domain, use-cases, core, ui:theme   (NEVER data, network, other features)
data ──> domain, use-cases, network, core
use-cases ──> domain
network ──> core
domain ──> (nothing but stdlib/coroutines — pure Kotlin JVM module, no Android)
```

Key implication: **feature modules never talk to `data` or `network` directly** — they inject use cases from `use-cases`, which depend only on repository interfaces defined in `domain`. `app` is the only module allowed to wire concrete implementations together (via Hilt) and is the only place a new feature's `NavHost` route gets registered (`app/.../MainScreensNavHost.kt`), so that `navigation` stays feature-agnostic.

Modules:
- `app` — application shell, Hilt setup, root `NavHost` (`App.kt` → `Route.Root`/`Welcome`/`Main`/`ArticleDetail`), `MainScreensNavHost.kt` composes each feature screen onto a tab route.
- `core` — no-dependency shared utilities: `ResultState`, `AppConfig` (base URL, sync page size — centralize config changes here), image/error models.
- `domain` — pure Kotlin: domain models, repository *interfaces* (`PostRepository`, `SavedPostRepository`, etc.), `SyncResult`/`SyncStatus`.
- `use-cases` — one class per user-facing action (`GetPostsFlowUseCase`, `SyncPostsUseCase`, `SavePostUseCase`, …), each depends only on a domain repository interface. Trivial one-line wrappers around a single repo call are intentionally skipped (see `docs/ARCHITECTURE_ROADMAP.md` Phase 2.4) — don't add a use case for something that doesn't need one.
- `data` — repository implementations, Room (`AppDatabase`, DAOs, entities, mappers in `source/local/database`), DataStore-backed repos (`source/local/datastore`), remote data sources wrapping Retrofit (`source/remote`), WorkManager sync (`worker/SyncWorker`, `worker/Sync`), Hilt modules in `data/di/`.
- `network` — Retrofit/OkHttp/kotlinx-serialization API client (`WPApiService`) and DTOs; must not know about `domain` or `data`.
- `navigation` — `Route`/`Screen` sealed classes, `MainAppState`, `MainScreen` scaffold (bottom nav/top bar). Takes feature content as a lambda slot so it never imports a `feature:*` module.
- `ui:theme` / `ui:common` — Compose theme (Material 3) and shared composables (article list item, skeleton, status message).
- `feature/*` — `feed`, `feeddetails`, `books`, `saved`, `search`, `settings`, `more`, `welcome`. Each follows `Screen`/`ViewModel`/`UiState` — see `docs/FEATURE_TEMPLATE.md` for the exact pattern to copy when adding a new feature.
- `reader` — a largely self-contained EPUB reader built on the Readium Kotlin toolkit 3.1.2 (own Room DB, own DI/ViewModel setup under package `com.jskaleel.epub`, uses fragments/ViewBinding for the reader screen itself, though its settings sheets (`reader/.../preferences/UserPreferences.kt`, `ComposeBottomSheetDialogFragment`) and the TTS control bar are built with embedded Jetpack Compose `ComposeView`s). It **is** wired into `app`'s navigation, but through an `Activity` boundary rather than a Compose `Route`: `feature:books` → tapping a book calls `OpenBookViewModel.openBook()` (`app/.../OpenBookViewModel.kt`), which imports the book into the reader's own Room DB on first open (caching the book-id → reader-id mapping via use-cases), then `App.kt` launches `ReaderActivity` via `ReaderActivityContract` (`context.launchReaderActivity()`). Any Compose content a Fragment here creates via `setContent { }` is **not** auto-themed — it must be wrapped in `AppTheme { }` (`reader/.../utils/compose/AppTheme.kt`) explicitly or it renders with Compose's default Material3 palette instead of the reader's warm cream/sepia scheme.
  - Non-obvious Readium gotcha: `EpubPreferences.publisherStyles` defaults to `true` when unset (`EpubSettingsResolver` fallback), and Readium's bundled CSS only makes body/paragraph text actually follow the user's line-height/letter-spacing/text-align/font-size settings when `publisherStyles` is `false` (CSS flag `readium-advanced-on`). This app pins it off via `EpubNavigatorFactory.Configuration(defaults = EpubDefaults(publisherStyles = false))` in `EBookReaderRepository.openEpub()`, and seeds `lineHeight`/`letterSpacing`/`textAlign` with real (non-null) starting values in `EpubPreferencesManagerFactory.emptyPreferences` (`reader/.../preferences/PreferencesManagers.kt`) — several of Readium's `isEffective` checks require the preference's *own* value to already be non-null before the corresponding UI control becomes interactive, which is otherwise a permanent deadlock for a fresh install.

### Package/namespace convention

Everything except `reader` lives under the `org.cpimtn.marxist` root: `org.cpimtn.marxist.android.*` for app/data/domain/feature modules, `org.cpimtn.marxist.<module>` for library modules with no Android-specific API (`network`, `core`, `navigation`), `org.cpimtn.marxist.android.ui` for `ui:theme`/`ui:common`. `reader` predates this convention and uses package/namespace `com.jskaleel.epub` throughout — don't "fix" this without checking with the user first, it's a known inconsistency, not an oversight.

## Offline-first / sync behavior

- Room is the single source of truth; ViewModels never call the network directly.
- `PostRepositoryImpl.fullSync()` (`data/.../repository/PostRepositoryImpl.kt`) paginates through the WordPress API and only calls `postDao.replaceAll()` once the full fetch succeeds — a failed or partial sync never wipes existing local data. Follow this "fetch fully, then replace in one transaction" pattern for any new syncable entity.
- Background sync runs via Hilt-injected `SyncWorker` (WorkManager, `NetworkType.CONNECTED` constraint, exponential backoff), triggered from `MarxistReaderApp.onCreate()` through `Sync.initialize()`.
- Errors surface as a `SyncResult` sealed type (`Success` / `NetworkError` / `ServerError` / `UnknownError`); UI-facing `*UiState` types should map these rather than swallowing them.

## Localization (Tamil default, English secondary)

- Every module with UI strings ships two resource sets: default `values/strings.xml` (Tamil — the app's default language) and `values-en/strings.xml` (English). There is no `values-ta/` folder; Tamil lives in the default `values/` since it doubles as the fallback.
- Strings not yet translated are marked `TODO_TA: <english text>` (Tamil file missing a translation) or `TODO_EN: <tamil text>` (English file missing one) — grep for these before shipping.
- `feature/welcome`'s onboarding carousel intentionally shows Tamil + English text side by side on every card (`*_title_en` / `*_desc_en` keys) regardless of the active app locale — that's a deliberate design choice, not part of the values/values-en split, so don't fold those keys into it.
- The active language (`AppLanguage`, `domain/.../model/AppLanguage.kt`) is persisted via `SettingsRepository`/DataStore (`UserSettingsRepositoryImpl`) and changed from Settings → Language (`feature/settings`). Actually applying it goes through `LocaleController` (`app/.../app/LocaleController.kt`), a thin wrapper around `AppCompatDelegate.setApplicationLocales()`: called once at cold start in `MarxistReaderApp.onCreate()` (blocking read of the persisted preference before the first UI frame) and reactively in `MainActivity`'s `LaunchedEffect(settings.language)` whenever the user changes it. `feature/settings` never calls `AppCompatDelegate` directly — that would violate the feature → app dependency rule.

## Adding a new feature module

Follow `docs/FEATURE_TEMPLATE.md` and `docs/MODULE_DEPENDENCIES.md` exactly:
1. New `feature/<name>` module, namespace `org.cpimtn.marxist.android.feature.<name>`, depending on `:domain` and `:use-cases` only (plus `:core`/`:ui:theme` as needed) — never `:data` or `:network`.
2. `<Name>Screen.kt` (Compose, no business logic), `<Name>ViewModel.kt` (single `StateFlow<*UiState>`, injects use cases only), `<Name>UiState.kt` (sealed: `Loading`/`Success`/`Empty`/`Error`).
3. Register the route in `app/.../MainScreensNavHost.kt` (or `App.kt` for a top-level route) — `navigation` module itself stays unmodified.
4. Add the module to `settings.gradle.kts` and as an `implementation(project(":feature:<name>"))` in `app/build.gradle.kts`.

`docs/ARCHITECTURE_ROADMAP.md` tracks the longer-term architecture plan (phased SOLID/Clean Architecture migration) and what's already landed vs. still open — check it before assuming a described gap is still real, since it's a living plan and some phases are already implemented (noted inline under each phase's "Outcome").
