# MarxistReader — Architecture & Scalability Roadmap

This roadmap aligns the project with **SOLID**, **MVVM**, and **Clean Architecture** so it stays maintainable and scalable as features grow.

---

## Current State (Summary)

| Area | Status |
|------|--------|
| **Modules** | Good: `app`, `core`, `data`, `domain`, `network`, `navigation`, `ui-theme`, `feature/*` |
| **Dependency flow** | Partial: `domain` is pure; `data` → `domain` + `network`; `feature/feed` → `domain` ✓ |
| **Offline-first** | Good: Room as source of truth, WorkManager sync, UI reads from DB |
| **Package names** | Mixed: `org.cpimtn.marxist.android` vs `com.jskaleel.android` (navigation, network, core) |
| **Use cases** | Missing: ViewModels call repositories directly |
| **Error handling** | Incomplete: `FeedUiState.Error` exists but is never emitted; sync failures not surfaced |
| **Navigation** | Navigation module depends on all feature modules (tight coupling) |
| **Testing** | Placeholder only (ExampleUnitTest); no ViewModel/Repository/UseCase tests |

---

## Roadmap Overview

1. **Phase 1 — Foundation & consistency**  
   Unify packages, naming, and build config so the codebase is consistent and easy to scale.

2. **Phase 2 — Clean Architecture (domain & use cases)**  
   Introduce use cases, keep domain independent, and enforce dependency direction.

3. **Phase 3 — SOLID & data layer**  
   Apply SRP/DIP in data (mappers, converters, repository responsibilities) and error handling.

4. **Phase 4 — MVVM & UI state**  
   One-way data flow, full UI state (loading/success/error/empty), and optional MVI for complex screens.

5. **Phase 5 — Navigation & module boundaries**  
   Decouple navigation from feature modules so adding features doesn’t force editing a central nav module.

6. **Phase 6 — Offline-first & sync**  
   Sync status, retry, and error reporting without breaking offline-first.

7. **Phase 7 — Scalability & quality**  
   Testing strategy, feature templates, and config/feature flags.

---

## Phase 1 — Foundation & Consistency

**Goal:** One package root, consistent naming, clean build layout.

| # | Task | Rationale |
|---|------|------------|
| 1.1 | **Unify package names** to a single root (e.g. `org.cpimtn.marxist.android`) across `navigation`, `network`, and `core`. Update namespaces in `build.gradle.kts` and move files. | Single root simplifies navigation, refactors, and onboarding (Single Responsibility of “one app identity”). |
| 1.2 | **Add `.gitignore`** entry for `**/bin/` (and any other build/IDE output) so `domain/bin/` is not committed. | Keeps repo clean and avoids accidental duplication. |
| 1.3 | **Standardize module namespaces** in each `build.gradle.kts`: `namespace = "<root>.navigation"`, `<root>.network`, `<root>.core`, etc. | Aligns with package structure and avoids conflicts. |
| 1.4 | **Document module dependency rules** in `docs/MODULE_DEPENDENCIES.md`: e.g. `app` → navigation, data, features; `feature/*` → domain (and optionally core/ui-theme); `data` → domain, network; `domain` → nothing. | Makes Clean Architecture and scalability explicit for the team. |

**Outcome:** Consistent packages and namespaces; clear, documented module boundaries.

---

## Phase 2 — Clean Architecture (Domain & Use Cases)

**Goal:** Domain is the center; all orchestration goes through use cases (Dependency Inversion).

| # | Task | Rationale |
|---|------|------------|
| 2.1 | **Introduce use cases in `domain`** (pure Kotlin, no Android): e.g. `GetPostsFlowUseCase`, `SyncPostsUseCase`. Each use case has a single responsibility and depends only on repository interfaces. | **Single Responsibility**: one action per use case. **Dependency Inversion**: ViewModels/Workers depend on abstractions (use cases), not on repositories directly. |
| 2.2 | **Keep repository interfaces in `domain`** (e.g. `PostRepository`). Implementations stay in `data`. Use cases depend on `PostRepository`; data layer provides the implementation. | Clear boundary: domain defines contracts; data implements them. |
| 2.3 | **Inject use cases into ViewModels and SyncWorker.** Replace direct `PostRepository` usage in `FeedViewModel` with `GetPostsFlowUseCase`; in `SyncWorker` use `SyncPostsUseCase`. | ViewModels/workers stay agnostic of “where” data comes from; easier to test and change. |
| 2.4 | **No use case for trivial one-liners** (e.g. “get post by id” that only calls repo once). Prefer use cases for flows that may later add logging, analytics, or multi-repo coordination. | Avoid over-engineering while keeping room to grow. |

**Outcome:** Domain-driven flow: UI/Worker → Use Case → Repository; domain has no framework dependencies.

---

## Phase 3 — SOLID & Data Layer

**Goal:** SRP and DIP in data: mappers, converters, and repository error handling.

