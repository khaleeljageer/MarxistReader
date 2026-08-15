git # MarxistReader — Code Review & Test Coverage Report

Generated as a full-codebase pass: code review (correctness, race conditions, resource
leaks, security) + test coverage / uncovered-feature analysis. Findings are ranked
Critical / High / Medium / Low per module. File:line references are given wherever
possible.

**Status:** complete — all seven sections (search, use-cases/network, reader, settings/
welcome/theme, app/navigation/core/domain, data, feed/saved/books/feeddetails) below,
plus an executive summary at the end.

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

## 2. `use-cases` + `network`

### Critical

**2.1 — Test-compile blocker: whole `use-cases` module test suite unbuildable**
`use-cases/src/test/java/org/cpimtn/marxist/android/domain/usecase/GetPostsFlowUseCaseTest.kt:25`
`Post(1, "2024-01-01", "slug", "Title", "Excerpt", emptyList(), emptyList())` supplies 7
positional args; `Post` (`domain/.../model/Post.kt:6-14`) now requires 8 (`id, date, slug,
title, content, excerpt, tags, categories`) — `categories` is missing. This fails
compilation for the entire `use-cases` test source set, so `SyncPostsUseCaseTest.kt` and
any new tests in this module can't run via `./gradlew :use-cases:test` until fixed. (Same
family of issue as `data`'s `PostRepositoryImplTest.kt`, noted in §1.)

### High

**2.2 — `BookDTO` fields are non-nullable with no defaults and `coerceInputValues` is not
set — one malformed book entry throws away the whole catalog.**
`network/.../model/BookDTO.kt:10-17` (`title`, `date`, `bookid`, `image`, `epub` all
required, no defaults). `network/di/NetworkModule.kt:36-40` sets `ignoreUnknownKeys =
true; isLenient = true; encodeDefaults = true` but not `coerceInputValues = true`. Unlike
`PostDTO` (which defensively defaults `tags`/`categories` to `null`), `BookDTO` assumes
every field is always present. The books catalog
(`AppConfig.Books.CATALOG_URL`) is a hand-maintained GitHub-hosted JSON file, not a
schema-validated API — one entry with a missing `image`/`epub` key throws a
`SerializationException` on `getBooks()`, failing `SyncBooksUseCase` for **every** book in
the response, not just the bad one.

**2.3 — `HttpLoggingInterceptor` runs at `Level.BODY` unconditionally, including release
builds.** `network/di/NetworkModule.kt:46-48` has no `BuildConfig.DEBUG` gate, unlike the
established pattern elsewhere in the repo (`reader/.../EpubApplication.kt:43`:
`if (BuildConfig.DEBUG) { ... }`). Full request/response bodies for every API call go to
Logcat in production. Content is public so this isn't a secrets leak, but it's
unnecessary I/O and inconsistent with the rest of the codebase's own convention.

### Medium

**2.4 — `toFeedItem` mapping logic is copy-pasted three times with subtly diverging
behavior.** `GetFeedItemsFlowUseCase.kt:27-36`, `GetSavedPostsFlowUseCase.kt:31-40`
(byte-for-byte identical), `GetFeedItemByIdFlowUseCase.kt:27-46` (diverges: also calls
`content.downgradeHeadings()` and passes **all** tags instead of capping at 3). A future
fix to category/tag-label resolution has to be applied in three places — easy to miss one,
as already happened with the tag-cap/heading-downgrade divergence. Extract a shared
mapper.

**2.5 — Saved posts silently vanish from "Saved" if the source post is removed from the
server, with no cleanup of the orphaned save row.** `GetSavedPostsFlowUseCase.kt:19-29`
filters `posts.filter { it.id in savedIds }`. Since `PostRepositoryImpl.fullSync()`
replaces the whole posts table, if WordPress unpublishes a bookmarked post, the next sync
drops it from `posts` — this use case just filters it out with no user-facing indication
and no cleanup of the now-orphaned id in `SavedPostRepository`'s storage; the id lingers
indefinitely.

**2.6 — `DownloadResult.Progress` is defined but never emitted — download progress is a
stub, not a working feature.** `network/utils/DownloadResult.kt:7` declares
`Progress(val id: String, val percent: Int)`, but `FileDownloaderImpl.downloadFile`
(`network/downloader/FileDownloader.kt:38-86`) never emits it (only `Queued` →
`Success`/`Error`). The only consumer explicitly no-ops on it:
`data/.../repository/BookRepositoryImpl.kt:84` —
`is DownloadResult.Queued, is DownloadResult.Progress -> Unit`. `BookDownloadState`
(domain) has no `Downloading(percent)` variant either, so the UI can only ever show a
generic spinner regardless of book size. Fully wired but functionally dead across three
modules — either implement it (track bytes read vs. `Content-Length`) or remove the type.

### Low

**2.7 — Broad `catch (e: Exception)` masks the specific I/O failure in the downloader,
plus an unused import.** `network/downloader/FileDownloader.kt:82-85` catches generic
`Exception` rather than `IOException`; `import java.io.IOException` at line 14 is unused
— likely left over from a narrower catch that was since widened.

**2.8 — `Post.formattedDate` uses `!!` on a `SimpleDateFormat.parse()` result, no explicit
`TimeZone`.** `domain/.../model/Post.kt:19-21`. Guarded by an outer try/catch today so not
a live crash risk, but a fragile pattern, and using device-default timezone can shift the
displayed day near midnight depending on device settings.

**2.9 — Stale copy-pasted KDoc** on `GetPostByIdFlowUseCase.kt:7-9` (still describes the
list-returning `GetPostsFlowUseCase`, not the by-id variant). Cosmetic.

**2.10 — `network` module ships `isMinifyEnabled = false` in `release`**
(`network/build.gradle.kts:23-28`). Low impact (GPL-3.0 open source, non-sensitive API
surface) but worth confirming it's intentional.

### Notable non-findings / scope notes
- No `!!` or unsafe casts found directly in `use-cases`/`network` source beyond §2.8.
- No exceptions are silently swallowed without being surfaced to a caller.
- The many single-line delegate use cases (`GetCategoriesFlowUseCase`,
  `SavePostUseCase`, `SetThemeUseCase`, etc.) are **not** flagged as smells — required by
  this repo's Clean Architecture boundary (feature modules can't see `data` directly), and
  intentionally kept per `docs/ARCHITECTURE_ROADMAP.md` Phase 2.4.
