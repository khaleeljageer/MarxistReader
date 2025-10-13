package com.jskaleel.android.ui.theme

import androidx.compose.ui.graphics.Color

@Suppress("detekt:MagicNumber")
object AppColor {
    object Light {
        val Primary = Color(0xFF4A90E2) // Deep sky blue for immersion
        val OnPrimary = Color(0xFFFFFFFF) // White for contrast
        val PrimaryContainer = Color(0xFFBBDEFB) // Light blue for containers
        val OnPrimaryContainer = Color(0xFF0D47A1) // Dark blue text on container
        val Secondary = Color(0xFF9C27B0) // Vibrant purple for highlights
        val OnSecondary = Color(0xFFFFFFFF) // White for contrast
        val Tertiary = Color(0xFF303F9F) // Deep indigo for accents
        val OnTertiary = Color(0xFFFFFFFF) // White for contrast
        val Background = Color(0xFFF8F9FA) // Soft off-white to reduce eye strain
        val OnBackground = Color(0xFF212529) // Dark charcoal for text
        val Surface = Color(0xFFFFFFFF) // Clean white for pages
        val OnSurface = Color(0xFF212529) // Dark charcoal for text
        val SurfaceVariant = Color(0xFFF1F3F4) // Subtle light gray variant
        val OnSurfaceVariant = Color(0xFF5F6368) // Medium gray for secondary text
        val SecondaryContainer = Color(0xFFE1BEE7) // Light purple for secondary UI
        val OnSecondaryContainer = Color(0xFF4A148C) // Dark purple text
        val Error = Color(0xFFE74C3C) // Coral red for errors
        val OnError = Color(0xFFFFFFFF) // White for contrast
    }
    object Dark {
        val Primary = Color(0xFF1E40AF) // Darker blue for primary elements
        val OnPrimary = Color(0xFFF0F9FF) // Light off-white for text
        val PrimaryContainer = Color(0xFF5DADE2) // Medium blue for containers
        val OnPrimaryContainer = Color(0xFF90D4FF) // Lighter blue text
        val Secondary = Color(0xFF7B1FA2) // Muted purple for highlights
        val OnSecondary = Color(0xFFF8F9FA) // Warm off-white
        val Tertiary = Color(0xFF283593) // Dark indigo for accents
        val OnTertiary = Color(0xFFF8F9FA) // Warm off-white
        val Background = Color(0xFF121212) // Near-black background
        val OnBackground = Color(0xFFEDEDED) // Light gray for text
        val Surface = Color(0xFF1E1E1E) // Dark gray for surfaces
        val OnSurface = Color(0xFFEDEDED) // Light gray for text
        val SurfaceVariant = Color(0xFF2C2C2C) // Subtle dark variant
        val OnSurfaceVariant = Color(0xFFBDBDBD) // Medium light gray
        val SecondaryContainer = Color(0xFF4A148C) // Dark purple for secondary
        val OnSecondaryContainer = Color(0xFFF8F9FA) // Warm off-white
        val Error = Color(0xFFE57373) // Muted red for errors
        val OnError = Color(0xFFF8F9FA) // Warm off-white for cohesion
    }
}