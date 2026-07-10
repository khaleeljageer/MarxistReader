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

// Warm terracotta accent used to mark the active choice in the settings sheets
// (user settings, TTS controls) — the only place `primary` is referenced today.
private val ReaderAccent = Color(0xFFC15B3D)
private val ReaderOnAccent = Color(0xFFFFFFFF)
private val ReaderAccentContainer = Color(0xFFF4DCCB)
private val ReaderOnAccentContainer = Color(0xFF5C2C16)

// Card surface used to group related settings, and its muted label/border tones.
private val ReaderCard = Color(0xFFF6ECDD)
private val ReaderOnCardMuted = Color(0xFF8A7F6E)
private val ReaderOutline = Color(0xFFE6D8C3)

// Mirrors ReaderTheme in reader/src/main/res/values/styles.xml so Compose
// bottom sheets (user settings, TTS controls) match the rest of the reader UI.
private val ReaderColorScheme = lightColorScheme(
    primary = ReaderAccent,
    onPrimary = ReaderOnAccent,
    primaryContainer = ReaderAccentContainer,
    onPrimaryContainer = ReaderOnAccentContainer,
    secondary = ReaderPrimary,
    onSecondary = ReaderOnPrimarySurface,
    background = ReaderPrimarySurface,
    onBackground = ReaderOnPrimarySurface,
    surface = ReaderPrimarySurface,
    onSurface = ReaderOnPrimarySurface,
    surfaceVariant = ReaderCard,
    onSurfaceVariant = ReaderOnCardMuted,
    outline = ReaderOutline,
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
