package org.cpimtn.marxist.android.core.model

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.cpimtn.marxist.android.ui.theme.LocalCustomColors

data class CustomColors(
    val accent: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val readingBackground: Color,
)

val MaterialTheme.customColors: CustomColors
    @Composable
    get() = LocalCustomColors.current
