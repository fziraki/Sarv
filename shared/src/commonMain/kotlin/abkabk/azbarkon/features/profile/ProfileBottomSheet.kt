package abkabk.azbarkon.features.profile

import abkabk.azbarkon.domain.model.ThemeMode
import abkabk.azbarkon.domain.model.profile.BadgeUi
import abkabk.azbarkon.domain.model.profile.GameLevelCatalog
import abkabk.azbarkon.domain.model.profile.LevelListItemUi
import abkabk.azbarkon.domain.model.profile.LevelRowState
import abkabk.azbarkon.domain.model.profile.ProfileSheet
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalContext
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.check_circle
import azbarkoncmp.shared.generated.resources.lock
import azbarkoncmp.shared.generated.resources.profile_badges_title
import azbarkoncmp.shared.generated.resources.profile_daily_beyt_subtitle
import azbarkoncmp.shared.generated.resources.profile_daily_beyt_title
import azbarkoncmp.shared.generated.resources.profile_level_format
import azbarkoncmp.shared.generated.resources.profile_level_score_required
import azbarkoncmp.shared.generated.resources.profile_level_start
import azbarkoncmp.shared.generated.resources.profile_levels_title
import azbarkoncmp.shared.generated.resources.profile_memorization_reminder_subtitle
import azbarkoncmp.shared.generated.resources.profile_memorization_reminder_title
import azbarkoncmp.shared.generated.resources.profile_settings_title
import azbarkoncmp.shared.generated.resources.profile_theme_dark
import azbarkoncmp.shared.generated.resources.profile_theme_light
import azbarkoncmp.shared.generated.resources.profile_theme_system
import azbarkoncmp.shared.generated.resources.profile_theme_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSheets(
    state: ProfileState,
    onAction: (ProfileAction) -> Unit,
) {
    val sheet = state.activeSheet ?: return
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = { onAction(ProfileAction.OnDismissSheet) },
        sheetState = sheetState,
    ) {
        when (sheet) {
            ProfileSheet.Settings ->
                ProfileSettingsSheetContent(
                    isDailyBeytEnabled = state.isDailyBeytNotificationEnabled,
                    isMemorizationReminderEnabled = state.isMemorizationReminderEnabled,
                    themeMode = state.themeMode,
                    onDailyBeytToggle = { onAction(ProfileAction.OnDailyBeytNotificationToggle(it)) },
                    onMemorizationReminderToggle = { onAction(ProfileAction.OnMemorizationReminderToggle(it)) },
                    onThemeModeSelected = { onAction(ProfileAction.OnThemeModeSelected(it)) },
                )

            ProfileSheet.Badges ->
                ProfileBadgesSheetContent(badges = state.allBadges)

            ProfileSheet.Levels ->
                ProfileLevelsSheetContent(levels = state.allLevels)
        }
    }
}

@Composable
private fun ProfileSettingsSheetContent(
    isDailyBeytEnabled: Boolean,
    isMemorizationReminderEnabled: Boolean,
    themeMode: ThemeMode,
    onDailyBeytToggle: (Boolean) -> Unit,
    onMemorizationReminderToggle: (Boolean) -> Unit,
    onThemeModeSelected: (ThemeMode) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.Start
    ) {


        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.profile_settings_title),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        ProfileSettingToggleRow(
            title = stringResource(Res.string.profile_daily_beyt_title),
            subtitle = stringResource(Res.string.profile_daily_beyt_subtitle),
            checked = isDailyBeytEnabled,
            onCheckedChange = onDailyBeytToggle,
        )


        ProfileSettingToggleRow(
            title = stringResource(Res.string.profile_memorization_reminder_title),
            subtitle = stringResource(Res.string.profile_memorization_reminder_subtitle),
            checked = isMemorizationReminderEnabled,
            onCheckedChange = onMemorizationReminderToggle,
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start) {
            Text(
                text = stringResource(Res.string.profile_theme_title),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
            )
            ProfileThemeOption(
                label = stringResource(Res.string.profile_theme_system),
                selected = themeMode == ThemeMode.System,
                onClick = { onThemeModeSelected(ThemeMode.System) },
            )
            ProfileThemeOption(
                label = stringResource(Res.string.profile_theme_light),
                selected = themeMode == ThemeMode.Light,
                onClick = { onThemeModeSelected(ThemeMode.Light) },
            )
            ProfileThemeOption(
                label = stringResource(Res.string.profile_theme_dark),
                selected = themeMode == ThemeMode.Dark,
                onClick = { onThemeModeSelected(ThemeMode.Dark) },
            )
        }
    }
}

@Composable
private fun ProfileSettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
            )
        }
    }
}

@Composable
private fun ProfileThemeOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (selected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceContainerHigh
                    },
                ).clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color =
                if (selected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
        )
    }
}

@Composable
private fun ProfileBadgesSheetContent(badges: List<BadgeUi>) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(Res.string.profile_badges_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            items(badges, key = { it.id }) { badge ->
                BadgeListRow(
                    item = badge,
                    modifier = Modifier.padding(vertical = 12.dp),
                )
                if (badge.id != badges.lastOrNull()?.id) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}

@Composable
private fun ProfileLevelsSheetContent(levels: List<LevelListItemUi>) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(Res.string.profile_levels_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(levels, key = { it.level.id }) { item ->
                ProfileLevelRow(item = item)
            }
        }
    }
}

@Composable
private fun ProfileLevelRow(item: LevelListItemUi) {
    val backgroundColor =
        when (item.state) {
            LevelRowState.Current -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
            else -> MaterialTheme.colorScheme.surfaceContainerHigh
        }
    val subtitle =
        if (item.level.id == 1) {
            stringResource(Res.string.profile_level_start)
        } else {
            stringResource(
                Res.string.profile_level_score_required,
                GameLevelCatalog.requiredScoreForLevel(item.level.id),
            )
        }

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(backgroundColor)
                .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(Res.string.profile_level_format, item.level.id),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = item.level.name,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }

        when (item.state) {
            LevelRowState.Locked ->
                Icon(
                    painter = painterResource(Res.drawable.lock),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )

            LevelRowState.Completed,
            LevelRowState.Current,
            ->
                Icon(
                    painter = painterResource(Res.drawable.check_circle),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
        }
    }
}
