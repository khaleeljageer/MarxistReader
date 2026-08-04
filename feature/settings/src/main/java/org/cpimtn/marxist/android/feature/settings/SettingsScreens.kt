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
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PrivacyTip
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.cpimtn.marxist.android.domain.model.AppLanguage
import org.cpimtn.marxist.android.domain.model.Theme
import org.cpimtn.marxist.core.clickableIf
import org.cpimtn.marxist.core.config.AppConfig
import org.cpimtn.marxist.android.ui.theme.MarxistExtendedColors
import org.cpimtn.marxist.android.ui.theme.MarxistReaderTheme

private const val SOURCE_CODE_URL = "https://github.com/khaleeljageer/MarxistReader/"
private const val PRIVACY_POLICY_URL = AppConfig.Site.PRIVACY_POLICY_URL
private const val TERMS_CONDITIONS_URL = AppConfig.Site.TERMS_CONDITIONS_URL

@Stable
data class SettingsUiState(
    val theme: Theme,
    val language: AppLanguage,
    val pushNotificationsEnabled: Boolean,
    val helpIconVisible: Boolean,
    val appVersion: String,
)

@Composable
fun SettingsScreenContent(
    modifier: Modifier = Modifier,
    colors: MarxistExtendedColors = MarxistReaderTheme.colors,
    scrollState: ScrollState = rememberScrollState(),
    uiState: SettingsUiState,
    themeDialogUpdate: (Boolean) -> Unit,
    languageDialogUpdate: (Boolean) -> Unit,
    helpIconDialogUpdate: (Boolean) -> Unit,
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
            icon = Icons.Outlined.Language,
            iconBgColor = colors.settingsIconBg,
            title = stringResource(R.string.settings_language),
            subtitle = languageLabel(uiState.language),
            showTrailingArrow = true,
            onClick = { languageDialogUpdate(true) },
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

        SettingsItemRow(
            icon = Icons.AutoMirrored.Outlined.HelpOutline,
            iconBgColor = colors.settingsIconBg,
            title = stringResource(R.string.settings_help_icon),
            subtitle = if (uiState.helpIconVisible) {
                stringResource(R.string.settings_help_icon_shown)
            } else {
                stringResource(R.string.settings_help_icon_hidden)
            },
            showTrailingArrow = true,
            onClick = { helpIconDialogUpdate(true) },
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
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

        val uriHandler = LocalUriHandler.current
        SettingsItemRow(
            icon = Icons.Outlined.Code,
            iconBgColor = colors.settingsIconBg,
            title = stringResource(R.string.settings_source_code),
            subtitle = stringResource(R.string.settings_source_code_subtitle),
            showTrailingArrow = true,
            onClick = { uriHandler.openUri(SOURCE_CODE_URL) },
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

        SettingsItemRow(
            icon = Icons.Outlined.MailOutline,
            iconBgColor = colors.settingsIconBg,
            title = stringResource(R.string.settings_contact),
            subtitle = stringResource(R.string.settings_contact_subtitle),
            showTrailingArrow = true,
            onClick = { uriHandler.openUri(AppConfig.Site.CONTACT_URL) },
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

        SettingsItemRow(
            icon = Icons.Outlined.PrivacyTip,
            iconBgColor = colors.settingsIconBg,
            title = stringResource(R.string.settings_privacy_policy),
            subtitle = stringResource(R.string.settings_privacy_policy_subtitle),
            showTrailingArrow = true,
            onClick = { uriHandler.openUri(PRIVACY_POLICY_URL) },
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

        SettingsItemRow(
            icon = Icons.Outlined.Gavel,
            iconBgColor = colors.settingsIconBg,
            title = stringResource(R.string.settings_terms_conditions),
            subtitle = stringResource(R.string.settings_terms_conditions_subtitle),
            showTrailingArrow = true,
            onClick = { uriHandler.openUri(TERMS_CONDITIONS_URL) },
        )

        Spacer(modifier = Modifier.height(24.dp))
        // Play's Misleading Claims policy requires an easy-to-see statement that this app is not
        // a government entity, since it publishes political/policy content.
        DisclaimerCard(colors = colors)

        Spacer(modifier = Modifier.height(24.dp))
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
private fun DisclaimerCard(
    colors: MarxistExtendedColors,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(colors.settingsIconBg)
            .padding(16.dp),
    ) {
        Text(
            text = stringResource(R.string.settings_disclaimer_title),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.settings_disclaimer_body),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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

@Composable
private fun themeLabel(theme: Theme): String = when (theme) {
    Theme.LIGHT -> stringResource(R.string.theme_light)
    Theme.DARK -> stringResource(R.string.theme_dark)
    Theme.DEFAULT -> stringResource(R.string.theme_default)
}

@Composable
private fun languageLabel(lang: AppLanguage): String = when (lang) {
    AppLanguage.TAMIL -> stringResource(R.string.language_tamil)
    AppLanguage.ENGLISH -> stringResource(R.string.language_english)
}

@Composable
fun ThemeDialog(
    current: Theme,
    onSelect: (Theme) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_theme_subtitle)) },
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
                        Text(text = themeLabel(theme))
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
        title = { Text(stringResource(R.string.settings_language)) },
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

/**
 * Educates the user about the contextual help icon and lets them show/hide it. Opened when the user
 * taps the "Help icon" settings row so the explanation appears exactly when they're deciding.
 */
@Composable
fun HelpIconDialog(
    visible: Boolean,
    onToggle: (Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                contentDescription = null,
            )
        },
        title = { Text(stringResource(R.string.settings_help_icon)) },
        text = {
            Column(modifier = Modifier.wrapContentSize()) {
                Text(
                    text = stringResource(R.string.settings_help_icon_dialog_message),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(R.string.settings_help_icon_toggle_label),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f),
                    )
                    Switch(
                        checked = visible,
                        onCheckedChange = onToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = MarxistReaderTheme.colors.settingsGroupTitle,
                            uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surface,
                            uncheckedBorderColor = MaterialTheme.colorScheme.outline,
                        ),
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.settings_help_icon_dialog_confirm))
            }
        },
    )
}
