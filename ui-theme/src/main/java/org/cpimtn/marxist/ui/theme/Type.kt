package org.cpimtn.marxist.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.cpimtn.marxist.ui.R

/**
 * ══════════════════════════════════════════════════════════════
 *  மார்க்சிஸ்ட் — Typography
 *
 *  Primary: Noto Sans Tamil — complete Tamil Unicode coverage
 *  with generous line heights for comfortable reading.
 *
 *  Add font files to res/font/:
 *    noto_sans_tamil_regular.ttf
 *    noto_sans_tamil_medium.ttf
 *    noto_sans_tamil_semibold.ttf
 *    noto_sans_tamil_bold.ttf
 *    noto_sans_tamil_extrabold.ttf
 *    noto_sans_tamil_black.ttf
 * ══════════════════════════════════════════════════════════════
 */

// Uncomment after adding font files to res/font/

val NotoSansTamil = FontFamily(
    Font(R.font.noto_sans_tamil_regular,   FontWeight.Normal),
    Font(R.font.noto_sans_tamil_medium,    FontWeight.Medium),
    Font(R.font.noto_sans_tamil_semibold,  FontWeight.SemiBold),
    Font(R.font.noto_sans_tamil_bold,      FontWeight.Bold),
    Font(R.font.noto_sans_tamil_extrabold, FontWeight.ExtraBold),
    Font(R.font.noto_sans_tamil_black,     FontWeight.Black),
)

// Fallback until font files are added
//val NotoSansTamil = FontFamily.Default

val MarxistTypography = Typography(

    // ── Display — Splash, hero sections ──
    displayLarge = TextStyle(
        fontFamily = NotoSansTamil,
        fontWeight = FontWeight.Black,
        fontSize = 32.sp,
        lineHeight = 48.sp,
        letterSpacing = (-0.5).sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = true
        )
    ),
    displayMedium = TextStyle(
        fontFamily = NotoSansTamil,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 42.sp,
        letterSpacing = (-0.3).sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = true
        )
    ),
    displaySmall = TextStyle(
        fontFamily = NotoSansTamil,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 38.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = true
        )
    ),

    // ── Headlines — Article detail title, section headers ──
    headlineLarge = TextStyle(
        fontFamily = NotoSansTamil,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 22.sp,
        lineHeight = 36.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = true
        )
    ),
    headlineMedium = TextStyle(
        fontFamily = NotoSansTamil,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 32.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = true
        )
    ),
    headlineSmall = TextStyle(
        fontFamily = NotoSansTamil,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 28.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = true
        )
    ),

    // ── Title — Card titles, featured card, book titles ──
    titleLarge = TextStyle(
        fontFamily = NotoSansTamil,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        lineHeight = 26.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = true
        )
    ),
    titleMedium = TextStyle(
        fontFamily = NotoSansTamil,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = true
        )
    ),
    titleSmall = TextStyle(
        fontFamily = NotoSansTamil,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = true
        )
    ),

    // ── Body — Article reading, excerpts ──
    bodyLarge = TextStyle(
        fontFamily = NotoSansTamil,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 28.sp,  // Extra generous for Tamil
        platformStyle = PlatformTextStyle(
            includeFontPadding = true
        )
    ),
    bodyMedium = TextStyle(
        fontFamily = NotoSansTamil,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 22.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = true
        )
    ),
    bodySmall = TextStyle(
        fontFamily = NotoSansTamil,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = true
        )
    ),

    // ── Labels — Tags, categories, timestamps, nav ──
    labelLarge = TextStyle(
        fontFamily = NotoSansTamil,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = true
        )
    ),
    labelMedium = TextStyle(
        fontFamily = NotoSansTamil,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.3.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = true
        )
    ),
    labelSmall = TextStyle(
        fontFamily = NotoSansTamil,
        fontWeight = FontWeight.SemiBold,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.5.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = true
        )
    ),
)