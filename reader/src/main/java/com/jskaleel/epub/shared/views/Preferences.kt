/*
 * Copyright 2022 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

@file:OptIn(ExperimentalReadiumApi::class)

package com.jskaleel.epub.shared.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.jskaleel.epub.utils.compose.ColorPicker
import com.jskaleel.epub.utils.compose.DropdownMenuButton
import org.readium.r2.navigator.preferences.EnumPreference
import org.readium.r2.navigator.preferences.Preference
import org.readium.r2.navigator.preferences.RangePreference
import org.readium.r2.navigator.preferences.clear
import org.readium.r2.navigator.preferences.toggle
import org.readium.r2.navigator.preferences.withSupportedValues
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.util.Language
import java.util.Locale
import org.readium.r2.navigator.preferences.Color as ReadiumColor

/**
 * Rounded card grouping a single setting: a small muted uppercase label followed by its control.
 * Every preference in the sheet lives in one of these, so the sheet reads as a set of
 * self-contained, scannable groups instead of a flat list of rows.
 */
@Composable
fun SettingsSection(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(14.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.6.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        content()
    }
}

/**
 * Component for an [EnumPreference] displayed as a group of mutually exclusive chips that
 * wrap onto multiple lines instead of overflowing — fixes long option groups (e.g. 4-way
 * alignment) squeezing their label into a vertical, unreadable column.
 */
@Composable
fun <T> ButtonGroupItem(
    title: String,
    preference: EnumPreference<T>,
    commit: () -> Unit,
    icon: ((T) -> ImageVector)? = null,
    formatValue: (T) -> String,
) {
    ButtonGroupItem(
        title = title,
        options = preference.supportedValues,
        isActive = preference.isEffective,
        activeOption = preference.effectiveValue,
        selectedOption = preference.value,
        formatValue = formatValue,
        icon = icon,
        onSelectedOptionChanged = { newValue ->
            if (newValue == preference.value) {
                preference.clear()
            } else {
                preference.set(newValue)
            }
            commit()
        }
    )
}

@Composable
private fun <T> ButtonGroupItem(
    title: String,
    options: List<T>,
    isActive: Boolean,
    activeOption: T,
    selectedOption: T?,
    icon: ((T) -> ImageVector)?,
    formatValue: (T) -> String,
    onSelectedOptionChanged: (T) -> Unit,
) {
    SettingsSection(label = title) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val current = selectedOption ?: activeOption
            for (option in options) {
                SegmentedChip(
                    selected = current == option,
                    enabled = isActive,
                    label = formatValue(option),
                    icon = icon?.invoke(option),
                    onClick = { onSelectedOptionChanged(option) }
                )
            }
        }
    }
}

@Composable
private fun SegmentedChip(
    selected: Boolean,
    enabled: Boolean,
    label: String,
    icon: ImageVector?,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(13.dp),
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        border = if (selected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = label, modifier = Modifier.widthIn(min = 20.dp))
            } else {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * Component for an [EnumPreference] displayed as a dropdown menu.
 */
@Composable
fun <T> MenuItem(
    title: String,
    preference: EnumPreference<T>,
    commit: () -> Unit,
    formatValue: (T) -> String,
) {
    MenuItem(
        title = title,
        value = preference.value ?: preference.effectiveValue,
        values = preference.supportedValues,
        formatValue = formatValue,
        onValueChanged = { value ->
            preference.set(value)
            commit()
        }
    )
}

@Composable
private fun <T> MenuItem(
    title: String,
    value: T,
    values: List<T>,
    formatValue: (T) -> String,
    onValueChanged: (T) -> Unit,
) {
    SettingsSection(label = title) {
        DropdownMenuButton(
            text = {
                Text(
                    text = formatValue(value),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        ) { dismiss ->
            for (aValue in values) {
                DropdownMenuItem(
                    text = { Text(formatValue(aValue)) },
                    onClick = {
                        dismiss()
                        onValueChanged(aValue)
                    }
                )
            }
        }
    }
}

/**
 * Component for a [RangePreference] with decrement/increment controls, shown as its own card.
 */
@Composable
fun <T : Comparable<T>> StepperItem(
    title: String,
    preference: RangePreference<T>,
    commit: () -> Unit,
) {
    SettingsSection(label = title) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            StepperControls(
                value = preference.formatValue(preference.value ?: preference.effectiveValue),
                enabled = preference.isEffective,
                onDecrement = {
                    preference.decrement()
                    commit()
                },
                onIncrement = {
                    preference.increment()
                    commit()
                }
            )
        }
    }
}

/**
 * A compact variant of [StepperItem] for grouping several related range preferences
 * (e.g. line height + letter spacing) inside one shared [SettingsSection].
 */
@Composable
fun <T : Comparable<T>> InlineStepperRow(
    title: String,
    preference: RangePreference<T>,
    commit: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        StepperControls(
            value = preference.formatValue(preference.value ?: preference.effectiveValue),
            enabled = preference.isEffective,
            buttonSize = 34.dp,
            onDecrement = {
                preference.decrement()
                commit()
            },
            onIncrement = {
                preference.increment()
                commit()
            }
        )
    }
}

