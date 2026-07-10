/*
 * Copyright 2022 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

@file:OptIn(ExperimentalReadiumApi::class)

package com.jskaleel.epub.reader.preferences

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.automirrored.filled.FormatAlignRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.FormatAlignJustify
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jskaleel.epub.R
import com.jskaleel.epub.reader.ARIMA_MADURAI
import com.jskaleel.epub.reader.HIND_MADURAI
import com.jskaleel.epub.reader.LOHIT_TAMIL
import com.jskaleel.epub.reader.MUKTA_MALAR
import com.jskaleel.epub.reader.tts.TtsPreferencesEditor
import com.jskaleel.epub.shared.views.ButtonGroupItem
import com.jskaleel.epub.shared.views.ColorItem
import com.jskaleel.epub.shared.views.InlineStepperRow
import com.jskaleel.epub.shared.views.SettingsSection
import com.jskaleel.epub.shared.views.StepperItem
import com.jskaleel.epub.shared.views.SwitchItem
import org.readium.navigator.media.tts.android.AndroidTtsEngine
import org.readium.r2.navigator.epub.EpubPreferencesEditor
import org.readium.r2.navigator.preferences.Axis
import org.readium.r2.navigator.preferences.Configurable
import org.readium.r2.navigator.preferences.EnumPreference
import org.readium.r2.navigator.preferences.Fit
import org.readium.r2.navigator.preferences.Preference
import org.readium.r2.navigator.preferences.PreferencesEditor
import org.readium.r2.navigator.preferences.RangePreference
import org.readium.r2.navigator.preferences.Spread
import org.readium.r2.navigator.preferences.Theme
import org.readium.r2.navigator.preferences.clear
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.Layout
import org.readium.r2.navigator.preferences.Color as ReadiumColor
import org.readium.r2.navigator.preferences.FontFamily as ReadiumFontFamily
import org.readium.r2.navigator.preferences.TextAlign as ReadiumTextAlign

/**
 * Stateful user settings component paired with a [ReaderViewModel].
 */
@Composable
fun UserPreferences(
    model: UserPreferencesViewModel<*, *>,
    title: String,
    onDismiss: () -> Unit = {},
) {
    val editor by model.editor.collectAsState()

    UserPreferences(
        editor = editor,
        commit = model::commit,
        title = title,
        onReset = {
            editor.clear()
            model.commit()
        },
        onDismiss = onDismiss
    )
}

@Composable
private fun <P : Configurable.Preferences<P>, E : PreferencesEditor<P>> UserPreferences(
    editor: E,
    commit: () -> Unit,
    title: String,
    onReset: () -> Unit,
    onDismiss: () -> Unit,
) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        SettingsSheetHeader(title = title, onReset = onReset, onDismiss = onDismiss)

        Column(
            modifier = Modifier.padding(top = 14.dp, start = 14.dp, end = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            when (editor) {
                is EpubPreferencesEditor ->
                    when (editor.layout) {
                        Layout.REFLOWABLE ->
                            ReflowableUserPreferences(
                                commit = commit,
                                fontFamily = editor.fontFamily,
                                fontSize = editor.fontSize,
                                letterSpacing = editor.letterSpacing,
                                lineHeight = editor.lineHeight,
                                pageMargins = editor.pageMargins,
                                textAlign = editor.textAlign,
                                theme = editor.theme
                            )

                        Layout.FIXED ->
                            FixedLayoutUserPreferences(
                                commit = commit,
                                backgroundColor = editor.backgroundColor,
                                spread = editor.spread
                            )

                        Layout.SCROLLED -> {}
                    }

                is TtsPreferencesEditor ->
                    MediaUserPreferences(
                        commit = commit,
                        voice = editor.voice,
                        voiceLabel = editor::voiceLabel,
                        speed = editor.speed,
                        pitch = editor.pitch
                    )
            }
        }
    }
}

/**
 * Drag handle + centered title + reset/close actions, matching the Material3 bottom-sheet
 * convention instead of a bare heading.
 */