- DTO↔domain mapping for `PostDTO`/`CategoryDTO`/`TagDTO` happens in
  `data/.../repository/PostRepositoryImpl.kt` and
  `data/.../source/local/database/mapper/TaxonomyMappers.kt`, outside this section's
  scope — flagging so it isn't assumed reviewed; worth a follow-up given `PostDTO.tags`/
  `categories` are nullable but `Post.tags`/`categories` are non-null lists.

### Most valuable spots to add test coverage
1. Fix `GetPostsFlowUseCaseTest.kt`'s constructor call (unblocks the whole module — §2.1).
2. `network` DTO serialization — currently zero tests. Highest value: `PostDTO` with
   `tags`/`categories` omitted (should succeed); `BookDTO` with a missing field (locks in
   or verifies a fix for §2.2); unknown extra JSON keys (`ignoreUnknownKeys` behavior).
3. `FileDownloaderImpl` — zero tests for the only nontrivial logic in `network/`:
   non-HTTPS URL rejection, non-2xx response, successful write, cancellation mid-stream
   deleting the partial file.
4. `GetFeedItemsFlowUseCase`/`GetSavedPostsFlowUseCase`/`GetFeedItemByIdFlowUseCase` — the
   `combine` + label-resolution logic has zero coverage.
5. `SyncTaxonomyUseCase` — no test for the partial-failure path (categories succeeds, tags
   throws → whole result should fail, sibling job cancellation).
6. `SyncPostsUseCase` — existing test only covers `Success`/`NetworkError`; `ServerError`/
   `UnknownError` branches and null-message fallback strings are untested.

---

## 3. `reader/` module (EPUB reader, package `com.jskaleel.epub`)

This module is architecturally distinct (own Room DB, own DI, Readium 3.1.2 toolkit,
Fragment/ViewBinding-based) and — notably — **has zero real test coverage**: both
`src/test/.../ExampleUnitTest.kt` and `src/androidTest/.../ExampleInstrumentedTest.kt`
are unmodified Android Studio templates.

### Critical

**3.1 — Per-book-open `CoroutineScope(Dispatchers.IO)` is created and never cancelled —
permanent leak on every open/close cycle.**
`reader/.../preferences/PreferencesManagers.kt:75` (`createPreferenceManager`) creates a
fresh `CoroutineScope(Dispatchers.IO)` per `EpubPreferencesManagerFactory`/
`AndroidTtsPreferencesManagerFactory` instance, one per book open
(`EBookReaderRepository.openEpub()`/`getTtsInitData()`, lines 200/225). The scope runs an
`Eagerly`-shared collector over `dataStore.data` (lines 129-130) that never stops.
`PreferencesManager` stores the scope "to keep it alive" (comment at line 45) but exposes
no `close()`/`cancel()`, and `EBookReaderRepository.close(bookId)` (lines 234-248) never
touches it. Failure scenario: open a book, back out, reopen the same book repeatedly
(normal reading-session usage) — each cycle leaks two IO-dispatcher coroutines collecting
DataStore forever, for the life of the process.

**3.2 — Missing/corrupted book row crashes the app instead of surfacing an error.**
`EBookReaderRepository.kt:140`: `checkNotNull(bookRepository.get(bookId)) { "Cannot find
book in database." }` throws `IllegalStateException` directly, unlike every other failure
path in this function (which returns `Try.failure(...)`). That propagates through
`CoroutineQueue.await` (rethrows, `CoroutineQueue.kt:72`) into
`app/.../OpenBookViewModel.kt:38-42`'s `viewModelScope.launch { ... }`, which has **no
try/catch**. Failure scenario: reader DB row for a cached `readerId` is gone (partial data
wipe, migration issue) — tapping the book crashes the app instead of showing "book not
found."

### High

**3.3 — Unsafe `!!` crashes when adding a bookmark.**
`reader/.../data/BookRepository.kt:35`:
`publication.readingOrder.indexOfFirstWithHref(locator.href)!!`, hit on every bookmark tap
(`BaseReaderFragment.kt:88`). If the current locator's href isn't in `readingOrder`
(non-linear resource, footnote-derived locator, href-normalization mismatch), this NPEs
inside an uncaught `viewModelScope.launch`.

**3.4 — `DrmManagementFragment` touches view binding from a non-view-scoped coroutine.**
`drm/DrmManagementFragment.kt:101,117` use `lifecycleScope` (survives view destruction
while the fragment sits on the back stack) then write to `binding.drmValueEnd.text`
(line 104); `binding` throws `IllegalStateException` once the view is destroyed
(`ViewLifecycleDelegates.kt:31-34`). Failure scenario: tap "Renew loan"/"Return
publication" (network call), press back before it completes → crash. Every other fragment
in this module correctly uses `viewLifecycleOwner.lifecycleScope`; this one doesn't.

**3.5 — Silent failure when a book fails to open — no user feedback at all.**
`app/.../OpenBookViewModel.kt:38-44` only handles `IResult.Success`; there is no `else`.
If `openBook()` returns `IResult.Failure` (corrupt EPUB, DRM-locked publication, disk I/O
error), nothing happens — no toast, no error state, no log. This is the exact "corrupt
EPUB / missing book file" edge case, and it's entirely unhandled on the non-crashing side
(the `!!`/`checkNotNull` paths above crash instead; this path just does nothing).

**3.6 — `ReaderActivity.readerFragment` lateinit left unassigned in the dummy-reader
path.** `reader/ReaderActivity.kt:57-66`: when init data is `DummyReaderInitData`
(repository empty after process death), `createReaderFragment()` returns `null` and the
`lateinit var readerFragment` is never assigned; `EpubReaderFragment.onCreate` calls
`finish()` in that case today, mitigating it, but any future code path reaching
`showOutlineFragment()`/`showDrmManagementFragment()` first would throw
`UninitializedPropertyAccessException`. Fragile, compiler-unenforced coupling.

### Medium

**3.7 — Bookmarks are never cleaned up when a book is deleted, and `deleteBook` is
entirely unwired dead code.** `EBookHighlight` cascades on book delete
(`ForeignKey(..., onDelete = CASCADE)`, `EBookHighlight.kt:36-42`) but `EBookBookmark`
has no `foreignKeys` at all. `BookRepository.deleteBook()`/`EBooksDao.deleteBook()`
(`BookRepository.kt:99-100`, `EBooksDao.kt:34-35`) have **no caller anywhere** in
`reader/` or `app/` — dead code. If ever wired up, bookmarks (and cover files, and the
imported EPUB itself) would orphan permanently.