@Composable
private fun StepperControls(
    value: String,
    enabled: Boolean,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    buttonSize: Dp = 40.dp,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StepButton(
            icon = Icons.Default.Remove,
            contentDescription = "Decrease",
            enabled = enabled,
            size = buttonSize,
            onClick = onDecrement
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(min = 40.dp)
        )
        StepButton(
            icon = Icons.Default.Add,
            contentDescription = "Increase",
            enabled = enabled,
            size = buttonSize,
            onClick = onIncrement
        )
    }
}

@Composable
private fun StepButton(
    icon: ImageVector,
    contentDescription: String,
    enabled: Boolean,
    size: Dp,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = Modifier.widthIn(min = size)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = contentDescription)
        }
    }
}

/**
 * Component for a boolean [Preference].
 */
@Composable
fun SwitchItem(
    title: String,
    preference: Preference<Boolean>,
    commit: () -> Unit,
) {
    SwitchItem(
        title = title,
        value = preference.value ?: preference.effectiveValue,
        isActive = preference.isEffective,
        onCheckedChange = {
            preference.set(it)
            commit()
        },
        onToggle = {
            preference.toggle()
            commit()
        }
    )
}

@Composable
private fun SwitchItem(
    title: String,
    value: Boolean,
    isActive: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onToggle: () -> Unit,
) {
    SettingsSection(label = title) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = isActive, onClick = onToggle),
            horizontalArrangement = Arrangement.End
        ) {
            Switch(
                checked = value,
                enabled = isActive,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

/**
 * Component for a [Preference<ReadiumColor>].
 */
@Composable
fun ColorItem(
    title: String,
    preference: Preference<ReadiumColor>,
    commit: () -> Unit,
) {
    ColorItem(
        title = title,
        isActive = preference.isEffective,
        value = preference.value ?: preference.effectiveValue,
        noValueSelected = preference.value == null,
        onColorChanged = {
            preference.set(it)
            commit()
        }
    )
}

@Composable
private fun ColorItem(
    title: String,
    isActive: Boolean,
    value: ReadiumColor,
    noValueSelected: Boolean,
    onColorChanged: (ReadiumColor?) -> Unit,
) {
    var isPicking by remember { mutableStateOf(false) }

    SettingsSection(label = title) {
        val color = Color(value.int)

        OutlinedButton(
            onClick = { isPicking = true },
            enabled = isActive,
            colors = ButtonDefaults.buttonColors(containerColor = color)
        ) {
            if (noValueSelected) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = "Change color",
                    tint = if (color.luminance() > 0.5) Color.Black else Color.White
                )
            }
        }

        if (isPicking) {
            Dialog(
                onDismissRequest = { isPicking = false }
            ) {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    ColorPicker { color ->
                        isPicking = false
                        onColorChanged(ReadiumColor(color))
                    }
                    Button(
                        onClick = {
                            isPicking = false
                            onColorChanged(null)
                        }
                    ) {
                        Text("Clear")
                    }
                }
            }
        }
    }
}

/**
 * Component for a [Preference<Language?>]`.
 */
@Composable
fun LanguageItem(
    preference: Preference<Language?>,
    commit: () -> Unit,
) {
    val languages = remember {
        Locale.getAvailableLocales()
            .map { Language(it).removeRegion() }
            .distinct()
            .sortedBy { it.locale.displayName }
    }

    MenuItem(
        title = "Language",
        preference = preference.withSupportedValues(languages + null),
        formatValue = { it?.locale?.displayName ?: "Unknown" },
        commit = commit
    )
}