@Composable
private fun SettingsSheetHeader(
    title: String,
    onReset: () -> Unit,
    onDismiss: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .padding(top = 10.dp)
                .align(Alignment.CenterHorizontally)
                .size(width = 36.dp, height = 4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 4.dp, top = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onReset) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.settings_reset)
                )
            }

            Text(
                text = title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.settings_close)
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = 8.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun MediaUserPreferences(
    commit: () -> Unit,
    voice: EnumPreference<AndroidTtsEngine.Voice.Id?>? = null,
    voiceLabel: (AndroidTtsEngine.Voice.Id) -> String = { it.value },
    speed: RangePreference<Double>? = null,
    pitch: RangePreference<Double>? = null,
) {
    if (speed != null) {
        StepperItem(
            title = stringResource(R.string.speed_rate),
            preference = speed,
            commit = commit
        )
    }

    if (pitch != null) {
        StepperItem(
            title = stringResource(R.string.pitch_rate),
            preference = pitch,
            commit = commit
        )
    }

    if (voice != null) {
        ButtonGroupItem(
            title = stringResource(R.string.tts_voice),
            preference = voice,
            commit = commit,
            formatValue = { it?.let(voiceLabel) ?: "Default" }
        )
    }
}

/**
 * User settings for a publication with a fixed layout, such as fixed-layout EPUB, PDF or comic book.
 */
@Composable
private fun FixedLayoutUserPreferences(
    commit: () -> Unit,
    backgroundColor: Preference<ReadiumColor>? = null,
    scroll: Preference<Boolean>? = null,
    scrollAxis: EnumPreference<Axis>? = null,
    fit: EnumPreference<Fit>? = null,
    spread: EnumPreference<Spread>? = null,
    offsetFirstPage: Preference<Boolean>? = null,
    pageSpacing: RangePreference<Double>? = null,
) {
    if (backgroundColor != null) {
        ColorItem(
            title = "Background color",
            preference = backgroundColor,
            commit = commit
        )
    }

    if (scroll != null) {
        SwitchItem(
            title = "Scroll",
            preference = scroll,
            commit = commit
        )
    }

    if (scrollAxis != null) {
        ButtonGroupItem(
            title = "Scroll axis",
            preference = scrollAxis,
            commit = commit
        ) { value ->
            when (value) {
                Axis.HORIZONTAL -> "Horizontal"
                Axis.VERTICAL -> "Vertical"
            }
        }
    }

    if (spread != null) {
        ButtonGroupItem(
            title = "Spread",
            preference = spread,
            commit = commit
        ) { value ->
            when (value) {
                Spread.AUTO -> "Auto"
                Spread.NEVER -> "Never"
                Spread.ALWAYS -> "Always"
            }
        }

        if (offsetFirstPage != null) {
            SwitchItem(
                title = "Offset",
                preference = offsetFirstPage,
                commit = commit
            )
        }
    }

    if (fit != null) {
        ButtonGroupItem(
            title = "Fit",
            preference = fit,
            commit = commit
        ) { value ->
            when (value) {
                Fit.CONTAIN -> "Contain"
                Fit.COVER -> "Cover"
                Fit.WIDTH -> "Width"
                Fit.HEIGHT -> "Height"
            }
        }
    }

    if (pageSpacing != null) {
        StepperItem(
            title = "Page spacing",
            preference = pageSpacing,
            commit = commit
        )
    }
}

/**
 * User settings for a publication with adjustable fonts and dimensions, such as
 * a reflowable EPUB, HTML document or PDF with reflow mode enabled.
 */
