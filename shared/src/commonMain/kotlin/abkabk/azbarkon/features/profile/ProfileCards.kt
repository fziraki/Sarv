package abkabk.azbarkon.features.profile

import abkabk.azbarkon.domain.model.profile.BadgeUi
import abkabk.azbarkon.domain.model.profile.GameProfileStats
import abkabk.azbarkon.domain.model.profile.MemorizationProfileStats
import abkabk.azbarkon.domain.model.profile.ProfileLevelProgress
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.cd_levels
import azbarkoncmp.shared.generated.resources.ic_help
import azbarkoncmp.shared.generated.resources.palette
import azbarkoncmp.shared.generated.resources.profile_game_status_title
import azbarkoncmp.shared.generated.resources.profile_game_streak
import azbarkoncmp.shared.generated.resources.profile_game_total_points
import azbarkoncmp.shared.generated.resources.profile_level_format
import azbarkoncmp.shared.generated.resources.profile_mem_poems
import azbarkoncmp.shared.generated.resources.profile_mem_poets
import azbarkoncmp.shared.generated.resources.profile_mem_streak
import azbarkoncmp.shared.generated.resources.profile_memorization_status_title
import azbarkoncmp.shared.generated.resources.profile_view_all_badges
import azbarkoncmp.shared.generated.resources.profile_xp_format
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileHeader(
    levelProgress: ProfileLevelProgress,
    onLevelsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(Res.drawable.palette),
            contentDescription = null,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = levelProgress.levelName,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
            IconButton(
                onClick = onLevelsClick,
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_help),
                    contentDescription = stringResource(Res.string.cd_levels),
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.profile_level_format, levelProgress.levelId),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            LinearProgressIndicator(
                progress = {
                    if (levelProgress.targetXp == 0) {
                        0f
                    } else {
                        levelProgress.currentXp.toFloat() / levelProgress.targetXp.toFloat()
                    }
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(8.dp),
                color = ProgressIndicatorDefaults.linearColor,
                trackColor = ProgressIndicatorDefaults.linearTrackColor,
                strokeCap = StrokeCap.Round,
                drawStopIndicator = {},
                gapSize = (-4).dp,
            )
        }

        Text(
            modifier = Modifier.fillMaxWidth(),
            text =
                stringResource(
                    Res.string.profile_xp_format,
                    levelProgress.currentXp,
                    levelProgress.targetXp,
                ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun MemorizationStatusCard(
    stats: MemorizationProfileStats,
    modifier: Modifier = Modifier,
) {
    ProfileStatusCard(
        modifier = modifier,
        title = stringResource(Res.string.profile_memorization_status_title),
        items =
            listOf(
                stats.practiceStreak to stringResource(Res.string.profile_mem_streak),
                stats.memorizingPoetsCount to stringResource(Res.string.profile_mem_poets),
                stats.inProgressPoemCount to stringResource(Res.string.profile_mem_poems),
            ),
    )
}

@Composable
fun GameStatusCard(
    stats: GameProfileStats,
    modifier: Modifier = Modifier,
) {
    ProfileStatusCard(
        modifier = modifier,
        title = stringResource(Res.string.profile_game_status_title),
        items =
            listOf(
                stats.visitStreak to stringResource(Res.string.profile_game_streak),
                stats.coinBalance to stringResource(Res.string.profile_game_total_points),
            ),
    )
}

@Composable
private fun ProfileStatusCard(
    title: String,
    items: List<Pair<Int, String>>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(12.dp),
                ).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.End,
        )

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
        ) {
            items.forEachIndexed { index, (value, label) ->
                ProfileStatItem(
                    modifier = Modifier.weight(1f),
                    value = value,
                    label = label,
                )
                if (index < items.lastIndex) {
                    VerticalDivider(
                        modifier = Modifier.fillMaxHeight(),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.surface,
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileStatItem(
    value: Int,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value.toString(),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun ProfileBadges(
    badges: List<BadgeUi>,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(12.dp),
                ).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.End,
    ) {
        Text(
            modifier = Modifier.clickable(onClick = onViewAllClick),
            text = stringResource(Res.string.profile_view_all_badges),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.End,
        )

        LazyRow(modifier = Modifier.fillMaxWidth()) {
            items(
                items = badges,
                key = { badge -> badge.id },
            ) { badge ->
                BadgeItem(badge)
            }
        }
    }
}

@Composable
fun BadgeItem(item: BadgeUi) {
    BadgeIcon(
        isEarned = item.isEarned,
        modifier = Modifier.width(80.dp),
        showName = true,
        name = item.name,
    )
}

@Composable
fun BadgeListRow(
    item: BadgeUi,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        BadgeIcon(
            isEarned = item.isEarned,
            modifier = Modifier.size(56.dp),
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge,
                color =
                    if (item.isEarned) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun BadgeIcon(
    isEarned: Boolean,
    modifier: Modifier = Modifier,
    showName: Boolean = false,
    name: String = "",
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(Res.drawable.palette),
            contentDescription = null,
            modifier = if (showName) Modifier else Modifier.fillMaxSize(),
            colorFilter =
                if (isEarned) {
                    null
                } else {
                    ColorFilter.colorMatrix(
                        androidx.compose.ui.graphics.ColorMatrix().apply { setToSaturation(0f) },
                    )
                },
            alpha = if (isEarned) 1f else 0.45f,
        )
        if (showName) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
                text = name,
                style = MaterialTheme.typography.labelSmall,
                color =
                    if (isEarned) {
                        MaterialTheme.colorScheme.onBackground
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                textAlign = TextAlign.Center,
            )
        }
    }
}