| # | Task | Rationale |
|---|------|------------|
| 3.1 | **Extract DTO ↔ Entity and Entity ↔ Domain mappers** from `PostEntity.kt` into dedicated classes (e.g. in `data/database/mapper/` or `data/mapper/`). Entity file keeps only Room `@Entity` and `@TypeConverters`. | **Single Responsibility**: entity = persistence shape; mappers = mapping logic. |
| 3.2 | **Move Room `TypeConverters`** (e.g. list ↔ string) to a dedicated class in `data/database/converters/` and reference it in `AppDatabase` and entity. | **SRP**: persistence format conversion separated from entity definition. |
| 3.3 | **Define a small domain-friendly result type** for sync (e.g. `SyncResult` sealed class: `Success`, `NetworkError`, `ServerError`). Have `PostRepository.fullSync()` return it instead of `Unit`, and/or expose a `Flow<SyncStatus>`. | Enables UI to show sync state and errors (Phase 4/6). **Interface Segregation**: callers that only need “did it work?” get a simple result. |
| 3.4 | **Avoid clearing DB before sync completes.** Consider: fetch into a temporary table or in-memory list, then replace DB content in a transaction; or implement “merge” instead of full replace so a failed sync doesn’t wipe data. | Offline-first: local DB stays valid even when sync fails. |
| 3.5 | **Optional: wrap API calls in a data-source abstraction** (e.g. `PostRemoteDataSource`) and have the repository depend on it. Repository then orchestrates local + remote; easier to test and swap implementations. | **DIP**: repository depends on abstractions; network is one implementation. |

**Outcome:** Data layer with clear responsibilities, safe sync behavior, and a path to expose sync/error state.

---

## Phase 4 — MVVM & UI State

**Goal:** Predictable one-way data flow and complete UI state (loading / success / error / empty).

| # | Task | Rationale |
|---|------|------------|
| 4.1 | **Complete `FeedUiState` usage:** Ensure ViewModel can emit `FeedUiState.Error` when the use case or repository signals failure (e.g. map repository/use case errors to `FeedUiState.Error`). | UI already handles Error; ViewModel must provide it. |
| 4.2 | **Keep UI state in a single sealed type per screen** (e.g. `FeedUiState`: Loading, Success, Empty, Error). ViewModel exposes one `StateFlow<FeedUiState>`; no separate loading/error flags. | **Single source of truth** for the screen; easier to reason about and test. |
| 4.3 | **One-way data flow:** User events → ViewModel methods → use case → repository; result → state update → UI. Avoid ViewModels holding mutable state that is updated from multiple unsynchronized paths. | Clear data flow and fewer bugs. |
| 4.4 | **Optional: MVI for complex screens** (e.g. Search with filters, pagination, sort). Introduce `Intent`/`Action` and a single reducer for that screen. Keep simple screens as MVVM with `StateFlow`. | Scale only where complexity justifies it. |
| 4.5 | **Use `core` `ResultState` (or a shared `Result` type) in use cases** and map to UI state in the ViewModel. Reuse error types across features. | Consistency and less duplication. |

**Outcome:** Every screen has a complete, single state; errors and loading are first-class; flow is one-way.

---

## Phase 5 — Navigation & Module Boundaries

**Goal:** Adding a new feature doesn’t require editing the navigation module’s code.

| # | Task | Rationale |
|---|------|------------|
| 5.1 | **Move NavHost composition to `app`.** App module already depends on navigation and features. Have `app` compose the main `NavHost` and register each feature route with a lambda that invokes the feature’s screen composable. Navigation module only provides: routes (e.g. sealed `Screen`), `MainAppState`, and scaffold (e.g. `MainScreen` with slot for content). | **Open/Closed:** New feature = new module + registration in app; navigation module stays closed for edit. |
| 5.2 | **Navigation module exposes only:** route constants, `MainAppState`, `TopLevelDestination`, and a content slot (e.g. `MainScreen(content: (NavController) -> @Composable () -> Unit)`). No direct dependency on `feature:feed`, `feature:books`, etc. | **Dependency rule:** navigation does not depend on feature modules. |
| 5.3 | **App module** holds the single place that maps `Screen` → composable (e.g. `when (screen) { Screen.Feed -> { FeedScreen() } ... }`). New feature = new `Screen` + new `when` branch and new dependency in `app`. | Scalable: one place to wire features; navigation stays stable. |

**Outcome:** New features are added by adding a module and one registration in app; navigation is feature-agnostic.

---

## Phase 6 — Offline-First & Sync

**Goal:** Sync status and errors visible to the user; retry and backoff without breaking offline-first.

| # | Task | Rationale |
|---|------|------------|
| 6.1 | **Expose sync status** (e.g. `Flow<SyncStatus>` from a use case or repository) and show “Syncing…”, “Last synced at …”, or “Sync failed” in UI (e.g. Feed or Settings). | Aligns with offline-first rule: “retry when back online” is clearer when the user sees status. |
| 6.2 | **Persist last sync time** (e.g. DataStore or Room) and optionally last error message/code for “Sync failed” UI. | Survives process death; consistent with local-first. |
| 6.3 | **Manual “Refresh”** that enqueues sync work and updates sync status. Keep automatic sync on app start as today. | User can force retry when they think they’re online. |
| 6.4 | **In SyncWorker**, use the sync result (from Phase 3) and report success/failure (e.g. via WorkManager’s `Result` or a shared state that the app observes). | Enables 6.1 and 6.2. |

