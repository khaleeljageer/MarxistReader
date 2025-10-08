package com.jskaleel.android.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import com.jskaleel.android.ui.theme.model.CustomColors


@Suppress("detekt:MagicNumber")
object AppColor {
    object Light {
        val Primary = Color(0xFFFFA726) // Warm Orange-Yellow
        val PrimaryContainer = Color(0xFFFFE0B2) // Light Peach
        val Secondary = Color(0xFF757575) // Medium Gray
        val OnSecondary = Color(0xFFFFFFFF) // White
        val Background = Color(0xFFFAFAFA) // Soft Off-White
        val OnBackground = Color(0xFF1A1A1A) // Near Black
        val Surface = Color(0xFFFFFFFF) // White
        val OnSurface = Color(0xFF1A1A1A) // Near Black
        val OnPrimary = Color(0xFF000000) // Black
        val TextPrimary = Color(0xFF1A1A1A) // Near Black
        val TextSecondary = Color(0xFF5F5F5F) // Medium Gray
        val Accent = Color(0xFFFF9800) // Deep Orange
        val Highlight = Color(0xFFFFF59D) // Soft Yellow
        val Divider = Color(0xFFE0E0E0) // Light Gray
        val Error = Color(0xFFD32F2F) // Red
        val OnError = Color(0xFFFFFFFF) // White
    }

    object Dark {
        val Primary = Color(0xFFFFD54F) // Light Yellow
        val PrimaryContainer = Color(0xFFF57C00) // Darker Orange
        val Secondary = Color(0xFFA0A0A0) // Light Gray
        val OnSecondary = Color(0xFF000000) // Black
        val Background = Color(0xFF121212) // True Dark
        val OnBackground = Color(0xFFE1E1E1) // Soft White
        val Surface = Color(0xFF1E1E1E) // Elevated Dark
        val OnSurface = Color(0xFFE1E1E1) // Soft White
        val OnPrimary = Color(0xFF000000) // Black
        val TextPrimary = Color(0xFFE1E1E1) // Soft White
        val TextSecondary = Color(0xFFA0A0A0) // Muted Gray
        val Accent = Color(0xFFFFB74D) // Light Orange
        val Highlight = Color(0xFF4A4A2A) // Muted Dark Yellow
        val Divider = Color(0xFF2C2C2C) // Subtle Gray
        val Error = Color(0xFFEF5350) // Light Red
        val OnError = Color(0xFF000000) // Black
    }
}

val LocalCustomColors = compositionLocalOf {
    CustomColors(
        accent = AppColor.Light.Accent,
        textPrimary = AppColor.Light.TextPrimary,
        textSecondary = AppColor.Light.TextSecondary,
        readingBackground = AppColor.Light.Background
    )
}

val MaterialTheme.customColors
    @Composable
    @ReadOnlyComposable
    get() = LocalCustomColors.current
