# MarxistReader — Code Review & Test Coverage Report

Generated as a full-codebase pass: code review (correctness, race conditions, resource
leaks, security) + test coverage / uncovered-feature analysis. Findings are ranked
Critical / High / Medium / Low per module. File:line references are given wherever
possible.

**Status:** work in progress — sections are being filled in as review passes complete.

---

## 1. `feature/search` (reviewed in depth this session — for context, not new findings)

This module received a full review-and-fix pass earlier in this session. Summary of
what was found and fixed, since it's directly relevant to reviewing the rest of the app
for the same bug classes:

- **Fixed:** `SearchDiscovery`'s `isFocused` was `mutableStateOf(false)` without
  `remember{}`, silently resetting on every recomposition (Compose correctness bug —
  worth checking for the same pattern elsewhere; the other review agents were asked to
  hunt for it in their modules).
- **Fixed:** FTS4 `MATCH` queries were built by simple string concatenation with no
  escaping and no real prefix-wildcard support (docs claimed prefix matching that the
  code didn't implement) — replaced with a tokenizing sanitizer that both escapes FTS
  operator syntax and enables genuine prefix matching.
- **Fixed:** recent-search storage used `"|"` as a delimiter with no escaping — a query
  containing `|` corrupted the stored list. Switched to a control character plus
  defensive stripping.
- **Fixed:** category/timeline browsing was entirely unwired (TODO stubs / no-op
  toggles); bookmark save/unsave was computed but never rendered in search results.
- **Fixed:** `GetCategoriesWithCountFlowUseCase`/`GetTimelineMonthsFlowUseCase` each
  independently re-subscribed to the full posts table — now share one `shareIn`'d flow.
- **Fixed:** a `combine()` staleness race caused a one-frame "No results found" flash
  before real results arrived on chip tap; later a proper `Loading` phase + skeleton
  state was added for the gap between tapping and results arriving.
- **Added:** `SearchViewModelTest.kt`, 11 passing tests.

**Still outstanding / known gaps (not yet actioned):**
- `data/src/test/.../PostRepositoryImplTest.kt` and
  `use-cases/src/test/.../GetPostsFlowUseCaseTest.kt` fail to compile on a clean
  checkout (stale constructor calls — `PostDTO` needs a `RenderedText`-typed param,
  `Post`/related test needs a `categories` param). This blocks `./gradlew test` for the
  **entire `data` and `use-cases` modules**, not just search-related tests. This is
  reconfirmed as a cross-cutting issue by the `data` and `use-cases` review passes below
  — see §3 and §4.
- Discovery's live-typing suggestion list still uses a bespoke `SearchResultRow`
  instead of the shared `ArticleListItem` (deliberately left, due to a padding/layout
  mismatch in the shared mixed-content `LazyColumn` — not a bug, just residual
  duplication).
- No test coverage for `SearchRepositoryImpl` (FTS query construction) or
  `RecentSearchRepositoryImpl` (DataStore-backed, needs Robolectric which isn't
  configured in this repo) — both are meaningfully complex and untested at the
  repository layer.

---

*(Sections 2–7 below are populated from parallel review passes over the rest of the
codebase — app shell/navigation/core/domain, data layer, use-cases/network, feed/saved/
books/feeddetails + ui:common, settings/welcome/theme, and the reader module.)*
