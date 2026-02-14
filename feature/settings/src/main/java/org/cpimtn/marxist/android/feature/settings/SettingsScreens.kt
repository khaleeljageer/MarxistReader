package org.cpimtn.marxist.android.feature.settings

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.cpimtn.marxist.android.domain.model.AppLanguage
import org.cpimtn.marxist.android.domain.model.FontSize
import org.cpimtn.marxist.android.domain.model.Theme
import org.cpimtn.marxist.ui.theme.MarxistExtendedColors
import org.cpimtn.marxist.ui.theme.MarxistReaderTheme

@Stable
data class SettingsUiState(
    val theme: Theme,
    val fontSize: FontSize,
    val language: AppLanguage,
    val pushNotificationsEnabled: Boolean,
    val appVersion: String,
)

@Composable
fun SettingsScreenContent(
    modifier: Modifier = Modifier,
    colors: MarxistExtendedColors = MarxistReaderTheme.colors,
    scrollState: ScrollState = rememberScrollState(),
    uiState: SettingsUiState,
    themeDialogUpdate: (Boolean) -> Unit,
    fontSizeDialogUpdate: (Boolean) -> Unit,
    languageDialogUpdate: (Boolean) -> Unit,
    onPushNotificationStatusChange: (Boolean) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
    ) {
        // ── Display ──
        SectionHeader(
            title = stringResource(R.string.settings_section_display),
            color = colors.settingsGroupTitle
        )
        SettingsRow(
            icon = {
                Icon(
                    imageVector = Icons.Outlined.DarkMode,
                    contentDescription = null,
                    tint = colors.settingsIconTint,
                    modifier = Modifier.size(24.dp)
                )
            },
            title = stringResource(R.string.settings_theme_subtitle),
            subtitle = themeLabel(uiState.theme),
            trailing = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            onClick = { themeDialogUpdate(true) },
        )
        SettingsRow(
            icon = {
                Icon(
                    imageVector = Icons.Outlined.TextFields,
                    contentDescription = null,
                    tint = colors.settingsIconTint,
                    modifier = Modifier.size(24.dp)
                )
            },
            title = "Font Size",
            subtitle = fontSizeLabel(uiState.fontSize),
            trailing = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            onClick = { fontSizeDialogUpdate(true) },
        )
        SettingsRow(
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Translate,
                    contentDescription = null,
                    tint = colors.settingsIconTint,
                    modifier = Modifier.size(24.dp)
                )
            },
            title = "Language",
            subtitle = languageLabel(uiState.language),
            trailing = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            onClick = { languageDialogUpdate(true) },
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        // ── Notifications ──
        SectionHeader(
            title = stringResource(R.string.settings_section_notifications),
            color = colors.settingsGroupTitle
        )
        SettingsRow(
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = null,
                    tint = colors.settingsIconTint,
                    modifier = Modifier.size(24.dp)
                )
            },
            title = "Push Notifications",
            subtitle = "New article notifications",
            trailing = {
                Switch(
                    checked = uiState.pushNotificationsEnabled,
                    onCheckedChange = { onPushNotificationStatusChange(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                        checkedTrackColor = colors.settingsGroupTitle,
                        uncheckedThumbColor = MaterialTheme.colorScheme.surface,
                        uncheckedTrackColor = MaterialTheme.colorScheme.outline,
                    ),
                )
            },
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        // ── About ──
        SectionHeader(
            title = stringResource(R.string.settings_section_about),
            color = colors.settingsGroupTitle
        )
        SettingsRow(
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = colors.settingsIconTint,
                    modifier = Modifier.size(24.dp)
                )
            },
            title = "Version",
            subtitle = "v${uiState.appVersion}",
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.settings_footer_license),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 40.dp),
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.Bold,
            color = color,
        ),
        modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp),
    )
}

@Composable
private fun SettingsRow(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String? = null,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    val modifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon()
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (trailing != null) trailing()
    }
}

private fun themeLabel(theme: Theme): String = when (theme) {
    Theme.LIGHT -> "Light"
    Theme.DARK -> "Dark"
    Theme.DEFAULT -> "Default"
}

private fun fontSizeLabel(fontSize: FontSize): String = when (fontSize) {
    FontSize.SMALL -> "Small"
    FontSize.MEDIUM -> "Medium"
    FontSize.NORMAL -> "Normal"
    FontSize.LARGE -> "Large"
    FontSize.EXTRA_LARGE -> "Extra Large"
}

private fun languageLabel(lang: AppLanguage): String = when (lang) {
    AppLanguage.TAMIL -> "Tamil"
    AppLanguage.ENGLISH -> "English"
}

@Composable
fun ThemeDialog(
    current: Theme,
    onSelect: (Theme) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Theme") },
        text = {
            Column {
                Theme.entries.forEach { theme ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onSelect(theme) }) {
                        RadioButton(selected = current == theme, onClick = { onSelect(theme) })
                        Spacer(Modifier.width(8.dp))
                        Text(
                            when (theme) {
                                Theme.LIGHT -> "Light"; Theme.DARK -> "Dark"; Theme.DEFAULT -> "Default"
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {},
    )
}

@Composable
fun FontSizeDialog(
    current: FontSize,
    onSelect: (FontSize) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Font Size") },
        text = {
            Column {
                FontSize.entries.forEach { size ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onSelect(size) }) {
                        RadioButton(selected = current == size, onClick = { onSelect(size) })
                        Spacer(Modifier.width(8.dp))
                        Text(fontSizeLabel(size))
                    }
                }
            }
        },
        confirmButton = {},
    )
}

@Composable
fun LanguageDialog(
    current: AppLanguage,
    onSelect: (AppLanguage) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Language") },
        text = {
            Column {
                AppLanguage.entries.forEach { lang ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onSelect(lang) }) {
                        RadioButton(selected = current == lang, onClick = { onSelect(lang) })
                        Spacer(Modifier.width(8.dp))
                        Text(languageLabel(lang))
                    }
                }
            }
        },
        confirmButton = {},
    )
}