**3.8 — `CoverStorage.storeCover` silently produces a valid-but-empty file when the EPUB
has no/corrupt cover.** `domain/CoverStorage.kt:27-36`: on `publication.cover() == null`,
the compress step is skipped but the empty file is still flushed, closed, and stored as
`EBook.cover` — reported as success. No fallback placeholder, no error surfaced.

**3.9 — `supportActionBar!!` used twice with no defensive handling**
(`ReaderActivity.kt:131`, `SystemUiManagement.kt:65`, the latter on every window-insets
pass). Currently safe (theme always has an action bar) but no compile/runtime guard.

**3.10 — No explicit audio-focus / becoming-noisy handling for TTS.** No
`AudioFocus`/`AudioManager`/`ACTION_AUDIO_BECOMING_NOISY` handling found anywhere in
`reader/src/main`. Not provably broken (Media3 sessions can auto-manage focus) but zero
explicit handling or coverage for "phone call interrupts TTS" — worth manual verification.

**3.11 — Repeated `!!` force-unwraps on DB-persisted strings deserialized back into
Readium types**, none defensively handled: `EBook.kt:60` (`AbsoluteUrl(href)!!`),
`EBook.kt:63` (`MediaType(rawMediaType)!!`), `EBookHighlight.kt:97` (`Url(href)!!`),
`EBookBookmark.kt:51` (`Url(resourceHref)!!`), `BookmarksFragment.kt:131`
(`Url(bookmark.resourceHref)!!` — crashes the *entire* bookmarks list render, not just one
row), `BookmarksFragment.kt:69` (`bookmark.id!!`). Any row with a malformed/legacy href
throws on read.

**3.12 — Unsafe cast in TTS error handling itself.** `tts/TtsViewModel.kt:254`:
`(error.cause as AndroidTtsEngine.Error)` — if `error.cause` is ever a different type,
the error *handler* throws a `ClassCastException`.

### Low
- `data/db/AppDatabase.kt:22-26`: `version = 1`, no migration strategy, no
  `fallbackToDestructiveMigration()` — landmine for the first schema bump.
- `VisualReaderFragment.kt:343`: unsafe `as Long` cast on decoration extras (safe today,
  no defensive `as?`).
- `VisualReaderFragment.kt:617`: `FIXME` — long-footnote popups known-broken/unhandled.
- `tts/TtsControls.kt:91`: hardcoded `Color(0xFF4A1200)` bypasses the dynamic `AppTheme`
  palette it's nested inside.
