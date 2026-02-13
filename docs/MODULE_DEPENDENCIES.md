# Module Dependencies

This document defines how modules may depend on each other. Respecting these rules keeps the project aligned with Clean Architecture and makes it scalable.

## Package root

- **App code:** `org.cpimtn.marxist`
- **Android app / features / data / domain:** `org.cpimtn.marxist.android.*`
- **Libraries (no Android in API):** `org.cpimtn.marxist.<module>` (e.g. `org.cpimtn.marxist.navigation`, `org.cpimtn.marxist.network`, `org.cpimtn.marxist.core`)
- **UI theme:** `org.cpimtn.marxist.ui`

## Dependency rules

### Allowed dependency direction

- **Domain** must not depend on any other app module (only Kotlin stdlib / coroutines).
- **Data** may depend on **domain** and **network** only.
- **Feature** modules may depend on **domain**, and optionally **core**, **ui-theme**. They must not depend on **data** or **network**.
- **Navigation** may depend on **core**, **ui-theme**, and **feature** modules (for composing screens). It must not depend on **data** or **domain**.
- **App** may depend on **navigation**, **data**, **ui-theme**, and **feature** modules. It wires DI (e.g. Hilt) and composes the root UI.

### Diagram

```
                    ┌─────────────┐
                    │     app     │
                    └──────┬──────┘
           ┌───────────────┼───────────────┐
           ▼               ▼               ▼
    ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
    │ navigation  │ │    data     │ │ feature:*   │
    │             │ │             │ │             │
    └──────┬──────┘ └──────┬──────┘ └──────┬──────┘
           │               │              │
           │               ▼              ▼
           │         ┌─────────────┐       │
           └────────►│   domain    │◄──────┘
                    └──────┬──────┘
                           │
                    ┌──────┴──────┐
                    ▼             ▼
              ┌──────────┐  ┌──────────┐
              │  network │  │   core    │
              └──────────┘  └──────────┘
```

### Per-module summary

| Module      | May depend on                          | Must not depend on   |
|------------|-----------------------------------------|----------------------|
| **app**    | navigation, data, ui-theme, feature:*   | —                    |
| **domain** | (none; stdlib/coroutines only)          | app, data, network, navigation, feature, core, ui-theme |
| **data**   | domain, use-cases, network              | app, navigation, feature, ui-theme |
| **use-cases** | domain                               | app, data, network, navigation, feature, core, ui-theme |
| **network**| (none or minimal; e.g. OkHttp/Retrofit)  | domain, data, app, feature |
| **core**   | (none or stdlib)                        | app, data, domain, navigation, feature |
| **navigation** | core, ui-theme, feature:*            | app, data, domain, network |
| **feature:*** | domain, core, ui-theme               | app, data, network, navigation, other features |
| **ui-theme**  | (none or Compose/Theme only)         | app, data, domain, feature, navigation |

## Namespaces (build.gradle.kts)

Keep `namespace` aligned with the package root for each module:

- `app` → `org.cpimtn.marxist.android`
- `data` → `org.cpimtn.marxist.android.data`
- `domain` → (no Android; N/A for JVM library)
- `network` → `org.cpimtn.marxist.network`
- `core` → `org.cpimtn.marxist.core`
- `navigation` → `org.cpimtn.marxist.navigation`
- `ui-theme` → `org.cpimtn.marxist.ui`
- `feature:feed` → `org.cpimtn.marxist.android.feature.feed`
- `feature:books` → `org.cpimtn.marxist.android.feature.books`
- `feature:more` → `org.cpimtn.marxist.android.feature.more`
- `use-cases` → `org.cpimtn.marxist.android.domain.usecase`

## Adding a new feature module

1. Create `feature:<name>` with package `org.cpimtn.marxist.android.feature.<name>`.
2. Add dependency on `:domain` and `:use-cases` (and optionally `:core`, `:ui-theme`). Do not add `:data` or `:network`.
3. Add the feature to the app’s navigation (e.g. in `app` or wherever the NavHost is composed): new route and composable for the feature screen.
4. If using Hilt, ensure the app component includes the feature (e.g. by depending on the feature module).

## Adding a new data source or repository

1. Define the repository interface in **domain**.
2. Implement in **data** and bind in the data layer DI (e.g. Hilt module).
3. Keep DTOs and mappers in **data**; domain exposes only domain models and interfaces.