@Composable
private fun ReflowableUserPreferences(
    commit: () -> Unit,
    fontFamily: Preference<ReadiumFontFamily?>? = null,
    fontSize: RangePreference<Double>? = null,
    letterSpacing: RangePreference<Double>? = null,
    lineHeight: RangePreference<Double>? = null,
    pageMargins: RangePreference<Double>? = null,
    textAlign: EnumPreference<ReadiumTextAlign?>? = null,
    theme: EnumPreference<Theme>? = null,
) {
    if (theme != null) {
        ThemeItem(preference = theme, commit = commit)
    }

    if (fontSize != null) {
        StepperItem(
            title = stringResource(R.string.settings_font_size),
            preference = fontSize,
            commit = commit
        )
    }

    if (fontFamily != null) {
        TypefaceItem(preference = fontFamily, commit = commit)
    }

    if (lineHeight != null || letterSpacing != null) {
        SettingsSection(label = stringResource(R.string.settings_spacing)) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (lineHeight != null) {
                    InlineStepperRow(
                        title = stringResource(R.string.settings_line_height),
                        preference = lineHeight,
                        commit = commit
                    )
                }

                if (letterSpacing != null) {
                    InlineStepperRow(
                        title = stringResource(R.string.settings_letter_spacing),
                        preference = letterSpacing,
                        commit = commit
                    )
                }
            }
        }
    }

    if (textAlign != null) {
        ButtonGroupItem(
            title = stringResource(R.string.settings_alignment),
            preference = textAlign,
            commit = commit,
            icon = { value ->
                when (value) {
                    ReadiumTextAlign.JUSTIFY -> Icons.Default.FormatAlignJustify
                    ReadiumTextAlign.END, ReadiumTextAlign.RIGHT -> Icons.AutoMirrored.Filled.FormatAlignRight
                    ReadiumTextAlign.CENTER -> Icons.Default.FormatAlignCenter
                    ReadiumTextAlign.START, ReadiumTextAlign.LEFT, null -> Icons.AutoMirrored.Filled.FormatAlignLeft
                }
            }
        ) { value ->
            when (value) {
                ReadiumTextAlign.CENTER -> "Center"
                ReadiumTextAlign.JUSTIFY -> "Justify"
                ReadiumTextAlign.START -> "Start"
                ReadiumTextAlign.END -> "End"
                ReadiumTextAlign.LEFT -> "Left"
                ReadiumTextAlign.RIGHT -> "Right"
                null -> "Default"
            }
        }
    }

    if (pageMargins != null) {
        StepperItem(
            title = stringResource(R.string.settings_page_margins),
            preference = pageMargins,
            commit = commit
        )
    }
}

/**
 * Theme choice shown as color swatches (rather than text pills) so the effect of each option
 * is visible at a glance.
 */
@Composable
private fun ThemeItem(
    preference: EnumPreference<Theme>,
    commit: () -> Unit,
) {
    val selected = preference.value ?: preference.effectiveValue

    SettingsSection(label = stringResource(R.string.settings_theme)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            for (option in preference.supportedValues) {
                ThemeSwatch(
                    label = themeLabel(option),
                    swatchColor = themeSwatchColor(option),
                    isSelected = selected == option,
                    enabled = preference.isEffective,
                    onClick = {
                        if (option == preference.value) {
                            preference.clear()
                        } else {
                            preference.set(option)
                        }
                        commit()
                    }
                )
            }
        }
    }
}

@Composable
private fun themeLabel(theme: Theme): String = when (theme) {
    Theme.LIGHT -> stringResource(R.string.theme_light)
    Theme.DARK -> stringResource(R.string.theme_dark)
    Theme.SEPIA -> stringResource(R.string.theme_sepia)
}

private fun themeSwatchColor(theme: Theme): Color = when (theme) {
    Theme.LIGHT -> Color(0xFFFCFBF7)
    Theme.DARK -> Color(0xFF1B1B1D)
    Theme.SEPIA -> Color(0xFFF2E6CD)
}

@Composable
private fun RowScope.ThemeSwatch(
    label: String,
    swatchColor: Color,
    isSelected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clickable(enabled = enabled, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(swatchColor)
                .border(
                    width = if (isSelected) 3.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    shape = CircleShape
                )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Typeface choice shown as a row of text-only pills naming each font, wrapping onto multiple
 * lines instead of scrolling or clipping long names off the edge.
 */
@Composable
private fun TypefaceItem(
    preference: Preference<ReadiumFontFamily?>,
    commit: () -> Unit,
) {
    val options = remember {
        listOf(
            null,
            ReadiumFontFamily.ARIMA_MADURAI,
            ReadiumFontFamily.HIND_MADURAI,
            ReadiumFontFamily.LOHIT_TAMIL,
            ReadiumFontFamily.MUKTA_MALAR
        )
    }
    val originalLabel = stringResource(R.string.settings_typeface_original)
    val selected = preference.value ?: preference.effectiveValue

    SettingsSection(label = stringResource(R.string.settings_typeface)) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            for (option in options) {
                TypefaceTile(
                    label = option?.name ?: originalLabel,
                    isSelected = selected == option,
                    onClick = {
                        if (option == preference.value) {
                            preference.clear()
                        } else {
                            preference.set(option)
                        }
                        commit()
                    }
                )
            }
        }
    }
}

@Composable
private fun TypefaceTile(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(13.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        )
    }
}