- `BookmarksFragment.kt:132`: hardcoded English `"*Title Missing*"` fallback string, not
  in `strings.xml` (rare path, but inconsistent with the app's bilingual convention).

### Structured summaries
- **`setContent {}` sites not wrapped in `AppTheme {}`:** none found — both sites
  (`VisualReaderFragment.kt:144-166`, `ComposeBottomSheetDialogFragment.kt:58-66`) are
  correctly wrapped.
- **Resource leaks:** only the `PreferencesManagers.kt` coroutine-scope leak (§3.1) is
  confirmed; `MediaService`/`MediaServiceFacade` and `EBookReaderRepository.close()`
  correctly release their sessions/navigators otherwise.
- **Swallowed exceptions around file I/O:** `FileSystem.kt`'s `InputStream.toFile`/
  `toFileUnsafe` correctly convert IOException/FileNotFoundException/SecurityException
  into `Try.failure` (good pattern). `CoverStorage.storeCover` (§3.8) is the one real gap.
- **Test coverage:** none — 100% untested module, in contrast to the rest of the app's
  (thin but real) coverage.
- **TODO/dead code:** one `FIXME` (§ Low), `deleteBook`/`EBooksDao.deleteBook` unwired
  dead code (§3.7).

---

## 4. `feature/settings` + `feature/welcome` + `ui/theme` (+ `feature/more` removal check)

### Critical

**4.1 — Welcome screen has a fully-built "no internet" error/retry state that is never
wired up — first-time users can get stuck indefinitely with just a spinner.**
`feature/welcome/.../values-ta/strings.xml:26-28` and `values/strings.xml:26-28` define
`welcome_internet_needed`, `welcome_internet_instruction`, `welcome_retry` — fully
translated in both locales. `WelcomeScreen.kt:21,24` imports `Icons.Filled.Refresh` and
`Icons.Outlined.WifiOff` — unreferenced anywhere else. A repo-wide grep for these three
string names returns zero `.kt` hits. `WelcomeViewModel.kt:27-33` (`canContinue`) only
checks `posts.size >= 50`; it never surfaces `SyncResult.NetworkError`/`ServerError`
(which `SyncWorker.kt:29-32` swallows into a bare `Result.retry()` anyway). Failure
scenario: first install with no connectivity, or a WordPress site with fewer than 50
total posts, leaves the user on "Fetching articles…" forever — no retry button, no
offline messaging, no way to reach Continue — despite the copy and icons for exactly this
state already existing in the codebase.

**4.2 — "Push Notifications" toggle in Settings is fully non-functional — persists a
flag that nothing reads.** `feature/settings/.../SettingsScreens.kt:109-126` renders a
working Switch → `data/.../UserSettingsRepositoryImpl.kt:67-69` persists it to DataStore
correctly — but it's **only** ever read back to drive the Switch's own checked state. No
FCM/Firebase Messaging integration exists anywhere (`grep -rln
"FirebaseMessaging|firebase-messaging"` = zero hits), no `NotificationChannel`/
`NotificationManager` in `app/`, no runtime permission request flow (`grep -rln
"RequestPermission|ActivityResultContracts" app/src` = zero hits) despite
`android.permission.POST_NOTIFICATIONS` being declared in `AndroidManifest.xml:6`.
100% UI theatre — flipping the toggle does nothing because there's no notification system
in the app at all.

### High

**4.3 — Tamil translations missing for all 16 of Settings' user-facing strings —
including the primary "Language"/"Theme" dialog labels.**
`feature/settings/src/main/res/values-ta/strings.xml` — every string (16/16) is still
`TODO_TA: <English>`. A Tamil-reading user sees an entirely English Settings screen, in
the app's own default/primary locale.

**4.4 — Welcome screen missing English translations for 10 strings** (greeting,
"fetching articles", the unused no-internet strings, "Continue") —
`feature/welcome/.../values/strings.xml:4-7,22-23,26-28,31`, still raw Tamil prefixed
`TODO_EN:`. An English-locale user sees Tamil text on first launch.

### Medium

**4.5 — Four dead string resources in `feature/settings`** (verified via repo-wide
`R.string.<name>` grep, zero references for all four): `settings_dark_mode`/
`settings_dark_mode_subtitle` (superseded by `settings_theme_subtitle`, which
`SettingsScreens.kt:84` actually uses); `settings_language_subtitle` (the row's subtitle
is computed dynamically via `languageLabel(uiState.language)` instead); and
`settings_version_subtitle` (hardcoded `"6.0.0"`, unused — the real version comes from
`uiState.appVersion`/`AppVersionProvider`). That hardcoded `"6.0.0"` will silently go
stale as the app ships new versions and risks misleading a future editor into "fixing"
the wrong string.

**4.6 — No Font Size control in Settings, despite domain/data layers fully supporting
it.** `FontSize` model, `SettingsRepository.setFontSize()`,
`UserSettingsRepositoryImpl.setFontSize()`, `SetFontSizeUseCase` all exist and are
wired — but `SettingsViewModel` never injects `SetFontSizeUseCase`, and there's no
font-size row in the UI. Font size is only controllable from inside the reader/article
screen, which may be intentional (per-reading-experience scope), but means global app
typography stays fixed regardless of the persisted setting — worth confirming with the
team whether this is a real gap or accepted scope.

### Low

**4.7 — Push-notification row only toggles via the Switch itself, not the row tap**
(`SettingsScreens.kt:109-126` — no `onClick` wrapper, unlike the Theme/Language rows).
Minor UX inconsistency.

**4.8 — `MarxistReaderTheme`'s `darkTheme: Boolean = isSystemInDarkTheme()` default is
effectively dead and slightly misleading.** Only call site (`MainActivity.kt:42`) always
passes an explicit value derived from settings, so the default can never diverge from the
user's persisted Theme choice today — but a future caller invoking `MarxistReaderTheme {
}` bare (e.g. a new screen or Compose preview) would silently ignore the user's Theme
setting.

### Notable non-findings / scope notes
- **No `mutableStateOf`-without-`remember{}` bug found** in any of these three modules —
  the only usage (`SettingsScreenRoute.kt:16-17`) is correctly `rememberSaveable {
  mutableStateOf(false) }`. This bug class (found and fixed in `feature/search`) does not
  repeat here.
- No `!!` in any of the three modules. One unguarded cast:
  `ui/theme/.../Theme.kt:454` — `(view.context as Activity)` inside
  `MarxistReaderTheme`'s `SideEffect`. Standard/idiomatic for
  `WindowCompat.getInsetsController` and low-risk today (only ever applied inside
  `MainActivity`), but would `ClassCastException` if `MarxistReaderTheme` (rather than
  `AppTheme{}`) were ever wrapped around Compose hosted in a Fragment/Dialog with a
  non-Activity context.
- Theme and Language settings persist and apply correctly end-to-end (DataStore →
  `GetSettingsFlowUseCase` → `LocaleController.apply()` in `MainActivity`'s
  `LaunchedEffect`). Push Notifications (§4.2) is the only setting on this screen that
  persists but has zero downstream effect.
- **`feature/more` removal is clean** — repo-wide grep for `feature.more`/`feature:more`/
  `MoreScreen`/`MoreViewModel`/`Route.More` found no stale references in source (only
  leftover `/build/` cache artifacts, already correctly gitignored).

### TODO_TA / TODO_EN counts
| Module | TODO_TA (Tamil missing) | TODO_EN (English missing) |
|---|---|---|
| feature/settings | 16 (values-ta — **all** strings) | 0 |
| feature/welcome | 0 | 10 (values) |
| ui/theme | 0 (no strings.xml) | 0 |

Matches the documented repo-wide reversed convention (not module-specific), but
`feature/settings` is unusually far behind (0% Tamil-translated) vs. `feature/welcome`
(100% Tamil-complete, only English gaps remaining).

### Most valuable spots to add test coverage
None of these three modules have a single test file, not even the default template.
Highest value: `WelcomeViewModel.canContinue`'s `>= 50` boundary logic (cheap, fake
`PostRepository` flow); `SettingsViewModel` (verify each `set*UseCase` invoked correctly,
reusing `feature:feed`'s `MainCoroutineRule` pattern); `LocaleController.apply()`
(untested — verify `AppLanguage.TAMIL`→`"ta"`/`ENGLISH`→`"en"` mapping); an
instrumented/Compose test asserting Welcome's Continue button tracks `canContinue` and
(once built) that the offline-retry state actually appears on sync failure.

---

## 5. `app/` + `navigation/` + `core/` + `domain/`

### High

**5.1 — Book-open failures are completely silent, no error surfaced to UI.**
`app/.../OpenBookViewModel.kt:39-55`: if `getBookFilePathUseCase(bookId)` returns null
(book not actually downloaded), or `importBook()`/`openBook()` return non-`Success`
`IResult`, the coroutine just returns — no exception, no emitted error, nothing written
to any state the UI observes. Traced the full chain (`feature/books` →
`MainScreensNavHost.kt:47-49` → `App.kt:84-86`) — there is no loading state, no error
state, nothing. Failure scenario: user taps a book whose downloaded epub was deleted
externally (low-storage cleanup) or whose import fails (corrupt/partial epub) — tapping
produces no spinner, no toast, no navigation, and no way to know why.

**5.2 — Race condition on rapid/duplicate taps can double-import the same book.**
`app/.../OpenBookViewModel.kt:39-47`: `getBookReaderIdUseCase(bookId) ?: importBook(bookId)`
is check-then-act with no mutex/de-dup guard; each call launches an independent
`viewModelScope` coroutine. Failure scenario: user double-taps a not-yet-imported book
before the first `saveBookReaderIdUseCase` write completes — both coroutines see `null`,
both import, book gets imported twice with two different `readerId`s, silently
overwriting the cached mapping and potentially orphaning reader-DB rows/progress records.

**5.3 — `catch (e: Error)` catches the wrong throwable hierarchy — mapping failures
crash instead of becoming `ResultState.Error`.** `core/.../model/ResultMapper.kt:15`:
`try { onSuccess(...) } catch (e: Error)` — `java.lang.Error` is the JVM's
unrecoverable-error hierarchy (`OutOfMemoryError`, etc.), it does **not** catch
`Exception` (NPE, IllegalStateException, IOException...). If a consumer's mapping lambda
throws any ordinary exception, it propagates uncaught instead of becoming
`ResultState.Error(...)` as the class's contract implies. Currently latent —
`ResultMapper`/`ResultState.map` have zero call sites in the repo — but a real bug a
future caller would silently inherit.

### Medium

**5.4 — Inconsistent timestamp units (millis vs. seconds) inside the same function
family.** `core/.../Utils.kt:20-65`: `getRelativeDateInTamil`/`getDaysDifference` treat
`timestamp` as milliseconds, but `formatAbsoluteDate(timestamp: Long)` (line 55) does
`Date(timestamp * 1000)`, treating the same-shaped parameter as seconds. For posts old
enough to hit the `else -> formatAbsoluteDate(timestamp)` branch, a millis-epoch value
gets multiplied by 1000 again, producing a date around year 55000+. Currently dead code
(no callers anywhere) but the bug fires immediately if ever wired up.

**5.5 — Blocking DataStore read on the main thread during cold start.**
`app/.../MarxistReaderApp.kt:31`: `runBlocking { getSettingsFlowUseCase().first().language }`
blocks `Application.onCreate()` on a disk read before the first UI frame. Documented as a
deliberate tradeoff in `CLAUDE.md`, so not a new finding, but worth re-flagging: on a
slow/cold disk (first launch, low-end device) this is a real ANR risk since
`Application.onCreate` has no watchdog slack.

**5.6 — Stale build-artifact duplicates tracked in git despite `.gitignore`.**
`domain/bin/main/.../Post.kt` and `.../PostRepository.kt` are tracked in git
(`.gitignore:16` has `**/bin/` but these predate the rule and were never removed).
Content is currently identical to the real sources, so no active bug, but exactly the
kind of stale copy that silently drifts and confuses IDE search. Should be `git rm
--cached`.

### Low

**5.7 — Top-level destination matching uses substring `.contains()` instead of exact
match.** `navigation/.../MainAppState.kt:119-122`: `isTopLevelDestinationInHierarchy`
does `it.route?.contains(destination.name, true)` rather than exact-matching like the
sibling `currentTopLevelDestination` (lines 38-45) does via `when`. Works today only
because no route contains another destination's name as a substring — fragile if a
future route like `"books/search"` were added.

**5.8 — `toImage()`/`toTypeString()` round-trip is lossy for `Vector` type, uses `Gson`
in a module documented "no-dependency."** `core/.../model/ImageType.kt:36,40-46`:
`toTypeString()` serializes `Vector` as `"Vector:${Gson().toJson(this)}"`, but
`toImage()` only special-cases the `"Drawable:"` prefix — a `"Vector:..."` string falls
into the `NetworkImage` branch, producing a broken `NetworkImage(url = "Vector:{...}")`.

**5.9 — Mutable `var isDisplayed` inside an otherwise-immutable `data class`, used as
one-shot event state.** `core/.../model/ErrorState.kt:9-16,29-34`: `consume()` mutates
`isDisplayed` in place rather than replacing the instance. If stored in a
`StateFlow`/Compose `State`, mutating a `var` field in place does not trigger
recomposition/re-collection — works against the whole point of `StateFlow` value
semantics.

**5.10 — Dead code**: `core/.../Utils.kt:91-100` (commented-out duplicate of
`Context.launchReaderActivity`), `core/.../Utils.kt:8-83` (`getRelativeDateInTamil` and
helpers — zero callers, carries the bug in §5.4), `core/.../model/ResultState.kt` +
`ResultMapper.kt` (entirely unused — zero call sites anywhere, carries the bug in §5.3).

### Notable non-findings / scope notes
- **No Critical-severity findings** in these four modules.
- No `!!`/unsafe casts of concern (`Post.kt:21`'s `!!` is redundant but guarded by an
  outer try/catch; `OpenBookViewModel.kt:52` uses a safe `as?`).
- **No module dependency-rule violations** — `app`, `navigation`, `core`, `domain` all
  check out clean against `docs/MODULE_DEPENDENCIES.md`.
- Hardcoded URLs (`AppConfig.BASE_URL`, `CATALOG_URL`) are by-design, not secrets, both
  HTTPS. `UserSettings.appVersion: String = "6.0.0"` (`domain/.../UserSettings.kt:8`) is
  a stale hardcoded default that could silently mismatch the real app version if
  `UserSettings()` default-constructs before settings load (`AppViewModel.kt:21`).

### Most valuable spots to add test coverage
Zero tests exist in `navigation/`, `core/`, or `domain/` (only the default template in
`app/`). Highest value, ranked: `ReadTimeEstimator` (pure function, non-trivial
Tamil/English branching — cheapest, highest-signal target); `Post.formattedDate`/
`Book.year` (date-parsing edge cases); `OpenBookViewModel` (given §5.1/§5.2, the most
bug-prone class reviewed and entirely untested — a `MainCoroutineRule`-based test
exercising import-fails/open-fails-after-import/concurrent-double-tap would catch both
directly); `RootViewModel` (welcome-vs-main routing); `MainAppState`'s
non-`@Composable` functions (would have caught §5.7 directly).

---

## 6. `data/` layer

### Critical

**6.1 — Missing Room migration 3→4 — will crash on app update for existing users.**
`data/.../di/DatabaseModule.kt:76` and `AppDatabase.kt:32`: `@Database(version = 6)` but
`.addMigrations()` only registers `MIGRATION_1_2, MIGRATION_2_3, MIGRATION_4_5,
MIGRATION_5_6` — **no `MIGRATION_3_4`**, and no `fallbackToDestructiveMigration()`. Git
history confirms the 3→4 bump happened when `PostFts` (search) was added and no
migration was ever written for it. Room validates the *full* chain, not just endpoints,
so any device that installed the app at schema version 3 (pre-search) and updates to any
build ≥ v4 gets `IllegalStateException: Migration didn't properly handle...` on
`Room.databaseBuilder(...).build()` — **crashes on next launch.** Given the app is
already shipped (release APK present in this repo), this is a live production risk for
existing installs, not theoretical. `exportSchema = false` also means there are no
schema JSON snapshots to test migrations against.

