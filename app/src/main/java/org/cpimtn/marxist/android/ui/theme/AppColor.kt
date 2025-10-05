package org.cpimtn.marxist.android.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

@Suppress("detekt:MagicNumber")
object AppColor {
    object Light {
        val Primary = Color(0xFFFFF5E0)
        val PrimaryContainer = Color(0xFFFFDBC3)
        val Secondary = Color(0xFFEE9E8E)
        val OnSecondary = Color(0xFF190933)
        val Background = Color(0xFFFFF5E0)
        val OnBackground = Color(0xFF190933)
        val Surface = Color(0xFFFFFFFF)
        val OnSurface = Color(0xFF190933)
        val OnPrimary = Color(0xFF190933)
        val TextPrimary = Color(0xFF190933)
        val TextSecondary = Color(0xFF5E4A58)
        val Accent = Color(0xFFEE9E8E)
        val Error = Color(0xFFD32F2F)
        val OnError = Color(0xFFFFFFFF)
    }

    object Dark {
        val Primary = Color(0xFF3D2E3A)
        val PrimaryContainer = Color(0xFF5E4A58)
        val Secondary = Color(0xFFEE9E8E)
        val OnSecondary = Color(0xFFFFF5E0)
        val Background = Color(0xFF190933)
        val OnBackground = Color(0xFFFFF5E0)
        val Surface = Color(0xFF2A1F2E)
        val OnSurface = Color(0xFFFFDBC3)
        val OnPrimary = Color(0xFFFFF5E0)
        val TextPrimary = Color(0xFFFFDBC3)
        val TextSecondary = Color(0xFFD4B5C8)
        val Accent = Color(0xFFFF9E8E)
        val Error = Color(0xFFEF5350)
        val OnError = Color(0xFF190933)
    }
}

val LocalCustomColors = compositionLocalOf { getCustomColor(darkTheme) }

val MaterialTheme.customColors
    @Composable
    @ReadOnlyComposable
    get() = LocalCustomColors.current
