package org.cpimtn.marxist.android.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.cpimtn.marxist.android.R

val fontFamily = FontFamily(
    Font(R.font.tau_marutham)
)

@Composable
fun getTypography(isDarkTheme: Boolean): Typography {
    val textPrimary = if (isDarkTheme) {
        AppColor.Dark.TextPrimary
    } else {
        AppColor.Light.TextPrimary
    }
    val textSecondary = if (isDarkTheme) {
        AppColor.Dark.TextSecondary
    } else {
        AppColor.Light.TextSecondary
    }

    return Typography(
        bodyLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp,
            color = textPrimary,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),
        bodyMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.0.sp,
            letterSpacing = 0.2.sp,
            color = textPrimary,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),
        bodySmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.4.sp,
            color = textSecondary,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),
        titleLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp,
            color = textPrimary,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),
        titleMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.2.sp,
            color = textPrimary,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),
        titleSmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp,
            color = textPrimary,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),
        labelMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp,
            color = textSecondary,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        ),
        labelSmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp,
            color = textSecondary,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        )
    )
}