### High

**6.2 — Category/Tag sync does NOT follow the "fetch fully, then replace atomically"
contract — partial replace on error.** `CategoryRepositoryImpl.kt:27` and
`TagRepositoryImpl.kt:27`: `remoteDataSource.fetchPage(...) ?: break` treats a failed
request (which returns `null` specifically to signal failure) identically to "no more
pages," then calls `replaceAll(all)` on whatever partial data was accumulated so far,
reporting `Result.success(Unit)`. Contrast with `PostRepositoryImpl`/`BookRepositoryImpl`,
which correctly return an error without calling `replaceAll` on fetch failure. Failure
scenario: category sync page 2 of 3 times out mid-pagination → loop breaks → `replaceAll`
wipes categories 2 and 3 from local DB, caller sees `Result.success`. Every post
referencing those category IDs silently loses its label until the next successful full
sync — directly violates the documented "a failed/partial sync never wipes existing local
data" invariant. Masked in the common case because WordPress returns HTTP 400 once you
request a page past the last one, which is indistinguishable here from a genuine
mid-sync failure.

**6.3 — `TaxonomySyncWorker`/`BookSyncWorker` have no network constraint or backoff
policy.** `data/.../worker/TaxonomySyncWorker.kt:25`, `BookSyncWorker.kt:26`: neither
sets `NetworkType.CONNECTED` nor backoff criteria, unlike `SyncWorker.startUpSyncWork()`
which does both. Real gap against the documented sync contract — books/taxonomy sync
runs immediately without waiting for connectivity, fails cold-start-offline, retries on
WorkManager's default linear (not exponential) backoff.

