package org.cpimtn.marxist.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * ══════════════════════════════════════════════════════════════
 *  மார்க்சிஸ்ட் — Theme
 *  CPI(M) Tamil Nadu
 *
 *  Palette: செம்மண் — Earthy Warmth
 *
 *  SINGLE SOURCE OF TRUTH for all colors in the app.
 *  Composables must ONLY use:
 *    MaterialTheme.colorScheme.primary     (M3 standard roles)
 *    MarxistTheme.colors.bookmarkActive    (app-specific roles)
 *
 *  NEVER reference Color.kt values directly in UI code.
 * ══════════════════════════════════════════════════════════════
 */

// ═════════════════════════════════════════════════════════════
//  1. MATERIAL 3 COLOR SCHEMES
// ═════════════════════════════════════════════════════════════

private val LightColorScheme: ColorScheme = lightColorScheme(
    primary                  = Terra40,
    onPrimary                = Warm100,
    primaryContainer         = Terra95,
    onPrimaryContainer       = Terra10,
    secondary                = Ochre50,
    onSecondary              = Warm100,
    secondaryContainer       = Ochre95,
    onSecondaryContainer     = Ochre10,
    tertiary                 = Sienna50,
    onTertiary               = Warm100,
    tertiaryContainer        = Sienna95,
    onTertiaryContainer      = Sienna10,
    background               = Warm97,
    onBackground             = OnBackgroundLight,
    surface                  = Warm100,
    onSurface                = OnBackgroundLight,
    surfaceVariant           = Warm95,
    onSurfaceVariant         = Warm40,
    surfaceDim               = Warm92,
    surfaceBright            = Warm97,
    surfaceContainerLowest   = Warm100,
    surfaceContainerLow      = Warm98,
    surfaceContainer         = Warm96,
    surfaceContainerHigh     = Warm95,
    surfaceContainerHighest  = Warm92,
    outline                  = Warm90,
    outlineVariant           = Warm94,
    error                    = ErrorLight,
    onError                  = OnErrorLight,
    errorContainer           = ErrorContLight,
    onErrorContainer         = OnErrorContLight,
    inverseSurface           = Warm10,
    inverseOnSurface         = Warm97,
    inversePrimary           = Terra80,
    surfaceTint              = Terra40,
    scrim                    = Warm0,
)

private val DarkColorScheme: ColorScheme = darkColorScheme(
    primary                  = Terra80,
    onPrimary                = Terra20,
    primaryContainer         = Terra30,
    onPrimaryContainer       = Terra95,
    secondary                = Ochre80,
    onSecondary              = Ochre20,
    secondaryContainer       = Ochre30,
    onSecondaryContainer     = Ochre90,
    tertiary                 = Sienna80,
    onTertiary               = Sienna20,
    tertiaryContainer        = Sienna30,
    onTertiaryContainer      = Sienna90,
    background               = Warm4,
    onBackground             = OnBackgroundDark,
    surface                  = Warm10,
    onSurface                = OnBackgroundDark,
    surfaceVariant           = Warm15,
    onSurfaceVariant         = Warm65,
    surfaceDim               = Warm4,
    surfaceBright            = Warm20,
    surfaceContainerLowest   = Warm0,
    surfaceContainerLow      = Warm6,
    surfaceContainer         = Warm10,
    surfaceContainerHigh     = Warm12,
    surfaceContainerHighest  = Warm17,
    outline                  = Warm25,
    outlineVariant           = Warm17,
    error                    = ErrorDark,
    onError                  = OnErrorDark,
    errorContainer           = ErrorContDark,
    onErrorContainer         = OnErrorContDark,
    inverseSurface           = Warm90,
    inverseOnSurface         = Warm10,
    inversePrimary           = Terra40,
    surfaceTint              = Terra80,
    scrim                    = Warm0,
)


// ═════════════════════════════════════════════════════════════
//  2. EXTENDED COLORS — App-Specific Semantic Tokens
//
//  These cover UI elements that have no direct M3 role.
//  Access via: MarxistTheme.colors.xxx
// ═════════════════════════════════════════════════════════════

