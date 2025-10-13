package com.jskaleel.android.ui.theme

import androidx.compose.ui.graphics.Color

@Suppress("detekt:MagicNumber")
object AppColor {
    object Light {
        val Primary = Color(0xFFC62828) // Deep Marxist red for brand identity
        val OnPrimary = Color(0xFFFFFFFF) // White for clear contrast
        val PrimaryContainer = Color(0xFFF2B8B5) // Soft red tint for containers
        val OnPrimaryContainer = Color(0xFF410002) // Deep tone for container text

        val Secondary = Color(0xFF607D8B) // Calm slate blue for highlights
        val OnSecondary = Color(0xFFFFFFFF) // White text on secondary
        val SecondaryContainer = Color(0xFFCFD8DC) // Light slate for cards
        val OnSecondaryContainer = Color(0xFF263238) // Deep slate text

        val Tertiary = Color(0xFFFFD54F) // Warm golden accent for highlights
        val OnTertiary = Color(0xFF4E3B00) // Deep brown for legibility

        val Background = Color(0xFFFFF8F5) // Gentle parchment tone for reading comfort
        val OnBackground = Color(0xFF1A1A1A) // Deep gray Tamil text
        val Surface = Color(0xFFFFFFFF) // Clean white surfaces
        val OnSurface = Color(0xFF1A1A1A) // Text on surface

        val SurfaceVariant = Color(0xFFF1F3F4) // Light gray for variant surfaces
        val OnSurfaceVariant = Color(0xFF5F6368) // Medium gray for secondary text

        val Error = Color(0xFFE74C3C) // Coral red for errors
        val OnError = Color(0xFFFFFFFF) // White for contrast
    }

    object Dark {
        val Primary = Color(0xFFEF5350) // Soft luminous red for dark mode accents
        val OnPrimary = Color(0xFFFFFFFF) // White for contrast
        val PrimaryContainer = Color(0xFF7F0000) // Deep red container
        val OnPrimaryContainer = Color(0xFFFFDAD6) // Soft pinkish text on red container

        val Secondary = Color(0xFF90A4AE) // Muted slate blue for balance
        val OnSecondary = Color(0xFF121212) // Text on secondary
        val SecondaryContainer = Color(0xFF37474F) // Dark slate for surfaces
        val OnSecondaryContainer = Color(0xFFECEFF1) // Light gray text

        val Tertiary = Color(0xFFFFD54F) // Golden accent consistent with light mode
        val OnTertiary = Color(0xFF2E2E2E) // Slightly darker contrast for readability

        val Background = Color(0xFF121212) // AMOLED-safe deep gray
        val OnBackground = Color(0xFFEDEDED) // Light gray Tamil text
        val Surface = Color(0xFF1E1E1E) // Dark gray reading surface
        val OnSurface = Color(0xFFEDEDED) // Text color on surface

        val SurfaceVariant = Color(0xFF2C2C2C) // Slight variant for elevated cards
        val OnSurfaceVariant = Color(0xFFBDBDBD) // Muted gray secondary text

        val Error = Color(0xFFE57373) // Muted error red for dark theme
        val OnError = Color(0xFFFFFFFF) // White text for errors
    }
}