package org.cpimtn.marxist.android.ui.theme

import android.app.Activity
import android.os.Build
import android.view.View
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import org.cpimtn.marxist.android.core.model.CustomColors

private val LightColorScheme = lightColorScheme(
    primary = AppColor.Light.Primary,
    onPrimary = AppColor.Light.OnPrimary,
    secondary = AppColor.Light.Secondary,
    onSecondary = AppColor.Light.OnSecondary,
    background = AppColor.Light.Background,
    onBackground = AppColor.Light.OnBackground,
    surface = AppColor.Light.Surface,
    onSurface = AppColor.Light.OnSurface,
    error = AppColor.Light.Error,
    onError = AppColor.Light.OnError,
    primaryContainer = AppColor.Light.PrimaryContainer,
    onPrimaryContainer = AppColor.Light.OnPrimary,
)

private val DarkColorScheme = lightColorScheme(
    primary = AppColor.Dark.Primary,
    onPrimary = AppColor.Dark.OnPrimary,
    secondary = AppColor.Dark.Secondary,
    onSecondary = AppColor.Dark.OnSecondary,
    background = AppColor.Dark.Background,
    onBackground = AppColor.Dark.OnBackground,
    surface = AppColor.Dark.Surface,
    onSurface = AppColor.Dark.OnSurface,
    error = AppColor.Dark.Error,
    onError = AppColor.Dark.OnError,
    primaryContainer = AppColor.Dark.PrimaryContainer,
    onPrimaryContainer = AppColor.Dark.OnPrimary,
)

fun getCustomColor(darkTheme: Boolean): CustomColors {
    return if (darkTheme) {
        CustomColors(
            accent = AppColor.Dark.Accent,
            textPrimary = AppColor.Dark.TextPrimary,
            textSecondary = AppColor.Dark.TextSecondary,
            readingBackground = AppColor.Dark.Background,
        )
    } else {
        CustomColors(
            accent = AppColor.Light.Accent,
            textPrimary = AppColor.Light.TextPrimary,
            textSecondary = AppColor.Light.TextSecondary,
            readingBackground = AppColor.Light.Background,
        )
    }
}

@Composable
fun MarxistReaderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }
    val customColors = getCustomColor(darkTheme)
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            window.decorView.importantForAutofill =
                View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.isNavigationBarContrastEnforced = false
            }

            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
        }
    }

    CompositionLocalProvider(LocalCustomColors provides customColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
