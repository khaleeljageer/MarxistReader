# Feature module template

Use this as the standard layout and rules when adding a new feature (e.g. `feature/<name>`).

## Structure

```
feature/<name>/
  src/main/java/.../feature/<name>/
    <Name>Screen.kt      # Composable UI
    <Name>ViewModel.kt   # State holder, uses use cases only
    <Name>UiState.kt     # Sealed interface or data class for UI state (can live next to ViewModel)
```

- **Screen**: Composable that observes ViewModel state and sends events (e.g. `refresh()`) to the ViewModel. No business logic; no direct repository or data access.
- **ViewModel**: Single responsibility: map use case outputs to a single `StateFlow<*UiState>` and expose one-off events if needed. Depends only on **use cases** (and optionally navigation if you pass a lambda).
- **UiState**: Sealed interface preferred (e.g. `Loading`, `Success(data)`, `Empty`, `Error(message)`). Keeps UI logic simple (when (state) { ... }).

## Dependencies

- **feature → domain**: Allowed (read domain models only).
- **feature → use-cases**: Required for ViewModels (inject use cases).
- **feature → data**: **Not allowed.** Features must not depend on the data layer; all data access goes through use cases and domain.
- **feature → core / ui-theme / navigation**: Allowed as needed for UI and navigation.

## Adding a new feature

1. Create module `feature/<name>` (Android library), same namespace pattern: `org.cpimtn.marxist.android.feature.<name>`.
2. Add `implementation(project(":domain"))`, `implementation(project(":use-cases"))`, and Compose/Hilt/ViewModel dependencies.
3. Implement **use case(s)** in `use-cases` and **domain** types in `domain` if new flows are needed.
4. Implement `<Name>ViewModel` with a single `StateFlow<*UiState>` and inject only use cases.
5. Implement `<Name>Screen` that uses `viewModel.fooState` and calls `viewModel.onEvent()` / `viewModel.refresh()` etc.
6. Register the screen in the app’s NavHost (e.g. in `app` module’s `MainScreensNavHost` or equivalent).
7. Add unit tests for the ViewModel (mock use cases, assert state transitions) and for new use cases in `use-cases`.

## Naming

- **Screen**: `*Screen.kt` — e.g. `FeedScreen`, `BookDetailScreen`.
- **ViewModel**: `*ViewModel.kt` — e.g. `FeedViewModel`.
- **UiState**: `*UiState` — e.g. `FeedUiState` (sealed interface with `Loading`, `Success`, `Empty`, `Error`).

This keeps features consistent and scalable and enforces Clean Architecture boundaries (UI → use cases → domain ← data).
