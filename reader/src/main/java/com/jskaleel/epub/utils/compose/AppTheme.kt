/*
 * Copyright 2022 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package com.jskaleel.epub.utils.compose

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ReaderPrimary = Color(0xFFFFDBC3)
private val ReaderPrimarySurface = Color(0xFFFFF5E0)
private val ReaderOnPrimarySurface = Color(0xFF190933)

// Mirrors ReaderTheme in reader/src/main/res/values/styles.xml so Compose
// bottom sheets (user settings, TTS controls) match the rest of the reader UI.
private val ReaderColorScheme = lightColorScheme(
    primary = ReaderPrimary,
    onPrimary = ReaderOnPrimarySurface,
    secondary = ReaderPrimary,
    onSecondary = ReaderOnPrimarySurface,
    background = ReaderPrimarySurface,
    onBackground = ReaderOnPrimarySurface,
    surface = ReaderPrimarySurface,
    onSurface = ReaderOnPrimarySurface,
)

/**
 * Setup the Compose app-wide theme.
 */
@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ReaderColorScheme,
        content = content
    )
}
