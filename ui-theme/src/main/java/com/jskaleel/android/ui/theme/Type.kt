package com.jskaleel.android.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// -- Font Families --
val mukta_malar = FontFamily(
    Font(R.font.mukta_malar_regular, FontWeight.Normal),
    Font(R.font.mukta_malar_medium, FontWeight.Medium),
    Font(R.font.mukta_malar_semibold, FontWeight.SemiBold),
    Font(R.font.mukta_malar_bold, FontWeight.Bold)
)

val noto_serif_tamil = FontFamily(
    Font(R.font.noto_serif_tamil_regular, FontWeight.Normal),
    Font(R.font.noto_serif_tamil_medium, FontWeight.Medium),
    Font(R.font.noto_serif_tamil_semibold, FontWeight.SemiBold),
    Font(R.font.noto_serif_tamil_bold, FontWeight.Bold)
)

val CustomTypography = Typography().run {
    copy(
        displayLarge = TextStyle(
            fontFamily = mukta_malar,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            lineHeight = 36.sp,
            letterSpacing = 0.sp,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),
        displayMedium = TextStyle(
            fontFamily = mukta_malar,
            fontWeight = FontWeight.SemiBold,
            fontSize = 26.sp,
            lineHeight = 32.sp,
            letterSpacing = 0.sp,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),
        displaySmall = TextStyle(
            fontFamily = mukta_malar,
            fontWeight = FontWeight.Medium,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),

        headlineLarge = TextStyle(
            fontFamily = mukta_malar,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            lineHeight = 26.sp,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),
        headlineMedium = TextStyle(
            fontFamily = mukta_malar,
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),
        headlineSmall = TextStyle(
            fontFamily = mukta_malar,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),

        titleLarge = TextStyle(
            fontFamily = mukta_malar,
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
            lineHeight = 22.sp,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),
        titleMedium = TextStyle(
            fontFamily = mukta_malar,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),
        titleSmall = TextStyle(
            fontFamily = mukta_malar,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 18.sp,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),

        bodyLarge = TextStyle(
            fontFamily = noto_serif_tamil,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),
        bodyMedium = TextStyle(
            fontFamily = noto_serif_tamil,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 26.sp,
            letterSpacing = 0.sp,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),
        bodySmall = TextStyle(
            fontFamily = noto_serif_tamil,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 22.sp,
            letterSpacing = 0.sp,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),

        labelLarge = TextStyle(
            fontFamily = mukta_malar,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 18.sp,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),
        labelMedium = TextStyle(
            fontFamily = mukta_malar,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),
        labelSmall = TextStyle(
            fontFamily = mukta_malar,
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),
    )
}