**6.4 — Book/taxonomy sync failures are invisible to `SyncStatusRepository`.** Only
`SyncPostsUseCase` calls `setSyncing`/`recordSyncSuccess`/`recordSyncError`.
`SyncBooksUseCase` is a bare pass-through and `SyncTaxonomyUseCase` wraps in
`runCatching` with zero status-repository interaction. `GetSyncStatusUseCase` (the
"Syncing…"/"Sync failed" UI signal) only ever reflects post-sync outcomes — a
books-catalog or category/tag sync failure (including §6.2's bug) produces **no
user-visible error state at all**.

**6.5 — DataStore enum decode can crash the app at cold start — no fallback for
`valueOf()`.** `UserSettingsRepositoryImpl.kt:44-49`: `Theme.valueOf(it)`/
`FontSize.valueOf(it)`/`AppLanguage.valueOf(it)` throw `IllegalArgumentException` if the
stored string doesn't match a current enum constant — no try/catch or safe lookup. The
`?:` fallback only covers a *missing* key, not a *present-but-invalid* value. Since
`MarxistReaderApp.onCreate()` does a blocking read of this exact preference before the
first UI frame (§5.5), a future enum rename/removal or DataStore corruption means **every
cold start throws before any UI renders** — a permanent crash loop until the user clears
app data.

### Medium

**6.6 — `catch (e: Exception)`/`runCatching` swallow `CancellationException`, breaking
structured concurrency.** `PostRepositoryImpl.kt:51`, `BookRepositoryImpl.kt:62`,
`CategoryRepositoryImpl.kt:22`, `TagRepositoryImpl.kt:22`, `SyncTaxonomyUseCase.kt:16`.
`CancellationException` is a subtype these blocks all catch; if a sync coroutine is
cancelled mid-`fullSync` (WorkManager `stopWork`, ViewModel scope teardown), these
convert the cancellation into `SyncResult.UnknownError`/`Result.failure` instead of
letting it propagate — can prevent proper cancellation and cause
`SyncWorker.doWork()` to report `Result.retry()` for what was actually intentional.

**6.7 — Race on shared `_isSyncing` flag between manual and background sync.**
`SyncStatusRepositoryImpl.kt:30,47`: `SyncPostsUseCase` runs from both `SyncWorker`
(background) and `FeedViewModel` (manual pull-to-refresh) against the same
`Singleton`-scoped flag. If both run concurrently, whichever finishes first sets
`isSyncing = false` while the other is still mid-sync — UI shows "not syncing" while a
sync is actually in progress. Cosmetic, no data corruption, but a genuine race.

**6.8 — Unsanitized remote `bookId` used directly in filesystem paths.**
`BookRepositoryImpl.kt:68,98-100,108-110`: `fileNameFor(bookId) = "$bookId.epub"`, where
`book.id` comes from `BookDTO.bookid`, deserialized from a remote JSON catalog file
(`raw.githubusercontent.com`) with no sanitization before being interpolated into a
filesystem path — a path-traversal-shaped construction. The trust boundary is the remote
catalog, not the local device; not exploitable by the end user today, but worth a
defensive `require(bookId.matches(Regex("[A-Za-z0-9_-]+")))`.

**6.9 — `SyncResult` type information is discarded once written to
`SyncStatusRepository`.** `recordSyncError(message: String)` only persists a message
string — by the time `GetSyncStatusUseCase` surfaces it, whether it was
`NetworkError`/`ServerError`/`UnknownError` is lost, so UI can't differentiate "no
connection, will retry" from "server issue."

### Low
- **6.10** — `SearchDao.rebuildIndex()` is dead code (no callers); also unnecessary,
  since Room auto-generates `posts`→`posts_fts` sync triggers for the FTS4
  `contentEntity` setup.
- **6.11** — `ListConverters.fromIntListString` silently coerces unparseable tokens to
  `0` rather than filtering/erroring — a landmine if `tagsId`/`categoriesId` is ever
  hand-edited or touched by a future migration.
- **6.12** — Orphaned `saved_posts` rows: `SavedPostEntity` has no FK/cascade to `posts`.
  If a saved post disappears from a later `fullSync()`, its saved-row is never cleaned
  up.

### Confirmed cross-cutting issue
`PostRepositoryImplTest.kt:47` — `PostDTO(1, "2024-01-01", "slug", RenderedText("Title"),
RenderedText("Excerpt"), emptyList(), emptyList())` supplies 7 args to an 8-param
constructor (missing `content: RenderedText`), confirmed still broken, blocks the whole
`data` module's test compilation. (Same underlying issue category as §2.1 in
`use-cases`.)