@Immutable
data class MarxistExtendedColors(

    // ── Featured Article Card ──
    val featuredGradientStart: Color,
    val featuredGradientEnd: Color,
    val featuredLabel: Color,
    val featuredTitle: Color,
    val featuredCategoryBg: Color,
    val featuredCategoryText: Color,
    val featuredDate: Color,

    // ── Article Card ──
    val categoryBadgeBg: Color,
    val categoryBadgeText: Color,
    val tagChipBg: Color,
    val tagChipBorder: Color,
    val tagChipText: Color,
    val articleExcerpt: Color,
    val articleTimestamp: Color,

    // ── Bookmark / Save ──
    val bookmarkActive: Color,
    val bookmarkInactive: Color,

    // ── Bottom Navigation ──
    val navBackground: Color,
    val navBorder: Color,
    val navIndicator: Color,
    val navActiveIcon: Color,
    val navActiveLabel: Color,
    val navInactiveIcon: Color,
    val navInactiveLabel: Color,

    // ── Top App Bar ──
    val appBarBackground: Color,
    val appBarTitle: Color,
    val appBarSubtitle: Color,
    val appBarActionIcon: Color,
    val logoBackground: Color,
    val logoSymbol: Color,

    // ── Search ──
    val searchFieldBg: Color,
    val searchFieldBorder: Color,
    val searchFieldBorderFocused: Color,
    val searchPlaceholder: Color,
    val searchSuggestionIcon: Color,
    val searchSuggestionText: Color,

    // ── Book Card ──
    val bookCoverGradientStart: Color,
    val bookCoverGradientEnd: Color,
    val bookYearText: Color,
    val bookDownloadBg: Color,
    val bookDownloadText: Color,

    // ── Switch / Toggle ──
    val switchCheckedTrack: Color,
    val switchCheckedThumb: Color,
    val switchCheckedBorder: Color,
    val switchUncheckedTrack: Color,
    val switchUncheckedThumb: Color,
    val switchUncheckedBorder: Color,

    // ── Settings ──
    val settingsGroupTitle: Color,
    val settingsIconBg: Color,
    val settingsIconTint: Color,

    // ── Accent Line (content top edge) ──
    val accentLineStart: Color,
    val accentLineEnd: Color,

    // ── Section Headers ──
    val sectionHeader: Color,

    // ── Article Detail / Reader ──
    val detailDivider: Color,
    val detailBody: Color,
    val detailDate: Color,

    // ── Shimmer / Skeleton Loading ──
    val shimmerBase: Color,
    val shimmerHighlight: Color,

    // ── Splash Screen ──
    val splashBackground: Color,
    val splashIcon: Color,

    // ── CPI(M) Party Identity ──
    val partyRedAccent: Color,
)

// ── Light Extended Instance ─────────────────────────────────

private val LightExtendedColors = MarxistExtendedColors(

    featuredGradientStart   = Terra40,
    featuredGradientEnd     = Terra25,
    featuredLabel           = Ochre70,
    featuredTitle           = Warm100,
    featuredCategoryBg      = Warm100.copy(alpha = 0.15f),
    featuredCategoryText    = Warm100.copy(alpha = 0.70f),
    featuredDate            = Warm100.copy(alpha = 0.45f),

    categoryBadgeBg         = Terra90.copy(alpha = 0.10f),
    categoryBadgeText       = Terra40,
    tagChipBg               = Warm95,
    tagChipBorder           = Warm90,
    tagChipText             = Warm50,
    articleExcerpt          = Warm40,
    articleTimestamp         = Warm60,

    bookmarkActive          = Ochre50,
    bookmarkInactive        = Warm70,

    navBackground           = Warm100,
    navBorder               = Warm90,
    navIndicator            = Terra40,
    navActiveIcon           = Terra40,
    navActiveLabel          = Terra40,
    navInactiveIcon         = Warm60,
    navInactiveLabel        = Warm60,

    appBarBackground        = Warm97,
    appBarTitle             = OnBackgroundLight,
    appBarSubtitle          = Terra40,
    appBarActionIcon        = Warm50,
    logoBackground          = Terra40,
    logoSymbol              = Ochre80,

    searchFieldBg           = Warm96,
    searchFieldBorder       = Warm90,
    searchFieldBorderFocused = Terra40,
    searchPlaceholder       = Warm60,
    searchSuggestionIcon    = Warm60,
    searchSuggestionText    = Warm40,

    bookCoverGradientStart  = Warm96,
    bookCoverGradientEnd    = WarmPeach,
    bookYearText            = Terra40.copy(alpha = 0.22f),
    bookDownloadBg          = Terra99,
    bookDownloadText        = Terra40,

    switchCheckedTrack      = Terra40,
    switchCheckedThumb      = Warm100,
    switchCheckedBorder     = Terra40,
    switchUncheckedTrack    = Warm80,
    switchUncheckedThumb    = Warm100,
    switchUncheckedBorder   = Warm60,

    settingsGroupTitle      = Terra40,
    settingsIconBg          = Warm95,
    settingsIconTint        = Warm40,

    accentLineStart         = Terra40,
    accentLineEnd           = Terra40.copy(alpha = 0f),

    sectionHeader           = Warm60,

    detailDivider           = Warm94,
    detailBody              = Warm40,
    detailDate              = Warm60,

    shimmerBase             = Warm96,
    shimmerHighlight        = Warm100,

    splashBackground        = Terra40,
    splashIcon              = Ochre80,

    partyRedAccent          = PartyRed,
)

