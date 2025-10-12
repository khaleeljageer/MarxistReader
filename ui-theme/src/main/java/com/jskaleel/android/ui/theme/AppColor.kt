package com.jskaleel.android.ui.theme

import androidx.compose.ui.graphics.Color

@Suppress("detekt:MagicNumber")
object AppColor {
    object Light {
        val Primary = Color(0xFFC62828) // Muted Red
        val OnPrimary = Color(0xFFFFFFFF) // White (high contrast on Primary)
        val PrimaryContainer = Color(0xFFFFCDD2) // Light Red-Pink
        val OnPrimaryContainer = Color(0xFF212121) // Dark Gray (high contrast on PrimaryContainer)
        val Secondary = Color(0xFF388E3C) // Forest Green
        val OnSecondary = Color(0xFFFFFFFF) // White
        val Tertiary = Color(0xFFF06292) // Soft Pink
        val OnTertiary = Color(0xFFFFFFFF) // White
        val Background = Color(0xFFFAFAFA) // Off-White
        val OnBackground = Color(0xFF212121) // Dark Gray
        val Surface = Color(0xFFFFFFFF) // White
        val OnSurface = Color(0xFF212121) // Dark Gray
        val SurfaceVariant = Color(0xFFE0E0E0) // Light Gray
        val OnSurfaceVariant = Color(0xFF212121) // Dark Gray
        val SecondaryContainer = Color(0xFFC8E6C9) // Light Green
        val OnSecondaryContainer = Color(0xFF212121) // Dark Gray
        val Error = Color(0xFFFF8989) // Soft Red
        val OnError = Color(0xFF000000) // Black
    }

    object Dark {
        val Primary = Color(0xFFEF5350) // Light Red
        val OnPrimary = Color(0xFFFFFFFF) // White (high contrast on Primary)
        val PrimaryContainer = Color(0xFFB71C1C) // Darker Red
        val OnPrimaryContainer = Color(0xFFFFFFFF) // White (high contrast on PrimaryContainer)
        val Secondary = Color(0xFF81C784) // Light Green
        val OnSecondary = Color(0xFF212121) // Dark Gray
        val Tertiary = Color(0xFFFF8A80) // Soft Coral
        val OnTertiary = Color(0xFF212121) // Dark Gray
        val Background = Color(0xFF161616) // Near Black
        val OnBackground = Color(0xFFE0E0E0) // Light Gray
        val Surface = Color(0xFF212121) // Dark Gray
        val OnSurface = Color(0xFFE0E0E0) // Light Gray
        val SurfaceVariant = Color(0xFF2E2E2E) // Darker Gray
        val OnSurfaceVariant = Color(0xFFE0E0E0) // Light Gray
        val SecondaryContainer = Color(0xFF388E3C) // Forest Green
        val OnSecondaryContainer = Color(0xFFE0E0E0) // Light Gray
        val Error = Color(0xFFFF8989) // Soft Red
        val OnError = Color(0xFF212121) // Dark Gray
    }
}