### Notable non-findings / scope notes
- No `!!`/unsafe casts found in `data/src/main`.
- **Fetch-fully-then-replace is NOT consistently applied**: `PostRepositoryImpl` and
  `BookRepositoryImpl` implement it correctly; `CategoryRepositoryImpl`/
  `TagRepositoryImpl` do not (§6.2).
- Migrations 1→2, 2→3, 4→5, 5→6 themselves look safe (additive, `CREATE TABLE IF NOT
  EXISTS`, no destructive drops) — only the 3→4 gap is missing (§6.1).

### Most valuable spots to add test coverage
Effectively 0% today (the one existing test file doesn't compile). Ranked: fix
`PostRepositoryImplTest`'s compile error, then extend the same "replaceAll not called on
partial failure" pattern to `CategoryRepositoryImpl`/`TagRepositoryImpl` (would catch
§6.2 directly); `UserSettingsRepositoryImpl.getSettings()` with a corrupted/unknown enum
string seeded into a fake DataStore (would catch §6.5); a Room migration test via
`MigrationTestHelper` for the full 1→6 chain, which requires turning `exportSchema` on
and committing schema JSONs (would have caught §6.1 immediately); a concurrent-sync test
for `SyncStatusRepositoryImpl` (would catch §6.7).

---

## 7. `feature/feed` + `feature/feeddetails` + `feature/saved` + `feature/books` + `ui:common`

### Critical

**7.1 — `FeedViewModelTest.kt` fails to compile — the module CLAUDE.md calls out as
having "comprehensive" test coverage currently has zero working tests.** Verified via
`./gradlew :feature:feed:compileDebugUnitTestKotlin`. Two independent defects, both in
the test file: `createViewModel()` (lines 39-45) passes `getFeedItemsFlowUseCase` and
`getSavedPostIdsFlowUseCase` in swapped order vs. `FeedViewModel`'s actual constructor;
and lines 64/100 construct `Post(1, "2024-01-01", "s", "Title", "Excerpt", emptyList(),
emptyList())` with 7 args where `Post` now requires 8 (missing `content`). `./gradlew
test` currently fails outright for `:feature:feed`. Also: the test class never uses the
already-written `MainCoroutineRule` — it manually calls `Dispatchers.setMain` per test
and never `resetMain`s, so `Dispatchers.Main` will leak across the test class even once
compile errors are fixed.

### High

**7.2 — Feed/Saved bookmark-toggle has no exception handling around the Room write —
inconsistent with `feature/feeddetails`, and can crash the app.**
`ArticleDetailViewModel.toggleSave()` (`ArticleDetailViewModel.kt:80-99`) wraps
`savePostUseCase`/`unsavePostUseCase` in try/catch and reverts optimistic UI state on
failure. `FeedViewModel.toggleSave` (`FeedViewModel.kt:60-76`) and
`SavedViewModel.unsave` (`SavedViewModel.kt:45-49`) do not — any Room/SQLite failure
(disk full, corruption, I/O error) is an uncaught exception in a bare `viewModelScope`
coroutine, crashing the app. Failure scenario: bookmarking from the Feed screen while
storage is full crashes the app; the identical action from Article Detail degrades
gracefully instead.

**7.3 — Rapid double-tap on "Download" launches two concurrent downloads writing to the
same file.** `BooksViewModel.downloadBook` (`BooksViewModel.kt:65-74`) has no
synchronous "already downloading" guard — the only check is against
`_downloadStates.value[book.id]`, which only updates *after* the flow's first emission.
Between tap and that first emission, a second tap passes the guard and starts a second
writer on the same destination file via a separate `outputStream()` handle. Failure
scenario: double-tap on a slow connection corrupts/truncates the downloaded `.epub`.

**7.4 — Unhandled exception in the download-flow collector can crash the app.**
`BooksViewModel.kt:69-73`: `FileDownloaderImpl.downloadFile` catches exceptions *inside*
its own try block, but anything thrown before that block is entered (e.g.
`context.getDownloadDir()` construction) propagates uncaught through the `viewModelScope`
`collect { }`, which has no try/catch. Failure scenario: download directory unavailable
(external storage unmounted) crashes the app instead of surfacing `Failed`.

### Medium

**7.5 — `refresh()` can get permanently stuck in `Loading` after recovering from an
error, if the sync returns an empty result set.** Both `FeedViewModel.refresh()` and
`BooksViewModel.refresh()` manually set state to `Loading` on `SyncResult.Success` when
recovering from `Error`, relying on the Room flow to re-emit and replace it. But
`PostRepositoryImpl.fullSync()`/`BookRepositoryImpl.fullSync()` both only call
`replaceAll(...)` `if (entities.isNotEmpty())` — a successful sync of a genuinely-empty
backend triggers no write, no invalidation, and the manually-set `Loading` never gets
replaced. The screen hangs on the skeleton forever.

**7.6 — `AnimatedContent` content lambdas re-read the live `uiState` instead of a frozen
per-branch snapshot, risking a blank frame mid-transition.** Identical pattern in
`FeedScreenRoute.kt:49`, `SavedScreen.kt:55`, `BooksScreenRoute.kt:41`: `val state =
uiState as? X.Success ?: return@AnimatedContent`. If `uiState` moves to a different
sealed variant while the old branch's composable is still alive during the crossfade
(e.g. rapid `Content → Error → Success` under flaky network), the cast fails mid-fade and
the branch renders nothing. Low probability, reproducible under rapid state churn.

**7.7 — `SavedFeedContent`'s `LazyColumn` has no bottom nav-bar inset padding, unlike
`FeedContent`/`BooksContent`.** `SavedScreen.kt:94-97` vs. `FeedScreens.kt:57-64` and
`BooksScreens.kt:55-65`, which both explicitly add `WindowInsets.navigationBars` to
`contentPadding`. Whether this actually obscures the last list item depends on whether
the shared `NavigationSuiteScaffold` already consumes that inset — either way, the three
otherwise-identical screens should share one convention.

**7.8 — `SavedViewModel` has no retry/refresh capability once `Error` is reached —
permanently stuck for the ViewModel's lifetime.** Unlike `FeedViewModel`/
`BooksViewModel`, there's no `refresh()` at all, and the `.catch { }` operator
(`SavedViewModel.kt:34`) terminates upstream collection rather than resuming it. Rare in
practice (local-only Room flow) but if it fires, the Saved tab is dead until the
ViewModel is recreated.

