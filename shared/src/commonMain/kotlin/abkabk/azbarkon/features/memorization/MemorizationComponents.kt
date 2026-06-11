package abkabk.azbarkon.features.memorization

import abkabk.azbarkon.ui.theme.AzbarkonTheme
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
import azbarkoncmp.shared.generated.resources.arrow_back
import azbarkoncmp.shared.generated.resources.forward
import azbarkoncmp.shared.generated.resources.heart
import azbarkoncmp.shared.generated.resources.ic_delete
import azbarkoncmp.shared.generated.resources.memorization_due_cards_format
import azbarkoncmp.shared.generated.resources.memorization_quick_start
import azbarkoncmp.shared.generated.resources.memorization_quick_start_desc
import azbarkoncmp.shared.generated.resources.memorization_quick_start_famous_ghazal
import azbarkoncmp.shared.generated.resources.memorization_quick_start_short_couplets
import azbarkoncmp.shared.generated.resources.memorization_quick_start_simple_poem
import azbarkoncmp.shared.generated.resources.memorization_select_hero_subtitle
import azbarkoncmp.shared.generated.resources.memorization_select_hero_title
import azbarkoncmp.shared.generated.resources.memorization_status_format
import azbarkoncmp.shared.generated.resources.newsstand
import azbarkoncmp.shared.generated.resources.palette
import azbarkoncmp.shared.generated.resources.search
import azbarkoncmp.shared.generated.resources.treasure
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun MemorizationHeroSection(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(Res.drawable.palette),
                contentDescription = null,
                modifier = Modifier.size(72.dp),
            )
        }

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
    onShortCoupletsClick: () -> Unit,
    onFamousGhazalClick: () -> Unit,
    onSimplePoemClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(Res.string.memorization_quick_start),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )

        Text(
            text = stringResource(Res.string.memorization_quick_start_desc),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            QuickStartCategoryTile(
                label = stringResource(Res.string.memorization_quick_start_short_couplets),
                icon = Res.drawable.heart,
                onClick = onShortCoupletsClick,
                modifier = Modifier.weight(1f)
            )
            QuickStartCategoryTile(
                label = stringResource(Res.string.memorization_quick_start_famous_ghazal),
                icon = Res.drawable.treasure,
                onClick = onFamousGhazalClick,
                modifier = Modifier.weight(1f)
            )
            QuickStartCategoryTile(
                label = stringResource(Res.string.memorization_quick_start_simple_poem),
                icon = Res.drawable.newsstand,
                onClick = onSimplePoemClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun QuickStartCategoryTile(
    label: String,
    icon: DrawableResource,
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
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(28.dp),
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.9f),
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
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
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
                    .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
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
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
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
            onShortCoupletsClick = {},
            onFamousGhazalClick = {},
            onSimplePoemClick = {},
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