**Outcome:** Users see when sync is running or failed and can retry; app stays offline-first with Room as source of truth.

---

## Phase 7 — Scalability & Quality

**Goal:** Safe refactors, regression safety, and a repeatable pattern for new features.

| # | Task | Rationale |
|---|------|------------|
| 7.1 | **Unit tests for use cases** (domain): mock repository, assert calls and outputs. No Android. | Fast, stable tests for business rules. |
| 7.2 | **Unit tests for ViewModels:** mock use cases, assert state transitions (Loading → Success/Error/Empty). | Validates MVVM behavior and state. |
| 7.3 | **Repository tests:** in-memory or test DB, optional mock API; test getPosts flow and sync behavior (e.g. don’t clear DB on API failure). | Protects data layer and offline-first behavior. |
| 7.4 | **Document a “feature template”:** e.g. `feature/<name>` with `*Screen`, `*ViewModel`, `*UiState`, and optionally one use case per user action. Same package layout and dependency rules (feature → domain, no feature → data). | **Open/Closed:** new features follow the same pattern without changing core architecture. |
| 7.5 | **Centralize config:** e.g. base URL, timeouts, page sizes in `core` or a small `config` module (or BuildConfig) so changing environment doesn’t scatter edits. | Easier to add environments (staging, prod) and feature flags later. |

**Outcome:** Test coverage on domain and presentation; clear template for new features; one place for config.

**Implemented:** Use-case tests (`GetPostsFlowUseCaseTest`, `SyncPostsUseCaseTest`) in `use-cases`; `FeedViewModelTest` in `feature:feed` with `MainCoroutineRule`; `PostRepositoryImplTest` (safe sync: no `replaceAll` when fetch returns null or empty); `docs/FEATURE_TEMPLATE.md`; centralized config in `core` (`AppConfig.Network`, `AppConfig.Sync`), used by `network` and `data` (SyncWorker, UseCaseModule). Mapper fix: `PostDTO.toEntity()` uses `tags ?: emptyList()` for nullable lists.

---

## Dependency Diagram (Target)

```
                    ┌─────────────┐
                    │     app     │
                    └──────┬──────┘
           ┌───────────────┼───────────────┐
           ▼               ▼               ▼
    ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
    │ navigation  │ │    data     │ │ feature:*   │
    │ (routes,    │ │ (repo impl, │ │ (UI, VM)    │
    │  scaffold)  │ │  DB, sync)  │ └──────┬──────┘
    └─────────────┘ └──────┬──────┘        │
           │                │              │
           │                ▼              ▼
           │         ┌─────────────┐ ┌─────────────┐
           └────────►│   domain    │◄┘   domain    │
                    │ (models,    │   (use cases, │
                    │  repo iface) │    repo iface)│
                    └──────┬──────┘ └─────────────┘
                           │
                    ┌──────┴──────┐
                    ▼             ▼
              ┌──────────┐  ┌──────────┐
              │  network │  │   core   │
              └──────────┘  └──────────┘
```

- **domain**: no dependency on other app modules (only stdlib/coroutines).
- **data** and **feature** depend on **domain**; they never depend on each other.
- **app** wires navigation and features and depends on **data** for DI (e.g. Hilt components).

---

## SOLID Mapping

| Principle | Application in this project |
|-----------|-----------------------------|
| **S**ingle Responsibility | Use cases (one action); mappers/converters separate from entities; ViewModel only maps use-case output to UI state. |
| **O**pen/Closed | New features = new modules + app registration; navigation and domain stay closed for modification. |
| **L**iskov Substitution | Repository implementations and data sources are substitutable via interfaces (domain contracts). |
| **I**nterface Segregation | Small repository interfaces; optional separate “sync status” or “sync trigger” interfaces if needed. |
| **D**ependency Inversion | ViewModels and Workers depend on use cases/repository interfaces (domain); data and network are concrete implementations. |

---

## Suggested Order of Implementation

1. **Phase 1** (foundation) first — packages and module docs.  
2. **Phase 3.3, 3.4** (sync result + safe sync) and **Phase 4.1** (emit Error) — quick wins for correctness and UX.  
3. **Phase 2** (use cases) — then refactor ViewModels and Worker to use them.  
4. **Phase 3.1, 3.2** (mappers, converters) — data-layer cleanup.  
5. **Phase 5** (navigation in app) — then add new features without touching navigation module.  
6. **Phase 6** (sync status UI and manual refresh).  
7. **Phase 7** (tests, feature template, config).

You can do Phase 1 and the Phase 3/4 quick wins in parallel if you split the work (e.g. one person on packages, one on sync/error state).

---

## References

- **Clean Architecture:** Domain at center; dependencies point inward; use cases orchestrate.
- **MVVM:** ViewModel holds state and logic; View (Compose) renders state and sends events to ViewModel.
- **Offline-first (project rule):** Local storage is source of truth; network is for sync; design for no connectivity.

If you want, next step can be a concrete task list (file-level changes) for Phase 1 and the sync/error quick wins.