**7.9 — Self-referential feedback write from inside a `combine` transform lambda.**
`ArticleDetailViewModel.kt:63-65` sets `_optimisticSaved.value = null` inside the lambda
of a `combine` that itself includes `_optimisticSaved` as one of its inputs. Currently
converges (the write stops the cascade) but is a fragile pattern relying on `StateFlow`'s
specific reentrancy/conflation guarantees rather than an explicit, testable operation.

### Low
- **7.10** — Duplicated UI code that should live in `ui:common`: `FeedLoadingSkeleton`
  is independently redefined in both `FeedScreens.kt` and `SavedScreen.kt:121-135`
  (near-identical, `repeat(4)` vs `repeat(5)`); the nav-bar-inset `contentPadding`
  boilerplate is copy-pasted in `FeedScreens.kt`/`BooksScreens.kt` (and inconsistently
  omitted in Saved, per §7.7); every feature hand-rolls its own `enum class StateKey` +
  `stateKey` extension with identical shape.
- **7.11** — `ArticleDetailRoute.kt:122-130` (`sharePost`) declares an unnecessary
  `Intent?` return type — `Intent.createChooser(sendIntent, null)` never returns null,
  and the caller has no null check, relying on Kotlin's platform-type leniency.
- **7.12** — Dead mock setup in `FeedViewModelTest.kt` (`getSyncStatusUseCase`,
  stubbed in every test but `FeedViewModel`'s constructor doesn't take it at all —
  leftover from a prior ViewModel version).

### Notable non-findings / scope notes
- **No `mutableStateOf`-without-`remember{}` bug found** in any of these five modules —
  this bug class (found and fixed in `feature/search`) does not repeat here.
- No `!!`/unsafe casts of concern.
- Bookmark/save wiring inconsistency summary (task item d): `ArticleDetailViewModel` is
  the only one of the three (Feed/Saved/ArticleDetail) that reverts optimistic UI state
  on a failed persistence call — Feed and Saved apply the optimistic flip with no
  failure path and no try/catch at all (§7.2).

### Most valuable spots to add test coverage
Fix `FeedViewModelTest.kt` first (§7.1 — it's the whole reason `./gradlew test` fails for
this module), then add a `toggleSave` failure-path test once §7.2 is fixed.
`BooksViewModel.downloadBook` is the most state-machine-heavy untested logic in this set
(re-download guard, state mapping, the double-tap race in §7.3). `SavedViewModel` and
`ArticleDetailViewModel` both have zero tests today — the latter's optimistic-save/revert
and reset-on-match logic (§7.9) is exactly the kind of subtle state logic that most
benefits from a regression test. A `PostRepositoryImpl`/`BookRepositoryImpl` test
asserting whether `replaceAll` is skipped on an empty successful sync would pin down the
root cause of §7.5 either way.

---

# Executive summary

Six parallel review passes plus the `feature/search` work already done this session
covered the entire codebase. The findings above total **7 Critical**, **~20 High**, and
a larger number of Medium/Low items. The picture that emerges:

**Cross-cutting patterns, not isolated bugs:**
- **Silent failure is the dominant defect class.** Book download failures (§5.1, §7.4),
  book-open failures (§5.1), category/tag/book/taxonomy sync failures (§6.2, §6.4),
  and the Welcome screen's offline state (§4.1) all fail *silently* — no crash, no error
  shown, the user just sees nothing happen or a spinner that never resolves. This is a
  much bigger practical risk to real users than the crash-prone paths, because crashes at
  least get reported; silent hangs just look like the app is broken.
- **The opposite failure mode — uncaught exceptions crashing the app — clusters around
  bookmark/save actions and downloads** (§7.2, §7.3, §7.4, reader's §3.3/§3.4), all
  missing the try/catch + revert pattern that `ArticleDetailViewModel` already
  demonstrates correctly. This is a "copy the pattern that already works" fix, not a
  design problem.
- **The `mutableStateOf` without `remember{}` bug found in `feature/search` earlier this
  session does not recur anywhere else** — every other module was checked and came back
  clean. Good sign that it was a one-off, not systemic.
- **Test infrastructure is broken in three separate places** for the same underlying
  reason (a `Post`/`PostDTO` constructor gained required fields without updating
  fixtures): `data`, `use-cases`, and `feature/feed`. All three block `./gradlew test`
  for their respective modules. This should probably be fixed as one coordinated pass
  rather than three, since it's the same root cause everywhere.
- **Two toggles that don't do anything real:** Settings' Push Notifications switch
  (§4.2, no notification system exists in the app at all) and — more architecturally —
  `DownloadResult.Progress` (§2.6, defined, wired through three modules, never emitted).
- **One live production risk:** the missing Room migration 3→4 (§6.1) will crash the app
  on next launch for any existing install still on schema version 3, i.e. anyone who
  installed before the search feature shipped. This is the single highest-priority item
  in the whole report given the app is already released.

**By the numbers (rough, from this pass):**
| Module | Critical | High | Test coverage |
|---|---|---|---|
| feature/search | (already fixed this session) | | 11 tests, passing |
| use-cases + network | 1 | 2 | ~0%, 2 test files (1 broken) |
| reader | 2 | 4 | 0%, template only |
| settings/welcome/theme | 2 | 2 | 0% |
| app/navigation/core/domain | 0 | 3 | 0% (except app template) |
| data | 1 | 4 | ~0%, 1 test file (broken) |
| feed/saved/books/feeddetails | 1 | 3 | ~0%, 1 test file (broken) |

**Suggested order of attack**, roughly by (blast radius) × (fix cost):
1. Room migration 3→4 (§6.1) — production crash risk for existing installs.
2. Fix the three broken test fixtures (§2.1, §6-note, §7.1) — one root cause, unblocks
   all further test-writing.
3. Bookmark/download exception-safety pass (§7.2–§7.4, §3.3–§3.4) — copy the
   already-correct `ArticleDetailViewModel` pattern to the other four sites.
4. Category/Tag partial-sync bug (§6.2) — silent data loss, same fix shape as the
   existing correct `PostRepositoryImpl`/`BookRepositoryImpl` pattern.
5. Welcome screen offline/retry state (§4.1) — UI already built, just needs wiring.
6. Everything else, roughly in Critical → High → Medium order per section above.

This report is descriptive only — nothing has been changed in the codebase as part of
generating it. Awaiting direction on which items to act on.