// ── Dark Extended Instance ──────────────────────────────────

private val DarkExtendedColors = MarxistExtendedColors(

    featuredGradientStart   = Terra35,
    featuredGradientEnd     = Terra25,
    featuredLabel           = Ochre80,
    featuredTitle           = Warm100,
    featuredCategoryBg      = Warm100.copy(alpha = 0.15f),
    featuredCategoryText    = Warm100.copy(alpha = 0.70f),
    featuredDate            = Warm100.copy(alpha = 0.45f),

    categoryBadgeBg         = Terra80.copy(alpha = 0.10f),
    categoryBadgeText       = Terra80,
    tagChipBg               = Warm12,
    tagChipBorder           = Warm25,
    tagChipText             = Warm50,
    articleExcerpt          = Warm65,
    articleTimestamp         = Warm40,

    bookmarkActive          = Ochre80,
    bookmarkInactive        = Warm40,

    navBackground           = Warm8,
    navBorder               = Warm25,
    navIndicator            = Terra80,
    navActiveIcon           = Terra80,
    navActiveLabel          = Terra80,
    navInactiveIcon         = Warm40,
    navInactiveLabel        = Warm40,

    appBarBackground        = Warm4,
    appBarTitle             = OnBackgroundDark,
    appBarSubtitle          = Terra80,
    appBarActionIcon        = Warm40,
    logoBackground          = Terra40,
    logoSymbol              = Ochre80,

    searchFieldBg           = Warm12,
    searchFieldBorder       = Warm25,
    searchFieldBorderFocused = Terra80,
    searchPlaceholder       = Warm40,
    searchSuggestionIcon    = Warm40,
    searchSuggestionText    = Warm65,

    bookCoverGradientStart  = Warm10,
    bookCoverGradientEnd    = DarkBrown,
    bookYearText            = Terra80.copy(alpha = 0.22f),
    bookDownloadBg          = Terra80.copy(alpha = 0.10f),
    bookDownloadText        = Terra80,

    switchCheckedTrack      = Terra80,
    switchCheckedThumb      = Warm4,
    switchCheckedBorder     = Terra80,
    switchUncheckedTrack    = Warm30,
    switchUncheckedThumb    = Warm60,
    switchUncheckedBorder   = Warm40,

    settingsGroupTitle      = Terra80,
    settingsIconBg          = Warm12,
    settingsIconTint        = Warm65,

    accentLineStart         = Terra80,
    accentLineEnd           = Terra80.copy(alpha = 0f),

    sectionHeader           = Warm40,

    detailDivider           = Warm25,
    detailBody              = Warm65,
    detailDate              = Warm40,

    shimmerBase             = Warm12,
    shimmerHighlight        = Warm20,

    splashBackground        = Terra35,
    splashIcon              = Ochre80,

    partyRedAccent          = PartyRedLight,
)


// ═════════════════════════════════════════════════════════════
//  3. COMPOSITION LOCAL + ACCESSOR
// ═════════════════════════════════════════════════════════════

val LocalMarxistColors = staticCompositionLocalOf { LightExtendedColors }

/**
 * Theme accessor object.
 *
 * Usage in any @Composable:
 *
 *   // M3 standard roles
 *   val primary = MaterialTheme.colorScheme.primary
 *   val bg = MaterialTheme.colorScheme.background
 *
 *   // App-specific roles
 *   val bm = MarxistTheme.colors.bookmarkActive
 *   val grad = Brush.linearGradient(
 *       listOf(
 *           MarxistTheme.colors.featuredGradientStart,
 *           MarxistTheme.colors.featuredGradientEnd,
 *       )
 *   )
 *
 *   // Theme check
 *   if (MarxistTheme.isDark) { ... }
 */
object MarxistReaderTheme {

    val colors: MarxistExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalMarxistColors.current

    val isDark: Boolean
        @Composable
        @ReadOnlyComposable
        get() = LocalMarxistColors.current === DarkExtendedColors
}


// ═════════════════════════════════════════════════════════════
//  4. THEME COMPOSABLE
// ═════════════════════════════════════════════════════════════

/**
 * Root theme for the Marxist app.
 *
 * Wraps content with:
 *  - Material 3 ColorScheme (light / dark)
 *  - MarxistExtendedColors via CompositionLocal
 *  - MarxistTypography (Noto Sans Tamil)
 *  - System bar appearance
 *
 * Apply at the root of your Activity:
 *
 *   setContent {
 *       MarxistTheme {
 *           MarxistApp()
 *       }
 *   }
 */
@Composable
fun MarxistReaderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    // System bar theming
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalMarxistColors provides extendedColors,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = MarxistTypography,
            content     = content,
        )
    }
}