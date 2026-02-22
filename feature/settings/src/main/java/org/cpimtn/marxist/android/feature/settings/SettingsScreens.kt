package org.cpimtn.marxist.android.feature.settings

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.cpimtn.marxist.android.domain.model.AppLanguage
import org.cpimtn.marxist.android.domain.model.FontSize
import org.cpimtn.marxist.android.domain.model.Theme
import org.cpimtn.marxist.core.clickableIf
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
        SettingsItemRow(
            icon = Icons.Outlined.DarkMode,
            iconBgColor = colors.settingsIconBg,
            title = stringResource(R.string.settings_theme_subtitle),
            subtitle = themeLabel(uiState.theme),
            showTrailingArrow = true,
            onClick = { themeDialogUpdate(true) },
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

        SettingsItemRow(
            icon = Icons.Outlined.TextFields,
            iconBgColor = colors.settingsIconBg,
            title = stringResource(R.string.settings_font_size),
            subtitle = fontSizeLabel(uiState.fontSize),
            showTrailingArrow = true,
            onClick = { fontSizeDialogUpdate(true) },
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

        SettingsItemRow(
            icon = Icons.Outlined.Language,
            iconBgColor = colors.settingsIconBg,
            title = stringResource(R.string.settings_language),
            subtitle = languageLabel(uiState.language),
            showTrailingArrow = true,
            onClick = { languageDialogUpdate(true) },
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

        Spacer(modifier = Modifier.height(24.dp))
        // ── Notifications ──
        SectionHeader(
            title = stringResource(R.string.settings_section_notifications),
            color = colors.settingsGroupTitle
        )


        SettingsItemRow(
            icon = Icons.Outlined.Notifications,
            iconBgColor = colors.settingsIconBg,
            title = stringResource(R.string.settings_push_notifications),
            subtitle = stringResource(R.string.settings_push_notifications_subtitle),
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

        Spacer(modifier = Modifier.height(24.dp))
        // ── About ──
        SectionHeader(
            title = stringResource(R.string.settings_section_about),
            color = colors.settingsGroupTitle
        )

        SettingsItemRow(
            icon = Icons.Outlined.Info,
            iconBgColor = colors.settingsIconBg,
            title = stringResource(R.string.settings_version),
            subtitle = "v${uiState.appVersion}",
        )

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(R.string.settings_footer_license),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
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
private fun SettingsItemRow(
    icon: ImageVector,
    iconBgColor: Color,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    showTrailingArrow: Boolean = false,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    ListItem(
        modifier = modifier.then(Modifier.clickableIf(onClick != null) { onClick?.invoke() }),
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
        supportingContent = {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        trailingContent = when {
            trailing != null -> trailing
            showTrailingArrow -> {
                {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            else -> null
        },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface,
            headlineColor = MaterialTheme.colorScheme.onSurface,
            supportingColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
    )
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
            Column(
                modifier = Modifier.wrapContentSize()
            ) {
                Theme.entries.forEach { theme ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(theme) }) {
                        RadioButton(selected = current == theme, onClick = { onSelect(theme) })
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = when (theme) {
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
            Column(
                modifier = Modifier.wrapContentSize()
            ) {
                FontSize.entries.forEach { size ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(size) }) {
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
            Column(
                modifier = Modifier.wrapContentSize()
            ) {
                AppLanguage.entries.forEach { lang ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(lang) }) {
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
