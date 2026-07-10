/*
 * Copyright 2022 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

@file:OptIn(ExperimentalReadiumApi::class)

package com.jskaleel.epub.reader.preferences

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
import com.jskaleel.epub.shared.views.LanguageItem
import com.jskaleel.epub.shared.views.MenuItem
import com.jskaleel.epub.shared.views.StepperItem
import com.jskaleel.epub.shared.views.SwitchItem
import org.readium.navigator.media.tts.android.AndroidTtsEngine
import org.readium.r2.navigator.epub.EpubPreferencesEditor
import org.readium.r2.navigator.preferences.Axis
import org.readium.r2.navigator.preferences.Color
import org.readium.r2.navigator.preferences.Configurable
import org.readium.r2.navigator.preferences.EnumPreference
import org.readium.r2.navigator.preferences.Fit
import org.readium.r2.navigator.preferences.FontFamily
import org.readium.r2.navigator.preferences.Preference
import org.readium.r2.navigator.preferences.PreferencesEditor
import org.readium.r2.navigator.preferences.RangePreference
import org.readium.r2.navigator.preferences.Spread
import org.readium.r2.navigator.preferences.Theme
import org.readium.r2.navigator.preferences.withSupportedValues
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.epub.EpubLayout
import org.readium.r2.shared.util.Language
import org.readium.r2.navigator.preferences.TextAlign as ReadiumTextAlign

/**
 * Stateful user settings component paired with a [ReaderViewModel].
 */
@Composable
fun UserPreferences(
    model: UserPreferencesViewModel<*, *>,
    title: String,
) {
    val editor by model.editor.collectAsState()

    UserPreferences(
        editor = editor,
        commit = model::commit,
        title = title
    )
}

@Composable
private fun <P : Configurable.Preferences<P>, E : PreferencesEditor<P>> UserPreferences(
    editor: E,
    commit: () -> Unit,
    title: String,
) {
    Column(
        modifier = Modifier.padding(vertical = 24.dp)
    ) {
        Text(
            text = title,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
        )

        Divider()

        when (editor) {
            is EpubPreferencesEditor ->
                when (editor.layout) {
                    EpubLayout.REFLOWABLE ->
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

                    EpubLayout.FIXED ->
                        FixedLayoutUserPreferences(
                            commit = commit,
                            backgroundColor = editor.backgroundColor,
                            spread = editor.spread
                        )
                }

            is TtsPreferencesEditor ->
                MediaUserPreferences(
                    commit = commit,
                    language = editor.language,
                    voice = editor.voice,
                    speed = editor.speed,
                    pitch = editor.pitch
                )
        }
    }
}

@Composable
private fun MediaUserPreferences(
    commit: () -> Unit,
    language: Preference<Language?>? = null,
    voice: EnumPreference<AndroidTtsEngine.Voice.Id?>? = null,
    speed: RangePreference<Double>? = null,
    pitch: RangePreference<Double>? = null,
) {
    Column {
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
        if (language != null) {
            LanguageItem(
                preference = language,
                commit = commit
            )
        }

        if (voice != null) {
            MenuItem(
                title = stringResource(R.string.tts_voice),
                preference = voice,
                formatValue = { it?.value ?: "Default" },
                commit = commit
            )
        }
    }
}

/**
 * User settings for a publication with a fixed layout, such as fixed-layout EPUB, PDF or comic book.
 */
@Composable
private fun FixedLayoutUserPreferences(
    commit: () -> Unit,
    backgroundColor: Preference<Color>? = null,
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

        Divider()
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
    fontFamily: Preference<FontFamily?>? = null,
    fontSize: RangePreference<Double>? = null,
    letterSpacing: RangePreference<Double>? = null,
    lineHeight: RangePreference<Double>? = null,
    pageMargins: RangePreference<Double>? = null,
    textAlign: EnumPreference<ReadiumTextAlign?>? = null,
    theme: EnumPreference<Theme>? = null,
) {
    if (pageMargins != null) {
        StepperItem(
            title = "Page margins",
            preference = pageMargins,
            commit = commit
        )

        Divider()
    }

    if (theme != null) {
        ButtonGroupItem(
            title = "Theme",
            preference = theme,
            commit = commit
        ) { value ->
            when (value) {
                Theme.LIGHT -> "Light"
                Theme.DARK -> "Dark"
                Theme.SEPIA -> "Sepia"
            }
        }

        Divider()
    }

    if (fontFamily != null || fontSize != null) {
        if (fontFamily != null) {
            MenuItem(
                title = "Typeface",
                preference = fontFamily
                    .withSupportedValues(
                        null,
                        FontFamily.ARIMA_MADURAI,
                        FontFamily.HIND_MADURAI,
                        FontFamily.LOHIT_TAMIL,
                        FontFamily.MUKTA_MALAR
                    ),
                commit = commit
            ) { value ->
                when (value) {
                    null -> "Original"
                    else -> value.name
                }
            }
        }

        if (fontSize != null) {
            StepperItem(
                title = "Font size",
                preference = fontSize,
                commit = commit
            )
        }

        Divider()
    }

    if (textAlign != null || lineHeight != null || letterSpacing != null) {
        if (textAlign != null) {
            ButtonGroupItem(
                title = "Alignment",
                preference = textAlign,
                commit = commit
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

        if (lineHeight != null) {
            StepperItem(
                title = "Line height",
                preference = lineHeight,
                commit = commit
            )
        }

        if (letterSpacing != null) {
            StepperItem(
                title = "Letter spacing",
                preference = letterSpacing,
                commit = commit
            )
        }
    }
}

@Composable
private fun Divider() {
    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
}
