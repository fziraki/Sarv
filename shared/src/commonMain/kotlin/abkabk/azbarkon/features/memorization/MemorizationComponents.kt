package abkabk.azbarkon.features.memorization

import abkabk.azbarkon.ui.theme.AzbarkonTheme
import abkabk.azbarkon.ui.theme.LightColorScheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.forward
import azbarkoncmp.shared.generated.resources.ic_delete
import azbarkoncmp.shared.generated.resources.memorization_due_cards_format
import azbarkoncmp.shared.generated.resources.memorization_quick_start
import azbarkoncmp.shared.generated.resources.memorization_quick_start_desc
import azbarkoncmp.shared.generated.resources.memorization_quick_start_ghazal
import azbarkoncmp.shared.generated.resources.memorization_quick_start_couplet
import azbarkoncmp.shared.generated.resources.memorization_quick_start_rubaiyat
import azbarkoncmp.shared.generated.resources.memorization_select_hero_subtitle
import azbarkoncmp.shared.generated.resources.memorization_select_hero_title
import azbarkoncmp.shared.generated.resources.memorization_status_format
import azbarkoncmp.shared.generated.resources.ornoment30
import azbarkoncmp.shared.generated.resources.search
import azbarkoncmp.shared.generated.resources.siahmashghkhat
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private const val LABEL_WIDTH_FRACTION = 0.9f

@Composable
fun MemorizationHeroSection(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Image(
            painter = painterResource(Res.drawable.siahmashghkhat),
            contentDescription = null,
        )

        Text(
            text = stringResource(Res.string.memorization_select_hero_title),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )

        Text(
            text = stringResource(Res.string.memorization_select_hero_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun QuickStartCard(
    onCoupletClick: () -> Unit,
    onGhazalClick: () -> Unit,
    onRubaiyatClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(LightColorScheme.primary)
                .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(Res.string.memorization_quick_start),
            style = MaterialTheme.typography.titleLarge,
            color = LightColorScheme.onPrimary,
        )

        Text(
            text = stringResource(Res.string.memorization_quick_start_desc),
            style = MaterialTheme.typography.bodySmall,
            color = LightColorScheme.onPrimary.copy(alpha = 0.85f),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            QuickStartCategoryTile(
                label = stringResource(Res.string.memorization_quick_start_couplet),
                onClick = onCoupletClick,
                modifier = Modifier.weight(1f)
            )
            QuickStartCategoryTile(
                label = stringResource(Res.string.memorization_quick_start_ghazal),
                onClick = onGhazalClick,
                modifier = Modifier.weight(1f)
            )
            QuickStartCategoryTile(
                label = stringResource(Res.string.memorization_quick_start_rubaiyat),
                onClick = onRubaiyatClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun QuickStartCategoryTile(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onClick)
                .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(LightColorScheme.surface.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(Res.drawable.ornoment30),
                contentDescription = null,
                tint = LightColorScheme.onPrimary,
                modifier = Modifier.size(42.dp),
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = LightColorScheme.onPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(LABEL_WIDTH_FRACTION),
        )
    }
}

@Composable
fun MemorizationOptionRow(
    title: String,
    description: String,
    icon: DrawableResource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(16.dp),
                ).clickable(onClick = onClick)
                .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {


        Box(
            modifier =
                Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(LightColorScheme.secondary),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = LightColorScheme.onSecondary,
                modifier = Modifier.size(24.dp),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Icon(
            painter = painterResource(Res.drawable.forward),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.scale(scaleX = -1f, scaleY = 1f).size(20.dp),
        )

    }
}

@Composable
fun ActivePoemCard(
    title: String,
    poetName: String,
    boxLevel: Int,
    level: Int,
    progress: Float,
    dueCards: Int,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(16.dp),
                ).clickable(onClick = onClick)
                .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = poetName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text =
                    stringResource(
                        Res.string.memorization_status_format,
                        boxLevel,
                        level,
                    ),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(),
                trackColor = LightColorScheme.outlineVariant,
                gapSize = (-4).dp
            )
            if (dueCards > 0) {
                Text(
                    text = stringResource(Res.string.memorization_due_cards_format, dueCards),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
        IconButton(onClick = onDeleteClick) {
            Icon(
                painter = painterResource(Res.drawable.ic_delete),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Preview
@Composable
private fun MemorizationHeroSectionPreview() {
    AzbarkonTheme {
        MemorizationHeroSection(modifier = Modifier.padding(16.dp))
    }
}

@Preview
@Composable
private fun QuickStartCardPreview() {
    AzbarkonTheme {
        QuickStartCard(
            onCoupletClick = {},
            onGhazalClick = {},
            onRubaiyatClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview
@Composable
private fun MemorizationOptionRowPreview() {
    AzbarkonTheme {
        MemorizationOptionRow(
            title = "جستجوی شعر",
            description = "شاعر، بیت یا نام شعر را جستجو کنید",
            icon = Res.drawable.search,
            onClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview
@Composable
private fun ActivePoemCardPreview() {
    AzbarkonTheme {
        ActivePoemCard(
            title = "غزل ۱",
            poetName = "حافظ",
            boxLevel = 2,
            level = 2,
            progress = 0.4f,
            dueCards = 3,
            onClick = {},
            onDeleteClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
